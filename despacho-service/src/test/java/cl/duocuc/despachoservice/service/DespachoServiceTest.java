package cl.duocuc.despachoservice.service;

import cl.duocuc.despachoservice.client.PedidoFeignClient;
import cl.duocuc.despachoservice.client.pedido.dto.PedidoResponse;
import cl.duocuc.despachoservice.common.exception.ConflictException;
import cl.duocuc.despachoservice.common.exception.ResourceNotFoundException;
import cl.duocuc.despachoservice.common.exception.ServiceUnavailableException;
import cl.duocuc.despachoservice.dto.DespachoRequest;
import cl.duocuc.despachoservice.dto.DespachoResponse;
import cl.duocuc.despachoservice.model.Despacho;
import cl.duocuc.despachoservice.repository.DespachoRepository;
import feign.FeignException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DespachoServiceTest {

    @Mock
    private DespachoRepository despachoRepository;

    @Mock
    private PedidoFeignClient pedidoFeignClient;

    @InjectMocks
    private DespachoService despachoService;

    @Test
    void crearDespachoGuardaDespachoCuandoPedidoExisteYNoHayDuplicado() {
        // Given
        DespachoRequest request = new DespachoRequest(1001L, " rm ", " Transporte Central ", " TRK-1001 ");
        when(pedidoFeignClient.obtenerPedidoPorNumero(1001L)).thenReturn(new PedidoResponse(1001L));
        when(despachoRepository.existsByNumeroPedido(1001L)).thenReturn(false);
        when(despachoRepository.save(any(Despacho.class))).thenAnswer(invocation -> {
            Despacho despacho = invocation.getArgument(0);
            despacho.setId(1L);
            return despacho;
        });

        // When
        DespachoResponse response = despachoService.crearDespacho(request);

        // Then
        assertEquals(1L, response.getId());
        assertEquals(1001L, response.getNumeroPedido());
        assertEquals("RM", response.getTipoDespacho());
        assertEquals("Transporte Central", response.getTransportista());
        assertEquals("TRK-1001", response.getTrackingCode());
        assertNotNull(response.getFechaDespacho());

        verify(pedidoFeignClient).obtenerPedidoPorNumero(1001L);
        verify(despachoRepository).existsByNumeroPedido(1001L);
        verify(despachoRepository).save(argThat(despacho ->
                despacho.getNumeroPedido().equals(1001L)
                        && despacho.getTipoDespacho().equals("RM")
                        && despacho.getTransportista().equals("Transporte Central")
                        && despacho.getTrackingCode().equals("TRK-1001")
                        && despacho.getFechaDespacho() != null
        ));
    }

    @Test
    void crearDespachoLanzaConflictExceptionCuandoYaExisteDespachoParaPedido() {
        // Given
        DespachoRequest request = request(1001L, "RM");
        when(pedidoFeignClient.obtenerPedidoPorNumero(1001L)).thenReturn(new PedidoResponse(1001L));
        when(despachoRepository.existsByNumeroPedido(1001L)).thenReturn(true);

        // When
        ConflictException exception = assertThrows(ConflictException.class,
                () -> despachoService.crearDespacho(request));

        // Then
        assertTrue(exception.getMessage().contains("Ya existe un despacho registrado para el pedido 1001"));
        verify(pedidoFeignClient).obtenerPedidoPorNumero(1001L);
        verify(despachoRepository).existsByNumeroPedido(1001L);
        verify(despachoRepository, never()).save(any());
    }

    @Test
    void crearDespachoLanzaResourceNotFoundExceptionCuandoPedidoNoExiste() {
        // Given
        DespachoRequest request = request(404L, "RM");
        when(pedidoFeignClient.obtenerPedidoPorNumero(404L)).thenThrow(feignException(404, "Not Found"));

        // When
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> despachoService.crearDespacho(request));

        // Then
        assertTrue(exception.getMessage().contains("Pedido no encontrado con número 404"));
        verify(pedidoFeignClient).obtenerPedidoPorNumero(404L);
        verify(despachoRepository, never()).existsByNumeroPedido(any());
        verify(despachoRepository, never()).save(any());
    }

    @Test
    void crearDespachoLanzaServiceUnavailableExceptionCuandoPedidoServiceFalla() {
        // Given
        DespachoRequest request = request(1001L, "RM");
        when(pedidoFeignClient.obtenerPedidoPorNumero(1001L)).thenThrow(feignException(503, "Service Unavailable"));

        // When
        ServiceUnavailableException exception = assertThrows(ServiceUnavailableException.class,
                () -> despachoService.crearDespacho(request));

        // Then
        assertTrue(exception.getMessage().contains("No se pudo validar el pedido en pedido-service"));
        verify(pedidoFeignClient).obtenerPedidoPorNumero(1001L);
        verify(despachoRepository, never()).existsByNumeroPedido(any());
        verify(despachoRepository, never()).save(any());
    }

    @Test
    void crearDespachoLanzaIllegalArgumentExceptionCuandoTipoNoEsValido() {
        // Given
        DespachoRequest request = request(1001L, "EXPRESS");
        when(pedidoFeignClient.obtenerPedidoPorNumero(1001L)).thenReturn(new PedidoResponse(1001L));
        when(despachoRepository.existsByNumeroPedido(1001L)).thenReturn(false);

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> despachoService.crearDespacho(request));

        // Then
        assertEquals("tipoDespacho debe ser RETIRO, RM o REGION", exception.getMessage());
        verify(pedidoFeignClient).obtenerPedidoPorNumero(1001L);
        verify(despachoRepository).existsByNumeroPedido(1001L);
        verify(despachoRepository, never()).save(any());
    }

    @Test
    void listarDespachosRetornaTodosCuandoTipoEsNullOBlanco() {
        // Given
        Despacho despacho = despacho(1L, 1001L, "RM");
        when(despachoRepository.findAll()).thenReturn(List.of(despacho));

        // When
        List<DespachoResponse> responsesNull = despachoService.listarDespachos(null);
        List<DespachoResponse> responsesBlanco = despachoService.listarDespachos("   ");

        // Then
        assertEquals(1, responsesNull.size());
        assertEquals(1, responsesBlanco.size());
        assertEquals("RM", responsesNull.get(0).getTipoDespacho());
        verify(despachoRepository, times(2)).findAll();
    }

    @Test
    void listarDespachosFiltraPorTipoNormalizado() {
        // Given
        Despacho despacho = despacho(1L, 1001L, "REGION");
        when(despachoRepository.findByTipoDespacho("REGION")).thenReturn(List.of(despacho));

        // When
        List<DespachoResponse> responses = despachoService.listarDespachos(" region ");

        // Then
        assertEquals(1, responses.size());
        assertEquals("REGION", responses.get(0).getTipoDespacho());
        verify(despachoRepository).findByTipoDespacho("REGION");
        verify(despachoRepository, never()).findAll();
    }

    @Test
    void listarDespachosLanzaIllegalArgumentExceptionCuandoTipoNoEsValido() {
        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> despachoService.listarDespachos("EXPRESS"));

        // Then
        assertEquals("tipoDespacho debe ser RETIRO, RM o REGION", exception.getMessage());
        verify(despachoRepository, never()).findByTipoDespacho(any());
        verify(despachoRepository, never()).findAll();
    }

    @Test
    void obtenerDespachoPorNumeroPedidoRetornaDespachoCuandoExiste() {
        // Given
        when(despachoRepository.findByNumeroPedido(1001L)).thenReturn(Optional.of(despacho(1L, 1001L, "RM")));

        // When
        DespachoResponse response = despachoService.obtenerDespachoPorNumeroPedido(1001L);

        // Then
        assertEquals(1L, response.getId());
        assertEquals(1001L, response.getNumeroPedido());
        assertEquals("RM", response.getTipoDespacho());
        verify(despachoRepository).findByNumeroPedido(1001L);
    }

    @Test
    void obtenerDespachoPorNumeroPedidoLanzaResourceNotFoundExceptionCuandoNoExiste() {
        // Given
        when(despachoRepository.findByNumeroPedido(404L)).thenReturn(Optional.empty());

        // When
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> despachoService.obtenerDespachoPorNumeroPedido(404L));

        // Then
        assertTrue(exception.getMessage().contains("Despacho no encontrado para el pedido 404"));
        verify(despachoRepository).findByNumeroPedido(404L);
    }

    @Test
    void actualizarDespachoActualizaDatosCuandoExisteYNoCambiaNumeroPedido() {
        // Given
        Despacho existente = despacho(1L, 1001L, "RM");
        DespachoRequest request = new DespachoRequest(1001L, " retiro ", " Nuevo Transportista ", " TRK-NUEVO ");
        when(despachoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(pedidoFeignClient.obtenerPedidoPorNumero(1001L)).thenReturn(new PedidoResponse(1001L));
        when(despachoRepository.save(any(Despacho.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        DespachoResponse response = despachoService.actualizarDespacho(1L, request);

        // Then
        assertEquals(1L, response.getId());
        assertEquals(1001L, response.getNumeroPedido());
        assertEquals("RETIRO", response.getTipoDespacho());
        assertEquals("Nuevo Transportista", response.getTransportista());
        assertEquals("TRK-NUEVO", response.getTrackingCode());
        verify(despachoRepository).findById(1L);
        verify(pedidoFeignClient).obtenerPedidoPorNumero(1001L);
        verify(despachoRepository, never()).findByNumeroPedido(any());
        verify(despachoRepository).save(existente);
    }

    @Test
    void actualizarDespachoPermiteCambiarNumeroPedidoCuandoNoExisteOtroDespacho() {
        // Given
        Despacho existente = despacho(1L, 1001L, "RM");
        DespachoRequest request = request(1002L, "REGION");
        when(despachoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(pedidoFeignClient.obtenerPedidoPorNumero(1002L)).thenReturn(new PedidoResponse(1002L));
        when(despachoRepository.findByNumeroPedido(1002L)).thenReturn(Optional.empty());
        when(despachoRepository.save(any(Despacho.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        DespachoResponse response = despachoService.actualizarDespacho(1L, request);

        // Then
        assertEquals(1002L, response.getNumeroPedido());
        assertEquals("REGION", response.getTipoDespacho());
        verify(despachoRepository).findByNumeroPedido(1002L);
        verify(despachoRepository).save(existente);
    }

    @Test
    void actualizarDespachoLanzaResourceNotFoundExceptionCuandoDespachoNoExiste() {
        // Given
        when(despachoRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> despachoService.actualizarDespacho(99L, request(1001L, "RM")));

        // Then
        assertTrue(exception.getMessage().contains("Despacho no encontrado con ID 99"));
        verify(despachoRepository).findById(99L);
        verify(pedidoFeignClient, never()).obtenerPedidoPorNumero(any());
        verify(despachoRepository, never()).save(any());
    }

    @Test
    void actualizarDespachoLanzaConflictExceptionCuandoNuevoNumeroPedidoYaTieneDespacho() {
        // Given
        Despacho existente = despacho(1L, 1001L, "RM");
        Despacho otro = despacho(2L, 1002L, "REGION");
        when(despachoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(pedidoFeignClient.obtenerPedidoPorNumero(1002L)).thenReturn(new PedidoResponse(1002L));
        when(despachoRepository.findByNumeroPedido(1002L)).thenReturn(Optional.of(otro));

        // When
        ConflictException exception = assertThrows(ConflictException.class,
                () -> despachoService.actualizarDespacho(1L, request(1002L, "REGION")));

        // Then
        assertTrue(exception.getMessage().contains("Ya existe un despacho registrado para el pedido 1002"));
        verify(despachoRepository).findById(1L);
        verify(pedidoFeignClient).obtenerPedidoPorNumero(1002L);
        verify(despachoRepository).findByNumeroPedido(1002L);
        verify(despachoRepository, never()).save(any());
    }

    @Test
    void actualizarDespachoLanzaResourceNotFoundExceptionCuandoNuevoPedidoNoExiste() {
        // Given
        Despacho existente = despacho(1L, 1001L, "RM");
        when(despachoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(pedidoFeignClient.obtenerPedidoPorNumero(404L)).thenThrow(feignException(404, "Not Found"));

        // When
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> despachoService.actualizarDespacho(1L, request(404L, "RM")));

        // Then
        assertTrue(exception.getMessage().contains("Pedido no encontrado con número 404"));
        verify(despachoRepository).findById(1L);
        verify(pedidoFeignClient).obtenerPedidoPorNumero(404L);
        verify(despachoRepository, never()).save(any());
    }

    private DespachoRequest request(Long numeroPedido, String tipoDespacho) {
        return new DespachoRequest(numeroPedido, tipoDespacho, "Transportista", "TRK-" + numeroPedido);
    }

    private Despacho despacho(Long id, Long numeroPedido, String tipoDespacho) {
        Despacho despacho = new Despacho();
        despacho.setId(id);
        despacho.setNumeroPedido(numeroPedido);
        despacho.setTipoDespacho(tipoDespacho);
        despacho.setTransportista("Transportista");
        despacho.setFechaDespacho(LocalDateTime.of(2026, 5, 22, 10, 0));
        despacho.setTrackingCode("TRK-" + numeroPedido);
        return despacho;
    }

    private FeignException feignException(int status, String reason) {
        Request request = Request.create(
                Request.HttpMethod.GET,
                "/api/pedidos/error",
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
