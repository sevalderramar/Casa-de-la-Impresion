package cl.duocuc.sistemagestionpedidos.estado.model;

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
public class CambioEstado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long numeroPedido;

    @Column(name = "estado_anterior")
    private String estadoAnterior;

    @Column(nullable = false)
    private String estadoNuevo;

    @Column(nullable = false)
    private LocalDateTime fechaCambio;

    @Column(length = 500)
    private String observacion;

    @PrePersist
    protected void onCreate() {
        if (this.fechaCambio == null) {
            this.fechaCambio = LocalDateTime.now();
        }
    }
}

