package cl.duocuc.pedidoservice.service;

import cl.duocuc.pedidoservice.client.ClienteFeignClient;
import cl.duocuc.pedidoservice.client.EstadoFeignClient;
import cl.duocuc.pedidoservice.client.ProductoFeignClient;
import cl.duocuc.pedidoservice.client.estado.dto.CambioEstadoRequest;
import cl.duocuc.pedidoservice.client.estado.dto.CambioEstadoResponse;
import cl.duocuc.pedidoservice.client.producto.dto.ProductoResponse;
import cl.duocuc.pedidoservice.common.exception.ResourceNotFoundException;
import cl.duocuc.pedidoservice.common.exception.ServiceUnavailableException;
import cl.duocuc.pedidoservice.dto.EstadoRequest;
import cl.duocuc.pedidoservice.dto.ItemPedidoRequest;
import cl.duocuc.pedidoservice.dto.ItemPedidoResponse;
import cl.duocuc.pedidoservice.dto.PedidoRequest;
import cl.duocuc.pedidoservice.dto.PedidoResponse;
import cl.duocuc.pedidoservice.model.ItemPedido;
import cl.duocuc.pedidoservice.model.Pedido;
import cl.duocuc.pedidoservice.repository.PedidoRepository;
import feign.FeignException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteFeignClient clienteFeignClient;
    private final ProductoFeignClient productoFeignClient;
    private final EstadoFeignClient estadoFeignClient;
    private final Map<Long, Pedido> cachePedidos = new ConcurrentHashMap<>();

    private static final Set<String> VALID_ESTADOS = Set.of(
            "COLA", "PRODUCCION", "LISTO", "DESPACHADO", "ENTREGADO"
    );

    public PedidoService(PedidoRepository pedidoRepository,
                         ClienteFeignClient clienteFeignClient,
                         ProductoFeignClient productoFeignClient,
                         EstadoFeignClient estadoFeignClient) {
        this.pedidoRepository = pedidoRepository;
        this.clienteFeignClient = clienteFeignClient;
        this.productoFeignClient = productoFeignClient;
        this.estadoFeignClient = estadoFeignClient;
    }

    public PedidoResponse crearPedido(PedidoRequest request) {
        validarCliente(request.getClienteId());

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
        registrarCambioEstado(
                guardado,
                "SIN_ESTADO",
                guardado.getEstado());
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

        if (nuevoEstado.equals(estadoAnteriorNorm)) {
            return convertirAResponse(pedido);
        }

        pedido.setEstado(nuevoEstado);
        Pedido actualizado = pedidoRepository.save(pedido);
        cachePedidos.put(actualizado.getNumeroPedido(), actualizado);

        registrarCambioEstado(actualizado, estadoAnteriorNorm, nuevoEstado);

        return convertirAResponse(actualizado);
    }

    public List<CambioEstadoResponse> listarHistorial(Long numeroPedido) {
        try {
            return estadoFeignClient.listarCambiosPorPedido(numeroPedido);
        } catch (FeignException.NotFound ex) {
            throw new ResourceNotFoundException("Historial de estados no encontrado para el pedido: " + numeroPedido);
        } catch (FeignException ex) {
            throw new ServiceUnavailableException("No se pudo conectar con el microservicio correspondiente", ex);
        }
    }

    public void eliminarPedido(Long numeroPedido) {
        if (!pedidoRepository.existsById(numeroPedido)) {
            throw new ResourceNotFoundException("Pedido no encontrado con numero " + numeroPedido);
        }
        pedidoRepository.deleteById(numeroPedido);
        cachePedidos.remove(numeroPedido);
    }

    private void validarCliente(Long clienteId) {
        try {
            clienteFeignClient.obtenerClientePorId(clienteId);
        } catch (FeignException.NotFound ex) {
            throw new ResourceNotFoundException("Cliente no encontrado con id: " + clienteId);
        } catch (FeignException ex) {
            System.out.println("ERROR CLIENTE FEIGN");
            System.out.println("STATUS: " + ex.status());
            System.out.println("MENSAJE: " + ex.getMessage());
            System.out.println("BODY: " + ex.contentUTF8());

            throw new ServiceUnavailableException("No se pudo conectar con el microservicio correspondiente", ex);
        }
    }

    private ItemPedido crearItemPedido(ItemPedidoRequest request, Pedido pedido) {
        ProductoResponse producto = obtenerProducto(request.getProductoId());

        ItemPedido item = new ItemPedido();
        item.setProductoId(producto.getId());
        item.setNombreProducto(normalizarTexto(producto.getNombre()));
        item.setCantidad(request.getCantidad());
        item.setPrecioUnitario(producto.getPrecio());
        item.setSubtotal(request.getCantidad() * producto.getPrecio());
        item.setPedido(pedido);
        return item;
    }

    private ProductoResponse obtenerProducto(Long productoId) {
        try {
            return productoFeignClient.obtenerProductoPorId(productoId);
        } catch (FeignException.NotFound ex) {
            throw new ResourceNotFoundException("Producto no encontrado con id: " + productoId);
        } catch (FeignException ex) {
            System.out.println("ERROR PRODUCTO FEIGN");
            System.out.println("STATUS: " + ex.status());
            System.out.println("MENSAJE: " + ex.getMessage());
            System.out.println("BODY: " + ex.contentUTF8());

            throw new ServiceUnavailableException("No se pudo conectar con el microservicio correspondiente", ex);
        }
    }

    private void registrarCambioEstado(Pedido pedido, String estadoAnteriorNorm, String nuevoEstado) {
        CambioEstadoRequest cambioRequest = new CambioEstadoRequest(
                pedido.getNumeroPedido(),
                estadoAnteriorNorm,
                nuevoEstado,
                "Cambio automático de estado"
        );

        try {
            estadoFeignClient.registrarCambioEstado(cambioRequest);
        } catch (FeignException.NotFound ex) {
            throw new ResourceNotFoundException("No se pudo registrar el cambio de estado para el pedido: " + pedido.getNumeroPedido());
        } catch (FeignException ex) {

            System.out.println("ERROR ESTADO FEIGN");
            System.out.println("STATUS: " + ex.status());
            System.out.println("MENSAJE: " + ex.getMessage());
            System.out.println("BODY: " + ex.contentUTF8());

            throw new ServiceUnavailableException("No se pudo conectar con el microservicio correspondiente", ex);
        }
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
