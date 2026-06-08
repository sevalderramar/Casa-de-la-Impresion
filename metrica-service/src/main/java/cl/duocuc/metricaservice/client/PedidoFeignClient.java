package cl.duocuc.metricaservice.client;

import cl.duocuc.metricaservice.dto.PedidoResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@FeignClient(name = "pedido-service", url = "${pedido.service.url}")
public interface PedidoFeignClient {

    @GetMapping("/api/pedidos")
    List<PedidoResponseDTO> listarPedidos(
            @RequestParam(value = "desde", required = false) LocalDate desde,
            @RequestParam(value = "hasta", required = false) LocalDate hasta
    );

    @GetMapping("/api/pedidos/cliente/{clienteId}")
    List<PedidoResponseDTO> listarPedidosPorCliente(@PathVariable("clienteId") Long clienteId);
}
