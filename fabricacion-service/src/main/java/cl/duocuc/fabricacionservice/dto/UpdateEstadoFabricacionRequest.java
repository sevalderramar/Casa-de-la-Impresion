package cl.duocuc.fabricacion.dto;

import cl.duocuc.fabricacion.entity.EstadoFabricacion;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEstadoFabricacionRequest {

    @Schema(description = "Nuevo estado de fabricación", example = "TERMINADO")
    @NotNull(message = "El nuevo estado es obligatorio")
    private EstadoFabricacion nuevoEstado;
    @Schema(description = "Motivo del cambio", example = "Control de calidad aprobado")
    private String motivo;
    @Schema(description = "Usuario que registra el cambio", example = "operador-01")
    private String usuarioId;
}
