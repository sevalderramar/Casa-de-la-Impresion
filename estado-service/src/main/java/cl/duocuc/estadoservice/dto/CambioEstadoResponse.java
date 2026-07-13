package cl.duocuc.estadoservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Cambio de estado registrado para un pedido")
public class CambioEstadoResponse {

    @Schema(description = "ID del cambio de estado", example = "1")
    private Long id;
    @Schema(description = "Número del pedido", example = "1001")
    private Long numeroPedido;
    @Schema(description = "Estado anterior del pedido", example = "CREADO")
    private String estadoAnterior;
    @Schema(description = "Nuevo estado del pedido", example = "EN_PREPARACION")
    private String estadoNuevo;
    @Schema(description = "Fecha y hora del cambio", example = "2026-07-12T10:30:00")
    private LocalDateTime fechaCambio;
    @Schema(description = "Observación del cambio", example = "Pedido enviado a preparación")
    private String observacion;
}
