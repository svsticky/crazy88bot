package nl.svsticky.crazy88.database.driver;

public record DiskMigration(int version, String name, String sql) { }
