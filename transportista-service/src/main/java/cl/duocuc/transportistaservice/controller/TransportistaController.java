package cl.duocuc.transportistaservice.controller;

import cl.duocuc.transportistaservice.dto.TransportistaRequestDTO;
import cl.duocuc.transportistaservice.dto.TransportistaResponseDTO;
import cl.duocuc.transportistaservice.dto.TransportistaUpdateDTO;
import cl.duocuc.transportistaservice.response.ApiResponse;
import cl.duocuc.transportistaservice.service.TransportistaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transportistas")
@RequiredArgsConstructor
@Tag(name = "Transportistas", description = "Administración de transportistas disponibles para despacho")
public class TransportistaController {

    private final TransportistaService transportistaService;

    @PostMapping
    @Operation(summary = "Crear transportista", description = "Registra un nuevo transportista activo para operaciones de despacho.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Transportista registrado",
                    content = @Content(schema = @Schema(implementation = TransportistaResponseDTO.class),
                            examples = @ExampleObject(value = "{\"mensaje\":\"Transportista registrado correctamente\",\"exitoso\":true,\"data\":{\"id\":1,\"nombre\":\"Blue Express\",\"codigoInterno\":\"TR-001\",\"contacto\":\"contacto@blue.cl\",\"regionesCobertura\":\"RM,V\",\"activo\":true}}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request inválido"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Código interno duplicado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno")
    })
    public ResponseEntity<ApiResponse<TransportistaResponseDTO>> registrarTransportista(
            @Valid @RequestBody TransportistaRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Transportista registrado correctamente", transportistaService.crearTransportista(request)));
    }

    @GetMapping
    @Operation(summary = "Listar transportistas activos", description = "Obtiene el listado de transportistas activos registrados.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Listado obtenido"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno")
    })
    public ResponseEntity<ApiResponse<List<TransportistaResponseDTO>>> listarTransportistasActivos() {
        return ResponseEntity.ok(ApiResponse.success("Listado obtenido", transportistaService.listarActivos()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener transportista por ID", description = "Consulta un transportista activo por su identificador.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Transportista obtenido"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Transportista no encontrado o inactivo"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno")
    })
    public ResponseEntity<ApiResponse<TransportistaResponseDTO>> obtenerTransportista(
            @Parameter(description = "ID del transportista", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Transportista obtenido", transportistaService.obtenerPorId(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar transportista", description = "Actualiza parcialmente los datos de un transportista existente.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Transportista actualizado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request inválido"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Transportista no encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno")
    })
    public ResponseEntity<ApiResponse<TransportistaResponseDTO>> actualizarTransportista(
            @Parameter(description = "ID del transportista", example = "1") @PathVariable Long id,
            @Valid @RequestBody TransportistaUpdateDTO request) {
        return ResponseEntity.ok(ApiResponse.success("Transportista actualizado", transportistaService.actualizarTransportista(id, request)));
    }

    @GetMapping("/ping")
    @Operation(summary = "Healthcheck de transportista-service", description = "Verifica que el servicio de transportistas esté operativo.")
    @ApiResponses(@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Servicio activo"))
    public ResponseEntity<ApiResponse<Void>> ping() {
        return ResponseEntity.ok(ApiResponse.success("transportista-service activo", null));
    }
}
