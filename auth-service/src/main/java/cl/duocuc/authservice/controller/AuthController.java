package cl.duocuc.authservice.controller;

import cl.duocuc.authservice.dto.ApiResponse;
import cl.duocuc.authservice.dto.LoginRequestDTO;
import cl.duocuc.authservice.dto.LoginResponseDTO;
import cl.duocuc.authservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Login, logout lógico y verificación del servicio de autenticación")
public class AuthController {

    private final AuthService authService;

    // POST /api/auth/login — público, no requiere token
    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Valida credenciales de usuario y retorna un token JWT firmado.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login exitoso",
                    content = @Content(schema = @Schema(implementation = LoginResponseDTO.class),
                            examples = @ExampleObject(value = "{\"mensaje\":\"Login exitoso\",\"exitoso\":true,\"data\":{\"token\":\"eyJhbGciOiJIUzI1NiJ9...\",\"tipo\":\"Bearer\",\"email\":\"admin@empresa.com\",\"rol\":\"ADMIN\",\"expiracion\":1760000000000}}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request inválido"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario autenticado sin rol configurado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno")
    })
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO request) {
        LoginResponseDTO response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login exitoso", response));
    }

    // POST /api/auth/logout — requiere token válido
    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión", description = "Confirma el logout lógico. Con JWT stateless el cliente debe descartar el token.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Logout confirmado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno")
    })
    public ResponseEntity<ApiResponse<Void>> logout() {
        // Con JWT stateless el logout es del lado del cliente (descartar el token).
        // El servidor confirma la recepción.
        return ResponseEntity.ok(
            ApiResponse.success("Sesión cerrada. Descarta el token en el cliente.", null));
    }

    // GET /api/auth/ping — healthcheck público
    @GetMapping("/ping")
    @Operation(summary = "Healthcheck de auth-service", description = "Verifica que el servicio de autenticación esté operativo.")
    @ApiResponses(@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Servicio activo"))
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("auth-service OK");
    }
}
