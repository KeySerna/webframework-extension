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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HttpServer {
    private final Router router;
    private final StaticFileService staticFileService;
    private final int port;
    private volatile boolean running = false;

    private ServerSocket serverSocket;
    private final ExecutorService clientHandlerPool = Executors.newVirtualThreadPerTaskExecutor();

    public HttpServer(Router router, StaticFileService staticFileService, int port) {
        this.router = router;
        this.staticFileService = staticFileService;
        this.port = port;
    }

    /**
     * Accepts connections in a loop and dispatches each one to the virtual-thread
     * executor, so multiple clients can be served concurrently instead of one at a
     * time. The accept loop itself stays single-threaded; only request handling is
     * parallelized.
     */
    public void start() throws IOException {
        running = true;
        try (ServerSocket socket = new ServerSocket(port)) {
            this.serverSocket = socket;
            System.out.println("Servidor escuchando en el puerto " + port);
            while (running) {
                try {
                    Socket clientSocket = socket.accept();
                    clientHandlerPool.submit(() -> handleClient(clientSocket));
                } catch (IOException e) {
                    if (running) {
                        System.out.println("Error aceptando conexión: " + e.getMessage());
                    }
                    // if !running, this IOException is expected: stop() closed the
                    // socket to unblock accept(), so we just fall through and exit.
                }
            }
        } finally {
            awaitInFlightRequests();
        }
        System.out.println("Server stopped gracefully.");
    }

    /**
     * Graceful shutdown: stop accepting new connections, unblock the accept() call
     * (which would otherwise block forever waiting for the next client), and let
     * requests already in progress finish before returning control to the caller.
     */
    public void stop() {
        running = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.out.println("Error closing server socket during shutdown: " + e.getMessage());
            }
        }
    }

    private void awaitInFlightRequests() {
        clientHandlerPool.shutdown();
        try {
            if (!clientHandlerPool.awaitTermination(10, TimeUnit.SECONDS)) {
                System.out.println("Timed out waiting for in-flight requests; forcing shutdown.");
                clientHandlerPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            clientHandlerPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
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
