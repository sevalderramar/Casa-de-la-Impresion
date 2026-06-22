package cl.duocuc.fabricacion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrdenFabricacionRequest {

    @Schema(description = "Número del pedido asociado", example = "1001")
    @NotNull(message = "El numeroPedido es obligatorio")
    private Long numeroPedido;

    @Schema(description = "Usuario responsable de la fabricación", example = "operador-01")
    @NotBlank(message = "El usuario responsable es obligatorio")
    @Size(max = 255)
    private String usuarioResponsable;

    @Schema(description = "Descripción inicial del estado", example = "Inicio de fabricación")
    @Size(max = 500)
    private String descripcionEstado;
}
