/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.edu.escuelaing.webframework;
/**
 *
 * @author keysi
 */
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Request {
    private final String method;
    private final URI uri;
    private final Map<String, String> queryParams;

    public Request(String method, URI uri) {
        this.method = method;
        this.uri = uri;
        this.queryParams = parseQuery(uri.getQuery());
    }

    public String getMethod() { return method; }
    public URI getUri() { return uri; }
    public String getPath() { return uri.getPath(); }

    public String getValue(String name) {
        return queryParams.get(name);
    }

    private Map<String, String> parseQuery(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null || query.isEmpty()) return params;
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=", 2);
            String key = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
            String value = kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : "";
            params.put(key, value);
        }
        return params;
    }
}