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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
    void crearProductoCuandoPrecioEsNullLanzaConflictException() {
        // Given
        ProductoRequest request = request("Resma Carta Blanca", "Papel blanco", "Papel", null, 120);
        when(productoRepository.existsByNombreIgnoreCase("Resma Carta Blanca")).thenReturn(false);

        // When / Then
        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> productoService.crearProducto(request)
        );
        assertEquals("El precio debe ser mayor a 0", exception.getMessage());
        verify(productoRepository).existsByNombreIgnoreCase("Resma Carta Blanca");
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void crearProductoCuandoPrecioEsCeroLanzaConflictException() {
        // Given
        ProductoRequest request = request("Resma Carta Blanca", "Papel blanco", "Papel", 0.0, 120);
        when(productoRepository.existsByNombreIgnoreCase("Resma Carta Blanca")).thenReturn(false);

        // When / Then
        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> productoService.crearProducto(request)
        );
        assertEquals("El precio debe ser mayor a 0", exception.getMessage());
        verify(productoRepository).existsByNombreIgnoreCase("Resma Carta Blanca");
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void crearProductoCuandoStockEsNullLanzaConflictException() {
        // Given
        ProductoRequest request = request("Resma Carta Blanca", "Papel blanco", "Papel", 3500.0, null);
        when(productoRepository.existsByNombreIgnoreCase("Resma Carta Blanca")).thenReturn(false);

        // When / Then
        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> productoService.crearProducto(request)
        );
        assertEquals("El stock no puede ser negativo", exception.getMessage());
        verify(productoRepository).existsByNombreIgnoreCase("Resma Carta Blanca");
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void crearProductoCuandoStockEsNegativoLanzaConflictException() {
        // Given
        ProductoRequest request = request("Resma Carta Blanca", "Papel blanco", "Papel", 3500.0, -1);
        when(productoRepository.existsByNombreIgnoreCase("Resma Carta Blanca")).thenReturn(false);

        // When / Then
        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> productoService.crearProducto(request)
        );
        assertEquals("El stock no puede ser negativo", exception.getMessage());
        verify(productoRepository).existsByNombreIgnoreCase("Resma Carta Blanca");
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void crearProductoPermiteDescripcionNull() {
        // Given
        ProductoRequest request = request("Resma Carta Blanca", null, "Papel", 3500.0, 120);
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
        assertNull(response.getDescripcion());
        verify(productoRepository).existsByNombreIgnoreCase("Resma Carta Blanca");
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    void listarProductosRetornaProductosDelRepositorio() {
        // Given
        Producto producto = producto(10L, "Resma Carta Blanca", "Papel blanco", "Papel", 3500.0, 120);
        when(productoRepository.findAll()).thenReturn(List.of(producto));

        // When
        List<ProductoResponse> responses = productoService.listarProductos();

        // Then
        assertEquals(1, responses.size());
        assertEquals(10L, responses.get(0).getId());
        assertEquals("Resma Carta Blanca", responses.get(0).getNombre());
        verify(productoRepository).findAll();
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
    void buscarPorNombreCuandoExisteRetornaProducto() {
        // Given
        Producto producto = producto(10L, "Resma Carta Blanca", "Papel blanco", "Papel", 3500.0, 120);
        when(productoRepository.findByNombreIgnoreCase("Resma Carta Blanca")).thenReturn(Optional.of(producto));

        // When
        ProductoResponse response = productoService.buscarPorNombre(" Resma Carta Blanca ");

        // Then
        assertEquals(10L, response.getId());
        assertEquals("Resma Carta Blanca", response.getNombre());
        verify(productoRepository).findByNombreIgnoreCase("Resma Carta Blanca");
    }

    @Test
    void buscarPorNombreCuandoNoExisteLanzaResourceNotFoundException() {
        // Given
        when(productoRepository.findByNombreIgnoreCase("Resma Carta Blanca")).thenReturn(Optional.empty());

        // When / Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> productoService.buscarPorNombre(" Resma Carta Blanca ")
        );
        assertEquals("Producto no encontrado con nombre Resma Carta Blanca", exception.getMessage());
        verify(productoRepository).findByNombreIgnoreCase("Resma Carta Blanca");
    }

    @Test
    void listarPorCategoriaRetornaProductosDelRepositorio() {
        // Given
        Producto producto = producto(10L, "Resma Carta Blanca", "Papel blanco", "Papel", 3500.0, 120);
        when(productoRepository.findByCategoriaIgnoreCase("Papel")).thenReturn(List.of(producto));

        // When
        List<ProductoResponse> responses = productoService.listarPorCategoria(" Papel ");

        // Then
        assertEquals(1, responses.size());
        assertEquals("Papel", responses.get(0).getCategoria());
        verify(productoRepository).findByCategoriaIgnoreCase("Papel");
    }

    @Test
    void listarPorCategoriaRetornaListaVaciaCuandoRepositorioNoEncuentraProductos() {
        // Given
        when(productoRepository.findByCategoriaIgnoreCase("Papel")).thenReturn(List.of());

        // When
        List<ProductoResponse> responses = productoService.listarPorCategoria(" Papel ");

        // Then
        assertEquals(0, responses.size());
        verify(productoRepository).findByCategoriaIgnoreCase("Papel");
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
    void actualizarProductoCuandoMantieneMismoNombreNoConsultaDuplicado() {
        // Given
        Producto producto = producto(10L, "Resma Carta Blanca", "Papel blanco", "Papel", 3500.0, 120);
        ProductoRequest request = request(" resma carta blanca ", "Papel blanco actualizado", "Papel", 3600.0, 100);
        when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(producto)).thenReturn(producto);

        // When
        ProductoResponse response = productoService.actualizarProducto(10L, request);

        // Then
        assertEquals("resma carta blanca", response.getNombre());
        assertEquals("Papel blanco actualizado", response.getDescripcion());
        assertEquals(3600.0, response.getPrecio());
        assertEquals(100, response.getStock());
        verify(productoRepository).findById(10L);
        verify(productoRepository, never()).existsByNombreIgnoreCase(anyString());
        verify(productoRepository).save(producto);
    }

    @Test
    void actualizarProductoCuandoNoExisteLanzaResourceNotFoundException() {
        // Given
        ProductoRequest request = request("Resma Carta Blanca", "Papel blanco", "Papel", 3500.0, 120);
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> productoService.actualizarProducto(99L, request)
        );
        assertEquals("Producto no encontrado con ID 99", exception.getMessage());
        verify(productoRepository).findById(99L);
        verify(productoRepository, never()).existsByNombreIgnoreCase(anyString());
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void actualizarProductoCuandoNuevoNombreYaExisteLanzaConflictException() {
        // Given
        Producto producto = producto(10L, "Resma Carta Blanca", "Papel blanco", "Papel", 3500.0, 120);
        ProductoRequest request = request("Resma Oficio Blanca", "Papel oficio", "Oficina", 4200.0, 80);
        when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));
        when(productoRepository.existsByNombreIgnoreCase("Resma Oficio Blanca")).thenReturn(true);

        // When / Then
        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> productoService.actualizarProducto(10L, request)
        );
        assertEquals("Ya existe otro producto con nombre Resma Oficio Blanca", exception.getMessage());
        verify(productoRepository).findById(10L);
        verify(productoRepository).existsByNombreIgnoreCase("Resma Oficio Blanca");
        verify(productoRepository, never()).save(any(Producto.class));
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

    @Test
    void eliminarProductoCuandoNoExisteLanzaResourceNotFoundException() {
        // Given
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> productoService.eliminarProducto(99L)
        );
        assertEquals("Producto no encontrado con ID 99", exception.getMessage());
        verify(productoRepository).findById(99L);
        verify(productoRepository, never()).delete(any(Producto.class));
    }

    private ProductoRequest request(String nombre, String descripcion, String categoria, Double precio, Integer stock) {
        return new ProductoRequest(nombre, descripcion, categoria, precio, stock);
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
