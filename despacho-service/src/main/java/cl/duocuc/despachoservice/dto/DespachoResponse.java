package cl.duocuc.despachoservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DespachoResponse {

    @Schema(description = "Identificador del despacho", example = "1")
    private Long id;
    @Schema(description = "Número del pedido", example = "1001")
    private Long numeroPedido;
    @Schema(description = "Tipo de despacho", example = "RM")
    private String tipoDespacho;
    @Schema(description = "Transportista asignado", example = "Transporte Central")
    private String transportista;
    @Schema(description = "Fecha de despacho", example = "2026-05-22T10:00:00")
    private LocalDateTime fechaDespacho;
    @Schema(description = "Código de seguimiento", example = "TRK-1001")
    private String trackingCode;
}

