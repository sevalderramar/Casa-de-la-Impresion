package cl.duocuc.sistemagestionpedidos.pedido.controller;

import cl.duocuc.sistemagestionpedidos.pedido.dto.PedidoRequest;
import cl.duocuc.sistemagestionpedidos.pedido.dto.PedidoResponse;
import cl.duocuc.sistemagestionpedidos.pedido.dto.EstadoRequest;
import cl.duocuc.sistemagestionpedidos.pedido.service.PedidoService;
import cl.duocuc.sistemagestionpedidos.estado.dto.CambioEstadoResponse;
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
                .created(URI.create("/api/pedidos/" + response.getNumeroPedido()))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponse>> listarPedidos() {
        return ResponseEntity.ok(pedidoService.listarPedidos());
    }

    @GetMapping("/{numeroPedido}")
    public ResponseEntity<PedidoResponse> obtenerPedidoPorNumero(@PathVariable Long numeroPedido) {
        return ResponseEntity.ok(pedidoService.obtenerPedidoPorNumero(numeroPedido));
    }

    @GetMapping("/numero/{numeroPedido}")
    public ResponseEntity<PedidoResponse> obtenerPedidoPorNumeroStr(@PathVariable String numeroPedido) {
        return ResponseEntity.ok(pedidoService.obtenerPedidoPorNumeroStr(numeroPedido));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PedidoResponse>> listarPedidosPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(pedidoService.listarPedidosPorCliente(clienteId));
    }

    @PatchMapping("/{numeroPedido}/estado")
    public ResponseEntity<PedidoResponse> actualizarEstado(@PathVariable Long numeroPedido,
                                                            @Valid @RequestBody EstadoRequest request) {
        return ResponseEntity.ok(pedidoService.actualizarEstado(numeroPedido, request));
    }

    @GetMapping("/{numeroPedido}/historial")
    public ResponseEntity<java.util.List<CambioEstadoResponse>> obtenerHistorial(@PathVariable Long numeroPedido) {
        return ResponseEntity.ok(pedidoService.listarHistorial(numeroPedido));
    }


    @DeleteMapping("/{numeroPedido}")
    public ResponseEntity<Void> eliminarPedido(@PathVariable Long numeroPedido) {
        pedidoService.eliminarPedido(numeroPedido);
        return ResponseEntity.noContent().build();
    }
}
