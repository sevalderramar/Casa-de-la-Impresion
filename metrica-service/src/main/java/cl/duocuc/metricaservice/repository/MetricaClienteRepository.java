package cl.duocuc.metricaservice.repository;

import cl.duocuc.metricaservice.entity.MetricaCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetricaClienteRepository extends JpaRepository<MetricaCliente, Long> {
}
