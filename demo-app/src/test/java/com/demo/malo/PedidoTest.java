package com.demo.malo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PedidoTest {

    @Test
    void los_valores_se_guardan_como_se_pasaron() {
        Pedido pedido = new Pedido("P-1", "C-1", "VIP", "Calle Mayor 1", 250, 120.5, false, "sin notas");

        assertEquals("P-1", pedido.getId());
        assertEquals("C-1", pedido.getClienteId());
        assertEquals("VIP", pedido.getTipo());
        assertEquals("Calle Mayor 1", pedido.getDireccion());
        assertEquals(250, pedido.getKilos());
        assertEquals(120.5, pedido.getImporte());
        assertEquals(false, pedido.isUrgente());
        assertEquals("sin notas", pedido.getNotas());
    }

    @Test
    void un_pedido_vacio_sigue_siendo_consultable() {
        Pedido pedido = new Pedido("P-2", "C-2", "NORMAL", "", 0, 0.0, true, "");

        assertEquals(0, pedido.getKilos());
        assertEquals(0.0, pedido.getImporte());
        assertEquals(true, pedido.isUrgente());
    }
}
