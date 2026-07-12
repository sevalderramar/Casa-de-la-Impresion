package cl.duocuc.estadoservice.service;

import cl.duocuc.estadoservice.common.exception.ResourceNotFoundException;
import cl.duocuc.estadoservice.dto.CambioEstadoRequest;
import cl.duocuc.estadoservice.dto.CambioEstadoResponse;
import cl.duocuc.estadoservice.model.CambioEstado;
import cl.duocuc.estadoservice.repository.CambioEstadoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstadoServiceTest {

    @Mock
    private CambioEstadoRepository cambioEstadoRepository;

    @InjectMocks
    private EstadoService estadoService;

    @Test
    void registrarCambioEstadoGuardaYRetornaResponse() {
        // Given
        CambioEstadoRequest request = new CambioEstadoRequest(1001L, "COLA", "PRODUCCION", "Inicio de fabricacion");
        when(cambioEstadoRepository.save(any(CambioEstado.class))).thenAnswer(invocation -> {
            CambioEstado cambio = invocation.getArgument(0);
            cambio.setId(1L);
            return cambio;
        });

        // When
        CambioEstadoResponse response = estadoService.registrarCambioEstado(request);

        // Then
        assertEquals(1L, response.getId());
        assertEquals(1001L, response.getNumeroPedido());
        assertEquals("COLA", response.getEstadoAnterior());
        assertEquals("PRODUCCION", response.getEstadoNuevo());
        assertEquals("Inicio de fabricacion", response.getObservacion());
        assertNotNull(response.getFechaCambio());

        verify(cambioEstadoRepository).save(argThat(cambio ->
                cambio.getNumeroPedido().equals(1001L)
                        && cambio.getEstadoAnterior().equals("COLA")
                        && cambio.getEstadoNuevo().equals("PRODUCCION")
                        && cambio.getObservacion().equals("Inicio de fabricacion")
                        && cambio.getFechaCambio() != null
        ));
    }

    @Test
    void listarCambiosPorPedidoRetornaCambiosDelRepositorio() {
        // Given
        CambioEstado primero = cambio(1L, 1001L, "SIN_ESTADO", "COLA", LocalDateTime.of(2026, 5, 22, 10, 0));
        CambioEstado segundo = cambio(2L, 1001L, "COLA", "PRODUCCION", LocalDateTime.of(2026, 5, 22, 11, 0));
        when(cambioEstadoRepository.findByNumeroPedidoOrderByFechaCambioAsc(1001L)).thenReturn(List.of(primero, segundo));

        // When
        List<CambioEstadoResponse> responses = estadoService.listarCambiosPorPedido(1001L);

        // Then
        assertEquals(2, responses.size());
        assertEquals(1L, responses.get(0).getId());
        assertEquals("SIN_ESTADO", responses.get(0).getEstadoAnterior());
        assertEquals("COLA", responses.get(0).getEstadoNuevo());
        assertEquals(2L, responses.get(1).getId());
        assertEquals("PRODUCCION", responses.get(1).getEstadoNuevo());

        verify(cambioEstadoRepository).findByNumeroPedidoOrderByFechaCambioAsc(1001L);
    }

    @Test
    void listarCambiosPorPedidoLanzaErrorCuandoNoHayCambios() {
        // Given
        when(cambioEstadoRepository.findByNumeroPedidoOrderByFechaCambioAsc(404L)).thenReturn(List.of());

        // When
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> estadoService.listarCambiosPorPedido(404L));

        // Then
        assertTrue(exception.getMessage().contains("No se encontraron cambios de estado para el pedido con numero: 404"));
        verify(cambioEstadoRepository).findByNumeroPedidoOrderByFechaCambioAsc(404L);
        verify(cambioEstadoRepository, never()).save(any());
    }

    @Test
    void obtenerUltimoEstadoPorPedidoRetornaUltimoCambio() {
        // Given
        CambioEstado primero = cambio(1L, 1001L, "SIN_ESTADO", "COLA", LocalDateTime.of(2026, 5, 22, 10, 0));
        CambioEstado ultimo = cambio(2L, 1001L, "COLA", "PRODUCCION", LocalDateTime.of(2026, 5, 22, 11, 0));
        when(cambioEstadoRepository.findByNumeroPedidoOrderByFechaCambioAsc(1001L)).thenReturn(List.of(primero, ultimo));

        // When
        CambioEstadoResponse response = estadoService.obtenerUltimoEstadoPorPedido(1001L);

        // Then
        assertEquals(2L, response.getId());
        assertEquals(1001L, response.getNumeroPedido());
        assertEquals("COLA", response.getEstadoAnterior());
        assertEquals("PRODUCCION", response.getEstadoNuevo());
        assertEquals(LocalDateTime.of(2026, 5, 22, 11, 0), response.getFechaCambio());

        verify(cambioEstadoRepository).findByNumeroPedidoOrderByFechaCambioAsc(1001L);
    }

    @Test
    void obtenerUltimoEstadoPorPedidoLanzaErrorCuandoNoHayCambios() {
        // Given
        when(cambioEstadoRepository.findByNumeroPedidoOrderByFechaCambioAsc(404L)).thenReturn(List.of());

        // When
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> estadoService.obtenerUltimoEstadoPorPedido(404L));

        // Then
        assertTrue(exception.getMessage().contains("No se encontraron cambios de estado para el pedido con numero: 404"));
        verify(cambioEstadoRepository).findByNumeroPedidoOrderByFechaCambioAsc(404L);
        verify(cambioEstadoRepository, never()).save(any());
    }

    private CambioEstado cambio(Long id, Long numeroPedido, String estadoAnterior, String estadoNuevo, LocalDateTime fechaCambio) {
        CambioEstado cambio = new CambioEstado();
        cambio.setId(id);
        cambio.setNumeroPedido(numeroPedido);
        cambio.setEstadoAnterior(estadoAnterior);
        cambio.setEstadoNuevo(estadoNuevo);
        cambio.setFechaCambio(fechaCambio);
        cambio.setObservacion("Cambio de estado");
        return cambio;
    }
}
