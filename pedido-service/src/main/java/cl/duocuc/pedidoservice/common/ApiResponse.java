package cl.duocuc.pedidoservice.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse<T> {

    private String mensaje;
    private T data;
}