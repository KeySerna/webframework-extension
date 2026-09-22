package co.edu.escuelaing.webframework;

public class WebFramework {
    private static final Router router = new Router();
    private static final StaticFileService staticFileService = new StaticFileService();
    private static HttpServer server;

    public static void get(String path, Service service) {
        router.addRoute(path, service);
    }

    public static void staticfiles(String folder) {
        staticFileService.setStaticFilesFolder(folder);
    }

    /** Lee el puerto de la variable de entorno PORT (default 8080 en local). */
    public static void start() throws Exception {
        String portValue = System.getenv("PORT");
        int port = (portValue == null || portValue.isBlank())
                ? 8080
                : Integer.parseInt(portValue);
        start(port);
    }

    public static void start(int port) throws Exception {
        server = new HttpServer(router, staticFileService, port);
        server.start();
    }

    public static void stop() {
        if (server != null) {
            server.stop();
        }
    }
}