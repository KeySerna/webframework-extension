/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.edu.escuelaing.webframework;

/**
 *
 * @author keysi
 */
import java.nio.charset.StandardCharsets;

public class Response {
    private int status = 200;
    private String contentType = "text/plain";

    public Response status(int status) { this.status = status; return this; }
    public Response type(String contentType) { this.contentType = contentType; return this; }

    public int getStatus() { return status; }
    public String getContentType() { return contentType; }
}