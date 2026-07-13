package cl.duocuc.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Respuesta de autenticación con token JWT")
public class LoginResponseDTO {

    @Schema(description = "Token JWT firmado", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;        // JWT firmado
    @Schema(description = "Tipo de token", example = "Bearer")
    private String tipo;         // siempre "Bearer"
    @Schema(description = "Email autenticado", example = "admin@empresa.com")
    private String email;
    @Schema(description = "Rol del usuario", example = "ADMIN")
    private String rol;
    @Schema(description = "Timestamp Unix de expiración", example = "1760000000000")
    private long   expiracion;   // timestamp Unix de expiración
}
