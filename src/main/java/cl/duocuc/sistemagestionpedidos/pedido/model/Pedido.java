package cl.duocuc.sistemagestionpedidos.pedido.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos", uniqueConstraints = {
        @UniqueConstraint(name = "uk_pedido_numero", columnNames = "numero_pedido")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_pedido", nullable = false, unique = true)
    private String numeroPedido;

    @Column(nullable = false)
    private Long clienteId;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false)
    private String tipoDespacho;

    private Double monto;
    private LocalDateTime fechaCreacion;
    // eliminado estadoRegistro - no se usa eliminación lógica

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> items = new ArrayList<>();
}
