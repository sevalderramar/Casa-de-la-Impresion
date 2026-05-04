package cl.duocuc.sistemagestionpedidos.pedido.service;

import cl.duocuc.sistemagestionpedidos.common.exception.ConflictException;
import cl.duocuc.sistemagestionpedidos.common.exception.ResourceNotFoundException;
import cl.duocuc.sistemagestionpedidos.pedido.dto.*;
import cl.duocuc.sistemagestionpedidos.estado.dto.CambioEstadoRequest;
import cl.duocuc.sistemagestionpedidos.estado.dto.CambioEstadoResponse;
import cl.duocuc.sistemagestionpedidos.estado.service.EstadoService;
import cl.duocuc.sistemagestionpedidos.pedido.model.ItemPedido;
import cl.duocuc.sistemagestionpedidos.pedido.model.Pedido;
import cl.duocuc.sistemagestionpedidos.pedido.repository.PedidoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PedidoService {

    // Constantes específicas de negocio pueden añadirse aquí si es necesario

    private final PedidoRepository pedidoRepository;
    private final EstadoService estadoService;
    private final Map<Long, Pedido> cachePedidos = new ConcurrentHashMap<>();

    private static final Set<String> VALID_ESTADOS = Set.of(
            "COLA", "PRODUCCION", "LISTO", "DESPACHADO", "ENTREGADO"
    );

    public PedidoService(PedidoRepository pedidoRepository, EstadoService estadoService) {
        this.pedidoRepository = pedidoRepository;
        this.estadoService = estadoService;
    }

    public PedidoResponse crearPedido(PedidoRequest request) {
        Pedido pedido = new Pedido();
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
        cachePedidos.put(guardado.getNumeroPedido(), guardado);
        return convertirAResponse(guardado);
    }

    public List<PedidoResponse> listarPedidos() {
        return pedidoRepository.findAll()
                .stream()
                .peek(pedido -> cachePedidos.put(pedido.getNumeroPedido(), pedido))
                .map(this::convertirAResponse)
                .toList();
    }

    public PedidoResponse obtenerPedidoPorNumero(Long numeroPedido) {
        Pedido pedido = cachePedidos.get(numeroPedido);
        if (pedido == null) {
            pedido = pedidoRepository.findById(numeroPedido)
                    .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con numero " + numeroPedido));
            cachePedidos.put(pedido.getNumeroPedido(), pedido);
        }
        return convertirAResponse(pedido);
    }

    public PedidoResponse obtenerPedidoPorNumeroStr(String numeroPedido) {
        // mantener compatibilidad caso se busque por string; intenta parsear a Long
        if (numeroPedido == null) {
            throw new ResourceNotFoundException("Numero de pedido inválido: null");
        }
        try {
            Long numero = Long.parseLong(numeroPedido.trim());
            return obtenerPedidoPorNumero(numero);
        } catch (NumberFormatException ex) {
            throw new ResourceNotFoundException("Numero de pedido inválido: " + numeroPedido);
        }
    }

    public List<PedidoResponse> listarPedidosPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteId(clienteId)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public PedidoResponse actualizarEstado(Long numeroPedido, EstadoRequest estadoRequest) {
        String nuevoEstadoNormalizado = normalizarTexto(estadoRequest.getEstado());
        String nuevoEstado = nuevoEstadoNormalizado == null ? null : nuevoEstadoNormalizado.toUpperCase();

        if (nuevoEstado == null || !VALID_ESTADOS.contains(nuevoEstado)) {
            throw new IllegalArgumentException("Estado no válido. Valores permitidos: " + VALID_ESTADOS);
        }

        Pedido pedido = pedidoRepository.findById(numeroPedido)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con numero " + numeroPedido));

        String estadoAnteriorNormalizado = normalizarTexto(pedido.getEstado());
        String estadoAnteriorNorm = estadoAnteriorNormalizado == null ? null : estadoAnteriorNormalizado.toUpperCase();

        // No registrar ni actualizar si el estado no cambia
        if (nuevoEstado.equals(estadoAnteriorNorm)) {
            return convertirAResponse(pedido);
        }

        pedido.setEstado(nuevoEstado);
        Pedido actualizado = pedidoRepository.save(pedido);
        cachePedidos.put(actualizado.getNumeroPedido(), actualizado);

        // Registrar cambio en el servicio de estados
        CambioEstadoRequest cambioRequest = new CambioEstadoRequest(
                actualizado.getNumeroPedido(),
                estadoAnteriorNorm,
                nuevoEstado,
                "Cambio automático de estado"
        );
        estadoService.registrarCambioEstado(cambioRequest);

        return convertirAResponse(actualizado);
    }

    public java.util.List<CambioEstadoResponse> listarHistorial(Long numeroPedido) {
        return estadoService.listarCambiosPorPedido(numeroPedido);
    }

    public void eliminarPedido(Long numeroPedido) {
        // Eliminación física del pedido y cascade de items (definido en la entidad)
        if (!pedidoRepository.existsById(numeroPedido)) {
            throw new ResourceNotFoundException("Pedido no encontrado con numero " + numeroPedido);
        }
        pedidoRepository.deleteById(numeroPedido);
        cachePedidos.remove(numeroPedido);
    }

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
                pedido.getNumeroPedido(),
                pedido.getClienteId(),
                pedido.getEstado(),
                pedido.getTipoDespacho(),
                pedido.getMonto(),
                pedido.getFechaCreacion(),
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
