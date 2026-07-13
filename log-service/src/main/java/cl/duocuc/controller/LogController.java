package cl.duocuc.logservice.controller;

import cl.duocuc.logservice.dto.ApiResponse;
import cl.duocuc.logservice.dto.LogRequestDTO;
import cl.duocuc.logservice.entity.LogEntrada;
import cl.duocuc.logservice.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
@Tag(name = "Logs", description = "Registro y consulta de eventos operacionales del sistema")
public class LogController {

    private final LogService logService;

    @PostMapping
    @Operation(summary = "Registrar log", description = "Registra un evento operacional generado por un servicio del ecosistema.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Log registrado",
                    content = @Content(schema = @Schema(implementation = LogEntrada.class),
                            examples = @ExampleObject(value = "{\"mensaje\":\"Log registrado correctamente\",\"exitoso\":true,\"data\":{\"id\":1,\"servicio\":\"pedido-service\",\"operacion\":\"CREAR_PEDIDO\",\"usuarioId\":\"42\",\"timestamp\":\"2026-07-12T10:30:00\",\"resultado\":\"OK\",\"detalle\":\"Pedido creado\"}}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request inválido"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno")
    })
    public ResponseEntity<ApiResponse<LogEntrada>> registrarLog(@Valid @RequestBody LogRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(logService.registrarLog(request));
    }

    @GetMapping
    @Operation(summary = "Consultar logs", description = "Consulta logs registrados con filtros opcionales por servicio y fecha inicial.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Logs consultados"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Parámetros inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno")
    })
    public ResponseEntity<ApiResponse<List<LogEntrada>>> consultarLogs(
            @Parameter(description = "Nombre del servicio emisor", example = "pedido-service") @RequestParam(required = false) String servicio,
            @Parameter(description = "Fecha inicial en formato ISO-8601", example = "2026-07-12T10:30:00") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde) {
        return ResponseEntity.ok(logService.consultarLogs(servicio, desde));
    }

    @GetMapping("/ping")
    @Operation(summary = "Healthcheck de log-service", description = "Verifica que el servicio de logs esté operativo.")
    @ApiResponses(@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Servicio activo"))
    public ResponseEntity<ApiResponse<String>> ping() {
        return ResponseEntity.ok(ApiResponse.success("log-service OK", "log-service OK"));
    }
}
