package cl.duocuc.transportistaservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Datos opcionales para actualizar un transportista")
public class TransportistaUpdateDTO {
    @Schema(description = "Nuevo nombre comercial del transportista", example = "Blue Express Actualizado")
    private String nombre;
    @Schema(description = "Nuevos datos de contacto", example = "nuevo@blue.cl")
    private String contacto;
    @Schema(description = "Nuevas regiones cubiertas", example = "RM,V,VI")
    private String regionesCobertura;
    @Schema(description = "Estado activo del transportista", example = "true")
    private Boolean activo;
}
