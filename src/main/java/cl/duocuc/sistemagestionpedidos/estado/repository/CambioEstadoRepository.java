package cl.duocuc.sistemagestionpedidos.estado.repository;

import cl.duocuc.sistemagestionpedidos.estado.model.CambioEstado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CambioEstadoRepository extends JpaRepository<CambioEstado, Long> {

    List<CambioEstado> findByNumeroPedido(Long numeroPedido);

    List<CambioEstado> findByNumeroPedidoOrderByFechaCambioAsc(Long numeroPedido);
}

