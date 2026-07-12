package cl.duocuc.fabricacion.client;

import cl.duocuc.fabricacion.dto.PedidoDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class PedidoFeignClientTest {

    @Test
    void pedidoFeignClientMantieneContratoConPedidoService() throws NoSuchMethodException {
        // Given
        FeignClient feignClient = PedidoFeignClient.class.getAnnotation(FeignClient.class);
        Method obtenerPedido = PedidoFeignClient.class.getMethod("obtenerPedido", Long.class);
        Method actualizarEstado = PedidoFeignClient.class.getMethod("actualizarEstado", Long.class, PedidoFeignClient.UpdateEstadoRequest.class);

        // When / Then
        assertEquals("pedido-service", feignClient.name());
        assertEquals("${services.pedido.url}", feignClient.url());
        assertArrayEquals(new String[]{"/api/pedidos/{id}"}, obtenerPedido.getAnnotation(GetMapping.class).value());
        assertArrayEquals(new String[]{"/api/pedidos/{id}/estado"}, actualizarEstado.getAnnotation(PostMapping.class).value());
    }

    @Test
    void pedidoDTORepresentaRespuestaMinimaDePedidoService() {
        // Given
        LocalDateTime fechaCreacion = LocalDateTime.of(2026, 5, 22, 10, 0);
        LocalDateTime fechaActualizacion = LocalDateTime.of(2026, 5, 22, 11, 0);

        // When
        PedidoDTO pedido = new PedidoDTO(1L, "1001", 10L, "PRODUCCION", BigDecimal.valueOf(7000),
                "RM", fechaCreacion, fechaActualizacion);

        // Then
        assertEquals(1L, pedido.id());
        assertEquals("1001", pedido.numeroPedido());
        assertEquals(10L, pedido.clienteId());
        assertEquals("PRODUCCION", pedido.estado());
        assertEquals(BigDecimal.valueOf(7000), pedido.monto());
        assertEquals("RM", pedido.tipoDespacho());
        assertEquals(fechaCreacion, pedido.fechaCreacion());
        assertEquals(fechaActualizacion, pedido.fechaActualizacion());
    }

    @Test
    void updateEstadoRequestPermiteRepresentarEstado() {
        // Given
        PedidoFeignClient.UpdateEstadoRequest request = new PedidoFeignClient.UpdateEstadoRequest();

        // When
        request.setEstado("LISTO");

        // Then
        assertEquals("LISTO", request.getEstado());
    }
}
