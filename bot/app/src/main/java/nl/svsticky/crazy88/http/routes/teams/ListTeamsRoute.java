package nl.svsticky.crazy88.http.routes.teams;

import nl.svsticky.crazy88.App;
import nl.svsticky.crazy88.database.driver.Driver;
import nl.svsticky.crazy88.database.model.Team;
import nl.svsticky.crazy88.http.HttpRequest;
import nl.svsticky.crazy88.http.HttpResponse;
import nl.svsticky.crazy88.http.RequestHandler;
import nl.svsticky.crazy88.http.response.EmptyHttpResponse;
import nl.svsticky.crazy88.http.response.JsonHttpResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ListTeamsRoute implements RequestHandler {
    private final Driver driver;

    public ListTeamsRoute(Driver driver) {
        this.driver = driver;
    }

    private record ListTeams(List<ResponseTeam> teams) {
        public record ResponseTeam(int id) {}
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        try {
            List<Team> teams = Team.getTeams(driver);
            ListTeams r = new ListTeams(teams.stream().map(team -> new ListTeams.ResponseTeam(team.teamId)).toList());
            return new JsonHttpResponse(request, 200, r);
        } catch (SQLException e) {
            App.getLogger().error(e);
            return new EmptyHttpResponse(request, 500);
        }
    }
}
