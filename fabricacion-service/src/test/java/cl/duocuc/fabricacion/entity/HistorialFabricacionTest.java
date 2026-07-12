package cl.duocuc.fabricacion.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class HistorialFabricacionTest {

    @Test
    void onCreateAsignaFechaCambio() {
        // Given
        HistorialFabricacion historial = new HistorialFabricacion();

        // When
        historial.onCreate();

        // Then
        assertNotNull(historial.getFechaCambio());
    }
}
