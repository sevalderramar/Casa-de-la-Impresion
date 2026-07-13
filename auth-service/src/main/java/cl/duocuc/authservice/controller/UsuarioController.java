package cl.duocuc.authservice.controller;

import cl.duocuc.authservice.dto.ApiResponse;
import cl.duocuc.authservice.dto.UsuarioRequestDTO;
import cl.duocuc.authservice.dto.UsuarioResponseDTO;
import cl.duocuc.authservice.service.UsuarioService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auth/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Gestión de usuarios del servicio de autenticación")
public class UsuarioController {

    private final UsuarioService usuarioService;

    // GET /api/auth/usuarios — solo ADMIN
    @GetMapping
    @Operation(summary = "Listar usuarios", description = "Obtiene los usuarios registrados en el dominio de autenticación.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuarios obtenidos",
                    content = @Content(schema = @Schema(implementation = UsuarioResponseDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno")
    })
    public ResponseEntity<ApiResponse<List<UsuarioResponseDTO>>> listar() {
        return ResponseEntity.ok(
            ApiResponse.success("Usuarios obtenidos", usuarioService.listar()));
    }

    // POST /api/auth/usuarios — solo ADMIN
    @PostMapping
    @Operation(summary = "Crear usuario", description = "Registra un nuevo usuario y almacena la contraseña hasheada.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Usuario creado",
                    content = @Content(schema = @Schema(implementation = UsuarioResponseDTO.class),
                            examples = @ExampleObject(value = "{\"mensaje\":\"Usuario creado\",\"exitoso\":true,\"data\":{\"id\":1,\"nombre\":\"Administrador\",\"email\":\"admin@empresa.com\",\"rol\":\"ADMIN\",\"activo\":true}}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request inválido"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Email duplicado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno")
    })
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> crear(
            @Valid @RequestBody UsuarioRequestDTO request) {
        UsuarioResponseDTO creado = usuarioService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Usuario creado", creado));
    }

    // PUT /api/auth/usuarios/{id} — solo ADMIN
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario", description = "Actualiza los datos de un usuario existente.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuario actualizado",
                    content = @Content(schema = @Schema(implementation = UsuarioResponseDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request inválido"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno")
    })
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> actualizar(
            @Parameter(description = "ID del usuario", example = "1") @PathVariable Long id,
            @Valid @RequestBody UsuarioRequestDTO request) {
        UsuarioResponseDTO actualizado = usuarioService.actualizar(id, request);
        return ResponseEntity.ok(
            ApiResponse.success("Usuario actualizado", actualizado));
    }
}
