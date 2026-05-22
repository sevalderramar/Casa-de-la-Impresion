# Modelo de datos - fabricacion-service

## Tablas principales

### `ordenes_fabricacion`
| Campo | Tipo | Restricciones | Descripcion |
|---|---|---|---|
| `id` | `BIGINT` | PK, identity | ID interno de la orden |
| `pedido_id` | `BIGINT` | NOT NULL, UNIQUE | Referencia logica al pedido |
| `estado_fabricacion` | `VARCHAR` | NOT NULL | Enum `EstadoFabricacion` |
| `fecha_inicio` | `TIMESTAMP` | NOT NULL | Inicio de fabricacion |
| `fecha_fin` | `TIMESTAMP` | NULL | Fin de fabricacion |
| `fecha_creacion` | `TIMESTAMP` | NOT NULL | Alta de registro |
| `fecha_actualizacion` | `TIMESTAMP` | NULL | Ultima actualizacion |
| `descripcion_estado` | `VARCHAR` | NULL | Detalle de estado |
| `usuario_responsable` | `VARCHAR` | NULL | Responsable de la orden |

### `historial_fabricacion`
| Campo | Tipo | Restricciones | Descripcion |
|---|---|---|---|
| `id` | `BIGINT` | PK, identity | ID del evento |
| `orden_fabricacion_id` | `BIGINT` | FK NOT NULL | Relacion a orden |
| `estado_anterior` | `VARCHAR` | NULL | Estado previo |
| `estado_nuevo` | `VARCHAR` | NOT NULL | Estado aplicado |
| `fecha_cambio` | `TIMESTAMP` | NOT NULL | Fecha del cambio |
| `usuario_id` | `VARCHAR` | NULL | Usuario que ejecuta el cambio |
| `motivo` | `VARCHAR` | NULL | Motivo informado |

## Relacion
- `OrdenFabricacion` 1..N `HistorialFabricacion`
- Cascade y orphan removal activos desde la entidad padre.

## Enum de estados
- `EN_PROCESO`
- `TERMINADO`
- `PAUSADO`

## Observaciones
- `pedido_id` unico evita mas de una orden por pedido.
- `@PrePersist` y `@PreUpdate` completan fechas de auditoria.
