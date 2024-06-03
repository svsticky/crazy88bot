package nl.svsticky.crazy88.http;

import java.io.IOException;

public interface RequestHandler {

    /**
     * Handle a HTTP request
     * @param request THe request
     * @return The response
     * @throws IOException IO error
     */
    HttpResponse handle(HttpRequest request) throws IOException;
}
