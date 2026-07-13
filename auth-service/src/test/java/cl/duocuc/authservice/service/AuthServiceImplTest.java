package cl.duocuc.authservice.service;

import cl.duocuc.authservice.config.JwtUtil;
import cl.duocuc.authservice.dto.LoginRequestDTO;
import cl.duocuc.authservice.dto.LoginResponseDTO;
import cl.duocuc.authservice.entity.Usuario;
import cl.duocuc.authservice.exception.ResourceNotFoundException;
import cl.duocuc.authservice.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void loginAutenticaCredencialesGeneraTokenYRetornaDatosDelUsuario() {
        // Given
        LoginRequestDTO request = new LoginRequestDTO("admin@empresa.com", "pass123");
        Usuario usuario = usuario(Usuario.Rol.ADMIN, true);
        UserDetails userDetails = User.withUsername("admin@empresa.com")
                .password("hash")
                .roles("ADMIN")
                .build();

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(usuarioRepository.findByEmail("admin@empresa.com")).thenReturn(Optional.of(usuario));
        when(jwtUtil.generarToken("admin@empresa.com", "ADMIN")).thenReturn("jwt-generado");
        when(jwtUtil.getExpirationMs()).thenReturn(60_000L);

        // When
        LoginResponseDTO response = authService.login(request);

        // Then
        ArgumentCaptor<UsernamePasswordAuthenticationToken> captor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(captor.capture());
        assertEquals("admin@empresa.com", captor.getValue().getPrincipal());
        assertEquals("pass123", captor.getValue().getCredentials());
        assertEquals("jwt-generado", response.getToken());
        assertEquals("Bearer", response.getTipo());
        assertEquals("admin@empresa.com", response.getEmail());
        assertEquals("ADMIN", response.getRol());
        assertTrue(response.getExpiracion() >= System.currentTimeMillis());
    }

    @Test
    void loginPropagaAuthenticationExceptionCuandoCredencialesSonInvalidas() {
        // Given
        LoginRequestDTO request = new LoginRequestDTO("admin@empresa.com", "incorrecta");
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("bad credentials"));

        // When / Then
        assertThrows(BadCredentialsException.class, () -> authService.login(request));
        verifyNoInteractions(usuarioRepository, jwtUtil);
    }

    @Test
    void loginLanzaResourceNotFoundCuandoUsuarioAutenticadoNoTieneRolEnRepositorio() {
        // Given
        LoginRequestDTO request = new LoginRequestDTO("admin@empresa.com", "pass123");
        UserDetails userDetails = User.withUsername("admin@empresa.com")
                .password("hash")
                .roles("ADMIN")
                .build();

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(usuarioRepository.findByEmail("admin@empresa.com")).thenReturn(Optional.empty());

        // When / Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> authService.login(request));
        assertEquals("Usuario autenticado sin rol configurado", exception.getMessage());
        verify(jwtUtil, never()).generarToken(any(), any());
    }

    private Usuario usuario(Usuario.Rol rol, boolean activo) {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Administrador");
        usuario.setEmail("admin@empresa.com");
        usuario.setPassword("hash");
        usuario.setRol(rol);
        usuario.setActivo(activo);
        return usuario;
    }
}
