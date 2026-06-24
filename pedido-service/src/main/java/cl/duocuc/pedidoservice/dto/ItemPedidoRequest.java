package cl.duocuc.pedidoservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Item solicitado dentro de un pedido")
public class ItemPedidoRequest {

    @NotNull(message = "El productoId es obligatorio")
    @Schema(description = "Identificador del producto", example = "10")
    private Long productoId;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a cero")
    @Schema(description = "Cantidad solicitada", example = "2")
    private Integer cantidad;
}
