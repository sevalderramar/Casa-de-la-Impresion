package cl.duocuc.pedidoservice.dto;

import java.time.LocalDateTime;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos de respuesta de un pedido")
public class PedidoResponse {
    @Schema(description = "Numero del pedido", example = "1001")
    private Long numeroPedido;

    @Schema(description = "Identificador del cliente", example = "1")
    private Long clienteId;

    @Schema(description = "Estado actual del pedido", example = "COLA")
    private String estado;

    @Schema(description = "Tipo de despacho solicitado", example = "RM")
    private String tipoDespacho;

    @Schema(description = "Monto total del pedido", example = "7000.0")
    private Double monto;

    @Schema(description = "Fecha de creacion del pedido", example = "2026-05-22T10:00:00")
    private LocalDateTime fechaCreacion;

    @Schema(description = "Items incluidos en el pedido")
    private List<ItemPedidoResponse> items;
}
