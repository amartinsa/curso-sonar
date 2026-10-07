package com.demo.malo;

/**
 * Cliente del sistema.
 *
 * Tiene equals() pero NO hashCode(): el analizador reporta java:S1206
 * y lo clasifica como Bug (Reliability), no como code smell.
 */
public class Cliente {

    private final String id;
    private final String nombre;
    private final String tipo;

    public Cliente(String id, String nombre, String tipo) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Cliente)) {
            return false;
        }
        Cliente otro = (Cliente) obj;
        return id == null ? otro.id == null : id.equals(otro.id);
    }

    @Override
    public String toString() {
        return "Cliente{id='" + id + "', nombre='" + nombre + "', tipo='" + tipo + "'}";
    }
}
