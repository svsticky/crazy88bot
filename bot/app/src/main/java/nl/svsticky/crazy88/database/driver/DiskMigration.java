package nl.svsticky.crazy88.database.driver;

/**
 * A migration according to the file on disk
 * @param version The version, as parsed from the filename
 * @param name The name, as parsed from the filename
 * @param sql The contained SQL statements
 */
public record DiskMigration(int version, String name, String sql) { }
