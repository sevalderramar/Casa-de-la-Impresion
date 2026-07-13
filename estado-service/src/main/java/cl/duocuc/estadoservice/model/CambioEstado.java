package cl.duocuc.estadoservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "cambios_estado")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que almacena el historial de cambios de estado de un pedido")
public class CambioEstado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID del cambio de estado", example = "1")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Número del pedido", example = "1001")
    private Long numeroPedido;

    @Column(name = "estado_anterior")
    @Schema(description = "Estado anterior del pedido", example = "CREADO")
    private String estadoAnterior;

    @Column(nullable = false)
    @Schema(description = "Nuevo estado del pedido", example = "EN_PREPARACION")
    private String estadoNuevo;

    @Column(nullable = false)
    @Schema(description = "Fecha y hora del cambio", example = "2026-07-12T10:30:00")
    private LocalDateTime fechaCambio;

    @Column(length = 500)
    @Schema(description = "Observación del cambio", example = "Pedido enviado a preparación")
    private String observacion;

    @PrePersist
    protected void onCreate() {
        if (this.fechaCambio == null) {
            this.fechaCambio = LocalDateTime.now();
        }
    }
}
