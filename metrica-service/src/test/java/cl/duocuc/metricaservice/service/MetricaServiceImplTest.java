package cl.duocuc.metricaservice.service;

import cl.duocuc.metricaservice.client.ClienteFeignClient;
import cl.duocuc.metricaservice.client.PedidoFeignClient;
import cl.duocuc.metricaservice.dto.ClienteResponseDTO;
import cl.duocuc.metricaservice.dto.ItemPedidoDTO;
import cl.duocuc.metricaservice.dto.MetricaClienteResponseDTO;
import cl.duocuc.metricaservice.dto.MetricaProductoResponseDTO;
import cl.duocuc.metricaservice.dto.PedidoResponseDTO;
import cl.duocuc.metricaservice.dto.ResumenVentasResponseDTO;
import cl.duocuc.metricaservice.entity.MetricaCliente;
import cl.duocuc.metricaservice.entity.MetricaProducto;
import cl.duocuc.metricaservice.exception.ResourceNotFoundException;
import cl.duocuc.metricaservice.repository.MetricaClienteRepository;
import cl.duocuc.metricaservice.repository.MetricaProductoRepository;
import feign.FeignException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MetricaServiceImplTest {

    @Mock
    private PedidoFeignClient pedidoFeignClient;

    @Mock
    private ClienteFeignClient clienteFeignClient;

    @Mock
    private MetricaClienteRepository metricaClienteRepository;

    @Mock
    private MetricaProductoRepository metricaProductoRepository;

    @InjectMocks
    private MetricaServiceImpl metricaService;

    @Test
    void obtenerMetricasClienteCalculaTotalesYGuardaSnapshot() {
        // Given
        Long clienteId = 1L;
        when(clienteFeignClient.obtenerCliente(clienteId)).thenReturn(new ClienteResponseDTO(clienteId, "Maria Perez"));
        when(pedidoFeignClient.listarPedidosPorCliente(clienteId)).thenReturn(List.of(
                pedido(1001L, clienteId, 7000.0, LocalDateTime.now().minusYears(2), null),
                pedido(1002L, clienteId, 3000.0, LocalDateTime.now().minusYears(1), null)
        ));

        // When
        MetricaClienteResponseDTO response = metricaService.obtenerMetricasCliente(clienteId);

        // Then
        assertEquals(clienteId, response.getClienteId());
        assertEquals("Maria Perez", response.getNombreCliente());
        assertEquals(10000.0, response.getMontoTotal());
        assertEquals(2, response.getCantidadPedidos());
        assertEquals(1.0, response.getFrecuenciaAnual());
        verify(metricaClienteRepository).save(argThat(snapshot ->
                snapshot.getClienteId().equals(clienteId)
                        && snapshot.getMontoTotal().equals(10000.0)
                        && snapshot.getCantidadPedidos().equals(2)
                        && snapshot.getFrecuenciaAnual().equals(1.0)
                        && snapshot.getUltimaActualizacion() != null
        ));
    }

    @Test
    void obtenerMetricasClienteUsaDesconocidoCuandoClienteServiceRetornaNull() {
        // Given
        when(clienteFeignClient.obtenerCliente(1L)).thenReturn(null);
        when(pedidoFeignClient.listarPedidosPorCliente(1L)).thenReturn(null);

        // When
        MetricaClienteResponseDTO response = metricaService.obtenerMetricasCliente(1L);

        // Then
        assertEquals("Desconocido", response.getNombreCliente());
        assertEquals(0.0, response.getMontoTotal());
        assertEquals(0, response.getCantidadPedidos());
        assertEquals(0.0, response.getFrecuenciaAnual());
        verify(metricaClienteRepository).save(any(MetricaCliente.class));
    }

    @Test
    void obtenerMetricasClienteLanzaResourceNotFoundCuandoClienteServiceRetorna404() {
        // Given
        when(clienteFeignClient.obtenerCliente(404L)).thenThrow(feignException(404, "Not Found"));

        // When
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> metricaService.obtenerMetricasCliente(404L));

        // Then
        assertEquals("Cliente no encontrado en cliente-service", exception.getMessage());
        verify(pedidoFeignClient, never()).listarPedidosPorCliente(any());
        verify(metricaClienteRepository, never()).save(any());
    }

    @Test
    void obtenerMetricasClienteUsaDesconocidoCuandoClienteServiceFallaCon503() {
        // Given
        when(clienteFeignClient.obtenerCliente(1L)).thenThrow(feignException(503, "Service Unavailable"));
        when(pedidoFeignClient.listarPedidosPorCliente(1L)).thenReturn(List.of(pedido(1001L, 1L, 5000.0, LocalDateTime.now(), null)));

        // When
        MetricaClienteResponseDTO response = metricaService.obtenerMetricasCliente(1L);

        // Then
        assertEquals("Desconocido", response.getNombreCliente());
        assertEquals(5000.0, response.getMontoTotal());
        assertEquals(1, response.getCantidadPedidos());
    }

    @Test
    void obtenerMetricasClientePropagaErrorRemotoDePedidoService() {
        // Given
        when(clienteFeignClient.obtenerCliente(1L)).thenReturn(new ClienteResponseDTO(1L, "Maria Perez"));
        FeignException error = feignException(503, "Service Unavailable");
        when(pedidoFeignClient.listarPedidosPorCliente(1L)).thenThrow(error);

        // When / Then
        assertThrows(FeignException.class, () -> metricaService.obtenerMetricasCliente(1L));
        verify(metricaClienteRepository, never()).save(any());
    }

    @Test
    void obtenerRankingClientesOrdenaPorMontoYRespetaLimite() {
        // Given
        when(pedidoFeignClient.listarPedidos(null, null)).thenReturn(List.of(
                pedido(1001L, 1L, 7000.0, LocalDateTime.now(), null),
                pedido(1002L, 2L, 20000.0, LocalDateTime.now(), null),
                pedido(1003L, 1L, 3000.0, LocalDateTime.now(), null)
        ));
        when(clienteFeignClient.obtenerCliente(1L)).thenReturn(new ClienteResponseDTO(1L, "Maria Perez"));
        when(clienteFeignClient.obtenerCliente(2L)).thenThrow(feignException(404, "Not Found"));

        // When
        List<MetricaClienteResponseDTO> ranking = metricaService.obtenerRankingClientes(2);

        // Then
        assertEquals(2, ranking.size());
        assertEquals(2L, ranking.get(0).getClienteId());
        assertEquals("Cliente 2", ranking.get(0).getNombreCliente());
        assertEquals(20000.0, ranking.get(0).getMontoTotal());
        assertEquals(1, ranking.get(0).getCantidadPedidos());
        assertEquals(1L, ranking.get(1).getClienteId());
        assertEquals("Maria Perez", ranking.get(1).getNombreCliente());
        assertEquals(10000.0, ranking.get(1).getMontoTotal());
        assertEquals(2, ranking.get(1).getCantidadPedidos());
    }

    @Test
    void obtenerRankingClientesUsaLimitePorDefectoYListaVaciaCuandoPedidoServiceNoTieneDatos() {
        // Given
        when(pedidoFeignClient.listarPedidos(null, null)).thenReturn(null);

        // When
        List<MetricaClienteResponseDTO> ranking = metricaService.obtenerRankingClientes(null);

        // Then
        assertTrue(ranking.isEmpty());
    }

    @Test
    void obtenerTopProductosSumaCantidadesYGuardaSnapshots() {
        // Given
        LocalDate desde = LocalDate.of(2026, 5, 1);
        LocalDate hasta = LocalDate.of(2026, 5, 31);
        when(pedidoFeignClient.listarPedidos(desde, hasta)).thenReturn(List.of(
                pedido(1001L, 1L, 7000.0, LocalDateTime.now(), List.of(
                        new ItemPedidoDTO(10L, "Resma Carta", 2, 3500.0),
                        new ItemPedidoDTO(20L, null, 1, 1000.0)
                )),
                pedido(1002L, 2L, 10000.0, LocalDateTime.now(), List.of(
                        new ItemPedidoDTO(10L, "Resma Carta", 3, 3500.0)
                )),
                pedido(1003L, 2L, 5000.0, LocalDateTime.now(), null)
        ));

        // When
        List<MetricaProductoResponseDTO> top = metricaService.obtenerTopProductos(desde, hasta, 2);

        // Then
        assertEquals(2, top.size());
        assertEquals(10L, top.get(0).getProductoId());
        assertEquals("Resma Carta", top.get(0).getNombre());
        assertEquals(5, top.get(0).getTotalVendido());
        assertEquals(20L, top.get(1).getProductoId());
        assertEquals("Producto 20", top.get(1).getNombre());
        assertEquals(1, top.get(1).getTotalVendido());
        verify(metricaProductoRepository).save(argThat(snapshot ->
                snapshot.getProductoId().equals(10L)
                        && snapshot.getNombre().equals("Resma Carta")
                        && snapshot.getTotalVendido().equals(5)
                        && snapshot.getPeriodo().equals(desde + " / " + hasta)
                        && snapshot.getUltimaActualizacion() != null
        ));
        verify(metricaProductoRepository).save(argThat(snapshot -> snapshot.getProductoId().equals(20L)));
    }

    @Test
    void obtenerTopProductosRetornaVacioCuandoPedidoServiceNoTieneDatos() {
        // Given
        LocalDate desde = LocalDate.of(2026, 5, 1);
        LocalDate hasta = LocalDate.of(2026, 5, 31);
        when(pedidoFeignClient.listarPedidos(desde, hasta)).thenReturn(null);

        // When
        List<MetricaProductoResponseDTO> top = metricaService.obtenerTopProductos(desde, hasta, null);

        // Then
        assertTrue(top.isEmpty());
        verify(metricaProductoRepository, never()).save(any(MetricaProducto.class));
    }

    @Test
    void obtenerTopProductosPropagaErrorRemotoDePedidoService() {
        // Given
        LocalDate desde = LocalDate.of(2026, 5, 1);
        LocalDate hasta = LocalDate.of(2026, 5, 31);
        when(pedidoFeignClient.listarPedidos(desde, hasta)).thenThrow(feignException(404, "Not Found"));

        // When / Then
        assertThrows(FeignException.class, () -> metricaService.obtenerTopProductos(desde, hasta, 10));
        verify(metricaProductoRepository, never()).save(any());
    }

    @Test
    void obtenerResumenVentasCalculaMontoYCantidad() {
        // Given
        LocalDate desde = LocalDate.of(2026, 5, 1);
        LocalDate hasta = LocalDate.of(2026, 5, 31);
        when(pedidoFeignClient.listarPedidos(desde, hasta)).thenReturn(List.of(
                pedido(1001L, 1L, 7000.0, LocalDateTime.now(), null),
                pedido(1002L, 2L, 3000.0, LocalDateTime.now(), null)
        ));

        // When
        ResumenVentasResponseDTO resumen = metricaService.obtenerResumenVentas(desde, hasta);

        // Then
        assertEquals(desde, resumen.getDesde());
        assertEquals(hasta, resumen.getHasta());
        assertEquals(10000.0, resumen.getMontoTotal());
        assertEquals(2, resumen.getCantidadPedidos());
    }

    @Test
    void obtenerResumenVentasRetornaCeroCuandoPedidoServiceNoTieneDatos() {
        // Given
        LocalDate desde = LocalDate.of(2026, 5, 1);
        LocalDate hasta = LocalDate.of(2026, 5, 31);
        when(pedidoFeignClient.listarPedidos(desde, hasta)).thenReturn(null);

        // When
        ResumenVentasResponseDTO resumen = metricaService.obtenerResumenVentas(desde, hasta);

        // Then
        assertEquals(0.0, resumen.getMontoTotal());
        assertEquals(0, resumen.getCantidadPedidos());
    }

    private PedidoResponseDTO pedido(Long id, Long clienteId, Double monto, LocalDateTime fechaCreacion, List<ItemPedidoDTO> items) {
        return new PedidoResponseDTO(id, clienteId, monto, fechaCreacion, items);
    }

    private FeignException feignException(int status, String reason) {
        Request request = Request.create(
                Request.HttpMethod.GET,
                "/api/error",
                Map.of(),
                null,
                StandardCharsets.UTF_8,
                null
        );
        Response response = Response.builder()
                .request(request)
                .status(status)
                .reason(reason)
                .headers(Map.of())
                .body(new byte[0])
                .build();

        return FeignException.errorStatus("test", response);
    }
}
