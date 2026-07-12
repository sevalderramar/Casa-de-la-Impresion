package cl.duocuc.fabricacion.controller;

import cl.duocuc.fabricacion.dto.OrdenFabricacionRequest;
import cl.duocuc.fabricacion.dto.OrdenFabricacionResponse;
import cl.duocuc.fabricacion.dto.UpdateEstadoFabricacionRequest;
import cl.duocuc.fabricacion.entity.EstadoFabricacion;
import cl.duocuc.fabricacion.exception.ConflictException;
import cl.duocuc.fabricacion.exception.PedidoNoEncontradoException;
import cl.duocuc.fabricacion.exception.ResourceNotFoundException;
import cl.duocuc.fabricacion.exception.ServiceUnavailableException;
import cl.duocuc.fabricacion.handler.GlobalExceptionHandler;
import cl.duocuc.fabricacion.service.OrdenFabricacionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrdenFabricacionControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private OrdenFabricacionService ordenService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new OrdenFabricacionController(ordenService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void pingRetornaPong() throws Exception {
        // When / Then
        mockMvc.perform(get("/api/fabricacion/ping"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("pong"))
                .andExpect(jsonPath("$.data").value("fabricacion-service"))
                .andExpect(jsonPath("$.exitoso").value(true));
    }

    @Test
    void crearOrdenRetornaCreatedCuandoRequestEsValido() throws Exception {
        // Given
        when(ordenService.crearOrden(any(OrdenFabricacionRequest.class))).thenReturn(response(1L, 1001L, EstadoFabricacion.EN_PROCESO));

        // When / Then
        mockMvc.perform(post("/api/fabricacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido(1001L))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensaje").value("Orden creada"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.numeroPedido").value(1001))
                .andExpect(jsonPath("$.data.estadoFabricacion").value("EN_PROCESO"));

        verify(ordenService).crearOrden(any(OrdenFabricacionRequest.class));
    }

    @Test
    void crearOrdenRetornaBadRequestCuandoRequestEsInvalido() throws Exception {
        // Given
        OrdenFabricacionRequest request = new OrdenFabricacionRequest(null, "", "Inicio");

        // When / Then
        mockMvc.perform(post("/api/fabricacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensaje", containsString("numeroPedido")));
    }

    @Test
    void crearOrdenResponde404CuandoPedidoRemotoNoExiste() throws Exception {
        // Given
        when(ordenService.crearOrden(any(OrdenFabricacionRequest.class)))
                .thenThrow(new PedidoNoEncontradoException("Pedido no encontrado: 404"));

        // When / Then
        mockMvc.perform(post("/api/fabricacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido(404L))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensaje").value("Pedido no encontrado: 404"));

        verify(ordenService).crearOrden(any(OrdenFabricacionRequest.class));
    }

    @Test
    void crearOrdenResponde409CuandoYaExisteOrden() throws Exception {
        // Given
        when(ordenService.crearOrden(any(OrdenFabricacionRequest.class)))
                .thenThrow(new ConflictException("Ya existe una orden para el pedido: 1001"));

        // When / Then
        mockMvc.perform(post("/api/fabricacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido(1001L))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.mensaje").value("Ya existe una orden para el pedido: 1001"));
    }

    @Test
    void crearOrdenResponde503CuandoPedidoServiceRetorna5xx() throws Exception {
        // Given
        when(ordenService.crearOrden(any(OrdenFabricacionRequest.class)))
                .thenThrow(new ServiceUnavailableException("No fue posible comunicarse con pedido-service (HTTP 500)"));

        // When / Then
        mockMvc.perform(post("/api/fabricacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido(1001L))))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value(503))
                .andExpect(jsonPath("$.mensaje").value("No fue posible comunicarse con pedido-service (HTTP 500)"));

        verify(ordenService).crearOrden(any(OrdenFabricacionRequest.class));
    }

    @Test
    void obtenerOrdenRetornaOrdenCuandoExiste() throws Exception {
        // Given
        when(ordenService.obtenerOrden(1L)).thenReturn(response(1L, 1001L, EstadoFabricacion.EN_PROCESO));

        // When / Then
        mockMvc.perform(get("/api/fabricacion/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Orden encontrada"))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(ordenService).obtenerOrden(1L);
    }

    @Test
    void obtenerOrdenRetornaNotFoundCuandoNoExiste() throws Exception {
        // Given
        when(ordenService.obtenerOrden(99L)).thenThrow(new ResourceNotFoundException("Orden no encontrada: 99"));

        // When / Then
        mockMvc.perform(get("/api/fabricacion/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensaje").value("Orden no encontrada: 99"));
    }

    @Test
    void actualizarEstadoRetornaOrdenActualizada() throws Exception {
        // Given
        when(ordenService.actualizarEstado(any(Long.class), any(UpdateEstadoFabricacionRequest.class)))
                .thenReturn(response(1L, 1001L, EstadoFabricacion.TERMINADO));

        // When / Then
        mockMvc.perform(patch("/api/fabricacion/{id}/estado", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateEstadoFabricacionRequest(EstadoFabricacion.TERMINADO, "OK", "op"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Estado actualizado"))
                .andExpect(jsonPath("$.data.estadoFabricacion").value("TERMINADO"));

        verify(ordenService).actualizarEstado(any(Long.class), any(UpdateEstadoFabricacionRequest.class));
    }

    @Test
    void actualizarEstadoRetornaBadRequestCuandoRequestEsInvalido() throws Exception {
        // Given
        UpdateEstadoFabricacionRequest request = new UpdateEstadoFabricacionRequest(null, "OK", "op");

        // When / Then
        mockMvc.perform(patch("/api/fabricacion/{id}/estado", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensaje", containsString("nuevoEstado")));
    }

    private OrdenFabricacionRequest requestValido(Long numeroPedido) {
        return new OrdenFabricacionRequest(numeroPedido, "operador-01", "Inicio de fabricacion");
    }

    private OrdenFabricacionResponse response(Long id, Long numeroPedido, EstadoFabricacion estado) {
        return new OrdenFabricacionResponse(
                id,
                numeroPedido,
                estado,
                LocalDateTime.of(2026, 5, 22, 10, 0),
                estado == EstadoFabricacion.TERMINADO ? LocalDateTime.of(2026, 5, 22, 12, 0) : null,
                LocalDateTime.of(2026, 5, 22, 10, 0),
                null,
                "Inicio",
                "operador-01"
        );
    }
}
