package cl.duocuc.pedidoservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos requeridos para crear un pedido")
public class PedidoRequest {

    @NotNull(message = "El clienteId es obligatorio")
    @Schema(description = "Identificador del cliente", example = "1")
    private Long clienteId;

    @NotBlank(message = "El estado es obligatorio")
    @Schema(description = "Estado inicial del pedido", example = "COLA")
    private String estado;

    @NotBlank(message = "El tipo de despacho es obligatorio")
    @Schema(description = "Tipo de despacho solicitado", example = "RM")
    private String tipoDespacho;

    @Valid
    @NotEmpty(message = "El pedido debe tener al menos un item")
    @Schema(description = "Items incluidos en el pedido")
    private List<ItemPedidoRequest> items;
}
