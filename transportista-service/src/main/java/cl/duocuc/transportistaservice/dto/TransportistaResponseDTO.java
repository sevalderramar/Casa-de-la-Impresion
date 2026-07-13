package cl.duocuc.transportistaservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Transportista retornado por la API")
public class TransportistaResponseDTO {
    @Schema(description = "ID del transportista", example = "1")
    private Long id;
    @Schema(description = "Nombre comercial del transportista", example = "Blue Express")
    private String nombre;
    @Schema(description = "Código interno único del transportista", example = "TR-001")
    private String codigoInterno;
    @Schema(description = "Datos de contacto", example = "contacto@blue.cl")
    private String contacto;
    @Schema(description = "Regiones cubiertas", example = "RM,V")
    private String regionesCobertura;
    @Schema(description = "Indica si el transportista está activo", example = "true")
    private boolean activo;
}
