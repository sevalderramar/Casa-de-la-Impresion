package cl.duocuc.productoservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductoRequest {

    @Schema(description = "Nombre del producto", example = "Resma Carta Blanca")
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Schema(description = "Descripción del producto", example = "Resma de papel blanco tamaño carta")
    private String descripcion;

    @Schema(description = "Categoría del producto", example = "Papel")
    @NotBlank(message = "La categoria es obligatoria")
    private String categoria;

    @Schema(description = "Precio unitario", example = "3500.0")
    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    private Double precio;

    @Schema(description = "Stock disponible", example = "120")
    @NotNull(message = "El stock es obligatorio")
    @PositiveOrZero(message = "El stock no puede ser negativo")
    private Integer stock;
}

