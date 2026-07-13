package cl.duocuc.logservice.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Evento operacional registrado por el sistema")
public class LogEntrada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID del log", example = "1")
    private Long id;

    @NotBlank
    @Column(nullable = false)
    @Schema(description = "Servicio que generó el evento", example = "pedido-service")
    private String servicio;

    @NotBlank
    @Column(nullable = false)
    @Schema(description = "Operación realizada", example = "CREAR_PEDIDO")
    private String operacion;

    @Schema(description = "Identificador del usuario relacionado al evento", example = "42")
    private String usuarioId;

    @Column(nullable = false)
    @Schema(description = "Fecha y hora del evento", example = "2026-07-12T10:30:00")
    private LocalDateTime timestamp;

    @NotBlank
    @Column(nullable = false)
    @Schema(description = "Resultado de la operación", example = "OK")
    private String resultado;

    @Column(length = 1000)
    @Schema(description = "Detalle opcional del evento", example = "Pedido creado correctamente")
    private String detalle;

    @PrePersist
    protected void beforeCreate() {
        normalize();
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void beforeUpdate() {
        normalize();
    }

    private void normalize() {
        servicio = normalizeText(servicio);
        operacion = normalizeText(operacion);
        usuarioId = normalizeNullableText(usuarioId);
        resultado = normalizeText(resultado);
        detalle = normalizeNullableText(detalle);
    }

    private String normalizeText(String value) {
        return value == null ? null : value.trim();
    }

    private String normalizeNullableText(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
