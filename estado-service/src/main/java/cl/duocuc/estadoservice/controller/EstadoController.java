package cl.duocuc.estadoservice.controller;

import cl.duocuc.estadoservice.dto.CambioEstadoRequest;
import cl.duocuc.estadoservice.dto.CambioEstadoResponse;
import cl.duocuc.estadoservice.service.EstadoService;
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

    @PostMapping
    public ResponseEntity<CambioEstadoResponse> crearCambioEstado(@Valid @RequestBody CambioEstadoRequest request) {
        CambioEstadoResponse response = estadoService.registrarCambioEstado(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/pedido/{numeroPedido}")
    public ResponseEntity<List<CambioEstadoResponse>> listarCambiosPorPedido(@PathVariable Long numeroPedido) {
        List<CambioEstadoResponse> cambios = estadoService.listarCambiosPorPedido(numeroPedido);
        return ResponseEntity.ok(cambios);
    }

    @GetMapping("/pedido/{numeroPedido}/ultimo")
    public ResponseEntity<CambioEstadoResponse> obtenerUltimoEstado(@PathVariable Long numeroPedido) {
        CambioEstadoResponse ultimoEstado = estadoService.obtenerUltimoEstadoPorPedido(numeroPedido);
        return ResponseEntity.ok(ultimoEstado);
    }
}

