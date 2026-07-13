package cl.duocuc.logservice.service;

import cl.duocuc.logservice.dto.ApiResponse;
import cl.duocuc.logservice.dto.LogRequestDTO;
import cl.duocuc.logservice.entity.LogEntrada;
import cl.duocuc.logservice.repository.LogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogServiceImplTest {

    @Mock
    private LogRepository logRepository;

    @InjectMocks
    private LogServiceImpl logService;

    @Test
    void registrarLogCopiaCamposAsignaTimestampYGuarda() {
        // Given
        LogRequestDTO request = LogRequestDTO.builder()
                .servicio("pedido-service")
                .operacion("CREAR_PEDIDO")
                .usuarioId("42")
                .resultado("OK")
                .detalle("Pedido creado")
                .build();

        when(logRepository.save(org.mockito.ArgumentMatchers.any())).thenAnswer(invocation -> {
            LogEntrada entrada = invocation.getArgument(0);
            entrada.setId(10L);
            return entrada;
        });

        // When
        ApiResponse<LogEntrada> response = logService.registrarLog(request);

        // Then
        ArgumentCaptor<LogEntrada> captor = ArgumentCaptor.forClass(LogEntrada.class);
        verify(logRepository).save(captor.capture());
        assertEquals("pedido-service", captor.getValue().getServicio());
        assertEquals("CREAR_PEDIDO", captor.getValue().getOperacion());
        assertEquals("42", captor.getValue().getUsuarioId());
        assertEquals("OK", captor.getValue().getResultado());
        assertEquals("Pedido creado", captor.getValue().getDetalle());
        assertNotNull(captor.getValue().getTimestamp());
        assertEquals("Log registrado correctamente", response.getMensaje());
        assertTrue(response.isExitoso());
        assertEquals(10L, response.getData().getId());
    }

    @Test
    void consultarLogsSinFiltrosUsaFindAll() {
        // Given
        List<LogEntrada> logs = List.of(log("pedido-service"));
        when(logRepository.findAll()).thenReturn(logs);

        // When
        ApiResponse<List<LogEntrada>> response = logService.consultarLogs(null, null);

        // Then
        assertEquals("Logs consultados correctamente", response.getMensaje());
        assertSame(logs, response.getData());
        verify(logRepository).findAll();
    }

    @Test
    void consultarLogsSoloPorServicioUsaFindByServicio() {
        // Given
        List<LogEntrada> logs = List.of(log("pedido-service"));
        when(logRepository.findByServicio("pedido-service")).thenReturn(logs);

        // When
        ApiResponse<List<LogEntrada>> response = logService.consultarLogs("pedido-service", null);

        // Then
        assertSame(logs, response.getData());
        verify(logRepository).findByServicio("pedido-service");
    }

    @Test
    void consultarLogsSoloPorFechaUsaFindByTimestampAfter() {
        // Given
        LocalDateTime desde = LocalDateTime.of(2026, 7, 1, 10, 0);
        List<LogEntrada> logs = List.of(log("pedido-service"));
        when(logRepository.findByTimestampAfter(desde)).thenReturn(logs);

        // When
        ApiResponse<List<LogEntrada>> response = logService.consultarLogs(null, desde);

        // Then
        assertSame(logs, response.getData());
        verify(logRepository).findByTimestampAfter(desde);
    }

    @Test
    void consultarLogsPorServicioYFechaUsaFindByServicioAndTimestampAfter() {
        // Given
        LocalDateTime desde = LocalDateTime.of(2026, 7, 1, 10, 0);
        List<LogEntrada> logs = List.of(log("pedido-service"));
        when(logRepository.findByServicioAndTimestampAfter("pedido-service", desde)).thenReturn(logs);

        // When
        ApiResponse<List<LogEntrada>> response = logService.consultarLogs("pedido-service", desde);

        // Then
        assertSame(logs, response.getData());
        verify(logRepository).findByServicioAndTimestampAfter("pedido-service", desde);
    }

    private LogEntrada log(String servicio) {
        LogEntrada entrada = new LogEntrada();
        entrada.setId(1L);
        entrada.setServicio(servicio);
        entrada.setOperacion("CREAR_PEDIDO");
        entrada.setResultado("OK");
        entrada.setTimestamp(LocalDateTime.of(2026, 7, 1, 11, 0));
        return entrada;
    }
}
