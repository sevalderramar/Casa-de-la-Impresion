package cl.duocuc.sistemagestionpedidos.pedido.dto;

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
public class PedidoRequest {

    @NotBlank(message = "El numero de pedido es obligatorio")
    private String numeroPedido;

    @NotNull(message = "El clienteId es obligatorio")
    private Long clienteId;

    @NotBlank(message = "El estado es obligatorio")
    private String estado;

    @NotBlank(message = "El tipo de despacho es obligatorio")
    private String tipoDespacho;

    @Valid
    @NotEmpty(message = "El pedido debe tener al menos un item")
    private List<ItemPedidoRequest> items;
}
