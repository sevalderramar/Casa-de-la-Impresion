package cl.duocuc.fabricacion.controller;

import cl.duocuc.fabricacion.dto.OrdenFabricacionRequest;
import cl.duocuc.fabricacion.exception.PedidoNoEncontradoException;
import cl.duocuc.fabricacion.exception.ServiceUnavailableException;
import cl.duocuc.fabricacion.handler.GlobalExceptionHandler;
import cl.duocuc.fabricacion.service.OrdenFabricacionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OrdenFabricacionControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private OrdenFabricacionService ordenService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ordenService = mock(OrdenFabricacionService.class);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new OrdenFabricacionController(ordenService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void crearOrdenResponde404CuandoPedidoRemotoNoExiste() throws Exception {
        when(ordenService.crearOrden(any(OrdenFabricacionRequest.class)))
                .thenThrow(new PedidoNoEncontradoException("Pedido no encontrado: 404"));

        mockMvc.perform(post("/api/fabricacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido(404L))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensaje").value("Pedido no encontrado: 404"));

        verify(ordenService).crearOrden(any(OrdenFabricacionRequest.class));
    }

    @Test
    void crearOrdenResponde503CuandoPedidoServiceRetorna5xx() throws Exception {
        when(ordenService.crearOrden(any(OrdenFabricacionRequest.class)))
                .thenThrow(new ServiceUnavailableException("No fue posible comunicarse con pedido-service (HTTP 500)"));

        mockMvc.perform(post("/api/fabricacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido(1001L))))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value(503))
                .andExpect(jsonPath("$.mensaje").value("No fue posible comunicarse con pedido-service (HTTP 500)"));

        verify(ordenService).crearOrden(any(OrdenFabricacionRequest.class));
    }

    private OrdenFabricacionRequest requestValido(Long numeroPedido) {
        return new OrdenFabricacionRequest(numeroPedido, "operador-01", "Inicio de fabricacion");
    }
}
