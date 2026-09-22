/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.edu.escuelaing.webframework;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StaticFileService {
    private String staticFilesFolder = "/webroot";

    public void setStaticFilesFolder(String folder) {
        this.staticFilesFolder = folder.startsWith("/") ? folder : "/" + folder;
    }

    public boolean serve(String path, OutputStream rawOut) throws IOException {
        String resourcePath = path.equals("/") ? "/index.html" : path;
        if (resourcePath.contains("..")) return false;

        String classpathResource = staticFilesFolder + resourcePath;
        try (InputStream is = StaticFileService.class.getResourceAsStream(classpathResource)) {
            if (is == null) return false;

            byte[] fileBytes = is.readAllBytes();
            String contentType = getContentType(resourcePath);
            String header = "HTTP/1.1 200 OK\r\n"
                    + "Content-Type: " + contentType + "\r\n"
                    + "Content-Length: " + fileBytes.length + "\r\n\r\n";
            rawOut.write(header.getBytes());
            rawOut.write(fileBytes);
            rawOut.flush();
            return true;
        }
    }

    private String getContentType(String path) {
        if (path.endsWith(".html")) return "text/html";
        if (path.endsWith(".css")) return "text/css";
        if (path.endsWith(".js")) return "application/javascript";
        if (path.endsWith(".png")) return "image/png";
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
        if (path.endsWith(".json")) return "application/json";
        return "application/octet-stream";
    }
}