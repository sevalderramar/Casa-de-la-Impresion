package cl.duocuc.clienteservice.service;

import cl.duocuc.clienteservice.dto.ClienteRequest;
import cl.duocuc.clienteservice.dto.ClienteResponse;
import cl.duocuc.clienteservice.exception.ConflictException;
import cl.duocuc.clienteservice.exception.ResourceNotFoundException;
import cl.duocuc.clienteservice.model.Cliente;
import cl.duocuc.clienteservice.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    void crearClienteCorrectamente() {
        // Given
        ClienteRequest request = request(" Maria Perez ", " 12.345.678-9 ", " MARIA@CORREO.CL ");

        when(clienteRepository.existsByRut("12.345.678-9")).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> {
            Cliente cliente = invocation.getArgument(0);
            cliente.setId(1L);
            return cliente;
        });

        // When
        ClienteResponse response = clienteService.crearCliente(request);

        // Then
        assertEquals(1L, response.getId());
        assertEquals("Maria Perez", response.getNombre());
        assertEquals("12.345.678-9", response.getRut());
        assertEquals("maria@correo.cl", response.getEmail());
        assertNotNull(response.getFechaRegistro());

        verify(clienteRepository).existsByRut("12.345.678-9");
        verify(clienteRepository).save(argThat(cliente ->
                cliente.getNombre().equals("Maria Perez")
                        && cliente.getRut().equals("12.345.678-9")
                        && cliente.getEmail().equals("maria@correo.cl")
                        && cliente.getFechaRegistro() != null
        ));
    }

    @Test
    void crearClienteLanzaErrorConRutDuplicado() {
        // Given
        ClienteRequest request = request("Maria Perez", "12.345.678-9", "maria@correo.cl");
        when(clienteRepository.existsByRut("12.345.678-9")).thenReturn(true);

        // When
        ConflictException exception = assertThrows(ConflictException.class,
                () -> clienteService.crearCliente(request));

        // Then
        assertTrue(exception.getMessage().contains("Ya existe un cliente registrado con el RUT 12.345.678-9"));
        verify(clienteRepository).existsByRut("12.345.678-9");
        verify(clienteRepository, never()).save(any());
    }

    @Test
    void obtenerClientePorIdCorrectamente() {
        // Given
        Cliente cliente = cliente(1L, "Maria Perez", "12.345.678-9", "maria@correo.cl");
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        // When
        ClienteResponse response = clienteService.obtenerClientePorId(1L);

        // Then
        assertEquals(1L, response.getId());
        assertEquals("Maria Perez", response.getNombre());
        assertEquals("12.345.678-9", response.getRut());
        verify(clienteRepository).findById(1L);
    }

    @Test
    void obtenerClientePorIdLanzaErrorCuandoNoExiste() {
        // Given
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> clienteService.obtenerClientePorId(99L));

        // Then
        assertTrue(exception.getMessage().contains("Cliente no encontrado con ID 99"));
        verify(clienteRepository).findById(99L);
    }

    @Test
    void obtenerClientePorRutCorrectamente() {
        // Given
        Cliente cliente = cliente(1L, "Maria Perez", "12.345.678-9", "maria@correo.cl");
        when(clienteRepository.findByRut("12.345.678-9")).thenReturn(Optional.of(cliente));

        // When
        ClienteResponse response = clienteService.obtenerClientePorRut(" 12.345.678-9 ");

        // Then
        assertEquals(1L, response.getId());
        assertEquals("12.345.678-9", response.getRut());
        verify(clienteRepository).findByRut("12.345.678-9");
    }

    @Test
    void actualizarClienteCorrectamente() {
        // Given
        Cliente existente = cliente(1L, "Maria Perez", "12.345.678-9", "maria@correo.cl");
        ClienteRequest request = request(" Maria Actualizada ", " 11.111.111-1 ", " NUEVA@CORREO.CL ");

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(clienteRepository.existsByRut("11.111.111-1")).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ClienteResponse response = clienteService.actualizarCliente(1L, request);

        // Then
        assertEquals(1L, response.getId());
        assertEquals("Maria Actualizada", response.getNombre());
        assertEquals("11.111.111-1", response.getRut());
        assertEquals("nueva@correo.cl", response.getEmail());

        verify(clienteRepository).findById(1L);
        verify(clienteRepository).existsByRut("11.111.111-1");
        verify(clienteRepository).save(argThat(cliente ->
                cliente.getId().equals(1L)
                        && cliente.getNombre().equals("Maria Actualizada")
                        && cliente.getRut().equals("11.111.111-1")
                        && cliente.getEmail().equals("nueva@correo.cl")
        ));
    }

    @Test
    void eliminarClienteCorrectamente() {
        // Given
        Cliente cliente = cliente(1L, "Maria Perez", "12.345.678-9", "maria@correo.cl");
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        // When
        clienteService.eliminarCliente(1L);

        // Then
        verify(clienteRepository).findById(1L);
        verify(clienteRepository).delete(cliente);
    }

    private ClienteRequest request(String nombre, String rut, String email) {
        return new ClienteRequest(
                nombre,
                rut,
                email,
                " +56 9 1234 5678 ",
                " Av. Siempre Viva 123 ",
                " Santiago ",
                " Metropolitana "
        );
    }

    private Cliente cliente(Long id, String nombre, String rut, String email) {
        return new Cliente(
                id,
                nombre,
                rut,
                email,
                "+56 9 1234 5678",
                "Av. Siempre Viva 123",
                "Santiago",
                "Metropolitana",
                LocalDate.now()
        );
    }
}
