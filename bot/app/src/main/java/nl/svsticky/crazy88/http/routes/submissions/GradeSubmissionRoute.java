package nl.svsticky.crazy88.http.routes.submissions;

import com.google.gson.Gson;
import dev.array21.classvalidator.ClassValidator;
import dev.array21.classvalidator.Pair;
import nl.svsticky.crazy88.App;
import nl.svsticky.crazy88.database.driver.Driver;
import nl.svsticky.crazy88.database.model.Team;
import nl.svsticky.crazy88.http.HttpRequest;
import nl.svsticky.crazy88.http.HttpResponse;
import nl.svsticky.crazy88.http.RequestHandler;
import nl.svsticky.crazy88.http.response.EmptyHttpResponse;
import nl.svsticky.crazy88.http.response.StringHttpResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;

public class GradeSubmissionRoute implements RequestHandler {

    private final Driver driver;

    public GradeSubmissionRoute(Driver driver) {
        this.driver = driver;
    }

    private record SubmissionRequest(int assignmentId, int teamId, int grade) {}

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        final Gson gson = new Gson();
        if(!request.getMethod().equalsIgnoreCase("POST")) {
            return new StringHttpResponse(request, 400, String.format("Invalid HTTP method %s", request.getMethod()));
        }

        App.getLogger().debug("Validating grade request");
        String body = new String(request.getRequestBody());

        SubmissionRequest payload = gson.fromJson(body, SubmissionRequest.class);
        Optional<HttpResponse> validationResult = validateBody(request, payload);
        if(validationResult.isPresent()) {
            return validationResult.get();
        }

        App.getLogger().debug("Grade request validated OK");

        try {
            Optional<Team> mTeam = Team.getbyId(driver, payload.teamId());
            if(mTeam.isEmpty()) {
                return new EmptyHttpResponse(request, 404);
            }

            Optional<Team.SubmittedAssignment> mSubmittedAssignment = mTeam.get().getSubmittedAssignmentById(payload.assignmentId());
            if(mSubmittedAssignment.isEmpty()) {
                return new EmptyHttpResponse(request, 404);
            }

            mTeam.get().setAssignmentGrade(payload.assignmentId(), payload.grade());
        } catch (SQLException e) {
            App.getLogger().error(e);
            return new EmptyHttpResponse(request, 500);
        }

        return new EmptyHttpResponse(request, 200);
    }

    private Optional<HttpResponse> validateBody(HttpRequest request, SubmissionRequest payload) throws IOException {
        Pair<Boolean, String> result = ClassValidator.validateType(payload);
        if (result.getA() == null) {
            App.getLogger().error("Validation failed {}", result.getB());
            return Optional.of(new EmptyHttpResponse(request, 500));
        } else if(!result.getA()) {
            return Optional.of(new StringHttpResponse(request, 400, result.getB()));
        } else {
            return Optional.empty();
        }
    }
}
