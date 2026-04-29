package cl.duocuc.sistemagestionpedidos.pedido.service;

import cl.duocuc.sistemagestionpedidos.common.exception.ConflictException;
import cl.duocuc.sistemagestionpedidos.common.exception.ResourceNotFoundException;
import cl.duocuc.sistemagestionpedidos.pedido.dto.*;
import cl.duocuc.sistemagestionpedidos.pedido.model.ItemPedido;
import cl.duocuc.sistemagestionpedidos.pedido.model.Pedido;
import cl.duocuc.sistemagestionpedidos.pedido.repository.PedidoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PedidoService {

    // Constantes específicas de negocio pueden añadirse aquí si es necesario

    private final PedidoRepository pedidoRepository;
    private final Map<Long, Pedido> cachePedidos = new ConcurrentHashMap<>();

    public PedidoService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public PedidoResponse crearPedido(PedidoRequest request) {
        String numeroNormalizado = normalizarTexto(request.getNumeroPedido());
        if (pedidoRepository.existsByNumeroPedido(numeroNormalizado)) {
            throw new ConflictException("Ya existe un pedido con el numero " + numeroNormalizado);
        }

        Pedido pedido = new Pedido();
        pedido.setNumeroPedido(numeroNormalizado);
        pedido.setClienteId(request.getClienteId());
        pedido.setEstado(normalizarTexto(request.getEstado()).toUpperCase());
        pedido.setTipoDespacho(normalizarTexto(request.getTipoDespacho()).toUpperCase());
        pedido.setFechaCreacion(LocalDateTime.now());

        List<ItemPedido> items = request.getItems().stream()
                .map(itemRequest -> crearItemPedido(itemRequest, pedido))
                .toList();

        double montoTotal = items.stream()
                .mapToDouble(ItemPedido::getSubtotal)
                .sum();

        pedido.setMonto(montoTotal);
        pedido.getItems().clear();
        pedido.getItems().addAll(items);

        Pedido guardado = pedidoRepository.save(pedido);
        cachePedidos.put(guardado.getId(), guardado);
        return convertirAResponse(guardado);
    }

    public List<PedidoResponse> listarPedidos() {
        return pedidoRepository.findAll()
                .stream()
                .peek(pedido -> cachePedidos.put(pedido.getId(), pedido))
                .map(this::convertirAResponse)
                .toList();
    }

    public PedidoResponse obtenerPedidoPorId(Long id) {
        Pedido pedido = cachePedidos.get(id);
        if (pedido == null) {
            pedido = pedidoRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ID " + id));
            cachePedidos.put(pedido.getId(), pedido);
        }
        return convertirAResponse(pedido);
    }

    public PedidoResponse obtenerPedidoPorNumero(String numeroPedido) {
        String numeroNormalizado = normalizarTexto(numeroPedido);
        Pedido pedido = pedidoRepository.findByNumeroPedido(numeroNormalizado)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con numero " + numeroNormalizado));
        cachePedidos.put(pedido.getId(), pedido);
        return convertirAResponse(pedido);
    }

    public List<PedidoResponse> listarPedidosPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteId(clienteId)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public PedidoResponse actualizarEstado(Long id, String nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ID " + id));
        pedido.setEstado(normalizarTexto(nuevoEstado).toUpperCase());
        Pedido actualizado = pedidoRepository.save(pedido);
        cachePedidos.put(actualizado.getId(), actualizado);
        return convertirAResponse(actualizado);
    }

    public List<PedidoResponse> filtrarPorEstado(String estado) {
        return pedidoRepository.findByEstado(normalizarTexto(estado).toUpperCase())
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public List<PedidoResponse> filtrarPorEstadoYTipoDespacho(String estado, String tipoDespacho) {
        return pedidoRepository.findByEstadoAndTipoDespacho(
                        normalizarTexto(estado).toUpperCase(),
                        normalizarTexto(tipoDespacho).toUpperCase()
                )
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public void eliminarPedido(Long id) {
        // Eliminación física del pedido y cascade de items (definido en la entidad)
        if (!pedidoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pedido no encontrado con ID " + id);
        }
        pedidoRepository.deleteById(id);
        cachePedidos.remove(id);
    }

    // Nota: se eliminó la lógica de validación de estadoRegistro (eliminación lógica).

    private ItemPedido crearItemPedido(ItemPedidoRequest request, Pedido pedido) {
        ItemPedido item = new ItemPedido();
        item.setProductoId(request.getProductoId());
        item.setNombreProducto(normalizarTexto(request.getNombreProducto()));
        item.setCantidad(request.getCantidad());
        item.setPrecioUnitario(request.getPrecioUnitario());
        item.setSubtotal(request.getCantidad() * request.getPrecioUnitario());
        item.setPedido(pedido);
        return item;
    }

    private PedidoResponse convertirAResponse(Pedido pedido) {
        List<ItemPedidoResponse> items = pedido.getItems().stream()
                .map(item -> new ItemPedidoResponse(
                        item.getId(),
                        item.getProductoId(),
                        item.getNombreProducto(),
                        item.getCantidad(),
                        item.getPrecioUnitario(),
                        item.getSubtotal()
                ))
                .toList();

        return new PedidoResponse(
                pedido.getId(),
                pedido.getNumeroPedido(),
                pedido.getClienteId(),
                pedido.getEstado(),
                pedido.getTipoDespacho(),
                pedido.getMonto(),
                pedido.getFechaCreacion(),
                // estadoRegistro eliminado
                items
        );
    }

    private String normalizarTexto(String texto) {
        if (texto == null) {
            return null;
        }
        return texto.trim();
    }
}
