package cl.duocuc.authservice.controller;

import cl.duocuc.authservice.dto.LoginResponseDTO;
import cl.duocuc.authservice.exception.GlobalExceptionHandler;
import cl.duocuc.authservice.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new AuthController(authService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void loginRetornaTokenCuandoCredencialesSonValidas() throws Exception {
        // Given
        LoginResponseDTO response = LoginResponseDTO.builder()
                .token("jwt-generado")
                .tipo("Bearer")
                .email("admin@empresa.com")
                .rol("ADMIN")
                .expiracion(1_700_000_000L)
                .build();

        when(authService.login(any())).thenReturn(response);

        // When / Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("{\"email\":\"admin@empresa.com\",\"password\":\"pass123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Login exitoso"))
                .andExpect(jsonPath("$.exitoso").value(true))
                .andExpect(jsonPath("$.data.token").value("jwt-generado"))
                .andExpect(jsonPath("$.data.tipo").value("Bearer"))
                .andExpect(jsonPath("$.data.email").value("admin@empresa.com"))
                .andExpect(jsonPath("$.data.rol").value("ADMIN"));

        verify(authService).login(any());
    }

    @Test
    void loginRetornaUnauthorizedCuandoCredencialesSonInvalidas() throws Exception {
        // Given
        when(authService.login(any())).thenThrow(new BadCredentialsException("bad credentials"));

        // When / Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("{\"email\":\"admin@empresa.com\",\"password\":\"incorrecta\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.mensaje").value("Credenciales inválidas"));
    }

    @Test
    void loginRetornaBadRequestCuandoRequestEsInvalido() throws Exception {
        // When / Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("{\"email\":\"no-es-email\",\"password\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(authService);
    }

    @Test
    void logoutRetornaConfirmacionSinInvocarServicio() throws Exception {
        // When / Then
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Sesión cerrada. Descarta el token en el cliente."))
                .andExpect(jsonPath("$.exitoso").value(true))
                .andExpect(jsonPath("$.data").doesNotExist());

        verifyNoInteractions(authService);
    }

    @Test
    void pingRetornaHealthcheck() throws Exception {
        // When / Then
        mockMvc.perform(get("/api/auth/ping"))
                .andExpect(status().isOk())
                .andExpect(content().string("auth-service OK"));
    }
}
