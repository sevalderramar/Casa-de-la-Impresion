package cl.duocuc.logservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos para registrar un evento operacional")
public class LogRequestDTO {

    @NotBlank
    @Schema(description = "Servicio que genera el evento", example = "pedido-service", requiredMode = Schema.RequiredMode.REQUIRED)
    private String servicio;

    @NotBlank
    @Schema(description = "Operación realizada", example = "CREAR_PEDIDO", requiredMode = Schema.RequiredMode.REQUIRED)
    private String operacion;

    @Schema(description = "Identificador del usuario relacionado al evento", example = "42")
    private String usuarioId;

    @NotBlank
    @Schema(description = "Resultado de la operación", example = "OK", requiredMode = Schema.RequiredMode.REQUIRED)
    private String resultado;

    @Schema(description = "Detalle opcional del evento", example = "Pedido creado correctamente")
    private String detalle;
}
