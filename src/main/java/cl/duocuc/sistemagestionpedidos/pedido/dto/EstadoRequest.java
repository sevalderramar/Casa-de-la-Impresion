package cl.duocuc.sistemagestionpedidos.pedido.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstadoRequest {

    @NotBlank(message = "estado es obligatorio")
    private String estado;

}

