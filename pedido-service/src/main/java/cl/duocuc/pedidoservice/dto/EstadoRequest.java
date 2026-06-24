package cl.duocuc.pedidoservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para actualizar el estado de un pedido")
public class EstadoRequest {

    @NotBlank(message = "El estado es obligatorio")
    @Schema(description = "Nuevo estado del pedido", example = "PRODUCCION", allowableValues = {"COLA", "PRODUCCION", "LISTO", "DESPACHADO", "ENTREGADO"})
    private String estado;

}

