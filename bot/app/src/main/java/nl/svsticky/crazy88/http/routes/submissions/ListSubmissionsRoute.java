package nl.svsticky.crazy88.http.routes.submissions;

import nl.svsticky.crazy88.App;
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
import java.util.List;
import java.util.Optional;

public class ListSubmissionsRoute implements RequestHandler {

    private final Driver driver;

    public ListSubmissionsRoute(Driver driver) {
        this.driver = driver;
    }

    private record ListSubmissionsResponse(List<Integer> submitted) {}

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

        List<Integer> submissions;
        try {
            // Check if the team exists
            Optional<Team> team = Team.getbyId(driver, teamId);
            if(team.isEmpty()) {
                return new StringHttpResponse(request, 404, "Team not found");
            }

            // List the submissions
            submissions = team.get().getSubmittedAssignments();
        } catch (SQLException e) {
            App.getLogger().error(e);
            return new EmptyHttpResponse(request, 500);
        }

        return new JsonHttpResponse(request, 200, new ListSubmissionsResponse(submissions));
    }
}
