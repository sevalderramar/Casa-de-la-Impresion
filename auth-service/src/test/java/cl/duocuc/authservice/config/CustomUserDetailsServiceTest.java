package cl.duocuc.authservice.config;

import cl.duocuc.authservice.entity.Usuario;
import cl.duocuc.authservice.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    void loadUserByUsernameRetornaUserDetailsConRolYEstadoActivo() {
        // Given
        when(usuarioRepository.findByEmail("admin@empresa.com"))
                .thenReturn(Optional.of(usuario("hash", Usuario.Rol.ADMIN, true)));

        // When
        var userDetails = userDetailsService.loadUserByUsername("admin@empresa.com");

        // Then
        assertEquals("admin@empresa.com", userDetails.getUsername());
        assertEquals("hash", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertEquals("ROLE_ADMIN", userDetails.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void loadUserByUsernameRetornaUserDetailsDeshabilitadoCuandoUsuarioInactivo() {
        // Given
        when(usuarioRepository.findByEmail("admin@empresa.com"))
                .thenReturn(Optional.of(usuario("hash", Usuario.Rol.ADMIN, false)));

        // When
        var userDetails = userDetailsService.loadUserByUsername("admin@empresa.com");

        // Then
        assertFalse(userDetails.isEnabled());
    }

    @Test
    void loadUserByUsernameLanzaCuandoUsuarioNoExiste() {
        // Given
        when(usuarioRepository.findByEmail("noexiste@empresa.com")).thenReturn(Optional.empty());

        // When / Then
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("noexiste@empresa.com"));
        assertEquals("Usuario no encontrado: noexiste@empresa.com", exception.getMessage());
    }

    @Test
    void loadUserByUsernameLanzaCuandoPasswordEstaVacio() {
        // Given
        when(usuarioRepository.findByEmail("admin@empresa.com"))
                .thenReturn(Optional.of(usuario(" ", Usuario.Rol.ADMIN, true)));

        // When / Then
        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("admin@empresa.com"));
    }

    private Usuario usuario(String password, Usuario.Rol rol, boolean activo) {
        Usuario usuario = new Usuario();
        usuario.setEmail("admin@empresa.com");
        usuario.setPassword(password);
        usuario.setRol(rol);
        usuario.setActivo(activo);
        return usuario;
    }
}
