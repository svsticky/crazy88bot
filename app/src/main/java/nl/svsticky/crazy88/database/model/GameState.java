package nl.svsticky.crazy88.database.model;

import nl.svsticky.crazy88.database.driver.Driver;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class GameState {
    private final Driver driver;

    private static final String PROP_GAME_RUNNING = "game_running";

    public GameState(Driver driver) {
        this.driver = driver;
    }

    public boolean isGameRunning() throws SQLException {
        Optional<String> prop = this.getProperty(PROP_GAME_RUNNING);
        return prop.filter(Boolean::parseBoolean).isPresent();
    }

    public void setIsGameRunning(boolean isGameRunning) throws SQLException {
        setProperty(PROP_GAME_RUNNING, Boolean.toString(isGameRunning));
    }

    private void setProperty(String property, String value) throws SQLException {
        boolean exists = this.getProperty(property).isPresent();

        PreparedStatement p;
        if(exists) {
            p = this.driver.getConnection().prepareStatement("UPDATE game_state SET value = ? WHERE property = ?");
            p.setString(1, value);
            p.setString(2, property);
        } else {
            p = this.driver.getConnection().prepareStatement("INSERT INTO game_state (property, value) VALUES (?, ?)");
            p.setString(1, property);
            p.setString(2, value);
        }

        p.execute();
    }

    private Optional<String> getProperty(String property) throws SQLException {
        PreparedStatement p = this.driver.getConnection().prepareStatement("SELECT value FROM game_state WHERE property = ?");
        p.setString(1, property);
        ResultSet r = p.executeQuery();

        boolean hasProperty = r.next();
        if(!hasProperty) return Optional.empty();

        String value = r.getString("value");
        return Optional.of(value);
    }
}
