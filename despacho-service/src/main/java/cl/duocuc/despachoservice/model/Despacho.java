package cl.duocuc.despachoservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "despachos", uniqueConstraints = {
        @UniqueConstraint(name = "uk_despachos_numero_pedido", columnNames = "numero_pedido")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Despacho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_pedido", nullable = false, unique = true)
    private Long numeroPedido;

    @Column(name = "tipo_despacho", nullable = false)
    private String tipoDespacho;

    @Column(nullable = false)
    private String transportista;

    @Column(name = "fecha_despacho", nullable = false)
    private LocalDateTime fechaDespacho;

    @Column(name = "tracking_code", nullable = false)
    private String trackingCode;
}

