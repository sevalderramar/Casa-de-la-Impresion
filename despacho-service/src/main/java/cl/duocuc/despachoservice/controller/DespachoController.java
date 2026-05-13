package cl.duocuc.despachoservice.controller;

import cl.duocuc.despachoservice.dto.DespachoRequest;
import cl.duocuc.despachoservice.dto.DespachoResponse;
import cl.duocuc.despachoservice.service.DespachoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/despachos")
public class DespachoController {

    private final DespachoService despachoService;

    public DespachoController(DespachoService despachoService) {
        this.despachoService = despachoService;
    }

    @PostMapping
    public ResponseEntity<DespachoResponse> crearDespacho(@Valid @RequestBody DespachoRequest request) {
        DespachoResponse response = despachoService.crearDespacho(request);
        return ResponseEntity
                .created(URI.create("/api/despachos/" + response.getId()))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<DespachoResponse>> listarDespachos(@RequestParam(value = "tipo", required = false) String tipo) {
        return ResponseEntity.ok(despachoService.listarDespachos(tipo));
    }

    @GetMapping("/{numeroPedido}")
    public ResponseEntity<DespachoResponse> obtenerDespachoPorNumeroPedido(@PathVariable Long numeroPedido) {
        return ResponseEntity.ok(despachoService.obtenerDespachoPorNumeroPedido(numeroPedido));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DespachoResponse> actualizarDespacho(@PathVariable Long id,
                                                               @Valid @RequestBody DespachoRequest request) {
        return ResponseEntity.ok(despachoService.actualizarDespacho(id, request));
    }
}

