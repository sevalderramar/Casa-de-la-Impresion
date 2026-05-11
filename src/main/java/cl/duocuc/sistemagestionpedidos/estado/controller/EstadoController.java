package cl.duocuc.sistemagestionpedidos.estado.controller;

import cl.duocuc.sistemagestionpedidos.estado.dto.CambioEstadoRequest;
import cl.duocuc.sistemagestionpedidos.estado.dto.CambioEstadoResponse;
import cl.duocuc.sistemagestionpedidos.estado.service.EstadoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estados")
public class EstadoController {

    private final EstadoService estadoService;

    public EstadoController(EstadoService estadoService) {
        this.estadoService = estadoService;
    }

    /**
     * POST /api/estados
     * Crea un nuevo registro de cambio de estado.
     */
    @PostMapping
    public ResponseEntity<CambioEstadoResponse> crearCambioEstado(@Valid @RequestBody CambioEstadoRequest request) {
        CambioEstadoResponse response = estadoService.registrarCambioEstado(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/estados/pedido/{pedidoId}
     * Lista todos los cambios de estado de un pedido, ordenados por fecha.
     */
    @GetMapping("/pedido/{numeroPedido}")
    public ResponseEntity<List<CambioEstadoResponse>> listarCambiosPorPedido(@PathVariable Long numeroPedido) {
        List<CambioEstadoResponse> cambios = estadoService.listarCambiosPorPedido(numeroPedido);
        return ResponseEntity.ok(cambios);
    }

    /**
     * GET /api/estados/pedido/{pedidoId}/ultimo
     * Obtiene el último cambio de estado de un pedido.
     */
    @GetMapping("/pedido/{numeroPedido}/ultimo")
    public ResponseEntity<CambioEstadoResponse> obtenerUltimoEstado(@PathVariable Long numeroPedido) {
        CambioEstadoResponse ultimoEstado = estadoService.obtenerUltimoEstadoPorPedido(numeroPedido);
        return ResponseEntity.ok(ultimoEstado);
    }
}

