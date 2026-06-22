package cl.duocuc.productoservice.service;

import cl.duocuc.productoservice.common.exception.ConflictException;
import cl.duocuc.productoservice.common.exception.ResourceNotFoundException;
import cl.duocuc.productoservice.dto.ProductoRequest;
import cl.duocuc.productoservice.dto.ProductoResponse;
import cl.duocuc.productoservice.model.Producto;
import cl.duocuc.productoservice.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    @Test
    void crearProductoCuandoDatosValidosGuardaYRetornaProducto() {
        // Given
        ProductoRequest request = new ProductoRequest(
                " Resma Carta Blanca ",
                " Papel blanco ",
                " Papel ",
                3500.0,
                120
        );
        when(productoRepository.existsByNombreIgnoreCase("Resma Carta Blanca")).thenReturn(false);
        when(productoRepository.save(any(Producto.class))).thenAnswer(invocation -> {
            Producto producto = invocation.getArgument(0);
            producto.setId(10L);
            return producto;
        });

        // When
        ProductoResponse response = productoService.crearProducto(request);

        // Then
        assertEquals(10L, response.getId());
        assertEquals("Resma Carta Blanca", response.getNombre());
        assertEquals("Papel blanco", response.getDescripcion());
        assertEquals("Papel", response.getCategoria());
        assertEquals(3500.0, response.getPrecio());
        assertEquals(120, response.getStock());
        assertNotNull(response.getFechaCreacion());

        ArgumentCaptor<Producto> productoCaptor = ArgumentCaptor.forClass(Producto.class);
        verify(productoRepository).existsByNombreIgnoreCase("Resma Carta Blanca");
        verify(productoRepository).save(productoCaptor.capture());
        Producto guardado = productoCaptor.getValue();
        assertEquals("Resma Carta Blanca", guardado.getNombre());
        assertEquals("Papel blanco", guardado.getDescripcion());
        assertEquals("Papel", guardado.getCategoria());
        assertEquals(3500.0, guardado.getPrecio());
        assertEquals(120, guardado.getStock());
        assertNotNull(guardado.getFechaCreacion());
    }

    @Test
    void crearProductoCuandoNombreYaExisteLanzaConflictException() {
        // Given
        ProductoRequest request = new ProductoRequest(
                " Resma Carta Blanca ",
                "Papel blanco",
                "Papel",
                3500.0,
                120
        );
        when(productoRepository.existsByNombreIgnoreCase("Resma Carta Blanca")).thenReturn(true);

        // When / Then
        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> productoService.crearProducto(request)
        );
        assertEquals("Ya existe un producto con nombre Resma Carta Blanca", exception.getMessage());
        verify(productoRepository).existsByNombreIgnoreCase("Resma Carta Blanca");
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void obtenerProductoPorIdCuandoExisteRetornaProducto() {
        // Given
        Producto producto = producto(10L, "Resma Carta Blanca", "Papel blanco", "Papel", 3500.0, 120);
        when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));

        // When
        ProductoResponse response = productoService.obtenerProductoPorId(10L);

        // Then
        assertEquals(10L, response.getId());
        assertEquals("Resma Carta Blanca", response.getNombre());
        assertEquals("Papel blanco", response.getDescripcion());
        assertEquals("Papel", response.getCategoria());
        assertEquals(3500.0, response.getPrecio());
        assertEquals(120, response.getStock());
        assertEquals(producto.getFechaCreacion(), response.getFechaCreacion());
        verify(productoRepository).findById(10L);
    }

    @Test
    void obtenerProductoPorIdCuandoNoExisteLanzaResourceNotFoundException() {
        // Given
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> productoService.obtenerProductoPorId(99L)
        );
        assertEquals("Producto no encontrado con ID 99", exception.getMessage());
        verify(productoRepository).findById(99L);
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void actualizarProductoCuandoDatosValidosGuardaYRetornaProductoActualizado() {
        // Given
        Producto producto = producto(10L, "Resma Carta Blanca", "Papel blanco", "Papel", 3500.0, 120);
        ProductoRequest request = new ProductoRequest(
                " Resma Oficio Blanca ",
                " Papel oficio blanco ",
                " Oficina ",
                4200.0,
                80
        );
        when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));
        when(productoRepository.existsByNombreIgnoreCase("Resma Oficio Blanca")).thenReturn(false);
        when(productoRepository.save(producto)).thenReturn(producto);

        // When
        ProductoResponse response = productoService.actualizarProducto(10L, request);

        // Then
        assertEquals(10L, response.getId());
        assertEquals("Resma Oficio Blanca", response.getNombre());
        assertEquals("Papel oficio blanco", response.getDescripcion());
        assertEquals("Oficina", response.getCategoria());
        assertEquals(4200.0, response.getPrecio());
        assertEquals(80, response.getStock());
        verify(productoRepository).findById(10L);
        verify(productoRepository).existsByNombreIgnoreCase("Resma Oficio Blanca");
        verify(productoRepository).save(producto);
    }

    @Test
    void eliminarProductoCuandoExisteEliminaProducto() {
        // Given
        Producto producto = producto(10L, "Resma Carta Blanca", "Papel blanco", "Papel", 3500.0, 120);
        when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));

        // When
        productoService.eliminarProducto(10L);

        // Then
        verify(productoRepository).findById(10L);
        verify(productoRepository).delete(producto);
    }

    private Producto producto(Long id, String nombre, String descripcion, String categoria, Double precio, Integer stock) {
        Producto producto = new Producto();
        producto.setId(id);
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setCategoria(categoria);
        producto.setPrecio(precio);
        producto.setStock(stock);
        producto.setFechaCreacion(LocalDateTime.of(2026, 5, 22, 10, 0));
        return producto;
    }
}
