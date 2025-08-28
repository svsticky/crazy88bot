package nl.svsticky.crazy88.database.model;

import nl.svsticky.crazy88.database.driver.DatabaseUtil;
import nl.svsticky.crazy88.database.driver.Driver;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Team {
    private final Driver driver;

    public final int teamId;

    public int helperLocation;

    public record AvailableLocation(int id) {}
    public record AvailableAssignment(int id, int locationId, String assignment) {}
    public record SubmittedAssignment(int id, Optional<Integer> assignedGrade) {}

    private Team(Driver driver, int teamId, int helperLocation) {
        this.driver = driver;
        this.teamId = teamId;
        this.helperLocation = helperLocation;
    }

    public static Team create(Driver driver, int teamId, int startingLocationId) throws SQLException {
        PreparedStatement pr = driver.getConnection().prepareStatement("INSERT INTO teams (team_id, helper_location) VALUES (?, ?)");
        pr.setInt(1, teamId);
        pr.setInt(2, startingLocationId);

        pr.execute();
        pr.close();

        return new Team(driver, teamId, startingLocationId);
    }

    public static Optional<Team> getbyId(Driver driver, int teamId) throws SQLException {
        PreparedStatement pr = driver.getConnection().prepareStatement("SELECT * FROM teams WHERE team_id = ?");
        pr.setInt(1, teamId);

        ResultSet rs = pr.executeQuery();
        if(!rs.next()) return Optional.empty();

        int helperLocation = rs.getInt("helper_location");

        pr.close();
        return Optional.of(new Team(driver, teamId, helperLocation));
    }

    public static List<Team> getTeams(Driver driver) throws SQLException {
        PreparedStatement pr = driver.getConnection().prepareStatement("SELECT * FROM teams");
        ResultSet rs = pr.executeQuery();
        List<Team> teams = new ArrayList<>();
        while(rs.next()) {
            teams.add(new Team(driver, rs.getInt("team_id"), rs.getInt("helper_location")));
        }

        pr.close();
        return teams;
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

        pr.close();
        return availableLocations;
    }

    public List<AvailableAssignment> getAvailableAssignments() throws SQLException {
        PreparedStatement pr = driver.getConnection().prepareStatement("SELECT * FROM team_available_assignments WHERE team_id = ?");
        pr.setInt(1, teamId);

        ResultSet rs = pr.executeQuery();
        List<AvailableAssignment> availableAssignments = new ArrayList<>();
        while(rs.next()) {
            availableAssignments.add(new AvailableAssignment(
                    rs.getInt("assignment_id"),
                    rs.getInt("location_id"),
                    rs.getString("assignment")
            ));
        }

        pr.close();
        return availableAssignments;
    }

    public Optional<AvailableAssignment> getAvailableAssignmentById(int assignmentId) throws SQLException {
        PreparedStatement pr = driver.getConnection().prepareStatement("SELECT * FROM team_available_assignments WHERE assignment_id = ? AND team_id = ?");
        pr.setInt(1, assignmentId);
        pr.setInt(2, teamId);

        ResultSet rs = pr.executeQuery();
        if(rs.next()) {
            return Optional.of(new AvailableAssignment(
                    rs.getInt("assignment_id"),
                    rs.getInt("location_id"),
                    rs.getString("assignment")
            ));
        }

        pr.close();
        return Optional.empty();
    }

    public void unlockLocation(int id) throws SQLException {
        PreparedStatement pr = driver.getConnection().prepareStatement("INSERT INTO team_available_locations (team_id, location_id) VALUES (?, ?)");
        pr.setInt(1, teamId);
        pr.setInt(2, id);

        pr.execute();
        pr.close();
    }

    public void unlockAssignments(int locationId, HashMap<Integer, String> assignments) throws SQLException {
        for (Map.Entry<Integer, String> entry : assignments.entrySet()) {
            PreparedStatement pr = driver.getConnection().prepareStatement("INSERT INTO team_available_assignments (assignment_id, team_id, location_id, assignment) VALUES (?, ?, ?, ?)");
            pr.setInt(1, entry.getKey());
            pr.setInt(2, teamId);
            pr.setInt(3, locationId);
            pr.setString(4, entry.getValue());

            pr.execute();
            pr.close();
        }
    }

    public List<SubmittedAssignment> getSubmittedAssignments() throws SQLException {
        PreparedStatement pr = driver.getConnection().prepareStatement("SELECT * FROM team_submitted_assignments WHERE team_id = ?");
        pr.setInt(1, teamId);
        ResultSet rs = pr.executeQuery();
        List<SubmittedAssignment> assignments = new ArrayList<>();

        while(rs.next()) {
            assignments.add(new SubmittedAssignment(
                    rs.getInt("assignment_id"),
                    Optional.ofNullable(DatabaseUtil.getInteger(rs, "assigned_grade"))
            ));
        }

        pr.close();
        return assignments;
    }

    public Optional<SubmittedAssignment> getSubmittedAssignmentById(int assignmentId) throws SQLException {
        PreparedStatement pr = driver.getConnection().prepareStatement("SELECT * FROM team_submitted_assignments WHERE assignment_id = ? AND team_id = ?");
        pr.setInt(1, assignmentId);
        pr.setInt(2, teamId);
        ResultSet rs = pr.executeQuery();
        if(rs.next()) {
            Optional<SubmittedAssignment> val = Optional.of(new SubmittedAssignment(
                    rs.getInt("assignment_id"),
                    Optional.ofNullable(DatabaseUtil.getInteger(rs, "assigned_grade"))
            ));

            rs.close();
            return val;
        }

        rs.close();
        return Optional.empty();
    }

    public void submitAssignment(int assignmentId) throws SQLException {
        if(getSubmittedAssignments().stream().anyMatch(v -> v.id == assignmentId)) return;

        PreparedStatement pr = driver.getConnection().prepareStatement("INSERT INTO team_submitted_assignments (assignment_id, team_id) VALUES (?, ?)");
        pr.setInt(1, assignmentId);
        pr.setInt(2, teamId);
        pr.execute();
        pr.close();
    }
    public void setAssignmentGrade(int assignmentId, int grade) throws SQLException {
        if(getSubmittedAssignments().stream().noneMatch(v -> v.id == assignmentId)) return;

        PreparedStatement pr = driver.getConnection().prepareStatement("UPDATE team_submitted_assignments SET assigned_grade = ? WHERE assignment_id = ? AND team_id = ?");
        pr.setInt(1, grade);
        pr.setInt(2, assignmentId);
        pr.setInt(3, teamId);

        pr.execute();
        pr.close();
    }

    public int getNextHelperLocation() throws SQLException{
        
        helperLocation++;
        if (helperLocation == 8)
            helperLocation = 1;
        
        PreparedStatement pr = driver.getConnection().prepareStatement("UPDATE teams SET helper_location = ? WHERE team_id = ?");
        pr.setInt(1, helperLocation);
        pr.setInt(2, teamId);

        pr.execute();
        pr.close();

        return helperLocation;
    }
}
