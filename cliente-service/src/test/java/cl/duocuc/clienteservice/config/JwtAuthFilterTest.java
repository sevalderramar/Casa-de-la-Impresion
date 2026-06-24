package cl.duocuc.clienteservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private TestableJwtAuthFilter jwtAuthFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        jwtAuthFilter = new TestableJwtAuthFilter(jwtUtil);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternalContinuaCadenaCuandoNoHayAuthorization() throws ServletException, IOException {
        // Given
        when(request.getHeader("Authorization")).thenReturn(null);

        // When
        jwtAuthFilter.doFilterForTest(request, response, filterChain);

        // Then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void doFilterInternalContinuaCadenaCuandoTokenEsInvalido() throws ServletException, IOException {
        // Given
        when(request.getHeader("Authorization")).thenReturn("Bearer token-invalido");
        when(jwtUtil.esValido("token-invalido")).thenReturn(false);

        // When
        jwtAuthFilter.doFilterForTest(request, response, filterChain);

        // Then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtUtil).esValido("token-invalido");
        verify(jwtUtil, never()).extraerUsername("token-invalido");
        verify(jwtUtil, never()).extraerRol("token-invalido");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternalAutenticaCuandoTokenEsValido() throws ServletException, IOException {
        // Given
        when(request.getHeader("Authorization")).thenReturn("Bearer token-valido");
        when(jwtUtil.esValido("token-valido")).thenReturn(true);
        when(jwtUtil.extraerUsername("token-valido")).thenReturn("usuario@test.cl");
        when(jwtUtil.extraerRol("token-valido")).thenReturn("ADMIN");

        // When
        jwtAuthFilter.doFilterForTest(request, response, filterChain);

        // Then
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals("usuario@test.cl", authentication.getName());
        assertEquals("ROLE_ADMIN", authentication.getAuthorities().iterator().next().getAuthority());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternalUsaRolUserCuandoTokenValidoNoTieneRol() throws ServletException, IOException {
        // Given
        when(request.getHeader("Authorization")).thenReturn("Bearer token-valido");
        when(jwtUtil.esValido("token-valido")).thenReturn(true);
        when(jwtUtil.extraerUsername("token-valido")).thenReturn("usuario@test.cl");
        when(jwtUtil.extraerRol("token-valido")).thenReturn(" ");

        // When
        jwtAuthFilter.doFilterForTest(request, response, filterChain);

        // Then
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals("usuario@test.cl", authentication.getName());
        assertEquals("ROLE_USER", authentication.getAuthorities().iterator().next().getAuthority());
        verify(filterChain).doFilter(request, response);
    }

    private static class TestableJwtAuthFilter extends JwtAuthFilter {

        private TestableJwtAuthFilter(JwtUtil jwtUtil) {
            super(jwtUtil);
        }

        private void doFilterForTest(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {
            doFilterInternal(request, response, filterChain);
        }
    }
}
