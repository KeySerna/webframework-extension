/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.edu.escuelaing.webframework;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author keysi
 */


public class Router {
    private final List<Route> routes = new ArrayList<>();

    public void addRoute(String path, Service service) {
        routes.add(new Route(path, service));
    }

    public Route findRoute(String requestPath) {
        for (Route route : routes) {
            if (route.matches(requestPath)) return route;
        }
        return null;
    }
}