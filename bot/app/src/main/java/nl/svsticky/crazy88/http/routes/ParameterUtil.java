package nl.svsticky.crazy88.http.routes;

import nl.svsticky.crazy88.http.HttpRequest;
import nl.svsticky.crazy88.http.HttpResponse;
import nl.svsticky.crazy88.http.response.StringHttpResponse;

import java.io.IOException;
import java.util.Optional;

public class ParameterUtil {
    public static Either<Integer, HttpResponse> getIntParameter(HttpRequest request, String name) throws IOException {
        try {
            Optional<String> mTeamId = request.getQueryParameter(name);
            if(mTeamId.isEmpty()) {
                return Either.right(new StringHttpResponse(request, 400, String.format("Missing required parameter '%s'", name)));
            }

            return Either.left(Integer.parseInt(mTeamId.get()));
        } catch (NumberFormatException e) {
            return Either.right(new StringHttpResponse(request, 400, String.format("Invalid number for parameter '%s'", name)));
        } catch (IllegalArgumentException e) {
            return Either.right(new StringHttpResponse(request, 400, e.getMessage()));
        }
    }
}
