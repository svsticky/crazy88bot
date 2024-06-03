package nl.svsticky.crazy88.database.driver;

import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseUtil {
    /**
     * Get a nullable Integer from the ResultSet
     * @param rs The result set
     * @param columnName The name of the column
     * @return The nullable integer
     * @throws SQLException The column did not exist or another error occured
     */
    public static @Nullable  Integer getInteger(ResultSet rs, String columnName) throws SQLException {
        int maybeVal = rs.getInt(columnName);
        if(rs.wasNull()) {
            return null;
        } else {
            return maybeVal;
        }
    }
}
