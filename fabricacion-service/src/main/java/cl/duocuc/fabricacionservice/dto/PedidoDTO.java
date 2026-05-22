package cl.duocuc.fabricacion.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public record PedidoDTO(
    Long id,
    String numeroPedido,
    Long clienteId,
    String estado,
    BigDecimal monto,
    String tipoDespacho,
    LocalDateTime fechaCreacion,
    LocalDateTime fechaActualizacion
) {
}
