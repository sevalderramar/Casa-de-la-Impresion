package cl.duocuc.sistemagestionpedidos.producto.service;

import cl.duocuc.sistemagestionpedidos.common.exception.ConflictException;
import cl.duocuc.sistemagestionpedidos.common.exception.ResourceNotFoundException;
import cl.duocuc.sistemagestionpedidos.producto.dto.ProductoRequest;
import cl.duocuc.sistemagestionpedidos.producto.dto.ProductoResponse;
import cl.duocuc.sistemagestionpedidos.producto.model.Producto;
import cl.duocuc.sistemagestionpedidos.producto.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductoService {

    private static final String ESTADO_ACTIVO = "ACTIVO";
    private static final String ESTADO_INACTIVO = "INACTIVO";

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public ProductoResponse crearProducto(ProductoRequest request) {
        String nombreNormalizado = normalizarTexto(request.getNombre());
        if (productoRepository.existsByNombreIgnoreCase(nombreNormalizado)) {
            throw new ConflictException("Ya existe un producto con nombre " + nombreNormalizado);
        }

        validarPrecioYStock(request.getPrecio(), request.getStock());

        Producto producto = new Producto();
        producto.setNombre(nombreNormalizado);
        producto.setDescripcion(normalizarTexto(request.getDescripcion()));
        producto.setCategoria(normalizarTexto(request.getCategoria()));
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setEstado(ESTADO_ACTIVO);
        producto.setFechaCreacion(LocalDateTime.now());

        Producto guardado = productoRepository.save(producto);
        return convertirAResponse(guardado);
    }

    public List<ProductoResponse> listarProductos() {
        return productoRepository.findByEstado(ESTADO_ACTIVO)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public ProductoResponse obtenerProductoPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID " + id));

        validarProductoActivo(producto);
        return convertirAResponse(producto);
    }

    public ProductoResponse buscarPorNombre(String nombre) {
        String nombreNormalizado = normalizarTexto(nombre);
        Producto producto = productoRepository.findByNombreIgnoreCase(nombreNormalizado)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con nombre " + nombreNormalizado));

        validarProductoActivo(producto);
        return convertirAResponse(producto);
    }

    public List<ProductoResponse> listarPorCategoria(String categoria) {
        String categoriaNormalizada = normalizarTexto(categoria);
        return productoRepository.findByCategoriaIgnoreCase(categoriaNormalizada)
                .stream()
                .filter(producto -> ESTADO_ACTIVO.equalsIgnoreCase(producto.getEstado()))
                .map(this::convertirAResponse)
                .toList();
    }

    public ProductoResponse actualizarProducto(Long id, ProductoRequest request) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID " + id));

        validarProductoActivo(producto);

        String nombreNormalizado = normalizarTexto(request.getNombre());
        if (!producto.getNombre().equalsIgnoreCase(nombreNormalizado)
                && productoRepository.existsByNombreIgnoreCase(nombreNormalizado)) {
            throw new ConflictException("Ya existe otro producto con nombre " + nombreNormalizado);
        }

        validarPrecioYStock(request.getPrecio(), request.getStock());

        producto.setNombre(nombreNormalizado);
        producto.setDescripcion(normalizarTexto(request.getDescripcion()));
        producto.setCategoria(normalizarTexto(request.getCategoria()));
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());

        Producto actualizado = productoRepository.save(producto);
        return convertirAResponse(actualizado);
    }

    public void eliminarProductoLogico(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID " + id));

        validarProductoActivo(producto);
        producto.setEstado(ESTADO_INACTIVO);
        productoRepository.save(producto);
    }

    private void validarProductoActivo(Producto producto) {
        if (ESTADO_INACTIVO.equalsIgnoreCase(producto.getEstado())) {
            throw new ResourceNotFoundException("Producto no encontrado o inactivo");
        }
    }

    private void validarPrecioYStock(Double precio, Integer stock) {
        if (precio == null || precio <= 0) {
            throw new ConflictException("El precio debe ser mayor a 0");
        }
        if (stock == null || stock < 0) {
            throw new ConflictException("El stock no puede ser negativo");
        }
    }

    private ProductoResponse convertirAResponse(Producto producto) {
        return new ProductoResponse(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getCategoria(),
                producto.getPrecio(),
                producto.getStock(),
                producto.getEstado(),
                producto.getFechaCreacion()
        );
    }

    private String normalizarTexto(String texto) {
        if (texto == null) {
            return null;
        }
        return texto.trim();
    }
}

