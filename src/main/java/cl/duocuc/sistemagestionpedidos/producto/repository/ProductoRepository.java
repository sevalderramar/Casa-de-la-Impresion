package cl.duocuc.sistemagestionpedidos.producto.repository;

import cl.duocuc.sistemagestionpedidos.producto.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Optional<Producto> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);

    List<Producto> findByCategoriaIgnoreCase(String categoria);
}

