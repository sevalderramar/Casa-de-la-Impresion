# API - fabricacion-service

Base URL local: `http://localhost:8085`

## Convenciones
- Rutas bajo `/api/fabricacion`.
- Header requerido en endpoints protegidos:

```http
Authorization: Bearer <TOKEN_JWT>
```

- Respuesta estandar (`ApiResponse<T>`):

```json
{
  "mensaje": "string",
  "data": {},
  "exitoso": true,
  "timestamp": "2026-05-22T10:00:00"
}
```

## Endpoints

### GET `/api/fabricacion/ping`
Healthcheck del servicio.

### POST `/api/fabricacion`
Crea una orden de fabricacion.

**Request**
```json
{
  "numeroPedido": 1001,
  "usuarioResponsable": "operador-01",
  "descripcionEstado": "Inicio de fabricacion"
}
```

**Response 201**
```json
{
  "mensaje": "Orden creada",
  "data": {
    "id": 1,
    "numeroPedido": 1001,
    "estadoFabricacion": "EN_PROCESO"
  },
  "exitoso": true,
  "timestamp": "2026-05-22T10:00:00"
}
```

### GET `/api/fabricacion/{id}`
Obtiene una orden por identificador interno.

### PATCH `/api/fabricacion/{id}/estado`
Actualiza estado de fabricacion.

**Request**
```json
{
  "nuevoEstado": "TERMINADO",
  "motivo": "Lote completado",
  "usuarioId": "operador-01"
}
```

## Integracion Feign
- `PedidoFeignClient` (`services.pedido.url`) para validar existencia de pedido y notificar cambios de estado.

## Errores comunes
- `400`: validacion o error de negocio.
- `404`: pedido/orden no encontrado.
- `409`: conflicto de estado/duplicidad.
- `502`: error de comunicacion con `pedido-service`.
- `500`: error interno.


