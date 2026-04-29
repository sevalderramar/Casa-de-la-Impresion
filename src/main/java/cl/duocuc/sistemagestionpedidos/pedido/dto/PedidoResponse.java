package cl.duocuc.sistemagestionpedidos.pedido.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponse {

    private Long id;
    private String numeroPedido;
    private Long clienteId;
    private String estado;
    private String tipoDespacho;
    private Double monto;
    private LocalDateTime fechaCreacion;
    private List<ItemPedidoResponse> items;
}
