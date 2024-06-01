package nl.svsticky.crazy88.database.model;

import nl.svsticky.crazy88.database.driver.Driver;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Team {
    private final Driver driver;

    public final int teamId;

    public record AvailableLocation(int id) {}
    public record AvailableAssignment(int locationId, String assignment) {}

    private Team(Driver driver, int teamId) {
        this.driver = driver;
        this.teamId = teamId;
    }

    public static Team create(Driver driver, int teamId) throws SQLException {
        PreparedStatement pr = driver.getConnection().prepareStatement("INSERT INTO teams (team_id) VALUES (?)");
        pr.setInt(1, teamId);

        pr.execute();

        return new Team(driver, teamId);
    }

    public static Optional<Team> getbyId(Driver driver, int teamId) throws SQLException {
        PreparedStatement pr = driver.getConnection().prepareStatement("SELECT * FROM teams WHERE team_id = ?");
        pr.setInt(1, teamId);

        ResultSet rs = pr.executeQuery();
        if(!rs.next()) return Optional.empty();

        return Optional.of(new Team(driver, teamId));
    }

    public List<AvailableLocation> getAvailableLocations() throws SQLException {
        PreparedStatement pr = driver.getConnection().prepareStatement("SELECT * FROM team_available_locations WHERE team_id = ?");
        pr.setInt(1, teamId);
        ResultSet rs = pr.executeQuery();

        List<AvailableLocation> availableLocations = new ArrayList<>();
        while(rs.next()) {
            availableLocations.add(new AvailableLocation(
                    rs.getInt("location_id")
            ));
        }

        return availableLocations;
    }

    public List<AvailableAssignment> getAvailableAssignments() throws SQLException {
        PreparedStatement pr = driver.getConnection().prepareStatement("SELECT * FROM team_available_assignments WHERE team_id = ?");
        pr.setInt(1, teamId);

        ResultSet rs = pr.executeQuery();
        List<AvailableAssignment> availableAssignments = new ArrayList<>();
        while(rs.next()) {
            availableAssignments.add(new AvailableAssignment(
                    rs.getInt("location_id"),
                    rs.getString("assignment")
            ));
        }

        return availableAssignments;
    }

    public void unlockLocation(int id) throws SQLException {
        PreparedStatement pr = driver.getConnection().prepareStatement("INSERT INTO team_available_locations (team_id, location_id) VALUES (?, ?)");
        pr.setInt(1, teamId);
        pr.setInt(2, id);

        pr.execute();
    }

    public void unlockAssignments(int locationId, String[] assignments) throws SQLException {
        for (String assignment : assignments) {
            PreparedStatement pr = driver.getConnection().prepareStatement("INSERT INTO team_available_assignments (team_id, location_id, assignment) VALUES (?, ?, ?)");
            pr.setInt(1, teamId);
            pr.setInt(2, locationId);
            pr.setString(3, assignment);

            pr.execute();
        }
    }
}
