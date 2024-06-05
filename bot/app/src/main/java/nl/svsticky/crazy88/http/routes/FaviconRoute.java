package nl.svsticky.crazy88.http.routes;

import nl.svsticky.crazy88.App;
import nl.svsticky.crazy88.http.HttpRequest;
import nl.svsticky.crazy88.http.HttpResponse;
import nl.svsticky.crazy88.http.RequestHandler;
import nl.svsticky.crazy88.http.response.ByteHttpResponse;
import nl.svsticky.crazy88.http.response.EmptyHttpResponse;
import nl.svsticky.crazy88.http.response.RedirectHttpResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

public class FaviconRoute implements RequestHandler {
    private static final String FAVICON_URL = "https://public.svsticky.nl/logos/hoofd_outline_zwart.png";

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        URL url = URI.create(FAVICON_URL).toURL();

        try (InputStream is = url.openStream()) {
            byte[] chunk = new byte[4096];

            int n;
            while ((n = is.read(chunk)) > 0) {
                bos.write(chunk, 0, n);
            }
        } catch (IOException e) {
            App.getLogger().error(e);
            return new EmptyHttpResponse(request, 500);
        }

        byte[] bytes = bos.toByteArray();
        return new ByteHttpResponse(request, 200, bytes, "image/png");
    }
}
