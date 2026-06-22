package cl.duocuc.clienteservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponse {

    @Schema(description = "Identificador del cliente", example = "1")
    private Long id;
    @Schema(description = "Nombre completo", example = "María Pérez González")
    private String nombre;
    @Schema(description = "RUT", example = "12.345.678-9")
    private String rut;
    @Schema(description = "Correo electrónico", example = "maria.perez@empresa.cl")
    private String email;
    @Schema(description = "Teléfono", example = "+56 9 8765 4321")
    private String telefono;
    @Schema(description = "Dirección", example = "Av. Siempre Viva 123")
    private String direccion;
    @Schema(description = "Comuna", example = "Santiago")
    private String comuna;
    @Schema(description = "Región", example = "Metropolitana")
    private String region;
    @Schema(description = "Fecha de registro", example = "2026-05-22")
    private LocalDate fechaRegistro;
}
