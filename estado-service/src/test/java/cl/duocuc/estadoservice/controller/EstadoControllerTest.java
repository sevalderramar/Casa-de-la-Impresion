package cl.duocuc.estadoservice.controller;

import cl.duocuc.estadoservice.common.exception.ResourceNotFoundException;
import cl.duocuc.estadoservice.config.JwtUtil;
import cl.duocuc.estadoservice.dto.CambioEstadoRequest;
import cl.duocuc.estadoservice.dto.CambioEstadoResponse;
import cl.duocuc.estadoservice.service.EstadoService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EstadoController.class)
@AutoConfigureMockMvc(addFilters = false)
class EstadoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private EstadoService estadoService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    void crearCambioEstadoRetornaCreatedCuandoRequestEsValido() throws Exception {
        // Given
        CambioEstadoRequest request = requestValido();
        when(estadoService.registrarCambioEstado(any(CambioEstadoRequest.class))).thenReturn(response(1L, "COLA", "PRODUCCION"));

        // When / Then
        mockMvc.perform(post("/api/estados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.numeroPedido").value(1001))
                .andExpect(jsonPath("$.estadoAnterior").value("COLA"))
                .andExpect(jsonPath("$.estadoNuevo").value("PRODUCCION"));

        verify(estadoService).registrarCambioEstado(any(CambioEstadoRequest.class));
    }

    @Test
    void crearCambioEstadoRetornaBadRequestCuandoRequestEsInvalido() throws Exception {
        // Given
        CambioEstadoRequest request = new CambioEstadoRequest(null, "COLA", "PRODUCCION", "Inicio de fabricacion");

        // When / Then
        mockMvc.perform(post("/api/estados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensaje", containsString("numeroPedido")));
    }

    @Test
    void listarCambiosPorPedidoRetornaListado() throws Exception {
        // Given
        when(estadoService.listarCambiosPorPedido(1001L)).thenReturn(List.of(response(1L, "SIN_ESTADO", "COLA")));

        // When / Then
        mockMvc.perform(get("/api/estados/pedido/{numeroPedido}", 1001L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].numeroPedido").value(1001))
                .andExpect(jsonPath("$[0].estadoNuevo").value("COLA"));

        verify(estadoService).listarCambiosPorPedido(1001L);
    }

    @Test
    void listarCambiosPorPedidoRetornaNotFoundCuandoNoHayCambios() throws Exception {
        // Given
        when(estadoService.listarCambiosPorPedido(404L))
                .thenThrow(new ResourceNotFoundException("No se encontraron cambios de estado para el pedido con numero: 404"));

        // When / Then
        mockMvc.perform(get("/api/estados/pedido/{numeroPedido}", 404L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensaje").value("No se encontraron cambios de estado para el pedido con numero: 404"));

        verify(estadoService).listarCambiosPorPedido(404L);
    }

    @Test
    void obtenerUltimoEstadoRetornaUltimoCambio() throws Exception {
        // Given
        when(estadoService.obtenerUltimoEstadoPorPedido(1001L)).thenReturn(response(2L, "COLA", "PRODUCCION"));

        // When / Then
        mockMvc.perform(get("/api/estados/pedido/{numeroPedido}/ultimo", 1001L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.numeroPedido").value(1001))
                .andExpect(jsonPath("$.estadoNuevo").value("PRODUCCION"));

        verify(estadoService).obtenerUltimoEstadoPorPedido(1001L);
    }

    @Test
    void obtenerUltimoEstadoRetornaNotFoundCuandoNoHayCambios() throws Exception {
        // Given
        when(estadoService.obtenerUltimoEstadoPorPedido(404L))
                .thenThrow(new ResourceNotFoundException("No se encontraron cambios de estado para el pedido con numero: 404"));

        // When / Then
        mockMvc.perform(get("/api/estados/pedido/{numeroPedido}/ultimo", 404L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensaje").value("No se encontraron cambios de estado para el pedido con numero: 404"));

        verify(estadoService).obtenerUltimoEstadoPorPedido(404L);
    }

    private CambioEstadoRequest requestValido() {
        return new CambioEstadoRequest(1001L, "COLA", "PRODUCCION", "Inicio de fabricacion");
    }

    private CambioEstadoResponse response(Long id, String estadoAnterior, String estadoNuevo) {
        return new CambioEstadoResponse(id, 1001L, estadoAnterior, estadoNuevo,
                LocalDateTime.of(2026, 5, 22, 10, 0), "Cambio de estado");
    }
}
