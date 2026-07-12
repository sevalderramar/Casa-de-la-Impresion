package cl.duocuc.despachoservice.controller;

import cl.duocuc.despachoservice.common.exception.ConflictException;
import cl.duocuc.despachoservice.common.exception.ResourceNotFoundException;
import cl.duocuc.despachoservice.common.exception.ServiceUnavailableException;
import cl.duocuc.despachoservice.config.JwtUtil;
import cl.duocuc.despachoservice.dto.DespachoRequest;
import cl.duocuc.despachoservice.dto.DespachoResponse;
import cl.duocuc.despachoservice.service.DespachoService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DespachoController.class)
@AutoConfigureMockMvc(addFilters = false)
class DespachoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private DespachoService despachoService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    void crearDespachoRetornaCreatedCuandoRequestEsValido() throws Exception {
        // Given
        DespachoRequest request = requestValido();
        when(despachoService.crearDespacho(any(DespachoRequest.class))).thenReturn(response(1L, 1001L, "RM"));

        // When / Then
        mockMvc.perform(post("/api/despachos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/despachos/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.numeroPedido").value(1001))
                .andExpect(jsonPath("$.tipoDespacho").value("RM"));

        verify(despachoService).crearDespacho(any(DespachoRequest.class));
    }

    @Test
    void crearDespachoRetornaBadRequestCuandoRequestEsInvalido() throws Exception {
        // Given
        DespachoRequest request = new DespachoRequest(null, "RM", "Transportista", "TRK-1001");

        // When / Then
        mockMvc.perform(post("/api/despachos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensaje", containsString("numeroPedido")));
    }

    @Test
    void crearDespachoRetornaConflictCuandoServicioDetectaDuplicado() throws Exception {
        // Given
        when(despachoService.crearDespacho(any(DespachoRequest.class)))
                .thenThrow(new ConflictException("Ya existe un despacho registrado para el pedido 1001"));

        // When / Then
        mockMvc.perform(post("/api/despachos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.mensaje").value("Ya existe un despacho registrado para el pedido 1001"));

        verify(despachoService).crearDespacho(any(DespachoRequest.class));
    }

    @Test
    void crearDespachoRetornaServiceUnavailableCuandoPedidoServiceFalla() throws Exception {
        // Given
        when(despachoService.crearDespacho(any(DespachoRequest.class)))
                .thenThrow(new ServiceUnavailableException("No se pudo validar el pedido en pedido-service"));

        // When / Then
        mockMvc.perform(post("/api/despachos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value(503))
                .andExpect(jsonPath("$.mensaje").value("No se pudo validar el pedido en pedido-service"));

        verify(despachoService).crearDespacho(any(DespachoRequest.class));
    }

    @Test
    void listarDespachosRetornaListado() throws Exception {
        // Given
        when(despachoService.listarDespachos(null)).thenReturn(List.of(response(1L, 1001L, "RM")));

        // When / Then
        mockMvc.perform(get("/api/despachos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].numeroPedido").value(1001));

        verify(despachoService).listarDespachos(null);
    }

    @Test
    void listarDespachosFiltraPorTipo() throws Exception {
        // Given
        when(despachoService.listarDespachos("RM")).thenReturn(List.of(response(1L, 1001L, "RM")));

        // When / Then
        mockMvc.perform(get("/api/despachos").param("tipo", "RM"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipoDespacho").value("RM"));

        verify(despachoService).listarDespachos("RM");
    }

    @Test
    void obtenerDespachoPorNumeroPedidoRetornaDespachoCuandoExiste() throws Exception {
        // Given
        when(despachoService.obtenerDespachoPorNumeroPedido(1001L)).thenReturn(response(1L, 1001L, "RM"));

        // When / Then
        mockMvc.perform(get("/api/despachos/{numeroPedido}", 1001L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.numeroPedido").value(1001));

        verify(despachoService).obtenerDespachoPorNumeroPedido(1001L);
    }

    @Test
    void obtenerDespachoPorNumeroPedidoRetornaNotFoundCuandoNoExiste() throws Exception {
        // Given
        when(despachoService.obtenerDespachoPorNumeroPedido(404L))
                .thenThrow(new ResourceNotFoundException("Despacho no encontrado para el pedido 404"));

        // When / Then
        mockMvc.perform(get("/api/despachos/{numeroPedido}", 404L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensaje").value("Despacho no encontrado para el pedido 404"));

        verify(despachoService).obtenerDespachoPorNumeroPedido(404L);
    }

    @Test
    void actualizarDespachoRetornaDespachoActualizado() throws Exception {
        // Given
        when(despachoService.actualizarDespacho(eq(1L), any(DespachoRequest.class)))
                .thenReturn(response(1L, 1001L, "REGION"));

        // When / Then
        mockMvc.perform(put("/api/despachos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.tipoDespacho").value("REGION"));

        verify(despachoService).actualizarDespacho(eq(1L), any(DespachoRequest.class));
    }

    private DespachoRequest requestValido() {
        return new DespachoRequest(1001L, "RM", "Transportista", "TRK-1001");
    }

    private DespachoResponse response(Long id, Long numeroPedido, String tipoDespacho) {
        return new DespachoResponse(id, numeroPedido, tipoDespacho, "Transportista",
                LocalDateTime.of(2026, 5, 22, 10, 0), "TRK-" + numeroPedido);
    }
}
