package cl.duocuc.metricaservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricaProductoResponseDTO {
    @Schema(description = "ID del producto", example = "10")
    private Long productoId;
    @Schema(description = "Nombre del producto", example = "Resma Carta Blanca")
    private String nombre;
    @Schema(description = "Total vendido", example = "250")
    private Integer totalVendido;
}
