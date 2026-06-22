package cl.duocuc.metricaservice.client;


import cl.duocuc.metricaservice.dto.ClienteResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cliente-service", url = "${services.cliente.url}")
public interface ClienteFeignClient {

    @GetMapping("/api/clientes/{id}")
    ClienteResponseDTO obtenerCliente(@PathVariable("id") Long id);
}
