package nl.svsticky.crazy88.http.response;

import nl.svsticky.crazy88.http.HttpRequest;
import nl.svsticky.crazy88.http.HttpResponse;

import java.io.IOException;

/**
 * HTTP response without a body
 */
public class EmptyHttpResponse extends HttpResponse {
    public EmptyHttpResponse(HttpRequest request, int status) throws IOException {
        super(request, status);
    }
}
