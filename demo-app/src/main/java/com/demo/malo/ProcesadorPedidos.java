package com.demo.malo;

import java.util.ArrayList;
import java.util.List;

/**
 * Procesador de pedidos en memoria. Sin base de datos.
 *
 * Esta clase concentra la mayoria de los code smells del proyecto:
 * metodo largo, demasiados parametros, bloques duplicados, numeros
 * magicos, System.out, if vacio, catch que no hace nada y un TODO.
 */
public class ProcesadorPedidos {

    private final List<Pedido> historial = new ArrayList<>();
    private final ServicioHuellas huellas = new ServicioHuellas();

    /**
     * Ocho parametros: el analizador avisa de que se esta pasando de la razon.
     */
    public Pedido registrar(String id, String clienteId, String tipo, String direccion,
                            int kilos, double importe, boolean urgente, String notas) {
        Pedido pedido = new Pedido(id, clienteId, tipo, direccion, kilos, importe, urgente, notas);
        historial.add(pedido);
        System.out.println("[PEDIDO] registrado " + id + " para " + clienteId);
        return pedido;
    }

    /**
     * El metodo largo. Mezcla calculo, presentacion, validacion y efectos
     * laterales, todo en el mismo sitio.
     */
    public double procesar(Pedido pedido) {
        if (pedido == null) {
            return 0.0;
        }

        double base = pedido.getImporte();
        double recargo = 0.0;
        double descuento = 0.0;
        double gastosEnvio = 0.0;
        double extra = 0.0;

        // TODO: sustituir por el descuento real del contrato DEV-123

        if (pedido.isUrgente()) {
            recargo = base * 0.15;
        }

        if (pedido.getKilos() > 1000) {
            gastosEnvio = 45.0;
        } else if (pedido.getKilos() > 500) {
            gastosEnvio = 25.0;
        } else if (pedido.getKilos() > 100) {
            gastosEnvio = 12.5;
        } else {
            gastosEnvio = 5.0;
        }

        if (esVip(pedido)) {
            descuento = base * 0.10;
        }

        if (pedido.getKilos() < 0) {
        }

        try {
            extra = Double.parseDouble(pedido.getNotas());
        } catch (NumberFormatException e) {
            // las notas no siempre son un numero
        }

        double total = base + recargo - descuento + gastosEnvio + extra;

        String zona;
        if (pedido.getKilos() > 1000) {
            zona = "MAYORISTA";
        } else if (pedido.getKilos() > 500) {
            zona = "SEMI-MAYORISTA";
        } else {
            zona = "DETALLE";
        }

        String albaran = huellas.huellaDe(pedido.getId() + zona);

        double redondeo = Math.round(total * 100.0) / 100.0;

        System.out.printf("[TOTAL] %s -> %.2f EUR (zona %s, albaran %s)%n",
                pedido.getId(), redondeo, zona, albaran);

        String sector;
        if (pedido.getKilos() > 1000) {
            sector = "MAYORISTA";
        } else if (pedido.getKilos() > 500) {
            sector = "SEMI-MAYORISTA";
        } else {
            sector = "DETALLE";
        }

        if (pedido.isUrgente()) {
            System.out.println("[AVISO] entrega prioritaria en " + sector);
        }

        historial.add(pedido);
        return redondeo;
    }

    /**
     * Comparacion con == en vez de equals(): java:S4973.
     */
    public boolean esVip(Pedido pedido) {
        return pedido.getTipo() == "VIP";
    }

    public List<Pedido> getHistorial() {
        return historial;
    }

    public int contarPorTipo(String tipo) {
        int cuenta = 0;
        for (Pedido p : historial) {
            if (p.getTipo().equals(tipo)) {
                cuenta = cuenta + 1;
            }
        }
        return cuenta;
    }
}
