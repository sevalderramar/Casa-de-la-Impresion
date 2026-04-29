package cl.duocuc.sistemagestionpedidos.pedido.repository;

import cl.duocuc.sistemagestionpedidos.pedido.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    Optional<Pedido> findByNumeroPedido(String numeroPedido);
    boolean existsByNumeroPedido(String numeroPedido);
    List<Pedido> findByEstado(String estado);
    List<Pedido> findByClienteId(Long clienteId);
    List<Pedido> findByEstadoAndTipoDespacho(String estado, String tipoDespacho);

}
