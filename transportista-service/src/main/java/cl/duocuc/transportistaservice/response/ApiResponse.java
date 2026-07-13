package cl.duocuc.transportistaservice.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Formato estándar de respuesta de la API")
public class ApiResponse<T> {
    @Schema(description = "Mensaje descriptivo de la operación", example = "Transportista obtenido")
    private String mensaje;
    @Schema(description = "Payload de respuesta")
    private T data;
    @Schema(description = "Indica si la operación fue exitosa", example = "true")
    private boolean exitoso;
    @Schema(description = "Fecha y hora de generación de la respuesta", example = "2026-07-12T21:00:00")
    private LocalDateTime timestamp;

    public static <T> ApiResponse<T> success(String mensaje, T data) {
        return new ApiResponse<>(mensaje, data, true, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> error(String mensaje) {
        return new ApiResponse<>(mensaje, null, false, LocalDateTime.now());
    }
}
