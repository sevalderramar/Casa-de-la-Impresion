package cl.duocuc.despachoservice.controller;

import cl.duocuc.despachoservice.dto.DespachoRequest;
import cl.duocuc.despachoservice.dto.DespachoResponse;
import cl.duocuc.despachoservice.service.DespachoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/despachos")
@Tag(name = "Despachos", description = "Gestión de despachos de pedidos")
public class DespachoController {

    private final DespachoService despachoService;

    public DespachoController(DespachoService despachoService) {
        this.despachoService = despachoService;
    }

    @PostMapping
    @Operation(summary = "Crear despacho", description = "Registra un despacho asociado a un pedido existente")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Despacho creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
            @ApiResponse(responseCode = "409", description = "Despacho duplicado"),
            @ApiResponse(responseCode = "503", description = "Servicio de pedidos no disponible")
    })
    public ResponseEntity<DespachoResponse> crearDespacho(@Valid @RequestBody DespachoRequest request) {
        DespachoResponse response = despachoService.crearDespacho(request);
        return ResponseEntity
                .created(URI.create("/api/despachos/" + response.getId()))
                .body(response);
    }

    @GetMapping
    @Operation(summary = "Listar despachos", description = "Obtiene todos los despachos o filtra por tipo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido")
    })
    public ResponseEntity<List<DespachoResponse>> listarDespachos(@Parameter(description = "Tipo de despacho opcional") @RequestParam(value = "tipo", required = false) String tipo) {
        return ResponseEntity.ok(despachoService.listarDespachos(tipo));
    }

    @GetMapping("/{numeroPedido}")
    @Operation(summary = "Buscar despacho por número de pedido", description = "Obtiene el despacho asociado a un pedido")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Despacho encontrado"),
            @ApiResponse(responseCode = "404", description = "Despacho no encontrado")
    })
    public ResponseEntity<DespachoResponse> obtenerDespachoPorNumeroPedido(@Parameter(description = "Número de pedido") @PathVariable Long numeroPedido) {
        return ResponseEntity.ok(despachoService.obtenerDespachoPorNumeroPedido(numeroPedido));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar despacho", description = "Actualiza los datos de un despacho existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Despacho actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Despacho no encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflicto de número de pedido"),
            @ApiResponse(responseCode = "503", description = "Servicio de pedidos no disponible")
    })
    public ResponseEntity<DespachoResponse> actualizarDespacho(@PathVariable Long id,
                                                               @Valid @RequestBody DespachoRequest request) {
        return ResponseEntity.ok(despachoService.actualizarDespacho(id, request));
    }
}

