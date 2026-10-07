package com.demo.malo;

import javax.servlet.http.HttpServletRequest;

/**
 * Alertas por red.
 *
 * El host llega de fuera: se lee de la peticion con
 * {@code HttpServletRequest.getParameter("host")} y se concatena en la orden
 * del sistema.
 * java:S2076 "OS commands should not be vulnerable to command injection attacks".
 * Es una INJECTION rule: se detecta con taint analysis y se reporta como
 * Vulnerability, con el flujo de datos desde la fuente (getParameter) hasta el
 * sink (Runtime.exec). Sin la fuente no salta.
 */
public class ServicioAlertas {

    public int pingDesdePeticion(HttpServletRequest peticion) {
        String host = peticion.getParameter("host");
        return ping(host);
    }

    public int ping(String host) {
        try {
            Process proceso = Runtime.getRuntime().exec("ping -n 1 " + host);
            return proceso.waitFor();
        } catch (Exception e) {
            return -1;
        }
    }

    public boolean esAlcanzable(String host) {
        return ping(host) == 0;
    }
}
