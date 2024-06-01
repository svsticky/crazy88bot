package nl.svsticky.crazy88.database.driver;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseUtil {
    public static Integer getInteger(ResultSet rs, String columnName) throws SQLException {
        int maybeVal = rs.getInt(columnName);
        if(rs.wasNull()) {
            return null;
        } else {
            return maybeVal;
        }
    }
}
