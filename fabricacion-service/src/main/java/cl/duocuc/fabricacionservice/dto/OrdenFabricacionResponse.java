package cl.duocuc.fabricacion.dto;

import cl.duocuc.fabricacion.entity.EstadoFabricacion;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "Identificador de la orden", example = "1")
    private Long id;
    @Schema(description = "Número del pedido", example = "1001")
    private Long numeroPedido;
    @Schema(description = "Estado de fabricación", example = "EN_PROCESO")
    private EstadoFabricacion estadoFabricacion;
    @Schema(description = "Fecha de inicio", example = "2026-05-22T10:00:00")
    private LocalDateTime fechaInicio;
    @Schema(description = "Fecha de término", example = "2026-05-22T15:30:00")
    private LocalDateTime fechaFin;
    @Schema(description = "Fecha de creación", example = "2026-05-22T10:00:00")
    private LocalDateTime fechaCreacion;
    @Schema(description = "Fecha de actualización", example = "2026-05-22T11:00:00")
    private LocalDateTime fechaActualizacion;
    @Schema(description = "Descripción del estado", example = "Inicio de fabricación")
    private String descripcionEstado;
    @Schema(description = "Usuario responsable", example = "operador-01")
    private String usuarioResponsable;
}

