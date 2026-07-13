package cl.duocuc.estadoservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para registrar un cambio de estado de pedido")
public class CambioEstadoRequest {

    @NotNull(message = "numeroPedido es obligatorio")
    @Schema(description = "Número del pedido asociado al cambio", example = "1001", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long numeroPedido;

    @Schema(description = "Estado anterior del pedido", example = "CREADO")
    private String estadoAnterior;

    @NotBlank(message = "estadoNuevo es obligatorio")
    @Schema(description = "Nuevo estado del pedido", example = "EN_PREPARACION", requiredMode = Schema.RequiredMode.REQUIRED)
    private String estadoNuevo;

    @Schema(description = "Observación opcional del cambio", example = "Pedido enviado a preparación")
    private String observacion;
}
