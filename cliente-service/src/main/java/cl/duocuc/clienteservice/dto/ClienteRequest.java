package cl.duocuc.clienteservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClienteRequest {

    @Schema(description = "Nombre completo del cliente", example = "María Pérez González")
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Schema(description = "RUT del cliente", example = "12.345.678-9")
    @NotBlank(message = "El RUT es obligatorio")
    private String rut;

    @Schema(description = "Correo electrónico del cliente", example = "maria.perez@empresa.cl")
    @Email(message = "El email debe tener un formato valido")
    private String email;

    @Schema(description = "Teléfono de contacto", example = "+56 9 8765 4321")
    @NotBlank(message = "El telefono es obligatorio")
    private String telefono;

    @Schema(description = "Dirección del cliente", example = "Av. Siempre Viva 123")
    @NotBlank(message = "La direccion es obligatoria")
    private String direccion;

    @Schema(description = "Comuna", example = "Santiago")
    @NotBlank(message = "La comuna es obligatoria")
    private String comuna;

    @Schema(description = "Región", example = "Metropolitana")
    @NotBlank(message = "La region es obligatoria")
    private String region;
}
