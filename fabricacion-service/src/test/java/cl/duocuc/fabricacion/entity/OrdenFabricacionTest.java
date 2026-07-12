package cl.duocuc.fabricacion.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class OrdenFabricacionTest {

    @Test
    void beforeCreateAsignaFechasYEstadoCuandoEstanNull() {
        // Given
        OrdenFabricacion orden = new OrdenFabricacion();

        // When
        orden.beforeCreate();

        // Then
        assertNotNull(orden.getFechaCreacion());
        assertNotNull(orden.getFechaInicio());
        assertEquals(EstadoFabricacion.EN_PROCESO, orden.getEstadoFabricacion());
    }

    @Test
    void beforeCreateConservaFechaInicioYEstadoCuandoYaExisten() {
        // Given
        LocalDateTime fechaInicio = LocalDateTime.of(2026, 5, 22, 10, 0);
        OrdenFabricacion orden = new OrdenFabricacion();
        orden.setFechaInicio(fechaInicio);
        orden.setEstadoFabricacion(EstadoFabricacion.PAUSADO);

        // When
        orden.beforeCreate();

        // Then
        assertEquals(fechaInicio, orden.getFechaInicio());
        assertEquals(EstadoFabricacion.PAUSADO, orden.getEstadoFabricacion());
        assertNotNull(orden.getFechaCreacion());
    }

    @Test
    void beforeUpdateAsignaFechaActualizacion() {
        // Given
        OrdenFabricacion orden = new OrdenFabricacion();

        // When
        orden.beforeUpdate();

        // Then
        assertNotNull(orden.getFechaActualizacion());
    }
}
