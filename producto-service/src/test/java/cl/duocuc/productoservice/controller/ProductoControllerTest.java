package cl.duocuc.productoservice.controller;

import cl.duocuc.productoservice.common.exception.ConflictException;
import cl.duocuc.productoservice.common.exception.ResourceNotFoundException;
import cl.duocuc.productoservice.config.JwtUtil;
import cl.duocuc.productoservice.dto.ProductoRequest;
import cl.duocuc.productoservice.dto.ProductoResponse;
import cl.duocuc.productoservice.service.ProductoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductoController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ProductoService productoService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    void crearProductoRetornaCreatedCuandoRequestEsValido() throws Exception {
        // Given
        ProductoRequest request = requestValido();
        ProductoResponse response = response(10L, "Resma Carta Blanca");
        when(productoService.crearProducto(any(ProductoRequest.class))).thenReturn(response);

        // When / Then
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/productos/10"))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.nombre").value("Resma Carta Blanca"))
                .andExpect(jsonPath("$.categoria").value("Papel"));

        verify(productoService).crearProducto(any(ProductoRequest.class));
    }

    @Test
    void listarProductosRetornaListado() throws Exception {
        // Given
        when(productoService.listarProductos()).thenReturn(List.of(response(10L, "Resma Carta Blanca")));

        // When / Then
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].nombre").value("Resma Carta Blanca"));

        verify(productoService).listarProductos();
    }

    @Test
    void obtenerProductoPorIdRetornaProductoCuandoExiste() throws Exception {
        // Given
        when(productoService.obtenerProductoPorId(10L)).thenReturn(response(10L, "Resma Carta Blanca"));

        // When / Then
        mockMvc.perform(get("/api/productos/{id}", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.nombre").value("Resma Carta Blanca"));

        verify(productoService).obtenerProductoPorId(10L);
    }

    @Test
    void obtenerProductoPorIdRetornaNotFoundCuandoNoExiste() throws Exception {
        // Given
        when(productoService.obtenerProductoPorId(99L))
                .thenThrow(new ResourceNotFoundException("Producto no encontrado con ID 99"));

        // When / Then
        mockMvc.perform(get("/api/productos/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensaje").value("Producto no encontrado con ID 99"));

        verify(productoService).obtenerProductoPorId(99L);
    }

    @Test
    void buscarPorNombreRetornaProductoCuandoExiste() throws Exception {
        // Given
        when(productoService.buscarPorNombre("Resma Carta Blanca")).thenReturn(response(10L, "Resma Carta Blanca"));

        // When / Then
        mockMvc.perform(get("/api/productos/nombre/{nombre}", "Resma Carta Blanca"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.nombre").value("Resma Carta Blanca"));

        verify(productoService).buscarPorNombre("Resma Carta Blanca");
    }

    @Test
    void listarPorCategoriaRetornaListado() throws Exception {
        // Given
        when(productoService.listarPorCategoria("Papel")).thenReturn(List.of(response(10L, "Resma Carta Blanca")));

        // When / Then
        mockMvc.perform(get("/api/productos/categoria/{categoria}", "Papel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].categoria").value("Papel"));

        verify(productoService).listarPorCategoria("Papel");
    }

    @Test
    void actualizarProductoRetornaOkCuandoRequestEsValido() throws Exception {
        // Given
        ProductoRequest request = requestValido();
        ProductoResponse response = response(10L, "Resma Carta Blanca");
        when(productoService.actualizarProducto(eq(10L), any(ProductoRequest.class))).thenReturn(response);

        // When / Then
        mockMvc.perform(put("/api/productos/{id}", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.nombre").value("Resma Carta Blanca"));

        verify(productoService).actualizarProducto(eq(10L), any(ProductoRequest.class));
    }

    @Test
    void crearProductoRetornaConflictCuandoServicioDetectaNombreDuplicado() throws Exception {
        // Given
        ProductoRequest request = requestValido();
        when(productoService.crearProducto(any(ProductoRequest.class)))
                .thenThrow(new ConflictException("Ya existe un producto con nombre Resma Carta Blanca"));

        // When / Then
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.mensaje").value("Ya existe un producto con nombre Resma Carta Blanca"));

        verify(productoService).crearProducto(any(ProductoRequest.class));
    }

    @Test
    void crearProductoRetornaBadRequestCuandoRequestEsInvalido() throws Exception {
        // Given
        ProductoRequest request = new ProductoRequest(null, "Papel blanco", "Papel", 3500.0, 120);

        // When / Then
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensaje", containsString("nombre")));
    }

    @Test
    void eliminarProductoRetornaNoContentCuandoExiste() throws Exception {
        // Given
        doNothing().when(productoService).eliminarProducto(10L);

        // When / Then
        mockMvc.perform(delete("/api/productos/{id}", 10L))
                .andExpect(status().isNoContent());

        verify(productoService).eliminarProducto(10L);
    }

    private ProductoRequest requestValido() {
        return new ProductoRequest("Resma Carta Blanca", "Papel blanco", "Papel", 3500.0, 120);
    }

    private ProductoResponse response(Long id, String nombre) {
        return new ProductoResponse(id, nombre, "Papel blanco", "Papel", 3500.0, 120,
                LocalDateTime.of(2026, 5, 22, 10, 0));
    }
}
