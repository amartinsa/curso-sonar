package com.demo.malo;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Huellas digitales de contenido.
 *
 * Usa MD5: java:S4790 "Hashing data is security-sensitive".
 * Es un SECURITY HOTSPOT, no una vulnerability: hay que REVISARLO a mano
 * y decidir si es peligroso en este contexto.
 */
public class ServicioHuellas {

    public String huellaDe(String contenido) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] bytes = digest.digest(contenido.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No se pudo calcular la huella", e);
        }
    }
}
