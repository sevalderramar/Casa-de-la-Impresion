package cl.duocuc.pedidoservice.repository;

import cl.duocuc.pedidoservice.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    Optional<Pedido> findByNumeroPedido(Long numeroPedido);
    boolean existsByNumeroPedido(Long numeroPedido);
    List<Pedido> findByClienteId(Long clienteId);

}
