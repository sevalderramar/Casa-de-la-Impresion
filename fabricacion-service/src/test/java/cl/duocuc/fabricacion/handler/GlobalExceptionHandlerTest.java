package cl.duocuc.fabricacion.handler;

import cl.duocuc.fabricacion.exception.ConflictException;
import cl.duocuc.fabricacion.exception.FabricacionException;
import cl.duocuc.fabricacion.exception.PedidoNoEncontradoException;
import cl.duocuc.fabricacion.exception.ResourceNotFoundException;
import cl.duocuc.fabricacion.exception.ServiceUnavailableException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleResourceNotFoundRetornaStatusNotFound() {
        // Given
        ResourceNotFoundException exception = new ResourceNotFoundException("Orden no encontrada");

        // When
        ResponseEntity<Map<String, Object>> response = handler.handleResourceNotFound(exception);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().get("status"));
        assertEquals("Orden no encontrada", response.getBody().get("mensaje"));
    }

    @Test
    void handlePedidoNoEncontradoRetornaStatusNotFound() {
        // Given
        PedidoNoEncontradoException exception = new PedidoNoEncontradoException("Pedido no encontrado");

        // When
        ResponseEntity<Map<String, Object>> response = handler.handleResourceNotFound(exception);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Pedido no encontrado", response.getBody().get("mensaje"));
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
        ConflictException exception = new ConflictException("Orden duplicada");

        // When
        ResponseEntity<Map<String, Object>> response = handler.handleConflict(exception);

        // Then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().get("status"));
        assertEquals("Orden duplicada", response.getBody().get("mensaje"));
    }

    @Test
    void handleFabricacionExceptionRetornaStatusConflict() {
        // Given
        FabricacionException exception = new FabricacionException("pedido-service rechaza la solicitud");

        // When
        ResponseEntity<Map<String, Object>> response = handler.handleConflict(exception);

        // Then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("pedido-service rechaza la solicitud", response.getBody().get("mensaje"));
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
    void handleResponseStatusExceptionUsaReasonCuandoExiste() {
        // Given
        ResponseStatusException exception = new ResponseStatusException(HttpStatus.CONFLICT, "conflicto remoto");

        // When
        ResponseEntity<Map<String, Object>> response = handler.handleResponseStatusException(exception);

        // Then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("conflicto remoto", response.getBody().get("mensaje"));
    }

    @Test
    void handleResponseStatusExceptionUsaMensajePorDefectoCuandoReasonEstaVacio() {
        // Given
        ResponseStatusException exception = new ResponseStatusException(HttpStatus.BAD_GATEWAY, "   ");

        // When
        ResponseEntity<Map<String, Object>> response = handler.handleResponseStatusException(exception);

        // Then
        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
        assertEquals("Error en la comunicacion con otro servicio", response.getBody().get("mensaje"));
    }

    @Test
    void handleValidationRetornaStatusBadRequestConIllegalArgumentException() {
        // Given
        IllegalArgumentException exception = new IllegalArgumentException("Dato invalido");

        // When
        ResponseEntity<Map<String, Object>> response = handler.handleValidation(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().get("status"));
        assertEquals("Dato invalido", response.getBody().get("mensaje"));
    }

    @Test
    void handleAllRetornaStatusInternalServerError() {
        // Given
        Exception exception = new Exception("Error inesperado");

        // When
        ResponseEntity<Map<String, Object>> response = handler.handleAll(exception);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().get("status"));
        assertEquals("Error interno del servidor", response.getBody().get("mensaje"));
    }
}
