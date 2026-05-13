package cl.duocuc.despachoservice.client;

import cl.duocuc.despachoservice.client.pedido.dto.PedidoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "pedido-service", url = "${services.pedido.url}")
public interface PedidoFeignClient {

    @GetMapping("/api/pedidos/{numeroPedido}")
    PedidoResponse obtenerPedidoPorNumero(@PathVariable("numeroPedido") Long numeroPedido);
}

