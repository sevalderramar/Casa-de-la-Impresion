package cl.duocuc.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Credenciales utilizadas para iniciar sesión")
public class LoginRequestDTO {

    @NotBlank
    @Email
    @Schema(description = "Email del usuario", example = "admin@empresa.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank
    @Schema(description = "Contraseña en texto plano enviada para autenticación", example = "pass123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;   // contraseña en texto plano
}
