package nl.svsticky.crazy88.http.routes.submissions;

import nl.svsticky.crazy88.App;
import nl.svsticky.crazy88.config.model.ConfigModel;
import nl.svsticky.crazy88.database.driver.Driver;
import nl.svsticky.crazy88.database.model.Team;
import nl.svsticky.crazy88.http.HttpRequest;
import nl.svsticky.crazy88.http.RequestHandler;
import nl.svsticky.crazy88.http.HttpResponse;
import nl.svsticky.crazy88.http.response.EmptyHttpResponse;
import nl.svsticky.crazy88.http.response.JsonHttpResponse;
import nl.svsticky.crazy88.http.response.StringHttpResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ListSubmissionsRoute implements RequestHandler {

    private final Driver driver;
    private final ConfigModel config;

    public ListSubmissionsRoute(Driver driver, ConfigModel config) {
        this.driver = driver;
        this.config = config;
    }

    private record ListSubmissionsResponse(List<SubmissionItem> submitted) {}
    private record SubmissionItem(int id, String assignment, Integer grade) {}

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        // Get the teamID from the query
        int teamId;
        try {
            Optional<String> mTeamId = request.getQueryParameter("teamId");
            if(mTeamId.isEmpty()) {
                return new StringHttpResponse(request, 400, "Missing required parameter 'teamId'");
            }

            teamId = Integer.parseInt(mTeamId.get());
        } catch (NumberFormatException e) {
            return new StringHttpResponse(request, 400, "Invalid number for parameter 'teamId'");
        } catch (IllegalArgumentException e) {
            return new StringHttpResponse(request, 400, e.getMessage());
        }

        List<SubmissionItem> submissions = new ArrayList<>();
        try {
            // Check if the team exists
            Optional<Team> team = Team.getbyId(driver, teamId);
            if(team.isEmpty()) {
                return new StringHttpResponse(request, 404, "Team not found");
            }


            for(Team.SubmittedAssignment submittedAssignment : team.get().getSubmittedAssignments()) {
                //noinspection OptionalGetWithoutIsPresent
                Team.AvailableAssignment availableAssignment = team.get().getAvailableAssignmentById(submittedAssignment.id()).get();
                submissions.add(new SubmissionItem(
                        submittedAssignment.id(),
                        availableAssignment.assignment(),
                        submittedAssignment.assignedGrade().orElse(null)
                ));
            }
        } catch (SQLException e) {
            App.getLogger().error(e);
            return new EmptyHttpResponse(request, 500);
        }

        return new JsonHttpResponse(request, 200, new ListSubmissionsResponse(submissions));
    }
}
