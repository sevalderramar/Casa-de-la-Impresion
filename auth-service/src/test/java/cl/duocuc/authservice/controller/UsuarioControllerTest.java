package cl.duocuc.authservice.controller;

import cl.duocuc.authservice.dto.UsuarioResponseDTO;
import cl.duocuc.authservice.exception.GlobalExceptionHandler;
import cl.duocuc.authservice.exception.ResourceNotFoundException;
import cl.duocuc.authservice.service.UsuarioService;
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
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new UsuarioController(usuarioService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void listarRetornaUsuarios() throws Exception {
        // Given
        when(usuarioService.listar()).thenReturn(List.of(usuarioResponse(1L, "ADMIN", true)));

        // When / Then
        mockMvc.perform(get("/api/auth/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Usuarios obtenidos"))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].rol").value("ADMIN"));

        verify(usuarioService).listar();
    }

    @Test
    void crearRetornaCreated() throws Exception {
        // Given
        when(usuarioService.crear(any())).thenReturn(usuarioResponse(2L, "COMERCIAL", true));

        // When / Then
        mockMvc.perform(post("/api/auth/usuarios")
                        .contentType("application/json")
                        .content("{\"nombre\":\"Maria\",\"email\":\"maria@empresa.com\",\"password\":\"user123\",\"rol\":\"COMERCIAL\",\"activo\":true}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensaje").value("Usuario creado"))
                .andExpect(jsonPath("$.data.id").value(2))
                .andExpect(jsonPath("$.data.rol").value("COMERCIAL"));

        verify(usuarioService).crear(any());
    }

    @Test
    void crearRetornaBadRequestCuandoRequestEsInvalido() throws Exception {
        // When / Then
        mockMvc.perform(post("/api/auth/usuarios")
                        .contentType("application/json")
                        .content("{\"nombre\":\"\",\"email\":\"no-es-email\",\"password\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(usuarioService);
    }

    @Test
    void actualizarRetornaUsuarioActualizado() throws Exception {
        // Given
        when(usuarioService.actualizar(eq(2L), any())).thenReturn(usuarioResponse(2L, "ENCARGADO_DESPACHO", false));

        // When / Then
        mockMvc.perform(put("/api/auth/usuarios/{id}", 2L)
                        .contentType("application/json")
                        .content("{\"nombre\":\"Maria\",\"email\":\"maria@empresa.com\",\"password\":\"user123\",\"rol\":\"ENCARGADO_DESPACHO\",\"activo\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Usuario actualizado"))
                .andExpect(jsonPath("$.data.id").value(2))
                .andExpect(jsonPath("$.data.activo").value(false));

        verify(usuarioService).actualizar(eq(2L), any());
    }

    @Test
    void actualizarRetornaNotFoundCuandoUsuarioNoExiste() throws Exception {
        // Given
        when(usuarioService.actualizar(eq(99L), any()))
                .thenThrow(new ResourceNotFoundException("Usuario no encontrado con ID: 99"));

        // When / Then
        mockMvc.perform(put("/api/auth/usuarios/{id}", 99L)
                        .contentType("application/json")
                        .content("{\"nombre\":\"Maria\",\"email\":\"maria@empresa.com\",\"password\":\"user123\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensaje").value("Usuario no encontrado con ID: 99"));
    }

    private UsuarioResponseDTO usuarioResponse(Long id, String rol, boolean activo) {
        return UsuarioResponseDTO.builder()
                .id(id)
                .nombre("Maria")
                .email("maria@empresa.com")
                .rol(rol)
                .activo(activo)
                .build();
    }
}
