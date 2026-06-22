package cl.duocuc.productoservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductoResponse {

    @Schema(description = "Identificador del producto", example = "10")
    private Long id;
    @Schema(description = "Nombre del producto", example = "Resma Carta Blanca")
    private String nombre;
    @Schema(description = "Descripción del producto", example = "Resma de papel blanco tamaño carta")
    private String descripcion;
    @Schema(description = "Categoría", example = "Papel")
    private String categoria;
    @Schema(description = "Precio unitario", example = "3500.0")
    private Double precio;
    @Schema(description = "Stock disponible", example = "120")
    private Integer stock;
    @Schema(description = "Fecha de creación", example = "2026-05-22T10:00:00")
    private LocalDateTime fechaCreacion;
}

