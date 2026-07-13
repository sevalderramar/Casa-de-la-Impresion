package cl.duocuc.transportistaservice.service;

import cl.duocuc.transportistaservice.dto.TransportistaRequestDTO;
import cl.duocuc.transportistaservice.dto.TransportistaResponseDTO;
import cl.duocuc.transportistaservice.dto.TransportistaUpdateDTO;
import cl.duocuc.transportistaservice.exception.ConflictException;
import cl.duocuc.transportistaservice.exception.ResourceNotFoundException;
import cl.duocuc.transportistaservice.model.Transportista;
import cl.duocuc.transportistaservice.repository.TransportistaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
class TransportistaServiceImplTest {

    @Mock
    private TransportistaRepository transportistaRepository;

    @InjectMocks
    private TransportistaServiceImpl transportistaService;

    @Test
    void crearTransportistaGuardaActivoCuandoCodigoNoExiste() {
        // Given
        TransportistaRequestDTO request = request("Blue Express", "TR-001", "contacto@blue.cl", "RM,V");
        when(transportistaRepository.existsByCodigoInterno("TR-001")).thenReturn(false);
        when(transportistaRepository.save(any())).thenAnswer(invocation -> {
            Transportista transportista = invocation.getArgument(0);
            transportista.setId(10L);
            return transportista;
        });

        // When
        TransportistaResponseDTO response = transportistaService.crearTransportista(request);

        // Then
        ArgumentCaptor<Transportista> captor = ArgumentCaptor.forClass(Transportista.class);
        verify(transportistaRepository).save(captor.capture());
        assertEquals("Blue Express", captor.getValue().getNombre());
        assertEquals("TR-001", captor.getValue().getCodigoInterno());
        assertTrue(captor.getValue().isActivo());
        assertEquals(10L, response.getId());
        assertEquals("TR-001", response.getCodigoInterno());
        assertTrue(response.isActivo());
    }

    @Test
    void crearTransportistaLanzaConflictCuandoCodigoYaExiste() {
        // Given
        TransportistaRequestDTO request = request("Blue Express", "TR-001", "contacto@blue.cl", "RM,V");
        when(transportistaRepository.existsByCodigoInterno("TR-001")).thenReturn(true);

        // When / Then
        ConflictException exception = assertThrows(ConflictException.class,
                () -> transportistaService.crearTransportista(request));
        assertEquals("El código interno ya existe", exception.getMessage());
        verify(transportistaRepository, never()).save(any());
    }

    @Test
    void listarActivosMapeaSoloResultadoDelRepositorio() {
        // Given
        when(transportistaRepository.findByActivoTrue()).thenReturn(List.of(
                transportista(1L, "Blue Express", "TR-001", "contacto@blue.cl", "RM", true),
                transportista(2L, "Chilexpress", "TR-002", "contacto@chile.cl", "V", true)));

        // When
        List<TransportistaResponseDTO> response = transportistaService.listarActivos();

        // Then
        assertEquals(2, response.size());
        assertEquals("TR-001", response.get(0).getCodigoInterno());
        assertEquals("TR-002", response.get(1).getCodigoInterno());
        assertTrue(response.get(0).isActivo());
    }

    @Test
    void obtenerPorIdRetornaTransportistaActivo() {
        // Given
        when(transportistaRepository.findById(1L)).thenReturn(Optional.of(
                transportista(1L, "Blue Express", "TR-001", "contacto@blue.cl", "RM", true)));

        // When
        TransportistaResponseDTO response = transportistaService.obtenerPorId(1L);

        // Then
        assertEquals(1L, response.getId());
        assertEquals("Blue Express", response.getNombre());
        assertTrue(response.isActivo());
    }

    @Test
    void obtenerPorIdLanzaNotFoundCuandoNoExiste() {
        // Given
        when(transportistaRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> transportistaService.obtenerPorId(99L));
        assertEquals("Transportista no encontrado", exception.getMessage());
    }

    @Test
    void obtenerPorIdLanzaNotFoundCuandoEstaInactivo() {
        // Given
        when(transportistaRepository.findById(1L)).thenReturn(Optional.of(
                transportista(1L, "Blue Express", "TR-001", "contacto@blue.cl", "RM", false)));

        // When / Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> transportistaService.obtenerPorId(1L));
        assertEquals("Transportista inactivo", exception.getMessage());
    }

    @Test
    void actualizarTransportistaModificaCamposInformados() {
        // Given
        Transportista existente = transportista(1L, "Blue Express", "TR-001", "antiguo@blue.cl", "RM", true);
        TransportistaUpdateDTO request = update("Blue Express Actualizado", "nuevo@blue.cl", "RM,V", false);
        when(transportistaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(transportistaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        TransportistaResponseDTO response = transportistaService.actualizarTransportista(1L, request);

        // Then
        assertEquals("Blue Express Actualizado", existente.getNombre());
        assertEquals("nuevo@blue.cl", existente.getContacto());
        assertEquals("RM,V", existente.getRegionesCobertura());
        assertFalse(existente.isActivo());
        assertEquals("TR-001", response.getCodigoInterno());
        assertFalse(response.isActivo());
    }

    @Test
    void actualizarTransportistaIgnoraNombreBlankYCamposNull() {
        // Given
        Transportista existente = transportista(1L, "Blue Express", "TR-001", "antiguo@blue.cl", "RM", true);
        TransportistaUpdateDTO request = update("   ", null, null, null);
        when(transportistaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(transportistaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        TransportistaResponseDTO response = transportistaService.actualizarTransportista(1L, request);

        // Then
        assertEquals("Blue Express", existente.getNombre());
        assertEquals("antiguo@blue.cl", existente.getContacto());
        assertEquals("RM", existente.getRegionesCobertura());
        assertTrue(response.isActivo());
    }

    @Test
    void actualizarTransportistaLanzaNotFoundCuandoNoExiste() {
        // Given
        when(transportistaRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> transportistaService.actualizarTransportista(99L, new TransportistaUpdateDTO()));
        assertEquals("Transportista no encontrado", exception.getMessage());
        verify(transportistaRepository, never()).save(any());
    }

    private TransportistaRequestDTO request(String nombre, String codigo, String contacto, String regiones) {
        TransportistaRequestDTO request = new TransportistaRequestDTO();
        request.setNombre(nombre);
        request.setCodigoInterno(codigo);
        request.setContacto(contacto);
        request.setRegionesCobertura(regiones);
        return request;
    }

    private TransportistaUpdateDTO update(String nombre, String contacto, String regiones, Boolean activo) {
        TransportistaUpdateDTO request = new TransportistaUpdateDTO();
        request.setNombre(nombre);
        request.setContacto(contacto);
        request.setRegionesCobertura(regiones);
        request.setActivo(activo);
        return request;
    }

    private Transportista transportista(Long id, String nombre, String codigo, String contacto, String regiones, boolean activo) {
        Transportista transportista = new Transportista();
        transportista.setId(id);
        transportista.setNombre(nombre);
        transportista.setCodigoInterno(codigo);
        transportista.setContacto(contacto);
        transportista.setRegionesCobertura(regiones);
        transportista.setActivo(activo);
        return transportista;
    }
}
