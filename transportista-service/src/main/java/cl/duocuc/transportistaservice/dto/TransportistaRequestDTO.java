package cl.duocuc.transportistaservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Datos requeridos para registrar un transportista")
public class TransportistaRequestDTO {
    @NotBlank
    @Schema(description = "Nombre comercial del transportista", example = "Blue Express", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;
    
    @NotBlank
    @Schema(description = "Código interno único del transportista", example = "TR-001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String codigoInterno;
    
    @Schema(description = "Datos de contacto del transportista", example = "contacto@blue.cl")
    private String contacto;
    
    @Schema(description = "Regiones cubiertas por el transportista", example = "RM,V")
    private String regionesCobertura;
}
