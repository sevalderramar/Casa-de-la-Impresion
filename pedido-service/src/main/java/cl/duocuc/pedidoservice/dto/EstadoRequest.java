package cl.duocuc.pedidoservice.dto;

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

    @NotBlank(message = "El estado es obligatorio")
    private String estado;

}

