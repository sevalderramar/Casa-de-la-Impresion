package cl.duocuc.despachoservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DespachoRequest {

    @NotNull(message = "numeroPedido es obligatorio")
    @Positive(message = "numeroPedido debe ser mayor que 0")
    private Long numeroPedido;

    @NotBlank(message = "tipoDespacho es obligatorio")
    @Pattern(regexp = "RETIRO|RM|REGION", message = "tipoDespacho debe ser RETIRO, RM o REGION")
    private String tipoDespacho;

    @NotBlank(message = "transportista es obligatorio")
    private String transportista;

    @NotBlank(message = "trackingCode es obligatorio")
    private String trackingCode;
}

