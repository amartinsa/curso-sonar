package com.demo.malo;

/**
 * Pedido en memoria. Sin base de datos: todo vive en la sesion.
 */
public class Pedido {

    private final String id;
    private final String clienteId;
    private final String tipo;
    private final String direccion;
    private final int kilos;
    private final double importe;
    private final boolean urgente;
    private final String notas;

    public Pedido(String id, String clienteId, String tipo, String direccion,
                  int kilos, double importe, boolean urgente, String notas) {
        this.id = id;
        this.clienteId = clienteId;
        this.tipo = tipo;
        this.direccion = direccion;
        this.kilos = kilos;
        this.importe = importe;
        this.urgente = urgente;
        this.notas = notas;
    }

    public String getId() {
        return id;
    }

    public String getClienteId() {
        return clienteId;
    }

    public String getTipo() {
        return tipo;
    }

    public String getDireccion() {
        return direccion;
    }

    public int getKilos() {
        return kilos;
    }

    public double getImporte() {
        return importe;
    }

    public boolean isUrgente() {
        return urgente;
    }

    public String getNotas() {
        return notas;
    }
}
