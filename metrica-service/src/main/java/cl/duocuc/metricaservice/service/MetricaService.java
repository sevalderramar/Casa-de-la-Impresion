package cl.duocuc.metricaservice.service;

import cl.duocuc.metricaservice.dto.MetricaClienteResponseDTO;
import cl.duocuc.metricaservice.dto.MetricaProductoResponseDTO;
import cl.duocuc.metricaservice.dto.ResumenVentasResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface MetricaService {
    MetricaClienteResponseDTO obtenerMetricasCliente(Long clienteId);
    List<MetricaClienteResponseDTO> obtenerRankingClientes(Integer limite);
    List<MetricaProductoResponseDTO> obtenerTopProductos(LocalDate desde, LocalDate hasta, Integer limite);
    ResumenVentasResponseDTO obtenerResumenVentas(LocalDate desde, LocalDate hasta);
}
