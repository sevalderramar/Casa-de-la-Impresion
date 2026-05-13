package cl.duocuc.despachoservice.dto;

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

    private Long id;
    private Long numeroPedido;
    private String tipoDespacho;
    private String transportista;
    private LocalDateTime fechaDespacho;
    private String trackingCode;
}

