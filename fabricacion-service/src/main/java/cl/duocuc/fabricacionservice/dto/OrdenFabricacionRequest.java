package cl.duocuc.fabricacion.dto;

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

    @NotNull(message = "El numeroPedido es obligatorio")
    private Long numeroPedido;

    @NotBlank(message = "El usuario responsable es obligatorio")
    @Size(max = 255)
    private String usuarioResponsable;

    @Size(max = 500)
    private String descripcionEstado;
}
