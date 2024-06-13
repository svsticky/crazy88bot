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
import nl.svsticky.crazy88.http.routes.Either;
import nl.svsticky.crazy88.http.routes.ParameterUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ListSubmissionsRoute implements RequestHandler {

    private final Driver driver;

    public ListSubmissionsRoute(Driver driver) {
        this.driver = driver;
    }

    private record ListSubmissionsResponse(List<SubmissionItem> submitted) {}
    private record SubmissionItem(int id, String assignment, Integer grade) {}

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        // Get the teamID from the query
        Either<Integer, HttpResponse> mTeamId = ParameterUtil.getIntParameter(request, "teamId");
        if(mTeamId.right().isPresent()) {
            return mTeamId.right().get();
        }

        // Unwrap is safe, the Either guarantees either left or right, we already checked right, so we can assume left is present
        //noinspection OptionalGetWithoutIsPresent
        int teamId = mTeamId.left().get();

        List<SubmissionItem> submissions = new ArrayList<>();
        try {
            // Check if the team exists
            Optional<Team> team = Team.getbyId(driver, mTeamId.left().get());
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
