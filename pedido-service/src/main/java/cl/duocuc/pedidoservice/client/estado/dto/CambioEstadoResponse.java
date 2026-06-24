package cl.duocuc.pedidoservice.client.estado.dto;

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
@Schema(description = "Cambio de estado registrado para un pedido")
public class CambioEstadoResponse {
    @Schema(description = "Identificador del cambio de estado", example = "1")
    private Long id;

    @Schema(description = "Numero del pedido", example = "1001")
    private Long numeroPedido;

    @Schema(description = "Estado anterior del pedido", example = "COLA")
    private String estadoAnterior;

    @Schema(description = "Estado nuevo del pedido", example = "PRODUCCION")
    private String estadoNuevo;

    @Schema(description = "Fecha del cambio de estado", example = "2026-05-22T11:00:00")
    private LocalDateTime fechaCambio;

    @Schema(description = "Observacion asociada al cambio", example = "Cambio automatico de estado")
    private String observacion;
}
