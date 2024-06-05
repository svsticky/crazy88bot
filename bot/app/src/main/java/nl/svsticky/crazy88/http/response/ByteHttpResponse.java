package nl.svsticky.crazy88.http.response;

import nl.svsticky.crazy88.http.HttpRequest;
import nl.svsticky.crazy88.http.HttpResponse;

import java.io.IOException;
import java.util.HashMap;

/**
 * HTTP response containing raw bytes
 */
public class ByteHttpResponse extends HttpResponse {

    public ByteHttpResponse(HttpRequest request, int status, byte[] body, String contentType) throws IOException {
        super(request, status, body, getContentTypeOnlyMap(contentType));
    }

    private static HashMap<String, String> getContentTypeOnlyMap(String contentType) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", contentType);
        return headers;
    }
}
