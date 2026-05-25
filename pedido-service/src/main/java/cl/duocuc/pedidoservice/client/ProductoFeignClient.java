package cl.duocuc.pedidoservice.client;

import cl.duocuc.pedidoservice.client.producto.dto.ProductoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import cl.duocuc.pedidoservice.common.ApiResponse;

@FeignClient(name = "producto-service", url = "${services.producto.url}")
public interface ProductoFeignClient {

    @GetMapping("/api/productos/{id}")
    ProductoResponse obtenerProductoPorId(@PathVariable("id") Long id);
}

