package cl.duocuc.metricaservice.handler;

import cl.duocuc.metricaservice.exception.ConflictException;
import cl.duocuc.metricaservice.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleResourceNotFoundRetornaStatusNotFound() {
        // Given
        ResourceNotFoundException exception = new ResourceNotFoundException("Cliente no encontrado");

        // When
        ResponseEntity<Map<String, Object>> response = handler.handleResourceNotFound(exception);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().get("status"));
        assertEquals("Cliente no encontrado", response.getBody().get("mensaje"));
    }

    @Test
    void handleConflictRetornaStatusConflict() {
        // Given
        ConflictException exception = new ConflictException("Conflicto de fechas");

        // When
        ResponseEntity<Map<String, Object>> response = handler.handleConflict(exception);

        // Then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().get("status"));
        assertEquals("Conflicto de fechas", response.getBody().get("mensaje"));
    }

    @Test
    void handleGenericRetornaStatusInternalServerError() {
        // Given
        Exception exception = new Exception("Error inesperado");

        // When
        ResponseEntity<Map<String, Object>> response = handler.handleGeneric(exception);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().get("status"));
        assertEquals("Error interno del servidor", response.getBody().get("mensaje"));
    }
}
