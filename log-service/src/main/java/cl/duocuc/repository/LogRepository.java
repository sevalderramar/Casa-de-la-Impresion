package cl.duocuc.logservice.repository;

import cl.duocuc.logservice.entity.LogEntrada;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<LogEntrada, Long> {

    List<LogEntrada> findByServicio(String servicio);

    List<LogEntrada> findByTimestampAfter(LocalDateTime desde);

    List<LogEntrada> findByServicioAndTimestampAfter(String servicio, LocalDateTime desde);
}