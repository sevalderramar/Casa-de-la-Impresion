package cl.duocuc.metricaservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumenVentasResponseDTO {
    @Schema(description = "Fecha inicial", example = "2026-05-01")
    private LocalDate desde;
    @Schema(description = "Fecha final", example = "2026-05-31")
    private LocalDate hasta;
    @Schema(description = "Monto total vendido", example = "2500000.0")
    private Double montoTotal;
    @Schema(description = "Cantidad total de pedidos", example = "42")
    private Integer cantidadPedidos;
}
