package nl.svsticky.crazy88.http.response;

import com.google.gson.Gson;
import nl.svsticky.crazy88.http.HttpRequest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * HTTP response containg a JSON body
 */
public class JsonHttpResponse extends ByteHttpResponse {
    public <T> JsonHttpResponse(HttpRequest request, int status, T payload) throws IOException {
        super(request, status, serializeBody(payload), "application/json");
    }

    private static <T> byte[] serializeBody(T payload) {
        final Gson gson = new Gson();
        final String json = gson.toJson(payload);
        return json.getBytes(StandardCharsets.UTF_8);
    }
}
