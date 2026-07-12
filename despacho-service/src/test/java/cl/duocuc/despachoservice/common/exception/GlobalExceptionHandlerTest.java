package cl.duocuc.despachoservice.common.exception;

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
        ResourceNotFoundException exception = new ResourceNotFoundException("No encontrado");

        // When
        ResponseEntity<Map<String, Object>> response = handler.handleResourceNotFound(exception);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().get("status"));
        assertEquals("No encontrado", response.getBody().get("mensaje"));
    }

    @Test
    void handleResourceNotFoundConMensajeBlancoUsaReasonPhrase() {
        // Given
        ResourceNotFoundException exception = new ResourceNotFoundException("   ");

        // When
        ResponseEntity<Map<String, Object>> response = handler.handleResourceNotFound(exception);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Not Found", response.getBody().get("mensaje"));
    }

    @Test
    void handleConflictRetornaStatusConflict() {
        // Given
        ConflictException exception = new ConflictException("Conflicto de prueba");

        // When
        ResponseEntity<Map<String, Object>> response = handler.handleConflict(exception);

        // Then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().get("status"));
        assertEquals("Conflicto de prueba", response.getBody().get("mensaje"));
    }

    @Test
    void handleServiceUnavailableRetornaStatusServiceUnavailable() {
        // Given
        ServiceUnavailableException exception = new ServiceUnavailableException("Servicio no disponible");

        // When
        ResponseEntity<Map<String, Object>> response = handler.handleServiceUnavailable(exception);

        // Then
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals(503, response.getBody().get("status"));
        assertEquals("Servicio no disponible", response.getBody().get("mensaje"));
    }

    @Test
    void handleBadRequestRetornaStatusBadRequestConIllegalArgumentException() {
        // Given
        IllegalArgumentException exception = new IllegalArgumentException("Dato invalido");

        // When
        ResponseEntity<Map<String, Object>> response = handler.handleBadRequest(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().get("status"));
        assertEquals("Dato invalido", response.getBody().get("mensaje"));
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
