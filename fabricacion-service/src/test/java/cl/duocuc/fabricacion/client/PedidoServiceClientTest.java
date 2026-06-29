package cl.duocuc.fabricacion.client;

import cl.duocuc.fabricacion.exception.PedidoNoEncontradoException;
import cl.duocuc.fabricacion.exception.ServiceUnavailableException;
import feign.FeignException;
import feign.Request;
import feign.Response;
import feign.RetryableException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PedidoServiceClientTest {

    @Mock
    private PedidoFeignClient feign;

    @InjectMocks
    private PedidoServiceClient client;

    @Test
    void validarExistenciaMantienePedidoNoEncontradoCuandoPedidoServiceRetorna404() {
        when(feign.obtenerPedido(404L)).thenThrow(feignException(404, "Not Found"));

        PedidoNoEncontradoException exception = assertThrows(
                PedidoNoEncontradoException.class,
                () -> client.validarExistencia(404L)
        );

        assertEquals("Pedido no encontrado: 404", exception.getMessage());
        verify(feign).obtenerPedido(404L);
    }

    @Test
    void validarExistenciaTraduceError5xxDePedidoServiceAServiceUnavailable() {
        FeignException remoteError = feignException(500, "Internal Server Error");
        when(feign.obtenerPedido(1001L)).thenThrow(remoteError);

        ServiceUnavailableException exception = assertThrows(
                ServiceUnavailableException.class,
                () -> client.validarExistencia(1001L)
        );

        assertTrue(exception.getMessage().contains("No fue posible comunicarse con pedido-service"), exception.getMessage());
        assertInstanceOf(FeignException.class, exception.getCause());
        verify(feign).obtenerPedido(1001L);
    }

    @Test
    void validarExistenciaTraduceTimeoutDePedidoServiceAServiceUnavailable() {
        RetryableException timeout = new RetryableException(
                503,
                "Read timed out",
                Request.HttpMethod.GET,
                new SocketTimeoutException("Read timed out"),
                (Long) null,
                request()
        );
        when(feign.obtenerPedido(1002L)).thenThrow(timeout);

        ServiceUnavailableException exception = assertThrows(
                ServiceUnavailableException.class,
                () -> client.validarExistencia(1002L)
        );

        assertTrue(exception.getMessage().contains("No fue posible conectar con pedido-service"), exception.getMessage());
        assertInstanceOf(RetryableException.class, exception.getCause());
        verify(feign).obtenerPedido(1002L);
    }

    private FeignException feignException(int status, String reason) {
        Response response = Response.builder()
                .request(request())
                .status(status)
                .reason(reason)
                .headers(Map.of())
                .body(new byte[0])
                .build();

        return FeignException.errorStatus("obtenerPedido", response);
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
