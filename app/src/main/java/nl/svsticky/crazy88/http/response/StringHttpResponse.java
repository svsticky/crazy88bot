package nl.svsticky.crazy88.http.response;

import nl.svsticky.crazy88.http.HttpRequest;
import nl.svsticky.crazy88.http.HttpResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * HTTP response containing a plain string
 */
public class StringHttpResponse extends HttpResponse {
    public StringHttpResponse(HttpRequest request, int status, String text) throws IOException {
        super(request, status, text.getBytes(StandardCharsets.UTF_8));
    }
}
