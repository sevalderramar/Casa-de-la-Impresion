package cl.duocuc.logservice.service;

import cl.duocuc.logservice.dto.ApiResponse;
import cl.duocuc.logservice.dto.LogRequestDTO;
import cl.duocuc.logservice.entity.LogEntrada;
import java.time.LocalDateTime;
import java.util.List;

public interface LogService {

    ApiResponse<LogEntrada> registrarLog(LogRequestDTO request);

    ApiResponse<List<LogEntrada>> consultarLogs(String servicio, LocalDateTime desde);
}