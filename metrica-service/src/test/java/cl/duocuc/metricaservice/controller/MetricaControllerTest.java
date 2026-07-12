package cl.duocuc.metricaservice.controller;

import cl.duocuc.metricaservice.dto.MetricaClienteResponseDTO;
import cl.duocuc.metricaservice.dto.MetricaProductoResponseDTO;
import cl.duocuc.metricaservice.dto.ResumenVentasResponseDTO;
import cl.duocuc.metricaservice.exception.ConflictException;
import cl.duocuc.metricaservice.exception.ResourceNotFoundException;
import cl.duocuc.metricaservice.handler.GlobalExceptionHandler;
import cl.duocuc.metricaservice.service.MetricaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MetricaControllerTest {

    @Mock
    private MetricaService metricaService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new MetricaController(metricaService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void obtenerMetricasClienteRetornaMetricas() throws Exception {
        // Given
        when(metricaService.obtenerMetricasCliente(1L))
                .thenReturn(new MetricaClienteResponseDTO(1L, "Maria Perez", 10000.0, 2, 1.0));

        // When / Then
        mockMvc.perform(get("/api/metricas/clientes/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Métricas del cliente obtenidas correctamente"))
                .andExpect(jsonPath("$.data.clienteId").value(1))
                .andExpect(jsonPath("$.data.nombreCliente").value("Maria Perez"));

        verify(metricaService).obtenerMetricasCliente(1L);
    }

    @Test
    void obtenerMetricasClienteRetornaNotFoundCuandoServicioNoEncuentraCliente() throws Exception {
        // Given
        when(metricaService.obtenerMetricasCliente(404L))
                .thenThrow(new ResourceNotFoundException("Cliente no encontrado en cliente-service"));

        // When / Then
        mockMvc.perform(get("/api/metricas/clientes/{id}", 404L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensaje").value("Cliente no encontrado en cliente-service"));
    }

    @Test
    void obtenerRankingClientesRetornaRanking() throws Exception {
        // Given
        when(metricaService.obtenerRankingClientes(2))
                .thenReturn(List.of(new MetricaClienteResponseDTO(1L, "Maria Perez", 10000.0, 2, 0.0)));

        // When / Then
        mockMvc.perform(get("/api/metricas/clientes/ranking").param("limite", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Ranking obtenido correctamente"))
                .andExpect(jsonPath("$.data[0].clienteId").value(1));

        verify(metricaService).obtenerRankingClientes(2);
    }

    @Test
    void obtenerTopProductosUsaFechasPorDefectoCuandoNoSeInforman() throws Exception {
        // Given
        when(metricaService.obtenerTopProductos(org.mockito.ArgumentMatchers.any(LocalDate.class),
                org.mockito.ArgumentMatchers.any(LocalDate.class), org.mockito.ArgumentMatchers.eq(10)))
                .thenReturn(List.of(new MetricaProductoResponseDTO(10L, "Resma Carta", 5)));

        // When / Then
        mockMvc.perform(get("/api/metricas/productos/top"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Top productos obtenido"))
                .andExpect(jsonPath("$.data[0].productoId").value(10));
    }

    @Test
    void obtenerTopProductosRetornaConflictCuandoRangoEsInvalido() throws Exception {
        // When / Then
        mockMvc.perform(get("/api/metricas/productos/top")
                        .param("desde", "2026-06-01")
                        .param("hasta", "2026-05-01"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.mensaje").value("La fecha 'desde' no puede ser posterior a 'hasta'"));
    }

    @Test
    void obtenerResumenVentasRetornaResumen() throws Exception {
        // Given
        LocalDate desde = LocalDate.of(2026, 5, 1);
        LocalDate hasta = LocalDate.of(2026, 5, 31);
        when(metricaService.obtenerResumenVentas(desde, hasta))
                .thenReturn(new ResumenVentasResponseDTO(desde, hasta, 10000.0, 2));

        // When / Then
        mockMvc.perform(get("/api/metricas/ventas")
                        .param("desde", "2026-05-01")
                        .param("hasta", "2026-05-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Resumen de ventas obtenido"))
                .andExpect(jsonPath("$.data.montoTotal").value(10000.0));

        verify(metricaService).obtenerResumenVentas(desde, hasta);
    }

    @Test
    void obtenerResumenVentasRetornaConflictCuandoRangoEsInvalido() throws Exception {
        // When / Then
        mockMvc.perform(get("/api/metricas/ventas")
                        .param("desde", "2026-06-01")
                        .param("hasta", "2026-05-01"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.mensaje").value("La fecha 'desde' no puede ser posterior a 'hasta'"));
    }

    @Test
    void pingRetornaServicioActivo() throws Exception {
        // When / Then
        mockMvc.perform(get("/api/metricas/ping"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("metrica-service activo"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.exitoso").value(true));
    }

    @Test
    void controllerAdviceManejaConflictExceptionDelServicio() throws Exception {
        // Given
        when(metricaService.obtenerRankingClientes(10)).thenThrow(new ConflictException("Conflicto de prueba"));

        // When / Then
        mockMvc.perform(get("/api/metricas/clientes/ranking"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").value("Conflicto de prueba"));
    }
}
