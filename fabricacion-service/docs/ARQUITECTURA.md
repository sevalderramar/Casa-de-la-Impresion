
# Arquitectura - fabricacion-service

## Vision
`fabricacion-service` sigue arquitectura en capas y usa Feign para coordinar estados con `pedido-service`.

## Capas
- `controller`: `OrdenFabricacionController`.
- `service`: `OrdenFabricacionService`.
- `repository`: `OrdenFabricacionRepository`, `HistorialFabricacionRepository`.
- `client`: `PedidoFeignClient`, `PedidoServiceClient`.
- `entity`: `OrdenFabricacion`, `HistorialFabricacion`, `EstadoFabricacion`.
- `handler/exception`: manejo global de errores (Lesson 18).

## Flujo de creacion
1. `POST /api/fabricacion`.
2. Validar pedido en `pedido-service`.
3. Verificar que no exista orden duplicada por `pedidoId`.
4. Persistir orden (`EN_PROCESO`) + registrar historial.
5. Notificar a `pedido-service` inicio de fabricacion.

## Flujo de cambio de estado
1. `PATCH /api/fabricacion/{id}/estado`.
2. Cargar orden y aplicar `nuevoEstado`.
3. Si estado final es `TERMINADO`, notificar pedido listo.
4. Persistir cambio y registrar historial.

## Dependencias externas
- `pedido-service` (REST via OpenFeign).

## Notas de seguridad
- Endpoints protegidos por JWT.
- Header `Authorization: Bearer <token>` propagado para llamadas Feign via interceptor.
