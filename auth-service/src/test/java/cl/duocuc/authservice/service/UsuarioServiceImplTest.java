package cl.duocuc.authservice.service;

import cl.duocuc.authservice.dto.UsuarioRequestDTO;
import cl.duocuc.authservice.dto.UsuarioResponseDTO;
import cl.duocuc.authservice.entity.Usuario;
import cl.duocuc.authservice.exception.ConflictException;
import cl.duocuc.authservice.exception.ResourceNotFoundException;
import cl.duocuc.authservice.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @Test
    void listarMapeaUsuariosSinExponerPassword() {
        // Given
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario(1L, "Ana", "ana@empresa.com",
                "hash", Usuario.Rol.COMERCIAL, true)));

        // When
        List<UsuarioResponseDTO> response = usuarioService.listar();

        // Then
        assertEquals(1, response.size());
        assertEquals(1L, response.get(0).getId());
        assertEquals("Ana", response.get(0).getNombre());
        assertEquals("ana@empresa.com", response.get(0).getEmail());
        assertEquals("COMERCIAL", response.get(0).getRol());
        assertTrue(response.get(0).isActivo());
    }

    @Test
    void crearLanzaConflictCuandoEmailYaExiste() {
        // Given
        UsuarioRequestDTO request = new UsuarioRequestDTO("Ana", "ana@empresa.com", "pass123", null, null);
        when(usuarioRepository.existsByEmail("ana@empresa.com")).thenReturn(true);

        // When / Then
        ConflictException exception = assertThrows(ConflictException.class, () -> usuarioService.crear(request));
        assertEquals("Ya existe un usuario con el email: ana@empresa.com", exception.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void crearUsaValoresPorDefectoYHasheaPassword() {
        // Given
        UsuarioRequestDTO request = new UsuarioRequestDTO("Ana", "ana@empresa.com", "pass123", null, null);
        when(usuarioRepository.existsByEmail("ana@empresa.com")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("hash-pass");
        when(usuarioRepository.save(any())).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            usuario.setId(10L);
            return usuario;
        });

        // When
        UsuarioResponseDTO response = usuarioService.crear(request);

        // Then
        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        assertEquals("hash-pass", captor.getValue().getPassword());
        assertEquals(Usuario.Rol.ENCARGADO_PEDIDOS, captor.getValue().getRol());
        assertTrue(captor.getValue().isActivo());
        assertEquals(10L, response.getId());
        assertEquals("ENCARGADO_PEDIDOS", response.getRol());
        assertTrue(response.isActivo());
    }

    @Test
    void crearRespetaRolYActivoInformados() {
        // Given
        UsuarioRequestDTO request = new UsuarioRequestDTO("Ana", "ana@empresa.com", "pass123",
                Usuario.Rol.ADMIN, false);
        when(passwordEncoder.encode("pass123")).thenReturn("hash-pass");
        when(usuarioRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        UsuarioResponseDTO response = usuarioService.crear(request);

        // Then
        assertEquals("ADMIN", response.getRol());
        assertFalse(response.isActivo());
    }

    @Test
    void actualizarLanzaNotFoundCuandoUsuarioNoExiste() {
        // Given
        UsuarioRequestDTO request = new UsuarioRequestDTO("Ana", "ana@empresa.com", "pass123", null, null);
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> usuarioService.actualizar(99L, request));
        assertEquals("Usuario no encontrado con ID: 99", exception.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void actualizarHasheaPasswordNuevoYActualizaCamposOpcionales() {
        // Given
        Usuario existente = usuario(2L, "Ana", "ana@empresa.com", "hash-antiguo",
                Usuario.Rol.COMERCIAL, true);
        UsuarioRequestDTO request = new UsuarioRequestDTO("Ana Nueva", "ana.nueva@empresa.com", "nuevo123",
                Usuario.Rol.ENCARGADO_DESPACHO, false);

        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(existente));
        when(passwordEncoder.encode("nuevo123")).thenReturn("hash-nuevo");
        when(usuarioRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        UsuarioResponseDTO response = usuarioService.actualizar(2L, request);

        // Then
        assertEquals("Ana Nueva", existente.getNombre());
        assertEquals("ana.nueva@empresa.com", existente.getEmail());
        assertEquals("hash-nuevo", existente.getPassword());
        assertEquals(Usuario.Rol.ENCARGADO_DESPACHO, existente.getRol());
        assertFalse(existente.isActivo());
        assertEquals("ENCARGADO_DESPACHO", response.getRol());
        assertFalse(response.isActivo());
    }

    @Test
    void actualizarMantienePasswordRolYActivoCuandoNoSeInforman() {
        // Given
        Usuario existente = usuario(2L, "Ana", "ana@empresa.com", "hash-antiguo",
                Usuario.Rol.COMERCIAL, true);
        UsuarioRequestDTO request = new UsuarioRequestDTO("Ana Nueva", "ana.nueva@empresa.com", "   ", null, null);

        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        UsuarioResponseDTO response = usuarioService.actualizar(2L, request);

        // Then
        assertEquals("hash-antiguo", existente.getPassword());
        assertEquals(Usuario.Rol.COMERCIAL, existente.getRol());
        assertTrue(existente.isActivo());
        assertEquals("COMERCIAL", response.getRol());
        verify(passwordEncoder, never()).encode(any());
    }

    private Usuario usuario(Long id, String nombre, String email, String password, Usuario.Rol rol, boolean activo) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setPassword(password);
        usuario.setRol(rol);
        usuario.setActivo(activo);
        return usuario;
    }
}
