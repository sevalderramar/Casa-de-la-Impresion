package cl.duocuc.metricaservice.controller;

import cl.duocuc.metricaservice.dto.ApiResponse;
import cl.duocuc.metricaservice.dto.MetricaClienteResponseDTO;
import cl.duocuc.metricaservice.dto.MetricaProductoResponseDTO;
import cl.duocuc.metricaservice.dto.ResumenVentasResponseDTO;
import cl.duocuc.metricaservice.exception.ConflictException;
import cl.duocuc.metricaservice.service.MetricaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/metricas")
@RequiredArgsConstructor
@Tag(name = "Métricas", description = "Consultas analíticas del sistema")
public class MetricaController {

    private final MetricaService metricaService;

    @GetMapping("/clientes/{id:\\d+}")
    @Operation(summary = "Métricas por cliente", description = "Obtiene métricas agregadas de un cliente específico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Métricas obtenidas"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    public ResponseEntity<ApiResponse<MetricaClienteResponseDTO>> obtenerMetricasCliente(@Parameter(description = "ID del cliente") @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Métricas del cliente obtenidas correctamente", metricaService.obtenerMetricasCliente(id)));
    }

    @GetMapping("/clientes/ranking")
    @Operation(summary = "Ranking de clientes", description = "Obtiene el ranking de clientes por monto total")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Ranking obtenido"))
    public ResponseEntity<ApiResponse<List<MetricaClienteResponseDTO>>> obtenerRankingClientes(
            @Parameter(description = "Cantidad máxima de clientes a retornar") @RequestParam(required = false, defaultValue = "10") Integer limite) {
        return ResponseEntity.ok(ApiResponse.success("Ranking obtenido correctamente", metricaService.obtenerRankingClientes(limite)));
    }

    @GetMapping("/productos/top")
    @Operation(summary = "Top productos", description = "Obtiene los productos más vendidos en un rango de fechas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Top productos obtenido"),
            @ApiResponse(responseCode = "400", description = "Rango de fechas inválido")
    })
    public ResponseEntity<ApiResponse<List<MetricaProductoResponseDTO>>> obtenerTopProductos(
            @Parameter(description = "Fecha inicial") @RequestParam(required = false) LocalDate desde,
            @Parameter(description = "Fecha final") @RequestParam(required = false) LocalDate hasta,
            @Parameter(description = "Cantidad máxima de productos a retornar") @RequestParam(required = false, defaultValue = "10") Integer limite) {

        if (desde == null) desde = LocalDate.now().withDayOfMonth(1);
        if (hasta == null) hasta = LocalDate.now();
        
        if (desde.isAfter(hasta)) {
            throw new ConflictException("La fecha 'desde' no puede ser posterior a 'hasta'");
        }
        
        return ResponseEntity.ok(ApiResponse.success("Top productos obtenido", metricaService.obtenerTopProductos(desde, hasta, limite)));
    }

    @GetMapping("/ventas")
    @Operation(summary = "Resumen de ventas", description = "Obtiene un resumen de ventas por rango de fechas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resumen obtenido"),
            @ApiResponse(responseCode = "400", description = "Rango de fechas inválido")
    })
    public ResponseEntity<ApiResponse<ResumenVentasResponseDTO>> obtenerResumenVentas(
            @Parameter(description = "Fecha inicial") @RequestParam(required = false) LocalDate desde,
            @Parameter(description = "Fecha final") @RequestParam(required = false) LocalDate hasta) {

        if (desde == null) desde = LocalDate.now().withDayOfMonth(1);
        if (hasta == null) hasta = LocalDate.now();

        if (desde.isAfter(hasta)) {
            throw new ConflictException("La fecha 'desde' no puede ser posterior a 'hasta'");
        }

        return ResponseEntity.ok(ApiResponse.success("Resumen de ventas obtenido", metricaService.obtenerResumenVentas(desde, hasta)));
    }

    @GetMapping("/ping")
    @Operation(summary = "Healthcheck de métricas", description = "Verifica que el servicio esté operativo")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Servicio activo"))
    public ResponseEntity<ApiResponse<String>> ping() {
        return ResponseEntity.ok(ApiResponse.success("metrica-service activo", null));
    }
}
