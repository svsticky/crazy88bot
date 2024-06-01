package nl.svsticky.crazy88.database.model;

import nl.svsticky.crazy88.database.driver.DatabaseUtil;
import nl.svsticky.crazy88.database.driver.Driver;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class User {
    private final Driver driver;
    public final long userId;
    public UserType userType;
    public Optional<Integer> teamId;
    public Optional<Integer> helperStationId;

    public enum UserType {
        ADMIN,
        HELPER,
        REGULAR;
    }

    private User(Driver driver, long userId, UserType userType, Optional<Integer> teamId, Optional<Integer> helperStationId) {
        this.driver = driver;
        this.userId = userId;
        this.userType = userType;
        this.teamId = teamId;
        this.helperStationId = helperStationId;
    }

    public static User create(Driver driver, long userId, UserType userType, Optional<Integer> teamId, Optional<Integer> helperStationId) throws SQLException {
        PreparedStatement pr = driver.getConnection().prepareStatement("INSERT INTO users (user_id, user_type, team_id, helper_station_id) VALUES (?, ?, ?, ?)");
        pr.setLong(1, userId);
        pr.setString(2, userType.name());
        pr.setInt(3, teamId.orElse(null));
        pr.setInt(4, helperStationId.orElse(null));

        pr.execute();

        return new User(
                driver,
                userId,
                userType,
                teamId,
                helperStationId
        );
    }

    public static Optional<User> getById(Driver driver, long userId) throws SQLException {
        PreparedStatement pr = driver.getConnection().prepareStatement("SELECT * FROM users WHERE user_id = ?");
        pr.setLong(1, userId);

        ResultSet rs = pr.executeQuery();
        if(!rs.next()) return Optional.empty();

        return Optional.of(new User(
                driver,
                rs.getLong("user_id"),
                UserType.valueOf(rs.getString("user_type")),
                Optional.ofNullable(DatabaseUtil.getInteger(rs, "team_id")),
                Optional.ofNullable(DatabaseUtil.getInteger(rs, "helper_station_id"))
        ));
    }

    public static List<User> getForTeam(Driver driver, int teamId) throws SQLException {
        PreparedStatement pr = driver.getConnection().prepareStatement("SELECT * FROM users WHERE team_id = ?");
        pr.setInt(1, teamId);
        ResultSet rs = pr.executeQuery();
        List<User> users = new ArrayList<>();
        while(rs.next()) {
            users.add(new User(
                    driver,
                    rs.getLong("user_id"),
                    UserType.valueOf(rs.getString("user_type")),
                    Optional.ofNullable(DatabaseUtil.getInteger(rs, "team_id")),
                    Optional.ofNullable(DatabaseUtil.getInteger(rs, "helper_station_id"))
            ));
        }

        return users;
    }

    public void setTeamId(Optional<Integer> teamId) throws SQLException {
        PreparedStatement pr = driver.getConnection().prepareStatement("UPDATE users SET team_id = ? WHERE user_id = ?");
        pr.setInt(1, teamId.orElse(null));
        pr.setLong(2, this.userId);

        pr.execute();

        this.teamId = teamId;
    }

    public void setHelperStationId(Optional<Integer> helperStationId) throws SQLException {
        PreparedStatement pr = driver.getConnection().prepareStatement("UPDATE users SET helperStationId = ? WHERE user_id = ?");
        pr.setInt(1, helperStationId.orElse(null));
        pr.setLong(2, this.userId);

        pr.execute();

        this.helperStationId = helperStationId;
    }

    public void setUserType(UserType userType) throws SQLException {
        PreparedStatement pr = driver.getConnection().prepareStatement("UPDATE users SET userType = ? WHERE user_id = ?");
        pr.setString(1, userType.name());
        pr.setLong(2, this.userId);

        pr.execute();

        this.userType = userType;
    }
}
