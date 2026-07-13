package cl.duocuc.authservice.config;

import cl.duocuc.authservice.entity.Usuario;
import cl.duocuc.authservice.repository.UsuarioRepository;
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
import java.util.Optional;

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
    private UsuarioRepository usuarioRepository;

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
        jwtAuthFilter = new TestableJwtAuthFilter(jwtUtil, usuarioRepository);
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
        verifyNoInteractions(jwtUtil, usuarioRepository);
    }

    @Test
    void doFilterInternalContinuaCadenaCuandoAuthorizationNoEsBearer() throws ServletException, IOException {
        // Given
        when(request.getHeader("Authorization")).thenReturn("Basic credenciales");

        // When
        jwtAuthFilter.doFilterForTest(request, response, filterChain);

        // Then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtUtil, usuarioRepository);
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
        verify(jwtUtil, never()).extraerEmail("token-invalido");
        verifyNoInteractions(usuarioRepository);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternalAutenticaCuandoTokenEsValidoYUsuarioActivo() throws ServletException, IOException {
        // Given
        when(request.getHeader("Authorization")).thenReturn("Bearer token-valido");
        when(jwtUtil.esValido("token-valido")).thenReturn(true);
        when(jwtUtil.extraerEmail("token-valido")).thenReturn("admin@empresa.com");
        when(jwtUtil.extraerRol("token-valido")).thenReturn("ADMIN");
        when(usuarioRepository.findByEmail("admin@empresa.com")).thenReturn(Optional.of(usuario(true)));

        // When
        jwtAuthFilter.doFilterForTest(request, response, filterChain);

        // Then
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals("admin@empresa.com", authentication.getName());
        assertEquals("ROLE_ADMIN", authentication.getAuthorities().iterator().next().getAuthority());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternalNoAutenticaCuandoUsuarioEstaInactivo() throws ServletException, IOException {
        // Given
        when(request.getHeader("Authorization")).thenReturn("Bearer token-valido");
        when(jwtUtil.esValido("token-valido")).thenReturn(true);
        when(jwtUtil.extraerEmail("token-valido")).thenReturn("admin@empresa.com");
        when(jwtUtil.extraerRol("token-valido")).thenReturn("ADMIN");
        when(usuarioRepository.findByEmail("admin@empresa.com")).thenReturn(Optional.of(usuario(false)));

        // When
        jwtAuthFilter.doFilterForTest(request, response, filterChain);

        // Then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternalNoAutenticaCuandoUsuarioNoExiste() throws ServletException, IOException {
        // Given
        when(request.getHeader("Authorization")).thenReturn("Bearer token-valido");
        when(jwtUtil.esValido("token-valido")).thenReturn(true);
        when(jwtUtil.extraerEmail("token-valido")).thenReturn("admin@empresa.com");
        when(jwtUtil.extraerRol("token-valido")).thenReturn("ADMIN");
        when(usuarioRepository.findByEmail("admin@empresa.com")).thenReturn(Optional.empty());

        // When
        jwtAuthFilter.doFilterForTest(request, response, filterChain);

        // Then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    private Usuario usuario(boolean activo) {
        Usuario usuario = new Usuario();
        usuario.setEmail("admin@empresa.com");
        usuario.setRol(Usuario.Rol.ADMIN);
        usuario.setActivo(activo);
        return usuario;
    }

    private static class TestableJwtAuthFilter extends JwtAuthFilter {

        private TestableJwtAuthFilter(JwtUtil jwtUtil, UsuarioRepository usuarioRepository) {
            super(jwtUtil, usuarioRepository);
        }

        private void doFilterForTest(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {
            doFilterInternal(request, response, filterChain);
        }
    }
}
