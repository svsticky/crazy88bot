package nl.svsticky.crazy88.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class HttpServer {
    private final int port;
    private boolean started = false;
    private final List<RoutedRequestHandler> handlers = new ArrayList<>();

    private record RoutedRequestHandler(String path, RequestHandler handler) {}

    public HttpServer(int port) {
        this.port = port;
    }

    /**
     * Register a route handler
     * @param path The path to register for
     * @param handler The handler
     */
    public void registerRoute(String path, RequestHandler handler) {
        if(started) throw new IllegalStateException("HttpServer already started");
        this.handlers.add(new RoutedRequestHandler(path, handler));
    }

    /**
     * Start the HTTP server on a different thread
     * @throws IOException IO error
     */
    public void start() throws IOException {
        started = true;
        com.sun.net.httpserver.HttpServer httpServer = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(port), 0);

        for(RoutedRequestHandler handler : handlers) {
            httpServer.createContext(handler.path(), exchange -> {
                HttpResponse r = handler.handler().handle(new HttpRequest(exchange));
                r.close();
            });
        }

        httpServer.setExecutor(null);
        httpServer.start();
    }
}
