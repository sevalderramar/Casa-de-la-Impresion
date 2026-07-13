# Documentacion Funcional

## Problema Que Resuelve

Casa de la Impresion necesita administrar pedidos de impresion desde el registro del cliente y productos hasta el seguimiento de estado, fabricacion, despacho, metricas operacionales y auditoria mediante logs. El sistema separa responsabilidades en microservicios para facilitar mantenimiento, pruebas y evolucion por modulo.

## Actores

| Actor | Descripcion | Acciones principales |
|---|---|---|
| Administrador | Usuario con permisos para operar el sistema completo | Gestionar usuarios, clientes, productos, pedidos, estados y reportes |
| Ejecutivo de ventas | Usuario que registra clientes y pedidos | Crear clientes, consultar productos, crear pedidos |
| Operador de fabricacion | Usuario que gestiona ordenes internas | Crear orden de fabricacion y cambiar estado de fabricacion |
| Operador de despacho | Usuario que prepara entregas | Registrar despacho, transportista y seguimiento |
| Supervisor | Usuario que revisa trazabilidad y metricas | Consultar metricas, historial, logs y estados |
| Sistema externo | Consumidor tecnico por API | Invocar endpoints REST mediante Gateway o servicio directo |

## Requerimientos Funcionales

| ID | Requerimiento | Servicio responsable | Endpoints principales |
|---|---|---|---|
| RF-01 | Autenticar usuarios | `auth-service` | `POST /api/auth/login`, `POST /api/auth/logout` |
| RF-02 | Gestionar usuarios | `auth-service` | `/api/auth/usuarios` |
| RF-03 | Gestionar clientes | `cliente-service` | `/api/clientes` |
| RF-04 | Gestionar productos | `producto-service` | `/api/productos` |
| RF-05 | Crear y consultar pedidos | `pedido-service` | `/api/pedidos` |
| RF-06 | Cambiar estado de pedido | `pedido-service`, `estado-service` | `/api/pedidos/{numeroPedido}/estado`, `/api/estados` |
| RF-07 | Consultar historial de estado | `pedido-service`, `estado-service` | `/api/pedidos/{numeroPedido}/historial`, `/api/estados/pedido/{numeroPedido}` |
| RF-08 | Gestionar despacho | `despacho-service` | `/api/despachos` |
| RF-09 | Gestionar fabricacion | `fabricacion-service` | `/api/fabricacion` |
| RF-10 | Consultar metricas | `metrica-service` | `/api/metricas/**` |
| RF-11 | Gestionar transportistas | `transportista-service` | `/api/transportistas` |
| RF-12 | Registrar y consultar logs | `log-service` | `/api/logs` |

## Flujos Principales

### Flujo De Pedido

1. Crear o consultar cliente en `cliente-service`.
2. Crear o consultar productos en `producto-service`.
3. Crear pedido en `pedido-service` usando cliente y productos existentes.
4. `pedido-service` valida datos remotos con Feign cuando corresponde.
5. Registrar estado inicial o cambio de estado en `estado-service`.
6. Consultar pedido, historial y estado actual desde Gateway o servicio directo.

### Flujo De Fabricacion

1. Crear orden de fabricacion para un pedido.
2. Consultar orden por ID.
3. Actualizar estado de fabricacion.
4. Mantener historial interno del proceso productivo.

### Flujo De Despacho

1. Crear despacho asociado a un numero de pedido.
2. Asignar tipo de despacho, transportista y codigo de seguimiento.
3. Consultar despacho por numero de pedido.
4. Actualizar datos del despacho si corresponde.

### Flujo De Metricas

1. Consultar metricas por cliente.
2. Consultar ranking de clientes.
3. Consultar productos mas vendidos.
4. Consultar resumen de ventas por rango de fechas.

### Flujo De Auditoria

1. Registrar eventos en `log-service` con servicio, operacion, usuario, resultado y detalle.
2. Consultar logs por servicio o fecha.
3. Usar logs como apoyo de trazabilidad operacional.

## Reglas De Negocio

| Regla | Descripcion | Servicio |
|---|---|---|
| RN-01 | Un pedido debe referenciar un cliente valido | `pedido-service` |
| RN-02 | Un pedido debe contener productos validos | `pedido-service` |
| RN-03 | Los cambios de estado deben mantener historial | `estado-service`, `pedido-service` |
| RN-04 | Un despacho debe estar asociado a un numero de pedido | `despacho-service` |
| RN-05 | Una orden de fabricacion debe estar asociada al flujo productivo | `fabricacion-service` |
| RN-06 | Los logs deben registrar servicio, operacion y resultado | `log-service` |
| RN-07 | Las metricas se calculan a partir de datos existentes y llamadas remotas toleradas | `metrica-service` |
| RN-08 | Las credenciales reales no deben almacenarse en el repositorio | Todos |

## Estados De Pedido

Los estados finales documentados por `pedido-service` son los siguientes:

| Estado | Uso |
|---|---|
| COLA | Pedido registrado y pendiente de procesamiento |
| PRODUCCION | Pedido enviado a produccion |
| LISTO | Pedido listo para despacho |
| DESPACHADO | Pedido entregado a transporte |
| ENTREGADO | Pedido finalizado |

## Restricciones

| Restriccion | Justificacion |
|---|---|
| Demo local usa H2 | Facilita evaluacion sin infraestructura externa |
| Docker Compose es demo minima | Cubre Gateway, pedido, cliente, producto y estado |
| Discovery usa Eureka | Los servicios se registran en `discovery-server` y el Gateway mantiene prefijos `/api/**` |
| Render no tiene URLs publicas versionadas | Se documenta configuracion con placeholders |
| Endpoints demo/local estan permitidos para facilitar pruebas | JWT esta implementado, pero la demo prioriza ejecucion evaluable |

## Ejemplos De Uso

### Crear Cliente

```http
POST http://localhost:8080/api/clientes
Content-Type: application/json

{
  "nombre": "Maria Perez",
  "rut": "12.345.678-9",
  "email": "maria.perez@empresa.cl",
  "telefono": "+56 9 8765 4321",
  "direccion": "Av. Siempre Viva 123",
  "comuna": "Santiago",
  "region": "Metropolitana"
}
```

### Consultar Productos

```http
GET http://localhost:8080/api/productos
```

### Consultar Pedido

```http
GET http://localhost:8080/api/pedidos/1001
```

## Datos De Prueba Sugeridos

| Entidad | Datos sugeridos |
|---|---|
| Cliente | Maria Perez, RUT `12.345.678-9`, region Metropolitana |
| Producto | Tarjetas de presentacion, categoria imprenta, stock 100 |
| Pedido | Cliente 1, producto 1, cantidad 2 |
| Estado | COLA, PRODUCCION, LISTO, DESPACHADO, ENTREGADO |
| Despacho | Tipo RM, transportista Transporte Central, tracking TRK-1001 |
| Transportista | Blue Express, codigo TR-001, regiones RM,V |
| Log | Servicio `pedido-service`, operacion `CREAR_PEDIDO`, resultado `OK` |
