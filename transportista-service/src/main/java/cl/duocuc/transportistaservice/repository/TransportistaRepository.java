package cl.duocuc.transportistaservice.repository;

import cl.duocuc.transportistaservice.model.Transportista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransportistaRepository extends JpaRepository<Transportista, Long> {
    boolean existsByCodigoInterno(String codigoInterno);
    List<Transportista> findByActivoTrue();
}
