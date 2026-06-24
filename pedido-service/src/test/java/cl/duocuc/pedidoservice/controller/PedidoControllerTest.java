package cl.duocuc.pedidoservice.controller;

import cl.duocuc.pedidoservice.client.estado.dto.CambioEstadoResponse;
import cl.duocuc.pedidoservice.common.exception.ResourceNotFoundException;
import cl.duocuc.pedidoservice.common.exception.ServiceUnavailableException;
import cl.duocuc.pedidoservice.config.JwtUtil;
import cl.duocuc.pedidoservice.dto.EstadoRequest;
import cl.duocuc.pedidoservice.dto.ItemPedidoRequest;
import cl.duocuc.pedidoservice.dto.PedidoRequest;
import cl.duocuc.pedidoservice.dto.PedidoResponse;
import cl.duocuc.pedidoservice.service.PedidoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PedidoController.class)
@AutoConfigureMockMvc(addFilters = false)
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private PedidoService pedidoService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    void crearPedidoRetornaCreatedCuandoRequestEsValido() throws Exception {
        // Given
        PedidoRequest request = requestValido();
        PedidoResponse response = response(1001L, "COLA");
        when(pedidoService.crearPedido(any(PedidoRequest.class))).thenReturn(response);

        // When / Then
        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/pedidos/1001"))
                .andExpect(jsonPath("$.numeroPedido").value(1001))
                .andExpect(jsonPath("$.clienteId").value(1))
                .andExpect(jsonPath("$.estado").value("COLA"));

        verify(pedidoService).crearPedido(any(PedidoRequest.class));
    }

    @Test
    void listarPedidosRetornaListado() throws Exception {
        // Given
        when(pedidoService.listarPedidos()).thenReturn(List.of(response(1001L, "COLA")));

        // When / Then
        mockMvc.perform(get("/api/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].numeroPedido").value(1001))
                .andExpect(jsonPath("$[0].estado").value("COLA"));

        verify(pedidoService).listarPedidos();
    }

    @Test
    void obtenerPedidoPorNumeroRetornaPedidoCuandoExiste() throws Exception {
        // Given
        when(pedidoService.obtenerPedidoPorNumero(1001L)).thenReturn(response(1001L, "COLA"));

        // When / Then
        mockMvc.perform(get("/api/pedidos/{numeroPedido}", 1001L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroPedido").value(1001))
                .andExpect(jsonPath("$.estado").value("COLA"));

        verify(pedidoService).obtenerPedidoPorNumero(1001L);
    }

    @Test
    void obtenerPedidoPorNumeroRetornaNotFoundCuandoNoExiste() throws Exception {
        // Given
        when(pedidoService.obtenerPedidoPorNumero(404L))
                .thenThrow(new ResourceNotFoundException("Pedido no encontrado con numero 404"));

        // When / Then
        mockMvc.perform(get("/api/pedidos/{numeroPedido}", 404L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensaje").value("Pedido no encontrado con numero 404"));

        verify(pedidoService).obtenerPedidoPorNumero(404L);
    }

    @Test
    void obtenerPedidoPorNumeroStrRetornaPedidoCuandoExiste() throws Exception {
        // Given
        when(pedidoService.obtenerPedidoPorNumeroStr("1001")).thenReturn(response(1001L, "COLA"));

        // When / Then
        mockMvc.perform(get("/api/pedidos/numero/{numeroPedido}", "1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroPedido").value(1001));

        verify(pedidoService).obtenerPedidoPorNumeroStr("1001");
    }

    @Test
    void listarPedidosPorClienteRetornaListado() throws Exception {
        // Given
        when(pedidoService.listarPedidosPorCliente(1L)).thenReturn(List.of(response(1001L, "COLA")));

        // When / Then
        mockMvc.perform(get("/api/pedidos/cliente/{clienteId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clienteId").value(1))
                .andExpect(jsonPath("$[0].numeroPedido").value(1001));

        verify(pedidoService).listarPedidosPorCliente(1L);
    }

    @Test
    void actualizarEstadoRetornaPedidoActualizado() throws Exception {
        // Given
        EstadoRequest request = new EstadoRequest("PRODUCCION");
        when(pedidoService.actualizarEstado(eq(1001L), any(EstadoRequest.class)))
                .thenReturn(response(1001L, "PRODUCCION"));

        // When / Then
        mockMvc.perform(post("/api/pedidos/{numeroPedido}/estado", 1001L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroPedido").value(1001))
                .andExpect(jsonPath("$.estado").value("PRODUCCION"));

        verify(pedidoService).actualizarEstado(eq(1001L), any(EstadoRequest.class));
    }

    @Test
    void actualizarEstadoRetornaBadRequestCuandoServicioRechazaEstado() throws Exception {
        // Given
        EstadoRequest request = new EstadoRequest("CANCELADO");
        when(pedidoService.actualizarEstado(eq(1001L), any(EstadoRequest.class)))
                .thenThrow(new IllegalArgumentException("Estado no válido. Valores permitidos: [COLA]"));

        // When / Then
        mockMvc.perform(post("/api/pedidos/{numeroPedido}/estado", 1001L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensaje", containsString("Estado no válido")));

        verify(pedidoService).actualizarEstado(eq(1001L), any(EstadoRequest.class));
    }

    @Test
    void crearPedidoRetornaBadRequestCuandoRequestEsInvalido() throws Exception {
        // Given
        PedidoRequest request = new PedidoRequest(null, "COLA", "RM", List.of(new ItemPedidoRequest(10L, 1)));

        // When / Then
        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensaje", containsString("clienteId")));
    }

    @Test
    void obtenerHistorialRetornaCambios() throws Exception {
        // Given
        CambioEstadoResponse cambio = new CambioEstadoResponse(1L, 1001L, "COLA", "PRODUCCION",
                LocalDateTime.of(2026, 5, 22, 10, 0), "Cambio automático de estado");
        when(pedidoService.listarHistorial(1001L)).thenReturn(List.of(cambio));

        // When / Then
        mockMvc.perform(get("/api/pedidos/{numeroPedido}/historial", 1001L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].numeroPedido").value(1001))
                .andExpect(jsonPath("$[0].estadoNuevo").value("PRODUCCION"));

        verify(pedidoService).listarHistorial(1001L);
    }

    @Test
    void obtenerHistorialRetornaServiceUnavailableCuandoServicioFalla() throws Exception {
        // Given
        when(pedidoService.listarHistorial(1001L))
                .thenThrow(new ServiceUnavailableException("No se pudo conectar con el microservicio correspondiente"));

        // When / Then
        mockMvc.perform(get("/api/pedidos/{numeroPedido}/historial", 1001L))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value(503))
                .andExpect(jsonPath("$.mensaje").value("No se pudo conectar con el microservicio correspondiente"));

        verify(pedidoService).listarHistorial(1001L);
    }

    @Test
    void eliminarPedidoRetornaNoContentCuandoExiste() throws Exception {
        // Given
        doNothing().when(pedidoService).eliminarPedido(1001L);

        // When / Then
        mockMvc.perform(delete("/api/pedidos/{numeroPedido}", 1001L))
                .andExpect(status().isNoContent());

        verify(pedidoService).eliminarPedido(1001L);
    }

    private PedidoRequest requestValido() {
        return new PedidoRequest(1L, "COLA", "RM", List.of(new ItemPedidoRequest(10L, 2)));
    }

    private PedidoResponse response(Long numeroPedido, String estado) {
        return new PedidoResponse(numeroPedido, 1L, estado, "RM", 7000.0,
                LocalDateTime.of(2026, 5, 22, 10, 0), List.of());
    }
}
