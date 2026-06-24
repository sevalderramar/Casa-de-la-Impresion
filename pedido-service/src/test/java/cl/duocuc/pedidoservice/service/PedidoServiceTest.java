package cl.duocuc.pedidoservice.service;

import cl.duocuc.pedidoservice.client.ClienteFeignClient;
import cl.duocuc.pedidoservice.client.EstadoFeignClient;
import cl.duocuc.pedidoservice.client.ProductoFeignClient;
import cl.duocuc.pedidoservice.client.cliente.dto.ClienteResponse;
import cl.duocuc.pedidoservice.client.estado.dto.CambioEstadoResponse;
import cl.duocuc.pedidoservice.client.producto.dto.ProductoResponse;
import cl.duocuc.pedidoservice.common.exception.ResourceNotFoundException;
import cl.duocuc.pedidoservice.common.exception.ServiceUnavailableException;
import cl.duocuc.pedidoservice.dto.EstadoRequest;
import cl.duocuc.pedidoservice.dto.ItemPedidoRequest;
import cl.duocuc.pedidoservice.dto.PedidoRequest;
import cl.duocuc.pedidoservice.dto.PedidoResponse;
import cl.duocuc.pedidoservice.model.ItemPedido;
import cl.duocuc.pedidoservice.model.Pedido;
import cl.duocuc.pedidoservice.repository.PedidoRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ClienteFeignClient clienteFeignClient;

    @Mock
    private ProductoFeignClient productoFeignClient;

    @Mock
    private EstadoFeignClient estadoFeignClient;

    @InjectMocks
    private PedidoService pedidoService;

    @Test
    void crearPedidoCorrectamenteCuandoClienteYProductoExisten() {
        // Given
        PedidoRequest request = new PedidoRequest(
                1L,
                "cola",
                "rm",
                List.of(new ItemPedidoRequest(10L, 2))
        );
        ClienteResponse cliente = new ClienteResponse(1L, "Maria Perez", "12.345.678-9", "maria@test.cl", null, null, null, null, LocalDate.now());
        ProductoResponse producto = new ProductoResponse(10L, "Resma Carta", "Papel carta", "PAPEL", 3500.0, 20);

        when(clienteFeignClient.obtenerClientePorId(1L)).thenReturn(cliente);
        when(productoFeignClient.obtenerProductoPorId(10L)).thenReturn(producto);
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido pedido = invocation.getArgument(0);
            pedido.setNumeroPedido(1001L);
            return pedido;
        });

        // When
        PedidoResponse response = pedidoService.crearPedido(request);

        // Then
        assertEquals(1001L, response.getNumeroPedido());
        assertEquals(1L, response.getClienteId());
        assertEquals("COLA", response.getEstado());
        assertEquals("RM", response.getTipoDespacho());
        assertEquals(7000.0, response.getMonto());
        assertEquals(1, response.getItems().size());
        assertEquals(10L, response.getItems().get(0).getProductoId());
        assertEquals("Resma Carta", response.getItems().get(0).getNombreProducto());
        assertEquals(2, response.getItems().get(0).getCantidad());
        assertEquals(3500.0, response.getItems().get(0).getPrecioUnitario());
        assertEquals(7000.0, response.getItems().get(0).getSubtotal());

        verify(clienteFeignClient).obtenerClientePorId(1L);
        verify(productoFeignClient).obtenerProductoPorId(10L);
        verify(pedidoRepository).save(argThat(pedido ->
                pedido.getClienteId().equals(1L)
                        && pedido.getEstado().equals("COLA")
                        && pedido.getTipoDespacho().equals("RM")
                        && pedido.getMonto().equals(7000.0)
                        && pedido.getItems().size() == 1
        ));
        verify(estadoFeignClient).registrarCambioEstado(argThat(cambio ->
                cambio.getNumeroPedido().equals(1001L)
                        && cambio.getEstadoAnterior().equals("SIN_ESTADO")
                        && cambio.getEstadoNuevo().equals("COLA")
        ));
    }

    @Test
    void crearPedidoLanzaErrorCuandoClienteNoExiste() {
        // Given
        PedidoRequest request = new PedidoRequest(
                99L,
                "COLA",
                "RM",
                List.of(new ItemPedidoRequest(10L, 1))
        );
        when(clienteFeignClient.obtenerClientePorId(99L)).thenThrow(notFoundException());

        // When
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> pedidoService.crearPedido(request));

        // Then
        assertTrue(exception.getMessage().contains("Cliente no encontrado con id: 99"));
        verify(clienteFeignClient).obtenerClientePorId(99L);
        verify(productoFeignClient, never()).obtenerProductoPorId(any());
        verify(pedidoRepository, never()).save(any());
        verify(estadoFeignClient, never()).registrarCambioEstado(any());
    }

    @Test
    void crearPedidoLanzaErrorCuandoProductoNoExiste() {
        // Given
        PedidoRequest request = new PedidoRequest(
                1L,
                "COLA",
                "RM",
                List.of(new ItemPedidoRequest(404L, 1))
        );
        ClienteResponse cliente = new ClienteResponse(1L, "Maria Perez", "12.345.678-9", "maria@test.cl", null, null, null, null, LocalDate.now());

        when(clienteFeignClient.obtenerClientePorId(1L)).thenReturn(cliente);
        when(productoFeignClient.obtenerProductoPorId(404L)).thenThrow(notFoundException());

        // When
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> pedidoService.crearPedido(request));

        // Then
        assertTrue(exception.getMessage().contains("Producto no encontrado con id: 404"));
        verify(clienteFeignClient).obtenerClientePorId(1L);
        verify(productoFeignClient).obtenerProductoPorId(404L);
        verify(pedidoRepository, never()).save(any());
        verify(estadoFeignClient, never()).registrarCambioEstado(any());
    }

    @Test
    void actualizarEstadoCorrectamente() {
        // Given
        Pedido pedido = new Pedido();
        pedido.setNumeroPedido(1001L);
        pedido.setClienteId(1L);
        pedido.setEstado("COLA");
        pedido.setTipoDespacho("RM");
        pedido.setMonto(7000.0);
        pedido.setFechaCreacion(LocalDateTime.now());

        when(pedidoRepository.findById(1001L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        PedidoResponse response = pedidoService.actualizarEstado(1001L, new EstadoRequest("produccion"));

        // Then
        assertEquals(1001L, response.getNumeroPedido());
        assertEquals("PRODUCCION", response.getEstado());

        verify(pedidoRepository).findById(1001L);
        verify(pedidoRepository).save(argThat(actualizado -> actualizado.getEstado().equals("PRODUCCION")));
        verify(estadoFeignClient).registrarCambioEstado(argThat(cambio ->
                cambio.getNumeroPedido().equals(1001L)
                        && cambio.getEstadoAnterior().equals("COLA")
                        && cambio.getEstadoNuevo().equals("PRODUCCION")
        ));
    }

    @Test
    void actualizarEstadoLanzaErrorCuandoPedidoNoExiste() {
        // Given
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> pedidoService.actualizarEstado(999L, new EstadoRequest("LISTO")));

        // Then
        assertTrue(exception.getMessage().contains("Pedido no encontrado con numero 999"));
        verify(pedidoRepository).findById(999L);
        verify(pedidoRepository, never()).save(any());
        verify(estadoFeignClient, never()).registrarCambioEstado(any());
    }

    @Test
    void listarPedidosRetornaResponsesYDejaPedidoEnCache() {
        // Given
        Pedido pedido = pedidoConItem(1001L, 1L, "COLA", "RM", 7000.0);
        when(pedidoRepository.findAll()).thenReturn(List.of(pedido));

        // When
        List<PedidoResponse> responses = pedidoService.listarPedidos();
        PedidoResponse responseCache = pedidoService.obtenerPedidoPorNumero(1001L);

        // Then
        assertEquals(1, responses.size());
        assertEquals(1001L, responses.get(0).getNumeroPedido());
        assertEquals("COLA", responses.get(0).getEstado());
        assertEquals(1, responses.get(0).getItems().size());
        assertEquals(1001L, responseCache.getNumeroPedido());

        verify(pedidoRepository).findAll();
        verify(pedidoRepository, never()).findById(1001L);
    }

    @Test
    void obtenerPedidoPorNumeroBuscaEnRepositorioCuandoNoEstaEnCache() {
        // Given
        Pedido pedido = pedidoConItem(1002L, 2L, "LISTO", "RETIRO", 1500.0);
        when(pedidoRepository.findById(1002L)).thenReturn(Optional.of(pedido));

        // When
        PedidoResponse response = pedidoService.obtenerPedidoPorNumero(1002L);

        // Then
        assertEquals(1002L, response.getNumeroPedido());
        assertEquals(2L, response.getClienteId());
        assertEquals("LISTO", response.getEstado());
        assertEquals(1, response.getItems().size());

        verify(pedidoRepository).findById(1002L);
    }

    @Test
    void obtenerPedidoPorNumeroLanzaErrorCuandoNoExiste() {
        // Given
        when(pedidoRepository.findById(404L)).thenReturn(Optional.empty());

        // When
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> pedidoService.obtenerPedidoPorNumero(404L));

        // Then
        assertTrue(exception.getMessage().contains("Pedido no encontrado con numero 404"));
        verify(pedidoRepository).findById(404L);
    }

    @Test
    void obtenerPedidoPorNumeroStrAceptaNumeroConEspacios() {
        // Given
        Pedido pedido = pedidoConItem(1003L, 3L, "DESPACHADO", "RM", 3000.0);
        when(pedidoRepository.findById(1003L)).thenReturn(Optional.of(pedido));

        // When
        PedidoResponse response = pedidoService.obtenerPedidoPorNumeroStr(" 1003 ");

        // Then
        assertEquals(1003L, response.getNumeroPedido());
        assertEquals("DESPACHADO", response.getEstado());
        verify(pedidoRepository).findById(1003L);
    }

    @Test
    void obtenerPedidoPorNumeroStrLanzaErrorCuandoEsNull() {
        // Given
        String numeroPedido = null;

        // When
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> pedidoService.obtenerPedidoPorNumeroStr(numeroPedido));

        // Then
        assertTrue(exception.getMessage().contains("Numero de pedido inválido: null"));
        verify(pedidoRepository, never()).findById(any());
    }

    @Test
    void obtenerPedidoPorNumeroStrLanzaErrorCuandoNoEsNumerico() {
        // Given
        String numeroPedido = "ABC-1";

        // When
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> pedidoService.obtenerPedidoPorNumeroStr(numeroPedido));

        // Then
        assertTrue(exception.getMessage().contains("Numero de pedido inválido: ABC-1"));
        verify(pedidoRepository, never()).findById(any());
    }

    @Test
    void listarPedidosPorClienteRetornaResponsesDelRepositorio() {
        // Given
        Pedido pedido = pedidoConItem(1004L, 7L, "ENTREGADO", "RM", 2500.0);
        when(pedidoRepository.findByClienteId(7L)).thenReturn(List.of(pedido));

        // When
        List<PedidoResponse> responses = pedidoService.listarPedidosPorCliente(7L);

        // Then
        assertEquals(1, responses.size());
        assertEquals(7L, responses.get(0).getClienteId());
        assertEquals("ENTREGADO", responses.get(0).getEstado());
        verify(pedidoRepository).findByClienteId(7L);
    }

    @Test
    void actualizarEstadoLanzaErrorCuandoEstadoEsNull() {
        // Given
        EstadoRequest request = new EstadoRequest(null);

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> pedidoService.actualizarEstado(1001L, request));

        // Then
        assertTrue(exception.getMessage().contains("Estado no válido"));
        verify(pedidoRepository, never()).findById(any());
        verify(pedidoRepository, never()).save(any());
        verify(estadoFeignClient, never()).registrarCambioEstado(any());
    }

    @Test
    void actualizarEstadoLanzaErrorCuandoEstadoNoEsPermitido() {
        // Given
        EstadoRequest request = new EstadoRequest("cancelado");

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> pedidoService.actualizarEstado(1001L, request));

        // Then
        assertTrue(exception.getMessage().contains("Estado no válido"));
        verify(pedidoRepository, never()).findById(any());
        verify(pedidoRepository, never()).save(any());
        verify(estadoFeignClient, never()).registrarCambioEstado(any());
    }

    @Test
    void actualizarEstadoNoGuardaCuandoEstadoNoCambia() {
        // Given
        Pedido pedido = pedidoConItem(1005L, 1L, "PRODUCCION", "RM", 7000.0);
        when(pedidoRepository.findById(1005L)).thenReturn(Optional.of(pedido));

        // When
        PedidoResponse response = pedidoService.actualizarEstado(1005L, new EstadoRequest(" produccion "));

        // Then
        assertEquals("PRODUCCION", response.getEstado());
        verify(pedidoRepository).findById(1005L);
        verify(pedidoRepository, never()).save(any());
        verify(estadoFeignClient, never()).registrarCambioEstado(any());
    }

    @Test
    void listarHistorialRetornaCambiosDeEstado() {
        // Given
        CambioEstadoResponse cambio = new CambioEstadoResponse(1L, 1001L, "COLA", "PRODUCCION", LocalDateTime.now(), "Cambio automático de estado");
        when(estadoFeignClient.listarCambiosPorPedido(1001L)).thenReturn(List.of(cambio));

        // When
        List<CambioEstadoResponse> historial = pedidoService.listarHistorial(1001L);

        // Then
        assertEquals(1, historial.size());
        assertEquals("PRODUCCION", historial.get(0).getEstadoNuevo());
        verify(estadoFeignClient).listarCambiosPorPedido(1001L);
    }

    @Test
    void listarHistorialLanzaErrorCuandoFeignRetornaNotFound() {
        // Given
        when(estadoFeignClient.listarCambiosPorPedido(404L)).thenThrow(notFoundException());

        // When
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> pedidoService.listarHistorial(404L));

        // Then
        assertTrue(exception.getMessage().contains("Historial de estados no encontrado para el pedido: 404"));
        verify(estadoFeignClient).listarCambiosPorPedido(404L);
    }

    @Test
    void listarHistorialLanzaServiceUnavailableCuandoFeignFalla() {
        // Given
        when(estadoFeignClient.listarCambiosPorPedido(1001L)).thenThrow(feignException(500, "Internal Server Error"));

        // When
        ServiceUnavailableException exception = assertThrows(ServiceUnavailableException.class,
                () -> pedidoService.listarHistorial(1001L));

        // Then
        assertTrue(exception.getMessage().contains("No se pudo conectar con el microservicio correspondiente"));
        verify(estadoFeignClient).listarCambiosPorPedido(1001L);
    }

    @Test
    void eliminarPedidoEliminaCuandoExiste() {
        // Given
        when(pedidoRepository.existsById(1001L)).thenReturn(true);

        // When
        pedidoService.eliminarPedido(1001L);

        // Then
        verify(pedidoRepository).existsById(1001L);
        verify(pedidoRepository).deleteById(1001L);
    }

    @Test
    void eliminarPedidoLanzaErrorCuandoNoExiste() {
        // Given
        when(pedidoRepository.existsById(404L)).thenReturn(false);

        // When
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> pedidoService.eliminarPedido(404L));

        // Then
        assertTrue(exception.getMessage().contains("Pedido no encontrado con numero 404"));
        verify(pedidoRepository).existsById(404L);
        verify(pedidoRepository, never()).deleteById(any());
    }

    @Test
    void crearPedidoLanzaServiceUnavailableCuandoClienteFeignFalla() {
        // Given
        PedidoRequest request = new PedidoRequest(
                1L,
                "COLA",
                "RM",
                List.of(new ItemPedidoRequest(10L, 1))
        );
        when(clienteFeignClient.obtenerClientePorId(1L)).thenThrow(feignException(503, "Service Unavailable"));

        // When
        ServiceUnavailableException exception = assertThrows(ServiceUnavailableException.class,
                () -> pedidoService.crearPedido(request));

        // Then
        assertTrue(exception.getMessage().contains("No se pudo conectar con el microservicio correspondiente"));
        verify(clienteFeignClient).obtenerClientePorId(1L);
        verify(productoFeignClient, never()).obtenerProductoPorId(any());
        verify(pedidoRepository, never()).save(any());
        verify(estadoFeignClient, never()).registrarCambioEstado(any());
    }

    @Test
    void crearPedidoLanzaServiceUnavailableCuandoProductoFeignFalla() {
        // Given
        PedidoRequest request = new PedidoRequest(
                1L,
                "COLA",
                "RM",
                List.of(new ItemPedidoRequest(10L, 1))
        );
        when(clienteFeignClient.obtenerClientePorId(1L)).thenReturn(cliente());
        when(productoFeignClient.obtenerProductoPorId(10L)).thenThrow(feignException(500, "Internal Server Error"));

        // When
        ServiceUnavailableException exception = assertThrows(ServiceUnavailableException.class,
                () -> pedidoService.crearPedido(request));

        // Then
        assertTrue(exception.getMessage().contains("No se pudo conectar con el microservicio correspondiente"));
        verify(clienteFeignClient).obtenerClientePorId(1L);
        verify(productoFeignClient).obtenerProductoPorId(10L);
        verify(pedidoRepository, never()).save(any());
        verify(estadoFeignClient, never()).registrarCambioEstado(any());
    }

    @Test
    void crearPedidoLanzaResourceNotFoundCuandoEstadoFeignRetornaNotFound() {
        // Given
        PedidoRequest request = new PedidoRequest(
                1L,
                "COLA",
                "RM",
                List.of(new ItemPedidoRequest(10L, 1))
        );
        when(clienteFeignClient.obtenerClientePorId(1L)).thenReturn(cliente());
        when(productoFeignClient.obtenerProductoPorId(10L)).thenReturn(producto(10L, "Resma Carta", 3500.0));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido pedido = invocation.getArgument(0);
            pedido.setNumeroPedido(1001L);
            return pedido;
        });
        when(estadoFeignClient.registrarCambioEstado(any())).thenThrow(notFoundException());

        // When
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> pedidoService.crearPedido(request));

        // Then
        assertTrue(exception.getMessage().contains("No se pudo registrar el cambio de estado para el pedido: 1001"));
        verify(clienteFeignClient).obtenerClientePorId(1L);
        verify(productoFeignClient).obtenerProductoPorId(10L);
        verify(pedidoRepository).save(any(Pedido.class));
        verify(estadoFeignClient).registrarCambioEstado(any());
    }

    @Test
    void actualizarEstadoLanzaServiceUnavailableCuandoEstadoFeignFalla() {
        // Given
        Pedido pedido = pedidoConItem(1006L, 1L, "COLA", "RM", 7000.0);
        when(pedidoRepository.findById(1006L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(estadoFeignClient.registrarCambioEstado(any())).thenThrow(feignException(503, "Service Unavailable"));

        // When
        ServiceUnavailableException exception = assertThrows(ServiceUnavailableException.class,
                () -> pedidoService.actualizarEstado(1006L, new EstadoRequest("LISTO")));

        // Then
        assertTrue(exception.getMessage().contains("No se pudo conectar con el microservicio correspondiente"));
        verify(pedidoRepository).findById(1006L);
        verify(pedidoRepository).save(argThat(actualizado -> actualizado.getEstado().equals("LISTO")));
        verify(estadoFeignClient).registrarCambioEstado(argThat(cambio ->
                cambio.getNumeroPedido().equals(1006L)
                        && cambio.getEstadoAnterior().equals("COLA")
                        && cambio.getEstadoNuevo().equals("LISTO")
        ));
    }

    private FeignException notFoundException() {
        return feignException(404, "Not Found");
    }

    private FeignException feignException(int status, String reason) {
        Request request = Request.create(
                Request.HttpMethod.GET,
                "/error",
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

    private Pedido pedidoConItem(Long numeroPedido, Long clienteId, String estado, String tipoDespacho, Double monto) {
        Pedido pedido = new Pedido();
        pedido.setNumeroPedido(numeroPedido);
        pedido.setClienteId(clienteId);
        pedido.setEstado(estado);
        pedido.setTipoDespacho(tipoDespacho);
        pedido.setMonto(monto);
        pedido.setFechaCreacion(LocalDateTime.now());

        ItemPedido item = new ItemPedido();
        item.setId(1L);
        item.setProductoId(10L);
        item.setNombreProducto("Resma Carta");
        item.setCantidad(2);
        item.setPrecioUnitario(monto / 2);
        item.setSubtotal(monto);
        item.setPedido(pedido);
        pedido.getItems().add(item);

        return pedido;
    }

    private ClienteResponse cliente() {
        return new ClienteResponse(1L, "Maria Perez", "12.345.678-9", "maria@test.cl", null, null, null, null, LocalDate.now());
    }

    private ProductoResponse producto(Long id, String nombre, Double precio) {
        return new ProductoResponse(id, nombre, "Papel carta", "PAPEL", precio, 20);
    }
}
