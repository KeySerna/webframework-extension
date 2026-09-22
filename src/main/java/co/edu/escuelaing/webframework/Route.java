/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.edu.escuelaing.webframework;

/**
 *
 * @author keysi
 */
public class Route {
    private final String path;
    private final Service service;

    public Route(String path, Service service) {
        this.path = path;
        this.service = service;
    }

    public String getPath() { return path; }
    public Service getService() { return service; }

    public boolean matches(String requestPath) {
        return path.equals(requestPath);
    }
}