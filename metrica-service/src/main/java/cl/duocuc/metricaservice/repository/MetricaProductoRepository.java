package cl.duocuc.metricaservice.repository;

import cl.duocuc.metricaservice.entity.MetricaProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetricaProductoRepository extends JpaRepository<MetricaProducto, Long> {
}
