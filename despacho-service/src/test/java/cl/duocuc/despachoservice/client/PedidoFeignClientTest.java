package cl.duocuc.despachoservice.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class PedidoFeignClientTest {

    @Test
    void pedidoFeignClientMantieneContratoConPedidoService() throws NoSuchMethodException {
        // Given
        FeignClient feignClient = PedidoFeignClient.class.getAnnotation(FeignClient.class);
        Method method = PedidoFeignClient.class.getMethod("obtenerPedidoPorNumero", Long.class);
        GetMapping getMapping = method.getAnnotation(GetMapping.class);

        // When / Then
        assertEquals("pedido-service", feignClient.name());
        assertEquals("${services.pedido.url}", feignClient.url());
        assertArrayEquals(new String[]{"/api/pedidos/{numeroPedido}"}, getMapping.value());
    }
}
