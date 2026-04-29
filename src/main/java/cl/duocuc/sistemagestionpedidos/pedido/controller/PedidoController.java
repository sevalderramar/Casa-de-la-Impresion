package cl.duocuc.sistemagestionpedidos.pedido.controller;

import cl.duocuc.sistemagestionpedidos.pedido.dto.PedidoRequest;
import cl.duocuc.sistemagestionpedidos.pedido.dto.PedidoResponse;
import cl.duocuc.sistemagestionpedidos.pedido.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> crearPedido(@Valid @RequestBody PedidoRequest request) {
        PedidoResponse response = pedidoService.crearPedido(request);
        return ResponseEntity
                .created(URI.create("/api/pedidos/" + response.getId()))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponse>> listarPedidos() {
        return ResponseEntity.ok(pedidoService.listarPedidos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> obtenerPedidoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.obtenerPedidoPorId(id));
    }

    @GetMapping("/numero/{numeroPedido}")
    public ResponseEntity<PedidoResponse> obtenerPedidoPorNumero(@PathVariable String numeroPedido) {
        return ResponseEntity.ok(pedidoService.obtenerPedidoPorNumero(numeroPedido));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PedidoResponse>> listarPedidosPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(pedidoService.listarPedidosPorCliente(clienteId));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<PedidoResponse> actualizarEstado(@PathVariable Long id, @RequestParam String estado) {
        return ResponseEntity.ok(pedidoService.actualizarEstado(id, estado));
    }

    @GetMapping("/filtro")
    public ResponseEntity<List<PedidoResponse>> filtrarPorEstado(@RequestParam String estado) {
        return ResponseEntity.ok(pedidoService.filtrarPorEstado(estado));
    }

    @GetMapping("/filtro-combinado")
    public ResponseEntity<List<PedidoResponse>> filtrarPorEstadoYTipoDespacho(@RequestParam String estado,
                                                                               @RequestParam String tipoDespacho) {
        return ResponseEntity.ok(pedidoService.filtrarPorEstadoYTipoDespacho(estado, tipoDespacho));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPedido(@PathVariable Long id) {
        pedidoService.eliminarPedido(id);
        return ResponseEntity.noContent().build();
    }
}
