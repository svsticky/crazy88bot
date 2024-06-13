package nl.svsticky.crazy88.http.routes.teams;

import nl.svsticky.crazy88.App;
import nl.svsticky.crazy88.database.driver.Driver;
import nl.svsticky.crazy88.database.model.Team;
import nl.svsticky.crazy88.http.HttpRequest;
import nl.svsticky.crazy88.http.HttpResponse;
import nl.svsticky.crazy88.http.RequestHandler;
import nl.svsticky.crazy88.http.response.EmptyHttpResponse;
import nl.svsticky.crazy88.http.response.JsonHttpResponse;
import nl.svsticky.crazy88.http.routes.Either;
import nl.svsticky.crazy88.http.routes.ParameterUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ScoreRoute implements RequestHandler {
    private final Driver driver;

    public ScoreRoute(Driver driver) {
        this.driver = driver;
    }

    private record ScoreRouteResponse(int totalScore) {}

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

        int totalScore;
        try {
            Optional<Team> mTeam = Team.getbyId(driver, teamId);
            if(mTeam.isEmpty()) {
                return new EmptyHttpResponse(request, 404);
            }

            List<Team.SubmittedAssignment> submittedAssignments = mTeam.get().getSubmittedAssignments();
            totalScore = submittedAssignments
                    .stream()
                    .filter(v -> v.assignedGrade().isPresent())
                    .mapToInt(v -> v.assignedGrade().get())
                    .sum();
        } catch (SQLException e) {
            App.getLogger().error(e);
            return new EmptyHttpResponse(request, 500);
        }

        return new JsonHttpResponse(request, 200, new ScoreRouteResponse(totalScore));
    }
}
