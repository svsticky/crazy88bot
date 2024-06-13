package nl.svsticky.crazy88.database.driver;

import nl.svsticky.crazy88.App;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Migration manager
 */
public class Migrator {
    private final List<Migration> migrations;
    private final Driver driver;

    private static final String MIGRATION_TABLE = "__crazy88_migrations";

    public Migrator(Driver driver) throws SQLException {
        this.driver = driver;

        if(this.migrationTableExists()) {
            this.migrations = this.loadExistingMigrations();
            App.getLogger().debug("Migrations table already exists");
        } else {
            this.migrations = new ArrayList<>();
            App.getLogger().debug("Migrations table does not exist");
        }
    }

    /**
     * Apply pending migrations
     * @throws SQLException SQL error
     * @throws IOException IO error
     */
    public void applyMigrations() throws SQLException, IOException {
        List<DiskMigration> diskMigrations = this.loadDiskMigrations().toList();
        App.getLogger().debug("Found {} available migrations", diskMigrations.size());

        List<DiskMigration> toApply = new ArrayList<>(diskMigrations
                .stream()
                .filter(migration -> this.migrations
                        .stream()
                        .noneMatch(m -> m.version() == migration.version())
                )
                .toList());
        toApply.sort(Comparator.comparingInt(DiskMigration::version));

        App.getLogger().info("Applying {} migrations", toApply.size());

        for (DiskMigration migration : toApply) {
            App.getLogger().info("Applying migration {}: {}", migration.version(), migration.name());

            for(String part :  migration.sql().split(Pattern.quote("\n\n"))) {
                if(part.trim().isEmpty()) continue;

                App.getLogger().debug(
                        "Applying SQL: \n'{}'", part
                );

                PreparedStatement p = this.driver.getConnection().prepareStatement(part);
                p.execute();
                p.close();
            }


            PreparedStatement p1 = this.driver.getConnection().prepareStatement(
                    "INSERT INTO __crazy88_migrations (version,name) VALUES (?, ?)"
            );
            p1.setInt(1, migration.version());
            p1.setString(2, migration.name());

            p1.execute();
            p1.close();

            this.migrations.add(new Migration(
                    migration.version(),
                    migration.name()
            ));
        }
    }

    /**
     * Load migrations stored inside the JAR file
     * @return The migrations
     * @throws IOException IO error
     */
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

                    try {
                        is.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

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

    /**
     * Get all resourcefiles in the JAR at the path provided
     * @param path The path inside the JAR
     * @return A list of files in the directory
     * @throws IOException IO error
     */
    @SuppressWarnings("SameParameterValue")
    private List<String> getResourceFiles(String path) throws IOException {
        try {
            URI uri = Objects.requireNonNull(Migrator.class.getResource(path)).toURI();
            Path fsPath;
            FileSystem fileSystem = null;
            if (uri.getScheme().equals("jar")) {
                fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
                fsPath = fileSystem.getPath(path);
            } else {
                fsPath = Paths.get(uri);
            }

            Stream<Path> walk = Files.walk(fsPath, 1);

            List<String> filenames = new ArrayList<>();

            Iterator<Path> it = walk.iterator();
            while(it.hasNext()) {
                Path p = it.next();

                if(!p.getFileName().toString().endsWith("sql")) {
                    App.getLogger().debug("Skipping {}, not an SQL file", p);
                    continue;
                }

                filenames.add(p.getFileName().toString());
            }

            if(fileSystem != null) {
                fileSystem.close();
            }

            return filenames;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get a JAR resource as a stream
     * @param resource The full path to the resource inside the JAR
     * @return The stream to the resource
     */
    @SuppressWarnings("resource")
    private InputStream getResourceAsStream(String resource) {
        final InputStream in = Migrator.class.getResourceAsStream(resource);
        return in == null ? getClass().getResourceAsStream(resource) : in;
    }


    /**
     * Load migrations which have already been applied
     * @return List of previously applied migrations
     * @throws SQLException SQL error
     */
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

    /**
     * Check if the migrations table exists
     * @return Whether the table exists
     * @throws SQLException SQL error
     */
    private boolean migrationTableExists() throws SQLException {
        DatabaseMetaData metdata = this.driver.getConnection().getMetaData();
        ResultSet tables = metdata.getTables(null, null, MIGRATION_TABLE, null);

        // If the migration table name has underscores in it (wildcards in SQL),
        // it could return valid matches, but the table might not exist,
        // so check to be sure
        while(tables.next()) {
            if(tables.getString("TABLE_NAME").equals(MIGRATION_TABLE)) {
                tables.close();
                return true;
            }
        }

        tables.close();
        return false;
    }
}
