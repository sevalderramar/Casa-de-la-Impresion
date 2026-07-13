package cl.duocuc.estadoservice.controller;

import cl.duocuc.estadoservice.dto.CambioEstadoRequest;
import cl.duocuc.estadoservice.dto.CambioEstadoResponse;
import cl.duocuc.estadoservice.service.EstadoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estados")
@Tag(name = "Estados", description = "Registro y consulta de cambios de estado de pedidos")
public class EstadoController {

    private final EstadoService estadoService;

    public EstadoController(EstadoService estadoService) {
        this.estadoService = estadoService;
    }

    @PostMapping
    @Operation(summary = "Registrar cambio de estado", description = "Registra un nuevo cambio de estado para un pedido.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Cambio de estado registrado",
                    content = @Content(schema = @Schema(implementation = CambioEstadoResponse.class),
                            examples = @ExampleObject(value = "{\"id\":1,\"numeroPedido\":1001,\"estadoAnterior\":\"CREADO\",\"estadoNuevo\":\"EN_PREPARACION\",\"fechaCambio\":\"2026-07-12T10:30:00\",\"observacion\":\"Pedido enviado a preparación\"}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request inválido"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno")
    })
    public ResponseEntity<CambioEstadoResponse> crearCambioEstado(@Valid @RequestBody CambioEstadoRequest request) {
        CambioEstadoResponse response = estadoService.registrarCambioEstado(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/pedido/{numeroPedido}")
    @Operation(summary = "Listar cambios por pedido", description = "Obtiene el historial de cambios de estado de un pedido, ordenado por fecha ascendente.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Historial obtenido"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No existen cambios para el pedido"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno")
    })
    public ResponseEntity<List<CambioEstadoResponse>> listarCambiosPorPedido(
            @Parameter(description = "Número del pedido", example = "1001") @PathVariable Long numeroPedido) {
        List<CambioEstadoResponse> cambios = estadoService.listarCambiosPorPedido(numeroPedido);
        return ResponseEntity.ok(cambios);
    }

    @GetMapping("/pedido/{numeroPedido}/ultimo")
    @Operation(summary = "Obtener último estado", description = "Obtiene el último cambio de estado registrado para un pedido.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Último estado obtenido"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No existen cambios para el pedido"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno")
    })
    public ResponseEntity<CambioEstadoResponse> obtenerUltimoEstado(
            @Parameter(description = "Número del pedido", example = "1001") @PathVariable Long numeroPedido) {
        CambioEstadoResponse ultimoEstado = estadoService.obtenerUltimoEstadoPorPedido(numeroPedido);
        return ResponseEntity.ok(ultimoEstado);
    }
}

