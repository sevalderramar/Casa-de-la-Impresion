package cl.duocuc.pedidoservice.service;

import cl.duocuc.pedidoservice.client.ClienteFeignClient;
import cl.duocuc.pedidoservice.client.EstadoFeignClient;
import cl.duocuc.pedidoservice.client.ProductoFeignClient;
import cl.duocuc.pedidoservice.client.cliente.dto.ClienteResponse;
import cl.duocuc.pedidoservice.client.producto.dto.ProductoResponse;
import cl.duocuc.pedidoservice.common.exception.ResourceNotFoundException;
import cl.duocuc.pedidoservice.dto.EstadoRequest;
import cl.duocuc.pedidoservice.dto.ItemPedidoRequest;
import cl.duocuc.pedidoservice.dto.PedidoRequest;
import cl.duocuc.pedidoservice.dto.PedidoResponse;
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

    private FeignException notFoundException() {
        Request request = Request.create(
                Request.HttpMethod.GET,
                "/not-found",
                Map.of(),
                null,
                StandardCharsets.UTF_8,
                null
        );
        Response response = Response.builder()
                .request(request)
                .status(404)
                .reason("Not Found")
                .headers(Map.of())
                .body(new byte[0])
                .build();

        return FeignException.errorStatus("test", response);
    }
}
