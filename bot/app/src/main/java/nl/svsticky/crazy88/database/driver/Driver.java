package nl.svsticky.crazy88.database.driver;

import nl.svsticky.crazy88.config.model.DatabaseModel;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * The database driver
 */
public class Driver {
    private final Connection connection;

    public Driver(DatabaseModel configuration) throws SQLException {
        this.connection = DriverManager.getConnection(
                String.format("jdbc:mysql://%s:%d/%s", configuration.host, configuration.port, configuration.database),
                configuration.username,
                configuration.password
        );
    }

    /**
     * Get a connection to the database
     * @return A connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Apply pending migrations
     * @throws IOException IO error
     * @throws SQLException SQL error
     */
    public void applyMigrations() throws IOException, SQLException {
        Migrator m = new Migrator(this);
        m.applyMigrations();
    }
}
