package cl.duocuc.logservice.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(MockitoExtension.class)
class LogEntradaTest {

    @Test
    void beforeCreateNormalizaCamposYAsignaTimestampSiFalta() {
        // Given
        LogEntrada entrada = new LogEntrada();
        entrada.setServicio(" pedido-service ");
        entrada.setOperacion(" CREAR_PEDIDO ");
        entrada.setUsuarioId("   ");
        entrada.setResultado(" OK ");
        entrada.setDetalle("  detalle  ");

        // When
        entrada.beforeCreate();

        // Then
        assertEquals("pedido-service", entrada.getServicio());
        assertEquals("CREAR_PEDIDO", entrada.getOperacion());
        assertNull(entrada.getUsuarioId());
        assertEquals("OK", entrada.getResultado());
        assertEquals("detalle", entrada.getDetalle());
        assertNotNull(entrada.getTimestamp());
    }

    @Test
    void beforeCreateMantieneTimestampExistente() {
        // Given
        LocalDateTime timestamp = LocalDateTime.of(2026, 7, 1, 10, 0);
        LogEntrada entrada = new LogEntrada();
        entrada.setServicio("log-service");
        entrada.setOperacion("CONSULTAR");
        entrada.setResultado("OK");
        entrada.setTimestamp(timestamp);

        // When
        entrada.beforeCreate();

        // Then
        assertSame(timestamp, entrada.getTimestamp());
    }

    @Test
    void beforeUpdateNormalizaCamposSinAsignarTimestamp() {
        // Given
        LogEntrada entrada = new LogEntrada();
        entrada.setServicio(" log-service ");
        entrada.setOperacion(" CONSULTAR ");
        entrada.setUsuarioId(" usuario ");
        entrada.setResultado(" OK ");
        entrada.setDetalle("   ");

        // When
        entrada.beforeUpdate();

        // Then
        assertEquals("log-service", entrada.getServicio());
        assertEquals("CONSULTAR", entrada.getOperacion());
        assertEquals("usuario", entrada.getUsuarioId());
        assertEquals("OK", entrada.getResultado());
        assertNull(entrada.getDetalle());
        assertNull(entrada.getTimestamp());
    }
}
