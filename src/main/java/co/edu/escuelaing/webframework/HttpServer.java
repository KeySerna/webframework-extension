/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.edu.escuelaing.webframework;

/**
 *
 * @author keysi
 */

import java.io.*;
import java.net.*;

public class HttpServer {
    private final Router router;
    private final StaticFileService staticFileService;
    private final int port;
    private volatile boolean running = false;

    public HttpServer(Router router, StaticFileService staticFileService, int port) {
        this.router = router;
        this.staticFileService = staticFileService;
        this.port = port;
    }

    public void start() throws IOException {
        running = true;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor escuchando en el puerto " + port);
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    handleClient(clientSocket);
                } catch (IOException e) {
                    if (running) {
                        System.out.println("Error aceptando conexión: " + e.getMessage());
                    }
                }
            }
        }
        System.out.println("Server stopped gracefully.");
    }

    public void stop() {
        running = false;
    }

    private void handleClient(Socket clientSocket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            OutputStream rawOut = clientSocket.getOutputStream();
            PrintWriter out = new PrintWriter(rawOut, true)
        ) {
            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isBlank()) {
                sendResponse(out, 400, "text/plain", "400 Bad Request");
                return;
            }

            String[] requestParts = requestLine.split(" ");
            if (requestParts.length < 2) {
                sendResponse(out, 400, "text/plain", "400 Bad Request");
                return;
            }

            String method = requestParts[0];
            String uriStr = requestParts[1];

            String line;
            while ((line = in.readLine()) != null && !line.isEmpty()) {
                // encabezados no procesados en esta versión
            }

            URI uri;
            try {
                uri = new URI(uriStr);
            } catch (URISyntaxException e) {
                sendResponse(out, 400, "text/plain", "400 Bad Request");
                return;
            }
            String path = uri.getPath();

            if (!method.equals("GET")) {
                sendResponse(out, 405, "text/plain", "405 Method Not Allowed");
                return;
            }

            Request req = new Request(method, uri);
            Route route = router.findRoute(path);

            if (route != null) {
                Response resp = new Response();
                String body;
                try {
                    body = route.getService().handle(req, resp);
                } catch (Exception e) {
                    sendResponse(out, 500, "text/plain", "500 Internal Server Error");
                    return;
                }
                sendResponse(out, resp.getStatus(), resp.getContentType(), body);
            } else {
                boolean served;
                try {
                    served = staticFileService.serve(path, rawOut);
                } catch (IOException e) {
                    served = false;
                }
                if (!served) {
                    sendResponse(out, 404, "text/plain", "404 Not Found");
                }
            }
        } catch (Exception e) {
            System.out.println("Error atendiendo la solicitud: " + e.getMessage());
        } finally {
            try { clientSocket.close(); } catch (IOException ignored) {}
        }
    }

    private void sendResponse(PrintWriter out, int status, String contentType, String body) {
        String statusText = switch (status) {
            case 200 -> "OK";
            case 400 -> "Bad Request";
            case 404 -> "Not Found";
            case 405 -> "Method Not Allowed";
            case 500 -> "Internal Server Error";
            default -> "Error";
        };
        out.println("HTTP/1.1 " + status + " " + statusText);
        out.println("Content-Type: " + contentType);
        out.println();
        out.println(body);
    }
}