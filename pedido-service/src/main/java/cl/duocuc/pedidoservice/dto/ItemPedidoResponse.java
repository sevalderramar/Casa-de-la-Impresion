package cl.duocuc.pedidoservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Item registrado dentro de un pedido")
public class ItemPedidoResponse {

    @Schema(description = "Identificador del item", example = "1")
    private Long id;

    @Schema(description = "Identificador del producto", example = "10")
    private Long productoId;

    @Schema(description = "Nombre del producto", example = "Resma Carta Blanca")
    private String nombreProducto;

    @Schema(description = "Cantidad solicitada", example = "2")
    private Integer cantidad;

    @Schema(description = "Precio unitario del producto", example = "3500.0")
    private Double precioUnitario;

    @Schema(description = "Subtotal del item", example = "7000.0")
    private Double subtotal;
}
