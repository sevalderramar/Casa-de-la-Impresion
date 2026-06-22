package cl.duocuc.metricaservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricaClienteResponseDTO {
    @Schema(description = "ID del cliente", example = "1")
    private Long clienteId;
    @Schema(description = "Nombre del cliente", example = "María Pérez González")
    private String nombreCliente;
    @Schema(description = "Monto total acumulado", example = "150000.0")
    private Double montoTotal;
    @Schema(description = "Cantidad de pedidos", example = "5")
    private Integer cantidadPedidos;
    @Schema(description = "Frecuencia anual estimada", example = "2.5")
    private Double frecuenciaAnual;
}
