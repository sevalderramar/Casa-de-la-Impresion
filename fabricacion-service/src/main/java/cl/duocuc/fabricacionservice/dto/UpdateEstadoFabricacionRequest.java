package cl.duocuc.fabricacion.dto;

import cl.duocuc.fabricacion.entity.EstadoFabricacion;
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
    @NotNull(message = "El nuevo estado es obligatorio")
    private EstadoFabricacion nuevoEstado;

    private String motivo;
    private String usuarioId;
}
