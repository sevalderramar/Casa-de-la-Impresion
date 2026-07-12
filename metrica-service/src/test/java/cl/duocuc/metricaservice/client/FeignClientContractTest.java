package cl.duocuc.metricaservice.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.lang.reflect.Method;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class FeignClientContractTest {

    @Test
    void pedidoFeignClientMantieneContratoConPedidoService() throws NoSuchMethodException {
        // Given
        FeignClient feignClient = PedidoFeignClient.class.getAnnotation(FeignClient.class);
        Method listarPedidos = PedidoFeignClient.class.getMethod("listarPedidos", LocalDate.class, LocalDate.class);
        Method listarPedidosPorCliente = PedidoFeignClient.class.getMethod("listarPedidosPorCliente", Long.class);

        // When / Then
        assertEquals("pedido-service", feignClient.name());
        assertEquals("${services.pedido.url}", feignClient.url());
        assertArrayEquals(new String[]{"/api/pedidos"}, listarPedidos.getAnnotation(GetMapping.class).value());
        assertArrayEquals(new String[]{"/api/pedidos/cliente/{clienteId}"}, listarPedidosPorCliente.getAnnotation(GetMapping.class).value());
    }

    @Test
    void clienteFeignClientMantieneContratoConClienteService() throws NoSuchMethodException {
        // Given
        FeignClient feignClient = ClienteFeignClient.class.getAnnotation(FeignClient.class);
        Method obtenerCliente = ClienteFeignClient.class.getMethod("obtenerCliente", Long.class);

        // When / Then
        assertEquals("cliente-service", feignClient.name());
        assertEquals("${services.cliente.url}", feignClient.url());
        assertArrayEquals(new String[]{"/api/clientes/{id}"}, obtenerCliente.getAnnotation(GetMapping.class).value());
    }
}
