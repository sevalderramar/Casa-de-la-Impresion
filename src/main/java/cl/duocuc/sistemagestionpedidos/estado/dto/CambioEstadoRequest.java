package cl.duocuc.sistemagestionpedidos.estado.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CambioEstadoRequest {

    @NotNull(message = "numeroPedido es obligatorio")
    private Long numeroPedido;

    private String estadoAnterior;

    @NotBlank(message = "estadoNuevo es obligatorio")
    private String estadoNuevo;

    private String observacion;
}

