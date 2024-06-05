package nl.svsticky.crazy88.http;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import net.dv8tion.jda.api.exceptions.HttpException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public abstract class HttpResponse {
    private final HttpExchange exchange;
    private boolean hasBody = false;

    public HttpResponse(HttpRequest request, int status) throws IOException {
        this.exchange = request.getExchange();
        setCors(exchange);
        exchange.sendResponseHeaders(status, 0);
    }

    public HttpResponse(HttpRequest request, int status, byte[] body) throws IOException {
        this.exchange = request.getExchange();
        hasBody = true;
        setCors(exchange);
        exchange.sendResponseHeaders(status, body.length);
        setBody(exchange, body);
    }

    public HttpResponse(HttpRequest request, int status, HashMap<String, String> headers) throws IOException {
        this.exchange = request.getExchange();
        hasBody = true;
        setHeaders(exchange, headers);
        setCors(exchange);
        exchange.sendResponseHeaders(status, 0);
    }

    public HttpResponse(HttpRequest request, int status, byte[] body, HashMap<String, String> headers) throws IOException {
        this.exchange = request.getExchange();
        hasBody = true;
        setHeaders(exchange, headers);
        setCors(exchange);
        exchange.sendResponseHeaders(status, body.length);
        setBody(exchange, body);
    }

    private static void setCors(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
    }

    /**
     * Set the HTTP response headers
     * @param exchange The exchange to modify
     * @param headers The headers to set
     */
    private static void setHeaders(HttpExchange exchange, HashMap<String, String> headers) {
        Headers h = exchange.getResponseHeaders();
        for(Map.Entry<String, String> entry : headers.entrySet()) {
            h.set(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Set the HTTP response body
     * @param exchange The exhange to modify
     * @param body The body to set
     * @throws IOException IO error
     */
    private static void setBody(HttpExchange exchange, byte[] body) throws IOException {
        OutputStream os = exchange.getResponseBody();
        os.write(body);
    }

    /**
     * Close the connection
     * @throws IOException IO error
     */
    protected void close() throws IOException {
        if(hasBody) {
            exchange.getResponseBody().close();
        } else {
            exchange.close();
        }
    }
}
