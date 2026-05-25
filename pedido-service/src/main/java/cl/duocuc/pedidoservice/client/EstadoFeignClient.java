package cl.duocuc.pedidoservice.client;

import cl.duocuc.pedidoservice.client.estado.dto.CambioEstadoRequest;
import cl.duocuc.pedidoservice.client.estado.dto.CambioEstadoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import cl.duocuc.pedidoservice.common.ApiResponse;
import java.util.List;

@FeignClient(name = "estado-service", url = "${services.estado.url}")
public interface EstadoFeignClient {

    @PostMapping("/api/estados")
    ApiResponse<Void> registrarCambioEstado(@RequestBody CambioEstadoRequest request);

    @GetMapping("/api/estados/pedido/{numeroPedido}")
    List<CambioEstadoResponse> listarCambiosPorPedido(@PathVariable("numeroPedido") Long numeroPedido);
}

