package cl.duocuc.despachoservice.repository;

import cl.duocuc.despachoservice.model.Despacho;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DespachoRepository extends JpaRepository<Despacho, Long> {

    Optional<Despacho> findByNumeroPedido(Long numeroPedido);

    List<Despacho> findByTipoDespacho(String tipoDespacho);

    boolean existsByNumeroPedido(Long numeroPedido);
}

