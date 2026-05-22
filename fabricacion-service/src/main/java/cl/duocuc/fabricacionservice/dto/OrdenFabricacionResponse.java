package cl.duocuc.fabricacion.dto;

import cl.duocuc.fabricacion.entity.EstadoFabricacion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrdenFabricacionResponse {
    private Long id;
    private Long numeroPedido;
    private EstadoFabricacion estadoFabricacion;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private String descripcionEstado;
    private String usuarioResponsable;
}

