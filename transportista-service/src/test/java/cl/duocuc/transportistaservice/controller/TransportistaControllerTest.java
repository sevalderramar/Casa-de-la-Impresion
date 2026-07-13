package cl.duocuc.transportistaservice.controller;

import cl.duocuc.transportistaservice.config.GlobalExceptionHandler;
import cl.duocuc.transportistaservice.dto.TransportistaResponseDTO;
import cl.duocuc.transportistaservice.exception.ConflictException;
import cl.duocuc.transportistaservice.exception.ResourceNotFoundException;
import cl.duocuc.transportistaservice.service.TransportistaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TransportistaControllerTest {

    @Mock
    private TransportistaService transportistaService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new TransportistaController(transportistaService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void registrarTransportistaRetornaCreated() throws Exception {
        // Given
        when(transportistaService.crearTransportista(any())).thenReturn(response(1L, "Blue Express", "TR-001", true));

        // When / Then
        mockMvc.perform(post("/api/transportistas")
                        .contentType("application/json")
                        .content("{\"nombre\":\"Blue Express\",\"codigoInterno\":\"TR-001\",\"contacto\":\"contacto@blue.cl\",\"regionesCobertura\":\"RM\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensaje").value("Transportista registrado correctamente"))
                .andExpect(jsonPath("$.exitoso").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.codigoInterno").value("TR-001"));

        verify(transportistaService).crearTransportista(any());
    }

    @Test
    void registrarTransportistaRetornaConflictCuandoCodigoExiste() throws Exception {
        // Given
        when(transportistaService.crearTransportista(any()))
                .thenThrow(new ConflictException("El código interno ya existe"));

        // When / Then
        mockMvc.perform(post("/api/transportistas")
                        .contentType("application/json")
                        .content("{\"nombre\":\"Blue Express\",\"codigoInterno\":\"TR-001\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.mensaje").value("El código interno ya existe"));
    }

    @Test
    void registrarTransportistaRetornaBadRequestCuandoRequestEsInvalido() throws Exception {
        // When / Then
        mockMvc.perform(post("/api/transportistas")
                        .contentType("application/json")
                        .content("{\"nombre\":\"\",\"codigoInterno\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(transportistaService);
    }

    @Test
    void listarTransportistasActivosRetornaListado() throws Exception {
        // Given
        when(transportistaService.listarActivos()).thenReturn(List.of(response(1L, "Blue Express", "TR-001", true)));

        // When / Then
        mockMvc.perform(get("/api/transportistas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Listado obtenido"))
                .andExpect(jsonPath("$.data[0].id").value(1));

        verify(transportistaService).listarActivos();
    }

    @Test
    void obtenerTransportistaRetornaTransportista() throws Exception {
        // Given
        when(transportistaService.obtenerPorId(1L)).thenReturn(response(1L, "Blue Express", "TR-001", true));

        // When / Then
        mockMvc.perform(get("/api/transportistas/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Transportista obtenido"))
                .andExpect(jsonPath("$.data.nombre").value("Blue Express"));

        verify(transportistaService).obtenerPorId(1L);
    }

    @Test
    void obtenerTransportistaRetornaNotFoundCuandoNoExiste() throws Exception {
        // Given
        when(transportistaService.obtenerPorId(99L))
                .thenThrow(new ResourceNotFoundException("Transportista no encontrado"));

        // When / Then
        mockMvc.perform(get("/api/transportistas/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensaje").value("Transportista no encontrado"));
    }

    @Test
    void actualizarTransportistaRetornaActualizado() throws Exception {
        // Given
        when(transportistaService.actualizarTransportista(eq(1L), any()))
                .thenReturn(response(1L, "Blue Express Actualizado", "TR-001", false));

        // When / Then
        mockMvc.perform(put("/api/transportistas/{id}", 1L)
                        .contentType("application/json")
                        .content("{\"nombre\":\"Blue Express Actualizado\",\"activo\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Transportista actualizado"))
                .andExpect(jsonPath("$.data.nombre").value("Blue Express Actualizado"))
                .andExpect(jsonPath("$.data.activo").value(false));

        verify(transportistaService).actualizarTransportista(eq(1L), any());
    }

    @Test
    void pingRetornaServicioActivo() throws Exception {
        // When / Then
        mockMvc.perform(get("/api/transportistas/ping"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("transportista-service activo"))
                .andExpect(jsonPath("$.exitoso").value(true))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    private TransportistaResponseDTO response(Long id, String nombre, String codigo, boolean activo) {
        TransportistaResponseDTO response = new TransportistaResponseDTO();
        response.setId(id);
        response.setNombre(nombre);
        response.setCodigoInterno(codigo);
        response.setContacto("contacto@transportista.cl");
        response.setRegionesCobertura("RM");
        response.setActivo(activo);
        return response;
    }
}
