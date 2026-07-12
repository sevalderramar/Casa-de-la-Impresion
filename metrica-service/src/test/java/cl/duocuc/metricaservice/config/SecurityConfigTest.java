package cl.duocuc.metricaservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {SecurityConfig.class, SecurityConfigTest.TestController.class, SecurityConfigTest.TestBeans.class})
class SecurityConfigTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilters(springSecurityFilterChain)
                .build();
    }

    @Test
    void sePuedeConstruirSecurityFilterChain() {
        // Given / When / Then
        assertNotNull(securityFilterChain);
    }

    @Test
    void passwordEncoderCodificaYValidaPassword() {
        // Given
        String rawPassword = "clave-temporal";

        // When
        String encoded = passwordEncoder.encode(rawPassword);

        // Then
        assertTrue(passwordEncoder.matches(rawPassword, encoded));
    }

    @Test
    void rutasConfiguradasComoPublicasPermitenAcceso() throws Exception {
        // When / Then
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk())
                .andExpect(content().string("ok"));

        mockMvc.perform(get("/v3/api-docs/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("ok"));

        mockMvc.perform(get("/auth/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("ok"));

        mockMvc.perform(get("/api/auth/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("ok"));

        mockMvc.perform(get("/h2-console/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("ok"));

        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("ok"));
    }

    @Test
    void cualquierRutaTambienQuedaPermitidaPorConfiguracionActual() throws Exception {
        // When / Then
        mockMvc.perform(get("/ruta-protegida"))
                .andExpect(status().isOk())
                .andExpect(content().string("ok"));
    }

    @RestController
    public static class TestController {

        @GetMapping({"/swagger-ui/index.html", "/v3/api-docs/test", "/auth/test", "/api/auth/test",
                "/h2-console/test", "/actuator/health", "/ruta-protegida"})
        String ok() {
            return "ok";
        }
    }

    @Configuration
    @EnableWebMvc
    static class TestBeans {

        @Bean
        JwtAuthFilter jwtAuthFilter() {
            return new JwtAuthFilter(new JwtUtil()) {
                @Override
                protected void doFilterInternal(HttpServletRequest request,
                                                HttpServletResponse response,
                                                FilterChain filterChain) throws ServletException, IOException {
                    filterChain.doFilter(request, response);
                }
            };
        }
    }
}
