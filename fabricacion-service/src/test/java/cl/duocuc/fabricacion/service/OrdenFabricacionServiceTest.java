package cl.duocuc.fabricacion.service;

import cl.duocuc.fabricacion.client.PedidoServiceClient;
import cl.duocuc.fabricacion.dto.OrdenFabricacionRequest;
import cl.duocuc.fabricacion.dto.OrdenFabricacionResponse;
import cl.duocuc.fabricacion.dto.UpdateEstadoFabricacionRequest;
import cl.duocuc.fabricacion.entity.EstadoFabricacion;
import cl.duocuc.fabricacion.entity.HistorialFabricacion;
import cl.duocuc.fabricacion.entity.OrdenFabricacion;
import cl.duocuc.fabricacion.exception.ConflictException;
import cl.duocuc.fabricacion.exception.PedidoNoEncontradoException;
import cl.duocuc.fabricacion.exception.ResourceNotFoundException;
import cl.duocuc.fabricacion.exception.ServiceUnavailableException;
import cl.duocuc.fabricacion.repository.HistorialFabricacionRepository;
import cl.duocuc.fabricacion.repository.OrdenFabricacionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrdenFabricacionServiceTest {

    @Mock
    private OrdenFabricacionRepository ordenRepository;

    @Mock
    private HistorialFabricacionRepository historialRepository;

    @Mock
    private PedidoServiceClient pedidoServiceClient;

    @InjectMocks
    private OrdenFabricacionService ordenFabricacionService;

    @Test
    void crearOrdenGuardaOrdenRegistraHistorialYNotificaPedido() {
        // Given
        OrdenFabricacionRequest request = new OrdenFabricacionRequest(1001L, "operador-01", "Inicio");
        doNothing().when(pedidoServiceClient).validarExistencia(1001L);
        when(ordenRepository.findByPedidoId(1001L)).thenReturn(Optional.empty());
        when(ordenRepository.save(any(OrdenFabricacion.class))).thenAnswer(invocation -> {
            OrdenFabricacion orden = invocation.getArgument(0);
            orden.setId(1L);
            orden.setFechaCreacion(LocalDateTime.of(2026, 5, 22, 10, 0));
            return orden;
        });

        // When
        OrdenFabricacionResponse response = ordenFabricacionService.crearOrden(request);

        // Then
        assertEquals(1L, response.getId());
        assertEquals(1001L, response.getNumeroPedido());
        assertEquals(EstadoFabricacion.EN_PROCESO, response.getEstadoFabricacion());
        assertEquals("operador-01", response.getUsuarioResponsable());
        assertEquals("Inicio", response.getDescripcionEstado());
        assertNotNull(response.getFechaInicio());

        verify(pedidoServiceClient).validarExistencia(1001L);
        verify(ordenRepository).findByPedidoId(1001L);
        verify(ordenRepository).save(argThat(orden ->
                orden.getPedidoId().equals(1001L)
                        && orden.getEstadoFabricacion() == EstadoFabricacion.EN_PROCESO
                        && orden.getUsuarioResponsable().equals("operador-01")
        ));
        verify(historialRepository).save(argThat(historial ->
                historial.getEstadoAnterior() == null
                        && historial.getEstadoNuevo() == EstadoFabricacion.EN_PROCESO
                        && historial.getUsuarioId().equals("operador-01")
                        && historial.getMotivo().equals("Inicio de fabricacion")
        ));
        verify(pedidoServiceClient).notificarPedidoEnFabricacion(1001L);
    }

    @Test
    void crearOrdenLanzaConflictExceptionCuandoYaExisteOrdenParaPedido() {
        // Given
        OrdenFabricacionRequest request = new OrdenFabricacionRequest(1001L, "operador-01", "Inicio");
        doNothing().when(pedidoServiceClient).validarExistencia(1001L);
        when(ordenRepository.findByPedidoId(1001L)).thenReturn(Optional.of(orden(1L, 1001L, EstadoFabricacion.EN_PROCESO)));

        // When
        ConflictException exception = assertThrows(ConflictException.class,
                () -> ordenFabricacionService.crearOrden(request));

        // Then
        assertTrue(exception.getMessage().contains("Ya existe una orden para el pedido: 1001"));
        verify(ordenRepository, never()).save(any());
        verify(historialRepository, never()).save(any());
        verify(pedidoServiceClient, never()).notificarPedidoEnFabricacion(any());
    }

    @Test
    void crearOrdenPropagaPedidoNoEncontradoException() {
        // Given
        OrdenFabricacionRequest request = new OrdenFabricacionRequest(404L, "operador-01", "Inicio");
        doThrow(new PedidoNoEncontradoException("Pedido no encontrado: 404"))
                .when(pedidoServiceClient).validarExistencia(404L);

        // When / Then
        assertThrows(PedidoNoEncontradoException.class, () -> ordenFabricacionService.crearOrden(request));
        verify(ordenRepository, never()).findByPedidoId(any());
        verify(ordenRepository, never()).save(any());
    }

    @Test
    void crearOrdenPropagaServiceUnavailableException() {
        // Given
        OrdenFabricacionRequest request = new OrdenFabricacionRequest(1001L, "operador-01", "Inicio");
        doThrow(new ServiceUnavailableException("pedido-service no disponible"))
                .when(pedidoServiceClient).validarExistencia(1001L);

        // When / Then
        assertThrows(ServiceUnavailableException.class, () -> ordenFabricacionService.crearOrden(request));
        verify(ordenRepository, never()).findByPedidoId(any());
    }

    @Test
    void crearOrdenPropagaResponseStatusException() {
        // Given
        OrdenFabricacionRequest request = new OrdenFabricacionRequest(1001L, "operador-01", "Inicio");
        doThrow(new ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, "conflicto remoto"))
                .when(pedidoServiceClient).validarExistencia(1001L);

        // When / Then
        assertThrows(ResponseStatusException.class, () -> ordenFabricacionService.crearOrden(request));
        verify(ordenRepository, never()).findByPedidoId(any());
    }

    @Test
    void crearOrdenTraduceErrorInesperadoAlValidarPedidoAServiceUnavailable() {
        // Given
        OrdenFabricacionRequest request = new OrdenFabricacionRequest(1001L, "operador-01", "Inicio");
        doThrow(new IllegalStateException("fallo inesperado"))
                .when(pedidoServiceClient).validarExistencia(1001L);

        // When
        ServiceUnavailableException exception = assertThrows(ServiceUnavailableException.class,
                () -> ordenFabricacionService.crearOrden(request));

        // Then
        assertEquals("Error al validar pedido", exception.getMessage());
        verify(ordenRepository, never()).findByPedidoId(any());
    }

    @Test
    void obtenerOrdenRetornaOrdenCuandoExiste() {
        // Given
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden(1L, 1001L, EstadoFabricacion.EN_PROCESO)));

        // When
        OrdenFabricacionResponse response = ordenFabricacionService.obtenerOrden(1L);

        // Then
        assertEquals(1L, response.getId());
        assertEquals(1001L, response.getNumeroPedido());
        assertEquals(EstadoFabricacion.EN_PROCESO, response.getEstadoFabricacion());
        verify(ordenRepository).findById(1L);
    }

    @Test
    void obtenerOrdenLanzaResourceNotFoundExceptionCuandoNoExiste() {
        // Given
        when(ordenRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> ordenFabricacionService.obtenerOrden(99L));

        // Then
        assertTrue(exception.getMessage().contains("Orden no encontrada: 99"));
        verify(ordenRepository).findById(99L);
    }

    @Test
    void actualizarEstadoCambiaEstadoYRegistraHistorialConDatosDelRequest() {
        // Given
        OrdenFabricacion orden = orden(1L, 1001L, EstadoFabricacion.EN_PROCESO);
        UpdateEstadoFabricacionRequest request = new UpdateEstadoFabricacionRequest(EstadoFabricacion.PAUSADO, "Falta material", "supervisor-01");
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));
        when(ordenRepository.save(any(OrdenFabricacion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        OrdenFabricacionResponse response = ordenFabricacionService.actualizarEstado(1L, request);

        // Then
        assertEquals(EstadoFabricacion.PAUSADO, response.getEstadoFabricacion());
        verify(pedidoServiceClient, never()).notificarPedidoListo(any());
        verify(historialRepository).save(argThat(historial ->
                historial.getEstadoAnterior() == EstadoFabricacion.EN_PROCESO
                        && historial.getEstadoNuevo() == EstadoFabricacion.PAUSADO
                        && historial.getUsuarioId().equals("supervisor-01")
                        && historial.getMotivo().equals("Falta material")
        ));
    }

    @Test
    void actualizarEstadoTerminadoAsignaFechaFinYNotificaPedidoListo() {
        // Given
        OrdenFabricacion orden = orden(1L, 1001L, EstadoFabricacion.EN_PROCESO);
        UpdateEstadoFabricacionRequest request = new UpdateEstadoFabricacionRequest(EstadoFabricacion.TERMINADO, null, null);
        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));
        when(ordenRepository.save(any(OrdenFabricacion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        OrdenFabricacionResponse response = ordenFabricacionService.actualizarEstado(1L, request);

        // Then
        assertEquals(EstadoFabricacion.TERMINADO, response.getEstadoFabricacion());
        assertNotNull(response.getFechaFin());
        verify(pedidoServiceClient).notificarPedidoListo(1001L);
        verify(historialRepository).save(argThat(historial ->
                historial.getUsuarioId().equals("operador-01")
                        && historial.getMotivo().equals("Cambio de estado")
        ));
    }

    @Test
    void actualizarEstadoLanzaResourceNotFoundExceptionCuandoOrdenNoExiste() {
        // Given
        when(ordenRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> ordenFabricacionService.actualizarEstado(99L,
                        new UpdateEstadoFabricacionRequest(EstadoFabricacion.TERMINADO, null, null)));

        // Then
        assertTrue(exception.getMessage().contains("Orden no encontrada: 99"));
        verify(ordenRepository, never()).save(any());
        verify(historialRepository, never()).save(any());
    }

    private OrdenFabricacion orden(Long id, Long pedidoId, EstadoFabricacion estado) {
        OrdenFabricacion orden = new OrdenFabricacion();
        orden.setId(id);
        orden.setPedidoId(pedidoId);
        orden.setEstadoFabricacion(estado);
        orden.setFechaInicio(LocalDateTime.of(2026, 5, 22, 10, 0));
        orden.setFechaCreacion(LocalDateTime.of(2026, 5, 22, 10, 0));
        orden.setDescripcionEstado("Inicio");
        orden.setUsuarioResponsable("operador-01");
        return orden;
    }
}
