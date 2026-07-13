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
@Schema(description = "Usuario retornado por la API sin exponer contraseña ni hash")
public class UsuarioResponseDTO {

    @Schema(description = "ID del usuario", example = "1")
    private Long    id;
    @Schema(description = "Nombre del usuario", example = "Administrador")
    private String  nombre;
    @Schema(description = "Email del usuario", example = "admin@empresa.com")
    private String  email;
    @Schema(description = "Rol del usuario", example = "ADMIN")
    private String  rol;
    @Schema(description = "Indica si el usuario está activo", example = "true")
    private boolean activo;
    // NUNCA incluir password ni hash
}
