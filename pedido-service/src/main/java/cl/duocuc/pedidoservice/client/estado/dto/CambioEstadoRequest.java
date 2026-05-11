package cl.duocuc.pedidoservice.client.estado.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CambioEstadoRequest {
    private Long numeroPedido;
    private String estadoAnterior;
    private String estadoNuevo;
    private String observacion;
}

