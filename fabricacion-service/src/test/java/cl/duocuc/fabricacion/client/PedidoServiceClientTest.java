package cl.duocuc.fabricacion.client;

import cl.duocuc.fabricacion.exception.FabricacionException;
import cl.duocuc.fabricacion.exception.PedidoNoEncontradoException;
import cl.duocuc.fabricacion.exception.ServiceUnavailableException;
import feign.FeignException;
import feign.Request;
import feign.Response;
import feign.RetryableException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoServiceClientTest {

    @Mock
    private PedidoFeignClient feign;

    @InjectMocks
    private PedidoServiceClient client;

    @Test
    void validarExistenciaNoLanzaErrorCuandoPedidoExiste() {
        // When
        client.validarExistencia(1001L);

        // Then
        verify(feign).obtenerPedido(1001L);
    }

    @Test
    void validarExistenciaMantienePedidoNoEncontradoCuandoPedidoServiceRetorna404() {
        // Given
        when(feign.obtenerPedido(404L)).thenThrow(feignException(404, "Not Found"));

        // When
        PedidoNoEncontradoException exception = assertThrows(
                PedidoNoEncontradoException.class,
                () -> client.validarExistencia(404L)
        );

        // Then
        assertEquals("Pedido no encontrado: 404", exception.getMessage());
        verify(feign).obtenerPedido(404L);
    }

    @Test
    void validarExistenciaTraduceBadRequestAFabricacionException() {
        // Given
        when(feign.obtenerPedido(1001L)).thenThrow(feignException(400, "Bad Request", "pedido invalido"));

        // When
        FabricacionException exception = assertThrows(FabricacionException.class,
                () -> client.validarExistencia(1001L));

        // Then
        assertEquals("pedido-service rechaza la solicitud: pedido invalido", exception.getMessage());
        verify(feign).obtenerPedido(1001L);
    }

    @Test
    void validarExistenciaTraduceUnauthorizedAServiceUnavailable() {
        // Given
        FeignException unauthorized = feignException(401, "Unauthorized");
        when(feign.obtenerPedido(1001L)).thenThrow(unauthorized);

        // When
        ServiceUnavailableException exception = assertThrows(ServiceUnavailableException.class,
                () -> client.validarExistencia(1001L));

        // Then
        assertEquals("pedido-service no permite la validacion", exception.getMessage());
        assertEquals(unauthorized, exception.getCause());
    }

    @Test
    void validarExistenciaTraduceError5xxDePedidoServiceAServiceUnavailable() {
        // Given
        FeignException remoteError = feignException(500, "Internal Server Error");
        when(feign.obtenerPedido(1001L)).thenThrow(remoteError);

        // When
        ServiceUnavailableException exception = assertThrows(
                ServiceUnavailableException.class,
                () -> client.validarExistencia(1001L)
        );

        // Then
        assertTrue(exception.getMessage().contains("No fue posible comunicarse con pedido-service"), exception.getMessage());
        assertInstanceOf(FeignException.class, exception.getCause());
        verify(feign).obtenerPedido(1001L);
    }

    @Test
    void validarExistenciaTraduceTimeoutDePedidoServiceAServiceUnavailable() {
        // Given
        RetryableException timeout = retryableException("Read timed out");
        when(feign.obtenerPedido(1002L)).thenThrow(timeout);

        // When
        ServiceUnavailableException exception = assertThrows(
                ServiceUnavailableException.class,
                () -> client.validarExistencia(1002L)
        );

        // Then
        assertTrue(exception.getMessage().contains("No fue posible conectar con pedido-service"), exception.getMessage());
        assertInstanceOf(RetryableException.class, exception.getCause());
        verify(feign).obtenerPedido(1002L);
    }

    @Test
    void validarExistenciaTraduceRuntimeExceptionInesperadaAServiceUnavailable() {
        // Given
        IllegalStateException cause = new IllegalStateException("sin conexion");
        when(feign.obtenerPedido(1001L)).thenThrow(cause);

        // When
        ServiceUnavailableException exception = assertThrows(ServiceUnavailableException.class,
                () -> client.validarExistencia(1001L));

        // Then
        assertEquals("Error inesperado al validar el pedido", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void notificarPedidoEnFabricacionActualizaEstadoAProduccion() {
        // When
        client.notificarPedidoEnFabricacion(1001L);

        // Then
        ArgumentCaptor<PedidoFeignClient.UpdateEstadoRequest> captor = ArgumentCaptor.forClass(PedidoFeignClient.UpdateEstadoRequest.class);
        verify(feign).actualizarEstado(org.mockito.ArgumentMatchers.eq(1001L), captor.capture());
        assertEquals("PRODUCCION", captor.getValue().getEstado());
    }

    @Test
    void notificarPedidoEnFabricacionTraduceNotFound() {
        // Given
        doThrow(feignException(404, "Not Found")).when(feign).actualizarEstado(org.mockito.ArgumentMatchers.eq(404L), org.mockito.ArgumentMatchers.any());

        // When
        PedidoNoEncontradoException exception = assertThrows(PedidoNoEncontradoException.class,
                () -> client.notificarPedidoEnFabricacion(404L));

        // Then
        assertEquals("Pedido no encontrado al iniciar fabricacion: 404", exception.getMessage());
    }

    @Test
    void notificarPedidoEnFabricacionTraduceConflictAResponseStatusException() {
        // Given
        doThrow(feignException(409, "Conflict", "estado no valido")).when(feign).actualizarEstado(org.mockito.ArgumentMatchers.eq(1001L), org.mockito.ArgumentMatchers.any());

        // When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> client.notificarPedidoEnFabricacion(1001L));

        // Then
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("pedido-service rechaza el inicio de fabricacion: estado no valido", exception.getReason());
    }

    @Test
    void notificarPedidoEnFabricacionTraduceBadRequestAFabricacionException() {
        // Given
        doThrow(feignException(400, "Bad Request")).when(feign).actualizarEstado(org.mockito.ArgumentMatchers.eq(1001L), org.mockito.ArgumentMatchers.any());

        // When
        FabricacionException exception = assertThrows(FabricacionException.class,
                () -> client.notificarPedidoEnFabricacion(1001L));

        // Then
        assertTrue(exception.getMessage().contains("pedido-service rechaza el inicio de fabricacion"));
    }

    @Test
    void notificarPedidoEnFabricacionTraduceRetryableExceptionAServiceUnavailable() {
        // Given
        doThrow(retryableException("connect timed out")).when(feign).actualizarEstado(org.mockito.ArgumentMatchers.eq(1001L), org.mockito.ArgumentMatchers.any());

        // When
        ServiceUnavailableException exception = assertThrows(ServiceUnavailableException.class,
                () -> client.notificarPedidoEnFabricacion(1001L));

        // Then
        assertTrue(exception.getMessage().contains("No fue posible conectar con pedido-service al iniciar fabricacion"));
    }

    @Test
    void notificarPedidoEnFabricacionTraduceFeignExceptionAServiceUnavailable() {
        // Given
        doThrow(feignException(503, "Service Unavailable")).when(feign).actualizarEstado(org.mockito.ArgumentMatchers.eq(1001L), org.mockito.ArgumentMatchers.any());

        // When
        ServiceUnavailableException exception = assertThrows(ServiceUnavailableException.class,
                () -> client.notificarPedidoEnFabricacion(1001L));

        // Then
        assertTrue(exception.getMessage().contains("No fue posible notificar a pedido-service"));
    }

    @Test
    void notificarPedidoListoActualizaEstadoAListo() {
        // When
        client.notificarPedidoListo(1001L);

        // Then
        ArgumentCaptor<PedidoFeignClient.UpdateEstadoRequest> captor = ArgumentCaptor.forClass(PedidoFeignClient.UpdateEstadoRequest.class);
        verify(feign).actualizarEstado(org.mockito.ArgumentMatchers.eq(1001L), captor.capture());
        assertEquals("LISTO", captor.getValue().getEstado());
    }

    @Test
    void notificarPedidoListoTraduceNotFound() {
        // Given
        doThrow(feignException(404, "Not Found")).when(feign).actualizarEstado(org.mockito.ArgumentMatchers.eq(404L), org.mockito.ArgumentMatchers.any());

        // When
        PedidoNoEncontradoException exception = assertThrows(PedidoNoEncontradoException.class,
                () -> client.notificarPedidoListo(404L));

        // Then
        assertEquals("Pedido no encontrado al notificar: 404", exception.getMessage());
    }

    @Test
    void notificarPedidoListoTraduceConflictAResponseStatusException() {
        // Given
        doThrow(feignException(409, "Conflict")).when(feign).actualizarEstado(org.mockito.ArgumentMatchers.eq(1001L), org.mockito.ArgumentMatchers.any());

        // When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> client.notificarPedidoListo(1001L));

        // Then
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertTrue(exception.getReason().contains("pedido-service rechaza el cierre de fabricacion"));
    }

    @Test
    void notificarPedidoListoTraduceBadRequestAFabricacionException() {
        // Given
        doThrow(feignException(400, "Bad Request", "estado invalido")).when(feign).actualizarEstado(org.mockito.ArgumentMatchers.eq(1001L), org.mockito.ArgumentMatchers.any());

        // When
        FabricacionException exception = assertThrows(FabricacionException.class,
                () -> client.notificarPedidoListo(1001L));

        // Then
        assertEquals("pedido-service rechaza el cambio de estado: estado invalido", exception.getMessage());
    }

    @Test
    void notificarPedidoListoTraduceRetryableExceptionAServiceUnavailable() {
        // Given
        doThrow(retryableException("Read timed out")).when(feign).actualizarEstado(org.mockito.ArgumentMatchers.eq(1001L), org.mockito.ArgumentMatchers.any());

        // When
        ServiceUnavailableException exception = assertThrows(ServiceUnavailableException.class,
                () -> client.notificarPedidoListo(1001L));

        // Then
        assertTrue(exception.getMessage().contains("No fue posible conectar con pedido-service al notificar"));
    }

    @Test
    void notificarPedidoListoTraduceFeignExceptionAServiceUnavailable() {
        // Given
        doThrow(feignException(503, "Service Unavailable")).when(feign).actualizarEstado(org.mockito.ArgumentMatchers.eq(1001L), org.mockito.ArgumentMatchers.any());

        // When
        ServiceUnavailableException exception = assertThrows(ServiceUnavailableException.class,
                () -> client.notificarPedidoListo(1001L));

        // Then
        assertTrue(exception.getMessage().contains("No fue posible notificar a pedido-service"));
    }

    @Test
    void notificarPedidoListoTraduceRuntimeExceptionInesperadaAServiceUnavailable() {
        // Given
        IllegalStateException cause = new IllegalStateException("sin contexto");
        doThrow(cause).when(feign).actualizarEstado(org.mockito.ArgumentMatchers.eq(1001L), org.mockito.ArgumentMatchers.any());

        // When
        ServiceUnavailableException exception = assertThrows(ServiceUnavailableException.class,
                () -> client.notificarPedidoListo(1001L));

        // Then
        assertEquals("Error inesperado al notificar el pedido", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    private FeignException feignException(int status, String reason) {
        return feignException(status, reason, null);
    }

    private FeignException feignException(int status, String reason, String body) {
        Response.Builder builder = Response.builder()
                .request(request())
                .status(status)
                .reason(reason)
                .headers(Map.of());
        if (body == null) {
            builder.body(new byte[0]);
        } else {
            builder.body(body, StandardCharsets.UTF_8);
        }

        return FeignException.errorStatus("pedido", builder.build());
    }

    private RetryableException retryableException(String message) {
        return new RetryableException(
                503,
                message,
                Request.HttpMethod.GET,
                new SocketTimeoutException(message),
                (Long) null,
                request()
        );
    }

    private Request request() {
        return Request.create(
                Request.HttpMethod.GET,
                "/api/pedidos/1001",
                Map.of(),
                null,
                StandardCharsets.UTF_8,
                null
        );
    }
}
