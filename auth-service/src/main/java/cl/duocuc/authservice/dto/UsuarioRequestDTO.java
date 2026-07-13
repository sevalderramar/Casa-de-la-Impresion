package cl.duocuc.authservice.dto;

import cl.duocuc.authservice.entity.Usuario;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para crear o actualizar un usuario")
public class UsuarioRequestDTO {

    @NotBlank
    @Schema(description = "Nombre del usuario", example = "Administrador", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;

    @NotBlank
    @Email
    @Schema(description = "Email único del usuario", example = "admin@empresa.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank
    @Schema(description = "Contraseña en texto plano; el servicio la almacena hasheada", example = "pass123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;  // texto plano → se hashea en el servicio

    @Schema(description = "Rol del usuario. Si no se informa, se usa ENCARGADO_PEDIDOS", example = "ADMIN")
    private Usuario.Rol rol;  // opcional; default = ENCARGADO_PEDIDOS

    @Schema(description = "Estado activo del usuario. Si no se informa al crear, se usa true", example = "true")
    private Boolean activo;   // opcional; default = true
}
