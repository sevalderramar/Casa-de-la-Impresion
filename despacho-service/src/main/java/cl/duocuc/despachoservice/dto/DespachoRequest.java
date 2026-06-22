package cl.duocuc.despachoservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "Número del pedido asociado", example = "1001")
    @NotNull(message = "numeroPedido es obligatorio")
    @Positive(message = "numeroPedido debe ser mayor que 0")
    private Long numeroPedido;

    @Schema(description = "Tipo de despacho", example = "RM", allowableValues = {"RETIRO", "RM", "REGION"})
    @NotBlank(message = "tipoDespacho es obligatorio")
    @Pattern(regexp = "RETIRO|RM|REGION", message = "tipoDespacho debe ser RETIRO, RM o REGION")
    private String tipoDespacho;

    @Schema(description = "Nombre del transportista", example = "Transporte Central")
    @NotBlank(message = "transportista es obligatorio")
    private String transportista;

    @Schema(description = "Código de seguimiento", example = "TRK-1001")
    @NotBlank(message = "trackingCode es obligatorio")
    private String trackingCode;
}

