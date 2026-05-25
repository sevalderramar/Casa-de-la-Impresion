package cl.duocuc.pedidoservice.client;

import cl.duocuc.pedidoservice.client.cliente.dto.ClienteResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import cl.duocuc.pedidoservice.common.ApiResponse;

@FeignClient(name = "cliente-service", url = "${services.cliente.url}")
public interface ClienteFeignClient {

    @GetMapping("/api/clientes/{id}")
    ClienteResponse obtenerClientePorId(@PathVariable("id") Long id);
}

