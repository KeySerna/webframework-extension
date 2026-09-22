/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.edu.escuelaing.app;

/**
 *
 * @author keysi
 */

import static co.edu.escuelaing.webframework.WebFramework.*;

public class Application {

    public static void main(String[] args) throws Exception {

        staticfiles("/webroot");

        get("/hello", (req, resp) -> {
            String name = req.getValue("name");
            if (name == null || name.isBlank()) {
                name = "world";
            }
            String greetingPrefix = System.getenv().getOrDefault("GREETING_PREFIX", "Hello");
            return greetingPrefix + " " + name;
        });

        get("/pi", (req, resp) -> String.valueOf(Math.PI));

        get("/square", (req, resp) -> {
            String valueStr = req.getValue("value");
            if (valueStr == null || valueStr.isBlank()) {
                resp.status(400);
                return "Falta el parámetro 'value'";
            }
            try {
                double value = Double.parseDouble(valueStr);
                return "El cuadrado de " + value + " es " + (value * value);
            } catch (NumberFormatException e) {
                resp.status(400);
                return "El parámetro 'value' no es un número válido";
            }
        });

        String environment = System.getenv().getOrDefault("APP_ENV", "development");
        if (environment.equals("development")) {
            get("/shutdown", (req, resp) -> {
                stop();
                return "Server will stop after this response.";
            });
        }

        start();
    }
}