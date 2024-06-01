package nl.svsticky.crazy88.database.driver;

import nl.svsticky.crazy88.App;

import java.io.*;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Migrator {
    private final List<Migration> migrations;
    private final Driver driver;

    private static final String MIGRATION_TABLE = "__crazy88_migrations";

    public Migrator(Driver driver) throws SQLException {
        this.driver = driver;

        if(this.migrationTableExists()) {
            this.migrations = this.loadExistingMigrations();
        } else {
            this.migrations = new ArrayList<>();
        }
    }

    public void applyMigrations() throws SQLException, IOException {
        Stream<DiskMigration> diskMigrations = this.loadDiskMigrations();
        List<DiskMigration> toApply = diskMigrations
                .filter(migration -> this.migrations
                        .stream()
                        .noneMatch(m -> m.version() == migration.version())
                )
                .toList();

        for (DiskMigration migration : toApply) {
            App.getLogger().info("Applying migration {}: {}", migration.version(), migration.name());
            App.getLogger().debug(
                    "Applying SQL: {}", migration.sql()
            );

            PreparedStatement p = this.driver.getConnection().prepareStatement(migration.sql());
            p.execute();

            PreparedStatement p1 = this.driver.getConnection().prepareStatement(
                    "INSERT INTO __crazy88_migrations (version,name) VALUES (?, ?)"
            );
            p1.setInt(1, migration.version());
            p1.setString(2, migration.name());

            p1.execute();

            this.migrations.add(new Migration(
                    migration.version(),
                    migration.name()
            ));
        }
    }

    private Stream<DiskMigration> loadDiskMigrations() throws IOException {
        List<String> resourceFiles = this.getResourceFiles("/migrations");
        return resourceFiles
                .stream()
                .map(fileName -> {
                    InputStream is = getResourceAsStream(
                            String.format("/migrations/%s", fileName)
                    );

                    String sql = new BufferedReader(new InputStreamReader(is))
                            .lines()
                            .collect(Collectors.joining("\n"));

                    String[] fileNameParts = fileName
                            .split(Pattern.quote("."))[0]
                            .split(Pattern.quote("_"));

                    int version = Integer.parseInt(fileNameParts[0]);
                    String name = Arrays.stream(fileNameParts)
                            .skip(1)
                            .collect(Collectors.joining("_"));

                    return new DiskMigration(
                            version,
                            name,
                            sql
                    );
                });
    }

    private List<String> getResourceFiles(String path) throws IOException {
        List<String> filenames = new ArrayList<>();

        try (
                InputStream in = getResourceAsStream(path);
                BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String resource;

            while ((resource = br.readLine()) != null) {
                filenames.add(resource);
            }
        }

        return filenames;
    }

    private InputStream getResourceAsStream(String resource) {
        final InputStream in
                = getContextClassLoader().getResourceAsStream(resource);

        return in == null ? getClass().getResourceAsStream(resource) : in;
    }

    private ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    private List<Migration> loadExistingMigrations() throws SQLException {
        PreparedStatement s = this.driver.getConnection().prepareStatement(
                String.format("SELECT version,name FROM %s", MIGRATION_TABLE)
        );

        ResultSet r = s.executeQuery();
        List<Migration> migrations = new ArrayList<>();
        while(r.next()) {
            migrations.add(new Migration(
                    r.getInt("version"),
                    r.getString("name")
            ));
        }

        s.close();
        return migrations;
    }

    private boolean migrationTableExists() throws SQLException {
        DatabaseMetaData metdata = this.driver.getConnection().getMetaData();
        ResultSet tables = metdata.getTables(null, null, MIGRATION_TABLE, null);

        // If the migration table name has underscores in it (wildcards in SQL),
        // it could return valid matches, but the table might not exist,
        // so check to be sure
        while(tables.next()) {
            if(tables.getString("TABLE_NAME").equals(MIGRATION_TABLE)) {
                tables.close();;
                return true;
            }
        }

        tables.close();
        return false;
    }
}
