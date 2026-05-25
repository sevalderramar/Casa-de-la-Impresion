package cl.duocuc.fabricacion.client;

import cl.duocuc.fabricacion.dto.PedidoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "pedido-service", url = "${services.pedido.url}")
public interface PedidoFeignClient {

    @GetMapping("/api/pedidos/{id}")
    PedidoDTO obtenerPedido(@PathVariable("id") Long id);

    @PostMapping("/api/pedidos/{id}/estado")
    PedidoDTO actualizarEstado(
            @PathVariable("id") Long id,
            @RequestBody UpdateEstadoRequest request
    );

    class UpdateEstadoRequest {

        private String estado;

        public UpdateEstadoRequest() {
        }

        public UpdateEstadoRequest(String estado) {
            this.estado = estado;
        }

        public String getEstado() {
            return estado;
        }

        public void setEstado(String estado) {
            this.estado = estado;
        }
    }
}
