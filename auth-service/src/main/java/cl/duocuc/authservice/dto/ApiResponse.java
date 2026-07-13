package cl.duocuc.authservice.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Formato estándar de respuesta de la API")
public class ApiResponse<T> {

    @Schema(description = "Mensaje descriptivo de la operación", example = "Login exitoso")
    private String mensaje;
    @Schema(description = "Payload de respuesta")
    private T data;
    @Schema(description = "Indica si la operación fue exitosa", example = "true")
    private boolean exitoso;
    @Schema(description = "Fecha y hora de generación de la respuesta", example = "2026-07-12T21:00:00")
    private LocalDateTime timestamp;

    public static <T> ApiResponse<T> success(String mensaje, T data) {
        return ApiResponse.<T>builder()
            .mensaje(mensaje)
            .data(data)
            .exitoso(true)
            .timestamp(LocalDateTime.now())
            .build();
    }

    public static <T> ApiResponse<T> error(String mensaje) {
        return ApiResponse.<T>builder()
            .mensaje(mensaje)
            .data(null)
            .exitoso(false)
            .timestamp(LocalDateTime.now())
            .build();
    }

    public static <T> ApiResponse<T> error(String mensaje, T data) {
        return ApiResponse.<T>builder()
            .mensaje(mensaje)
            .data(data)
            .exitoso(false)
            .timestamp(LocalDateTime.now())
            .build();
    }
}
