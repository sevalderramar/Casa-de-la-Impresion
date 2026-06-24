package cl.duocuc.pedidoservice.controller;

import cl.duocuc.pedidoservice.dto.EstadoRequest;
import cl.duocuc.pedidoservice.dto.PedidoRequest;
import cl.duocuc.pedidoservice.dto.PedidoResponse;
import cl.duocuc.pedidoservice.client.estado.dto.CambioEstadoResponse;
import cl.duocuc.pedidoservice.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(name = "Pedidos", description = "Operaciones relacionadas con pedidos")
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    @Operation(summary = "Crear pedido", description = "Registra un pedido para un cliente existente y sus productos")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos del pedido a crear",
            required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PedidoRequest.class),
                    examples = @ExampleObject(value = "{\"clienteId\":1,\"estado\":\"COLA\",\"tipoDespacho\":\"RM\",\"items\":[{\"productoId\":10,\"cantidad\":2}]}")))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido creado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PedidoResponse.class),
                            examples = @ExampleObject(value = "{\"numeroPedido\":1001,\"clienteId\":1,\"estado\":\"COLA\",\"tipoDespacho\":\"RM\",\"monto\":7000.0,\"fechaCreacion\":\"2026-05-22T10:00:00\",\"items\":[{\"id\":1,\"productoId\":10,\"nombreProducto\":\"Resma Carta Blanca\",\"cantidad\":2,\"precioUnitario\":3500.0,\"subtotal\":7000.0}]}"))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"mensaje\":\"clienteId: El clienteId es obligatorio\",\"status\":400}"))),
            @ApiResponse(responseCode = "404", description = "Cliente, producto o registro de estado no encontrado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"mensaje\":\"Cliente no encontrado con id: 1\",\"status\":404}"))),
            @ApiResponse(responseCode = "503", description = "Microservicio externo no disponible",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"mensaje\":\"No se pudo conectar con el microservicio correspondiente\",\"status\":503}")))
    })
    public ResponseEntity<PedidoResponse> crearPedido(@Valid @RequestBody PedidoRequest request) {
        PedidoResponse response = pedidoService.crearPedido(request);
        return ResponseEntity
                .created(URI.create("/api/pedidos/" + response.getNumeroPedido()))
                .body(response);
    }

    @GetMapping
    @Operation(summary = "Listar pedidos", description = "Obtiene todos los pedidos registrados")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = PedidoResponse.class)),
                            examples = @ExampleObject(value = "[{\"numeroPedido\":1001,\"clienteId\":1,\"estado\":\"COLA\",\"tipoDespacho\":\"RM\",\"monto\":7000.0,\"fechaCreacion\":\"2026-05-22T10:00:00\",\"items\":[{\"id\":1,\"productoId\":10,\"nombreProducto\":\"Resma Carta Blanca\",\"cantidad\":2,\"precioUnitario\":3500.0,\"subtotal\":7000.0}]}]")))
    })
    public ResponseEntity<List<PedidoResponse>> listarPedidos() {
        return ResponseEntity.ok(pedidoService.listarPedidos());
    }

    @GetMapping("/{numeroPedido}")
    @Operation(summary = "Buscar pedido por numero", description = "Consulta un pedido por su numero identificador")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PedidoResponse.class),
                            examples = @ExampleObject(value = "{\"numeroPedido\":1001,\"clienteId\":1,\"estado\":\"COLA\",\"tipoDespacho\":\"RM\",\"monto\":7000.0,\"fechaCreacion\":\"2026-05-22T10:00:00\",\"items\":[{\"id\":1,\"productoId\":10,\"nombreProducto\":\"Resma Carta Blanca\",\"cantidad\":2,\"precioUnitario\":3500.0,\"subtotal\":7000.0}]}"))),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"mensaje\":\"Pedido no encontrado con numero 1001\",\"status\":404}")))
    })
    public ResponseEntity<PedidoResponse> obtenerPedidoPorNumero(
            @Parameter(description = "Numero del pedido", example = "1001") @PathVariable Long numeroPedido) {
        return ResponseEntity.ok(pedidoService.obtenerPedidoPorNumero(numeroPedido));
    }

    @GetMapping("/numero/{numeroPedido}")
    @Operation(summary = "Buscar pedido por numero en texto", description = "Consulta un pedido recibiendo el numero como texto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PedidoResponse.class),
                            examples = @ExampleObject(value = "{\"numeroPedido\":1001,\"clienteId\":1,\"estado\":\"COLA\",\"tipoDespacho\":\"RM\",\"monto\":7000.0,\"fechaCreacion\":\"2026-05-22T10:00:00\",\"items\":[{\"id\":1,\"productoId\":10,\"nombreProducto\":\"Resma Carta Blanca\",\"cantidad\":2,\"precioUnitario\":3500.0,\"subtotal\":7000.0}]}"))),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado o numero invalido",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"mensaje\":\"Numero de pedido inválido: abc\",\"status\":404}")))
    })
    public ResponseEntity<PedidoResponse> obtenerPedidoPorNumeroStr(
            @Parameter(description = "Numero del pedido como texto", example = "1001") @PathVariable String numeroPedido) {
        return ResponseEntity.ok(pedidoService.obtenerPedidoPorNumeroStr(numeroPedido));
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Listar pedidos por cliente", description = "Obtiene los pedidos registrados para un cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = PedidoResponse.class)),
                            examples = @ExampleObject(value = "[{\"numeroPedido\":1001,\"clienteId\":1,\"estado\":\"COLA\",\"tipoDespacho\":\"RM\",\"monto\":7000.0,\"fechaCreacion\":\"2026-05-22T10:00:00\",\"items\":[{\"id\":1,\"productoId\":10,\"nombreProducto\":\"Resma Carta Blanca\",\"cantidad\":2,\"precioUnitario\":3500.0,\"subtotal\":7000.0}]}]")))
    })
    public ResponseEntity<List<PedidoResponse>> listarPedidosPorCliente(
            @Parameter(description = "ID del cliente", example = "1") @PathVariable Long clienteId) {
        return ResponseEntity.ok(pedidoService.listarPedidosPorCliente(clienteId));
    }

    @PostMapping("/{numeroPedido}/estado")
    @Operation(summary = "Actualizar estado de pedido", description = "Cambia el estado de un pedido existente")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Nuevo estado del pedido",
            required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = EstadoRequest.class),
                    examples = @ExampleObject(value = "{\"estado\":\"PRODUCCION\"}")))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado actualizado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PedidoResponse.class),
                            examples = @ExampleObject(value = "{\"numeroPedido\":1001,\"clienteId\":1,\"estado\":\"PRODUCCION\",\"tipoDespacho\":\"RM\",\"monto\":7000.0,\"fechaCreacion\":\"2026-05-22T10:00:00\",\"items\":[{\"id\":1,\"productoId\":10,\"nombreProducto\":\"Resma Carta Blanca\",\"cantidad\":2,\"precioUnitario\":3500.0,\"subtotal\":7000.0}]}"))),
            @ApiResponse(responseCode = "400", description = "Estado invalido o datos invalidos",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"mensaje\":\"estado: El estado es obligatorio\",\"status\":400}"))),
            @ApiResponse(responseCode = "404", description = "Pedido o registro de estado no encontrado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"mensaje\":\"Pedido no encontrado con numero 1001\",\"status\":404}"))),
            @ApiResponse(responseCode = "503", description = "Microservicio externo no disponible",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"mensaje\":\"No se pudo conectar con el microservicio correspondiente\",\"status\":503}")))
    })
    public ResponseEntity<PedidoResponse> actualizarEstado(
            @Parameter(description = "Numero del pedido", example = "1001") @PathVariable Long numeroPedido,
            @Valid @RequestBody EstadoRequest request) {
        return ResponseEntity.ok(pedidoService.actualizarEstado(numeroPedido, request));
    }

    @GetMapping("/{numeroPedido}/historial")
    @Operation(summary = "Obtener historial de estado", description = "Consulta los cambios de estado registrados para un pedido")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Historial obtenido",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CambioEstadoResponse.class)),
                            examples = @ExampleObject(value = "[{\"id\":1,\"numeroPedido\":1001,\"estadoAnterior\":\"COLA\",\"estadoNuevo\":\"PRODUCCION\",\"fechaCambio\":\"2026-05-22T11:00:00\",\"observacion\":\"Cambio automatico de estado\"}]"))),
            @ApiResponse(responseCode = "404", description = "Historial no encontrado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"mensaje\":\"Historial de estados no encontrado para el pedido: 1001\",\"status\":404}"))),
            @ApiResponse(responseCode = "503", description = "Microservicio externo no disponible",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"mensaje\":\"No se pudo conectar con el microservicio correspondiente\",\"status\":503}")))
    })
    public ResponseEntity<List<CambioEstadoResponse>> obtenerHistorial(
            @Parameter(description = "Numero del pedido", example = "1001") @PathVariable Long numeroPedido) {
        return ResponseEntity.ok(pedidoService.listarHistorial(numeroPedido));
    }


    @DeleteMapping("/{numeroPedido}")
    @Operation(summary = "Eliminar pedido", description = "Elimina un pedido existente")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Pedido eliminado"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"mensaje\":\"Pedido no encontrado con numero 1001\",\"status\":404}")))
    })
    public ResponseEntity<Void> eliminarPedido(
            @Parameter(description = "Numero del pedido", example = "1001") @PathVariable Long numeroPedido) {
        pedidoService.eliminarPedido(numeroPedido);
        return ResponseEntity.noContent().build();
    }









}
