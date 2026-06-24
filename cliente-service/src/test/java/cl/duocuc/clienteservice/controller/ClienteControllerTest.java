package cl.duocuc.clienteservice.controller;

import cl.duocuc.clienteservice.config.JwtUtil;
import cl.duocuc.clienteservice.dto.ClienteRequest;
import cl.duocuc.clienteservice.dto.ClienteResponse;
import cl.duocuc.clienteservice.exception.ConflictException;
import cl.duocuc.clienteservice.exception.ResourceNotFoundException;
import cl.duocuc.clienteservice.service.ClienteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClienteController.class)
@AutoConfigureMockMvc(addFilters = false)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ClienteService clienteService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    void crearClienteRetornaCreatedCuandoRequestEsValido() throws Exception {
        // Given
        ClienteRequest request = requestValido();
        ClienteResponse response = response(1L, "Maria Perez", "12.345.678-9");
        when(clienteService.crearCliente(any(ClienteRequest.class))).thenReturn(response);

        // When / Then
        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/clientes/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Maria Perez"))
                .andExpect(jsonPath("$.rut").value("12.345.678-9"));

        verify(clienteService).crearCliente(any(ClienteRequest.class));
    }

    @Test
    void listarClientesRetornaListado() throws Exception {
        // Given
        when(clienteService.listarClientes()).thenReturn(List.of(response(1L, "Maria Perez", "12.345.678-9")));

        // When / Then
        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Maria Perez"));

        verify(clienteService).listarClientes();
    }

    @Test
    void obtenerClientePorIdRetornaClienteCuandoExiste() throws Exception {
        // Given
        when(clienteService.obtenerClientePorId(1L)).thenReturn(response(1L, "Maria Perez", "12.345.678-9"));

        // When / Then
        mockMvc.perform(get("/api/clientes/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.rut").value("12.345.678-9"));

        verify(clienteService).obtenerClientePorId(1L);
    }

    @Test
    void obtenerClientePorIdRetornaNotFoundCuandoNoExiste() throws Exception {
        // Given
        when(clienteService.obtenerClientePorId(99L))
                .thenThrow(new ResourceNotFoundException("Cliente no encontrado con ID 99"));

        // When / Then
        mockMvc.perform(get("/api/clientes/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensaje").value("Cliente no encontrado con ID 99"));

        verify(clienteService).obtenerClientePorId(99L);
    }

    @Test
    void obtenerClientePorRutRetornaClienteCuandoExiste() throws Exception {
        // Given
        when(clienteService.obtenerClientePorRut("12.345.678-9"))
                .thenReturn(response(1L, "Maria Perez", "12.345.678-9"));

        // When / Then
        mockMvc.perform(get("/api/clientes/rut/{rut}", "12.345.678-9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.rut").value("12.345.678-9"));

        verify(clienteService).obtenerClientePorRut("12.345.678-9");
    }

    @Test
    void actualizarClienteRetornaClienteActualizado() throws Exception {
        // Given
        ClienteRequest request = requestValido();
        when(clienteService.actualizarCliente(eq(1L), any(ClienteRequest.class)))
                .thenReturn(response(1L, "Maria Perez", "12.345.678-9"));

        // When / Then
        mockMvc.perform(put("/api/clientes/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("maria@correo.cl"));

        verify(clienteService).actualizarCliente(eq(1L), any(ClienteRequest.class));
    }

    @Test
    void crearClienteRetornaConflictCuandoRutEstaDuplicado() throws Exception {
        // Given
        ClienteRequest request = requestValido();
        when(clienteService.crearCliente(any(ClienteRequest.class)))
                .thenThrow(new ConflictException("Ya existe un cliente registrado con el RUT 12.345.678-9"));

        // When / Then
        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.mensaje").value("Ya existe un cliente registrado con el RUT 12.345.678-9"));

        verify(clienteService).crearCliente(any(ClienteRequest.class));
    }

    @Test
    void crearClienteRetornaBadRequestCuandoRequestEsInvalido() throws Exception {
        // Given
        ClienteRequest request = new ClienteRequest(
                null,
                "12.345.678-9",
                "maria@correo.cl",
                "+56 9 1234 5678",
                "Av. Siempre Viva 123",
                "Santiago",
                "Metropolitana"
        );

        // When / Then
        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensaje", containsString("nombre")));
    }

    @Test
    void eliminarClienteRetornaNoContentCuandoExiste() throws Exception {
        // Given
        doNothing().when(clienteService).eliminarCliente(1L);

        // When / Then
        mockMvc.perform(delete("/api/clientes/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(clienteService).eliminarCliente(1L);
    }

    private ClienteRequest requestValido() {
        return new ClienteRequest(
                "Maria Perez",
                "12.345.678-9",
                "maria@correo.cl",
                "+56 9 1234 5678",
                "Av. Siempre Viva 123",
                "Santiago",
                "Metropolitana"
        );
    }

    private ClienteResponse response(Long id, String nombre, String rut) {
        return new ClienteResponse(
                id,
                nombre,
                rut,
                "maria@correo.cl",
                "+56 9 1234 5678",
                "Av. Siempre Viva 123",
                "Santiago",
                "Metropolitana",
                LocalDate.of(2026, 5, 22)
        );
    }
}
