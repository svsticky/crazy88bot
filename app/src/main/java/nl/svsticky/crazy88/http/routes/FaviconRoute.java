package nl.svsticky.crazy88.http.routes;

import nl.svsticky.crazy88.http.HttpRequest;
import nl.svsticky.crazy88.http.HttpResponse;
import nl.svsticky.crazy88.http.RequestHandler;
import nl.svsticky.crazy88.http.response.RedirectHttpResponse;

import java.io.IOException;

public class FaviconRoute implements RequestHandler {
    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        return new RedirectHttpResponse(request, false, "https://public.svsticky.nl/logos/hoofd_outline_zwart.png");
    }
}
