package cl.duocuc.fabricacion.controller;

import cl.duocuc.fabricacion.dto.ApiResponse;
import cl.duocuc.fabricacion.dto.OrdenFabricacionRequest;
import cl.duocuc.fabricacion.dto.OrdenFabricacionResponse;
import cl.duocuc.fabricacion.dto.UpdateEstadoFabricacionRequest;
import cl.duocuc.fabricacion.service.OrdenFabricacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/fabricacion")
@RequiredArgsConstructor
@Tag(name = "Fabricación", description = "Gestión de órdenes de fabricación")
public class OrdenFabricacionController {

    private final OrdenFabricacionService ordenService;

    @GetMapping("/ping")
    @Operation(summary = "Healthcheck de fabricación", description = "Verifica que el servicio esté operativo")
    @ApiResponses(@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Servicio activo"))
    public ResponseEntity<ApiResponse<String>> ping() {
        return ResponseEntity.ok(ApiResponse.success("pong", "fabricacion-service"));
    }

    @PostMapping
    @Operation(summary = "Crear orden de fabricación", description = "Registra una nueva orden asociada a un pedido existente")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Orden creada"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Orden duplicada"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "503", description = "Servicio de pedidos no disponible")
    })
    public ResponseEntity<ApiResponse<OrdenFabricacionResponse>> crearOrden(@Valid @RequestBody OrdenFabricacionRequest request) {
        OrdenFabricacionResponse resp = ordenService.crearOrden(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Orden creada", resp));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar orden de fabricación", description = "Obtiene una orden por su identificador")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Orden encontrada"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Orden no encontrada")
    })
    public ResponseEntity<ApiResponse<OrdenFabricacionResponse>> obtenerOrden(@Parameter(description = "ID de la orden") @PathVariable Long id) {
        OrdenFabricacionResponse resp = ordenService.obtenerOrden(id);
        return ResponseEntity.ok(ApiResponse.success("Orden encontrada", resp));
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Actualizar estado de fabricación", description = "Cambia el estado de una orden de fabricación")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Estado actualizado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Orden no encontrada"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Conflicto de estado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "503", description = "Servicio de pedidos no disponible")
    })
    public ResponseEntity<ApiResponse<OrdenFabricacionResponse>> actualizarEstado(
            @Parameter(description = "ID de la orden") @PathVariable Long id,
            @Valid @RequestBody UpdateEstadoFabricacionRequest request) {
        OrdenFabricacionResponse resp = ordenService.actualizarEstado(id, request);
        return ResponseEntity.ok(ApiResponse.success("Estado actualizado", resp));
    }
}
