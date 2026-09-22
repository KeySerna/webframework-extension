/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.edu.escuelaing.webframework;

/**
 *
 * @author keysi
 */

@FunctionalInterface
public interface Service {
    String handle(Request req, Response res);
}