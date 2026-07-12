package cl.duocuc.estadoservice.common.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

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

    @Test
    void handleResourceNotFoundSinMensajeUsaReasonPhrase() {
        // Given
        ResourceNotFoundException exception = new ResourceNotFoundException();

        // When
        ResponseEntity<Map<String, Object>> response = handler.handleResourceNotFound(exception);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().get("status"));
        assertEquals("Not Found", response.getBody().get("mensaje"));
    }
}
