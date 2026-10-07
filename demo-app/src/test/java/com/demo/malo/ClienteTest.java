package com.demo.malo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ClienteTest {

    @Test
    void dos_clientes_con_el_mismo_id_se_dan_por_iguales() {
        Cliente a = new Cliente("C-1", "Ana", "VIP");
        Cliente b = new Cliente("C-1", "Ana", "VIP");

        assertEquals(a, b);
    }

    @Test
    void dos_clientes_con_ids_distintos_no_son_iguales() {
        Cliente a = new Cliente("C-1", "Ana", "VIP");
        Cliente b = new Cliente("C-2", "Luis", "NORMAL");

        assertNotEquals(a, b);
    }

    @Test
    void el_to_string_incluye_el_identificador() {
        Cliente cliente = new Cliente("C-9", "Marta", "NORMAL");

        assertEquals("Cliente{id='C-9', nombre='Marta', tipo='NORMAL'}", cliente.toString());
    }
}
