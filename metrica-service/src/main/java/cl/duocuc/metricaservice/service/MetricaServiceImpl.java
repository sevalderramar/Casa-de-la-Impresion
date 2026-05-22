package cl.duocuc.metricaservice.service;

import cl.duocuc.metricaservice.client.ClienteFeignClient;
import cl.duocuc.metricaservice.client.PedidoFeignClient;
import cl.duocuc.metricaservice.dto.*;
import cl.duocuc.metricaservice.exception.ResourceNotFoundException;
import cl.duocuc.metricaservice.entity.MetricaCliente;
import cl.duocuc.metricaservice.entity.MetricaProducto;
import cl.duocuc.metricaservice.repository.MetricaClienteRepository;
import cl.duocuc.metricaservice.repository.MetricaProductoRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("null")
@Service
@RequiredArgsConstructor
@Slf4j
public class MetricaServiceImpl implements MetricaService {

    private final PedidoFeignClient pedidoFeignClient;
    private final ClienteFeignClient clienteFeignClient;
    private final MetricaClienteRepository metricaClienteRepository;
    private final MetricaProductoRepository metricaProductoRepository;

    @Override
    public MetricaClienteResponseDTO obtenerMetricasCliente(Long clienteId) {
        log.info("Calculando métricas para clienteId={}", clienteId);
        
        String nombreCliente = obtenerNombreClienteConFallback(clienteId);

        List<PedidoResponseDTO> pedidos = pedidoFeignClient.listarPedidos(clienteId, null, null).data();
        if (pedidos == null) {
            pedidos = new ArrayList<>();
        }

        double montoTotal = pedidos.stream().mapToDouble(PedidoResponseDTO::getMonto).sum();
        int cantidadPedidos = pedidos.size();
        double frecuenciaAnual = 0.0;

        if (cantidadPedidos > 0) {
            LocalDate fechaPrimerPedido = pedidos.stream()
                    .map(p -> p.getFechaCreacion().toLocalDate())
                    .min(LocalDate::compareTo)
                    .orElse(LocalDate.now());

            long anios = ChronoUnit.YEARS.between(fechaPrimerPedido, LocalDate.now());
            double denominador = Math.max(1.0, (double) anios);
            frecuenciaAnual = cantidadPedidos / denominador;
        }

        // Snapshot opcional en BD
        MetricaCliente mc = new MetricaCliente();
        mc.setClienteId(clienteId);
        mc.setMontoTotal(montoTotal);
        mc.setCantidadPedidos(cantidadPedidos);
        mc.setFrecuenciaAnual(frecuenciaAnual);
        mc.setUltimaActualizacion(LocalDateTime.now());
        metricaClienteRepository.save(mc);

        return new MetricaClienteResponseDTO(clienteId, nombreCliente, montoTotal, cantidadPedidos, frecuenciaAnual);
    }

    @Override
    public List<MetricaClienteResponseDTO> obtenerRankingClientes(Integer limite) {
        log.info("Generando ranking de clientes, limite={}", limite);
        List<PedidoResponseDTO> pedidos = pedidoFeignClient.listarPedidos(null, null, null).data();
        if (pedidos == null) pedidos = new ArrayList<>();

        Map<Long, Double> montoPorCliente = pedidos.stream()
                .collect(Collectors.groupingBy(PedidoResponseDTO::getClienteId, Collectors.summingDouble(PedidoResponseDTO::getMonto)));

        Map<Long, Long> cantidadPorCliente = pedidos.stream()
                .collect(Collectors.groupingBy(PedidoResponseDTO::getClienteId, Collectors.counting()));

        return montoPorCliente.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(limite != null ? limite : 10)
                .map(entry -> {
                    Long clienteId = entry.getKey();
                    Double monto = entry.getValue();
                    int cantidad = cantidadPorCliente.getOrDefault(clienteId, 0L).intValue();
                    String nombre = obtenerNombreClienteOcultandoError(clienteId);
                    return new MetricaClienteResponseDTO(clienteId, nombre, monto, cantidad, 0.0);
                })
                .toList();
    }

