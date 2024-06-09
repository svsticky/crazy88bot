package nl.svsticky.crazy88.http;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * A HTTP request
 */
public class HttpRequest {
    private final HttpExchange exchange;

    public HttpRequest(HttpExchange exchange) {
        this.exchange = exchange;
    }

    protected HttpExchange getExchange() {
        return exchange;
    }

    /**
     * Get the HTTP headers
     * @return The headers
     */
    public Headers getHeaders() {
        return exchange.getRequestHeaders();
    }

    /**
     * Get a HTTP header
     * @param name The name
     * @return The header, if it exists
     */
    public Optional<String> getHeader(String name) {
        return Optional.ofNullable(exchange.getRequestHeaders().getFirst(name));
    }

    /**
     * Get the HTTP method
     * @return The method as a string
     */
    public String getMethod() {
        return exchange.getRequestMethod();
    }

    /**
     * Get the request body
     * @return The request body. If none exists, an empty array
     * @throws IOException IO error
     */
    public byte[] getRequestBody() throws IOException {
        InputStream is = exchange.getRequestBody();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int read = 0;
        byte[] data = new byte[4096];
        while((read = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, read);
        }

        return buffer.toByteArray();
    }

    /**
     * Get the HTTP query
     * @return The query
     * @throws IllegalArgumentException The querystring is invalid
     */
    public HashMap<String, String> getQuery() throws IllegalArgumentException {
        return parseQuery(exchange.getRequestURI().getQuery());
    }

    /**
     * Get a query parameter
     * @param name The name
     * @return The parameter, if it exists
     * @throws IllegalArgumentException The querystring is invalid
     */
    public Optional<String> getQueryParameter(String name) throws IllegalArgumentException {
        return Optional.ofNullable(parseQuery(exchange.getRequestURI().getQuery()).get(name));
    }

    /**
     * Parse the HTTP query
     * @param query The HTTP querystring
     * @return The parsed query
     * @throws IllegalArgumentException The querystring is invalid
     */
    private HashMap<String, String> parseQuery(String query) throws IllegalArgumentException {
        HashMap<String, String> result = new HashMap<>();
        if (query != null) {
            String[] kvPairs = query.split(Pattern.quote("&"));
            Arrays.stream(kvPairs).forEach(kvPair -> {
                String[] kvPairContent = kvPair.split(Pattern.quote("="));
                if(kvPairContent.length != 2) {
                    throw new IllegalArgumentException("Invalid query parameter: " + kvPair);
                }

                result.put(kvPairContent[0], kvPairContent[1]);
            });
        }

        return result;
    }
}
