package nl.svsticky.crazy88.http.response;

import nl.svsticky.crazy88.http.HttpRequest;
import nl.svsticky.crazy88.http.HttpResponse;

import java.io.IOException;
import java.util.HashMap;

/**
 * HTTP response performing a redirect
 */
public class RedirectHttpResponse extends HttpResponse {

    public RedirectHttpResponse(HttpRequest request, boolean permanent, String to) throws IOException {
        super(
                request,
                permanent ? 301 : 302,
                headersOnlyLocation(to)
        );
    }

    private static HashMap<String, String> headersOnlyLocation(String to) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Location", to);
        return headers;
    }
}
