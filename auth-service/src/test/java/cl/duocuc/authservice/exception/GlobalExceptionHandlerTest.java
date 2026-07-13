package cl.duocuc.authservice.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleResourceNotFoundRetorna404() {
        // When
        var response = handler.handleResourceNotFound(new ResourceNotFoundException("no encontrado"));

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertBody(response.getBody(), 404, "no encontrado");
    }

    @Test
    void handleConflictRetorna409() {
        // When
        var response = handler.handleConflict(new ConflictException("duplicado"));

        // Then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertBody(response.getBody(), 409, "duplicado");
    }

    @Test
    void handleValidationRetornaPrimerMensajeDeError() throws NoSuchMethodException {
        // Given
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "email", "email inválido"));
        Method method = GlobalExceptionHandlerTest.class.getDeclaredMethod("metodoValidado", String.class);
        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(new MethodParameter(method, 0), bindingResult);

        // When
        var response = handler.handleValidation(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertBody(response.getBody(), 400, "email inválido");
    }

    @Test
    void handleValidationUsaMensajeGenericoCuandoNoHayErrores() throws NoSuchMethodException {
        // Given
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        Method method = GlobalExceptionHandlerTest.class.getDeclaredMethod("metodoValidado", String.class);
        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(new MethodParameter(method, 0), bindingResult);

        // When
        var response = handler.handleValidation(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertBody(response.getBody(), 400, "Datos inválidos");
    }

    @Test
    void handleAuthenticationRetorna401ConMensajeControlado() {
        // When
        var response = handler.handleAuthentication(new BadCredentialsException("bad credentials"));

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertBody(response.getBody(), 401, "Credenciales inválidas");
    }

    @Test
    void handleGenericRetorna500SinExponerDetalleInterno() {
        // When
        var response = handler.handleGeneric(new RuntimeException("detalle interno"));

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertBody(response.getBody(), 500, "Error interno del servidor");
    }

    @SuppressWarnings("unused")
    private void metodoValidado(String request) {
    }

    private void assertBody(Map<String, Object> body, int status, String mensaje) {
        assertEquals(status, body.get("status"));
        assertEquals(mensaje, body.get("mensaje"));
    }
}