    @Override
    public List<MetricaProductoResponseDTO> obtenerTopProductos(LocalDate desde, LocalDate hasta, Integer limite) {
        log.info("Generando top productos desde={} hasta={}, limite={}", desde, hasta, limite);
        List<PedidoResponseDTO> pedidos = pedidoFeignClient.listarPedidos(null, desde, hasta).data();
        if (pedidos == null) pedidos = new ArrayList<>();

        Map<Long, Integer> cantidadPorProducto = new HashMap<>();
        Map<Long, String> nombrePorProducto = new HashMap<>();

        for (PedidoResponseDTO pedido : pedidos) {
            if (pedido.getItems() != null) {
                for (ItemPedidoDTO item : pedido.getItems()) {
                    cantidadPorProducto.merge(item.getProductoId(), item.getCantidad(), Integer::sum);
                    nombrePorProducto.putIfAbsent(item.getProductoId(), item.getNombreProducto() != null ? item.getNombreProducto() : "Producto " + item.getProductoId());
                }
            }
        }

        List<MetricaProductoResponseDTO> top = cantidadPorProducto.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(limite != null ? limite : 10)
                .map(entry -> {
                    Long prodId = entry.getKey();
                    Integer cant = entry.getValue();
                    String nom = nombrePorProducto.get(prodId);
                    return new MetricaProductoResponseDTO(prodId, nom, cant);
                })
                .toList();

        // Snapshot opcional
        for (MetricaProductoResponseDTO dto : top) {
            MetricaProducto mp = new MetricaProducto();
            mp.setProductoId(dto.getProductoId());
            mp.setNombre(dto.getNombre());
            mp.setTotalVendido(dto.getTotalVendido());
            mp.setPeriodo(desde + " / " + hasta);
            mp.setUltimaActualizacion(LocalDateTime.now());
            metricaProductoRepository.save(mp);
        }

        return top;
    }

    @Override
    public ResumenVentasResponseDTO obtenerResumenVentas(LocalDate desde, LocalDate hasta) {
        log.info("Generando resumen de ventas desde={} hasta={}", desde, hasta);
        List<PedidoResponseDTO> pedidos = pedidoFeignClient.listarPedidos(null, desde, hasta).data();
        if (pedidos == null) pedidos = new ArrayList<>();

        double montoTotal = pedidos.stream().mapToDouble(PedidoResponseDTO::getMonto).sum();
        int cantidad = pedidos.size();

        return new ResumenVentasResponseDTO(desde, hasta, montoTotal, cantidad);
    }

    private String obtenerNombreClienteConFallback(Long clienteId) {
        try {
            ApiResponse<ClienteResponseDTO> response = clienteFeignClient.obtenerCliente(clienteId);
            if (response != null && response.data() != null) {
                return response.data().getNombreCl();
            }
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw new ResourceNotFoundException("Cliente no encontrado en cliente-service");
            }
            log.warn("Error Feign al obtener clienteId={} (status={}): {}", clienteId, e.status(), e.getMessage());
        } catch (Exception e) {
            log.warn("Error inesperado al obtener clienteId={}: {}", clienteId, e.getMessage());
        }
        return "Desconocido";
    }

    private String obtenerNombreClienteOcultandoError(Long clienteId) {
        try {
            ApiResponse<ClienteResponseDTO> response = clienteFeignClient.obtenerCliente(clienteId);
            if (response != null && response.data() != null) {
                return response.data().getNombreCl();
            }
        } catch (FeignException e) {
            log.warn("Fallo tolerado Feign al obtener clienteId={} para ranking (status={}): {}", clienteId, e.status(), e.getMessage());
        } catch (Exception e) {
            log.warn("Fallo tolerado al obtener clienteId={} para ranking: {}", clienteId, e.getMessage());
        }
        return "Cliente " + clienteId;
    }
}

