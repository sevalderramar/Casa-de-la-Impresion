package cl.duocuc.estadoservice.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CambioEstadoTest {

    @Test
    void onCreateAsignaFechaCambioCuandoEstaNull() {
        // Given
        CambioEstado cambio = new CambioEstado();

        // When
        cambio.onCreate();

        // Then
        assertNotNull(cambio.getFechaCambio());
    }

    @Test
    void onCreateConservaFechaCambioCuandoYaExiste() {
        // Given
        LocalDateTime fechaExistente = LocalDateTime.of(2026, 5, 22, 10, 0);
        CambioEstado cambio = new CambioEstado();
        cambio.setFechaCambio(fechaExistente);

        // When
        cambio.onCreate();

        // Then
        assertEquals(fechaExistente, cambio.getFechaCambio());
    }
}
