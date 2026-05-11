# Módulo Estado - Sistema de Gestión de Pedidos

## 1. Objetivo del módulo estado

El módulo `estado` gestiona el historial de cambios de estado de los pedidos. Su propósito es proporcionar una traza auditable de todas las transiciones de estado que experimenta un pedido desde su creación hasta su entrega o cancelación.

Este módulo es fundamental para:
- **Rastrabilidad**: mantener un registro cronológico de los cambios.
- **Auditoría**: cumplir con requisitos de auditoría y compliance.
- **Análisis**: facilitar análisis de tiempo de procesamiento y cuellos de botella.
- **Integraciones futuras**: permitir notificaciones automáticas y eventos de dominio.

## 2. Qué problema resuelve

En sistemas de gestión de pedidos, es crítico saber:
- **Cuándo** cambió de estado un pedido.
- **De qué estado** cambió.
- **A qué estado** cambió.
- **Por qué** cambió (observación).
- **Cuál fue el tiempo** entre cambios.

Sin un módulo de auditoría, esta información se pierde y es imposible auditar o diagnosticar problemas en el flujo de procesamiento. El módulo `estado` soluciona esto registrando cada transición de estado de forma persistente.

## 3. Cómo aplica la lección 13 de historial y auditoría

El módulo `estado` implementa un patrón de auditoría de dominio:

- **Inmutabilidad**: cada registro de `CambioEstado` es inmutable después de su creación (solo insert, nunca update/delete).
- **Trazabilidad**: cada cambio incluye timestamp automático (`fechaCambio`).
- **Contexto**: se captura el estado anterior, nuevo y una observación opcional.
- **Separación de responsabilidades**: la auditoría es un módulo independiente que no modifica la entidad principal.

Este enfoque permite que:
- La tabla de cambios sea utilizada como fuente de verdad histórica.
- Se replique a sistemas de analytics o data warehouse.
- Se implemente expiration policies o archivado a largo plazo.
- Otros servicios se suscriban a eventos de cambio de estado futuros.

## 4. Por qué se usa `numeroPedido` en lugar de relación directa con `Pedido`

Actualmente, el módulo `estado` utiliza `numeroPedido` (Long) en lugar de crear una relación JPA directa con la entidad `Pedido`:

**Razones:**

1. **Preparación para microservicios**: esto permite que en el futuro `estado-service` sea un microservicio independiente que consulte el estado de pedidos de `pedido-service` vía API REST.

2. **Desacoplamiento**: evita que la eliminación accidental de un pedido elimine su historial de estados. El historial es independiente.

3. **Escalabilidad**: si `estado` se convierte en un servicio separado, puede escalar independientemente.

4. **Consistencia eventual**: permite implementar patrones de consistencia eventual: primero se registra el cambio, luego se notifica al pedido.

5. **Flexibilidad**: un mismo registro de estado puede ser consultado por múltiples servicios sin tener que mantener una relación bidireccional.

## 5. Entidad `CambioEstado` y sus atributos

```sql
CREATE TABLE cambios_estado (
    id IDENTITY PRIMARY KEY,
    numero_pedido BIGINT NOT NULL,
    estado_anterior VARCHAR(50),
    estado_nuevo VARCHAR(50) NOT NULL,
    fecha_cambio TIMESTAMP NOT NULL,
    observacion VARCHAR(500)
);
```

### Atributos

| Atributo | Tipo | Nullable | Descripción |
|----------|------|----------|-------------|
| `id` | Long | ❌ | Identificador único, auto-generado. |
| `numeroPedido` | Long | ❌ | Identificador del pedido asociado (sin FK, referencia lógica). |
| `estadoAnterior` | String | ✅ | Estado previo (null si es el primer estado). |
| `estadoNuevo` | String | ❌ | Nuevo estado asignado. |
| `fechaCambio` | LocalDateTime | ❌ | Timestamp del cambio (asignado automáticamente con `LocalDateTime.now()`). |
| `observacion` | String | ✅ | Razón del cambio o comentario adicional (máx 500 caracteres). |

**Valores permitidos para `estadoAnterior` y `estadoNuevo`:**
- `COLA`
- `PRODUCCION`
- `LISTO`
- `DESPACHADO`
- `ENTREGADO`

## 6. DTOs

### `CambioEstadoRequest`

DTO utilizado para crear un nuevo cambio de estado.

```java
public class CambioEstadoRequest {
    @NotNull(message = "numeroPedido es obligatorio")
    private Long numeroPedido;

    private String estadoAnterior;  // nullable

    @NotBlank(message = "estadoNuevo es obligatorio")
    private String estadoNuevo;

    private String observacion;     // nullable
}
```

**Validaciones aplicadas:**
- `numeroPedido`: obligatorio, no nulo.
- `estadoNuevo`: obligatorio, no vacío.
- `estadoAnterior`: opcional.
- `observacion`: opcional.

### `CambioEstadoResponse`

DTO devuelto tras crear un cambio de estado.

```java
public class CambioEstadoResponse {
    private Long id;
    private Long numeroPedido;
    private String estadoAnterior;
    private String estadoNuevo;
    private LocalDateTime fechaCambio;
    private String observacion;
}
```

## 7. Repository

### `CambioEstadoRepository`

Interfaz que extiende `JpaRepository<CambioEstado, Long>`.

```java
public interface CambioEstadoRepository extends JpaRepository<CambioEstado, Long> {

    List<CambioEstado> findByNumeroPedido(Long numeroPedido);

    List<CambioEstado> findByNumeroPedidoOrderByFechaCambioAsc(Long numeroPedido);
}
```

**Métodos:**

| Método | Retorno | Descripción |
|--------|---------|-------------|
| `findByNumeroPedido(Long numeroPedido)` | `List<CambioEstado>` | Retorna todos los cambios de un pedido (sin ordenar). |
| `findByNumeroPedidoOrderByFechaCambioAsc(Long numeroPedido)` | `List<CambioEstado>` | Retorna todos los cambios de un pedido ordenados por fecha ascendente (más antiguo primero). |

## 8. Service

### `EstadoService`

Capa de lógica de negocio del módulo `estado`.

```java
@Service
public class EstadoService {
    
    public CambioEstadoResponse registrarCambioEstado(CambioEstadoRequest request);
    
    public List<CambioEstadoResponse> listarCambiosPorPedido(Long numeroPedido);
    
    public CambioEstadoResponse obtenerUltimoEstadoPorPedido(Long numeroPedido);
}
```

**Métodos:**

#### `registrarCambioEstado(CambioEstadoRequest request)`
- **Descripción**: crea un nuevo registro de cambio de estado.
- **Entrada**: DTO con los datos del cambio.
- **Salida**: `CambioEstadoResponse` con el registro creado, incluyendo ID y timestamp.
- **Lógica**:
  - Valida que `numeroPedido` y `estadoNuevo` no sean nulos/vacíos.
  - Asigna automáticamente `fechaCambio = LocalDateTime.now()`.
  - Persiste el registro en BD.
  - Retorna el DTO response.

#### `listarCambiosPorPedido(Long numeroPedido)`
- **Descripción**: lista todos los cambios de estado de un pedido.
- **Entrada**: `numeroPedido` del pedido.
- **Salida**: `List<CambioEstadoResponse>` ordenada por fecha ascendente.
- **Lógica**:
  - Consulta el repositorio con ordenamiento por `fechaCambio ASC`.
  - Si no hay cambios, lanza `ResourceNotFoundException`.
  - Mapea cada entidad a DTO.
  - Retorna la lista.

#### `obtenerUltimoEstadoPorPedido(Long numeroPedido)`
- **Descripción**: obtiene el último cambio de estado de un pedido.
- **Entrada**: `numeroPedido` del pedido.
- **Salida**: `CambioEstadoResponse` del último cambio.
- **Lógica**:
  - Consulta todos los cambios ordenados por fecha.
  - Si no hay cambios, lanza `ResourceNotFoundException`.
  - Retorna el último de la lista.

**Reglas de negocio:**
- `registrarCambioEstado` es idempotente: dos llamadas con los mismos datos crean dos registros diferentes.
- `listarCambiosPorPedido` siempre retorna los cambios ordenados cronológicamente.
- `obtenerUltimoEstadoPorPedido` representa el estado actual del pedido.
- Si un pedido no tiene cambios registrados, se lanza `ResourceNotFoundException` (requiere registro previo).

## 9. Controller

### `EstadoController`

Expone los endpoints REST del módulo `estado` en la ruta base `/api/estados`.

```java
@RestController
@RequestMapping("/api/estados")
public class EstadoController {
    
    @PostMapping
    public ResponseEntity<CambioEstadoResponse> crearCambioEstado(
        @Valid @RequestBody CambioEstadoRequest request);
    
    @GetMapping("/pedido/{numeroPedido}")
    public ResponseEntity<List<CambioEstadoResponse>> listarCambiosPorPedido(
        @PathVariable Long numeroPedido);
    
    @GetMapping("/pedido/{numeroPedido}/ultimo")
    public ResponseEntity<CambioEstadoResponse> obtenerUltimoEstado(
        @PathVariable Long numeroPedido);
}
```

**Características:**
- Valida automáticamente `@RequestBody` con `@Valid`.
- Retorna `ResponseEntity` con status HTTP apropiado.
- Manejo de errores delegado a `GlobalExceptionHandler` en `common.exception`.
- Rutas RESTful claras y predecibles.

## 10. Endpoints disponibles

### Crear cambio de estado

```http
POST /api/estados
Content-Type: application/json

{
  "numeroPedido": 1,
  "estadoAnterior": null,
  "estadoNuevo": "COLA",
  "observacion": "Pedido iniciado en el almacén"
}
```

**Respuesta (201 Created):**
```json
{
  "id": 1,
  "numeroPedido": 1,
  "estadoAnterior": "COLA",
  "estadoNuevo": "PRODUCCION",
  "fechaCambio": "2026-04-29T10:15:30",
  "observacion": "Pedido iniciado en el almacén"
}
```

**Errores:**
- `400 Bad Request`: falta `pedidoId` o `estadoNuevo`.
- `400 Bad Request`: `estadoNuevo` está vacío.

---

### Listar cambios de un pedido

```http
GET /api/estados/pedido/1
```

**Respuesta (200 OK):**
```json
[
  {
    "id": 1,
    "numeroPedido": 1,
    "estadoAnterior": null,
    "estadoNuevo": "COLA",
    "fechaCambio": "2026-04-29T09:00:00",
    "observacion": null
  },
  {
    "id": 2,
    "numeroPedido": 1,
    "estadoAnterior": "COLA",
    "estadoNuevo": "PRODUCCION",
    "fechaCambio": "2026-04-29T10:15:30",
    "observacion": "Pedido iniciado en el almacén"
  },
  {
    "id": 3,
    "numeroPedido": 1,
    "estadoAnterior": "PRODUCCION",
    "estadoNuevo": "LISTO",
    "fechaCambio": "2026-04-29T11:45:00",
    "observacion": "Pedido empaquetado"
  }
]
```

**Errores:**
- `404 Not Found`: no existen cambios para ese `pedidoId`.

---

### Obtener último estado de un pedido

```http
GET /api/estados/pedido/1/ultimo
```

**Respuesta (200 OK):**
```json
{
  "id": 3,
  "numeroPedido": 1,
  "estadoAnterior": "PRODUCCION",
  "estadoNuevo": "LISTO",
  "fechaCambio": "2026-04-29T11:45:00",
  "observacion": "Pedido empaquetado"
}
```

**Errores:**
- `404 Not Found`: no existen cambios para ese `numeroPedido`.

## 11. Ejemplos JSON para Postman

### Colección de ejemplos

#### 1. Crear primer cambio de estado (creación del pedido)

**Request:**
```http
POST http://localhost:8080/api/estados
Content-Type: application/json

{
  "numeroPedido": 1,
  "estadoAnterior": null,
  "estadoNuevo": "COLA",
  "observacion": null
}
```

**Response (201):**
```json
{
  "id": 1,
  "numeroPedido": 1,
  "estadoAnterior": null,
  "estadoNuevo": "COLA",
  "fechaCambio": "2026-04-29T09:00:00.123",
  "observacion": null
}
```

---

#### 2. Registrar cambio: de COLA a PRODUCCION

**Request:**
```http
POST http://localhost:8080/api/estados
Content-Type: application/json

{
  "numeroPedido": 1,
  "estadoAnterior": "COLA",
  "estadoNuevo": "PRODUCCION",
  "observacion": "Personal de almacén inicia procesamiento"
}
```

**Response (201):**
```json
{
  "id": 2,
  "numeroPedido": 1,
  "estadoAnterior": "COLA",
  "estadoNuevo": "PRODUCCION",
  "fechaCambio": "2026-04-29T10:30:00.456",
  "observacion": "Personal de almacén inicia procesamiento"
}
```

---

#### 3. Registrar cambio: de PRODUCCION a LISTO

**Request:**
```http
POST http://localhost:8080/api/estados
Content-Type: application/json

{
  "numeroPedido": 1,
  "estadoAnterior": "PRODUCCION",
  "estadoNuevo": "LISTO",
  "observacion": "Pedido empaquetado y etiquetado para envío"
}
```

**Response (201):**
```json
{
  "id": 3,
  "numeroPedido": 1,
  "estadoAnterior": "PRODUCCION",
  "estadoNuevo": "LISTO",
  "fechaCambio": "2026-04-29T12:00:00.789",
  "observacion": "Pedido empaquetado y etiquetado para envío"
}
```

---

#### 4. Listar todo el historial de un pedido

**Request:**
```http
GET http://localhost:8080/api/estados/pedido/1
```

**Response (200):**
```json
[
  {
    "id": 1,
    "pedidoId": 1,
    "estadoAnterior": null,
    "estadoNuevo": "COLA",
    "fechaCambio": "2026-04-29T09:00:00.123",
    "observacion": null
  },
  {
    "id": 2,
    "pedidoId": 1,
    "estadoAnterior": "COLA",
    "estadoNuevo": "PRODUCCION",
    "fechaCambio": "2026-04-29T10:30:00.456",
    "observacion": "Personal de almacén inicia procesamiento"
  },
  {
    "id": 3,
    "pedidoId": 1,
    "estadoAnterior": "PRODUCCION",
    "estadoNuevo": "LISTO",
    "fechaCambio": "2026-04-29T12:00:00.789",
    "observacion": "Pedido empaquetado y etiquetado para envío"
  }
]
```

---

#### 5. Obtener estado actual del pedido

**Request:**
```http
GET http://localhost:8080/api/estados/pedido/1/ultimo
```

**Response (200):**
```json
{
  "id": 3,
  "pedidoId": 1,
  "estadoAnterior": "COLA",
  "estadoNuevo": "LISTO",
  "fechaCambio": "2026-04-29T12:00:00.789",
  "observacion": "Pedido empaquetado y etiquetado para envío"
}
```

---

#### 6. Error: pedidoId faltante

**Request:**
```http
POST http://localhost:8080/api/estados
Content-Type: application/json

{
  "estadoAnterior": "COLA",
  "estadoNuevo": "PRODUCCION",
  "observacion": "Intentando sin pedidoId"
}
```

**Response (400):**
```json
{
  "timestamp": "2026-04-29T14:30:00.000Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "details": [
    "numeroPedido es obligatorio"
  ]
}
```

---

#### 7. Error: estadoNuevo vacío

**Request:**
```http
POST http://localhost:8080/api/estados
Content-Type: application/json

{
  "numeroPedido": 1,
  "estadoAnterior": "COLA",
  "estadoNuevo": "",
  "observacion": "Estado vacío"
}
```

**Response (400):**
```json
{
  "timestamp": "2026-04-29T14:30:00.000Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "details": [
    "estadoNuevo es obligatorio"
  ]
}
```

---

#### 8. Error: pedido sin historial

**Request:**
```http
GET http://localhost:8080/api/estados/pedido/999
```

**Response (404):**
```json
{
  "timestamp": "2026-04-29T14:35:00.000Z",
  "status": 404,
  "error": "Not Found",
  "message": "No se encontraron cambios de estado para el pedido con numeroPedido: 999"
}
```

## 12. Integración actual con `pedido-service`

### Flujo actual

El módulo de pedidos registra los cambios de estado usando `numeroPedido`. El flujo actual es:

1. **En `PedidoService.actualizarEstado(Long numeroPedido, EstadoRequest request)`:**
   - Validar que el pedido exista con `pedidoRepository.findById(numeroPedido)`.
   - Crear un `CambioEstadoRequest` con `numeroPedido`, `estadoAnterior`, `estadoNuevo` y `observacion`.
   - Registrar el cambio mediante `estadoService.registrarCambioEstado(cambio)`.
   - Actualizar `Pedido.estado` y persistir el pedido.
   - Mantener abierto el punto de extensión para eventos de dominio futuros.

2. **Ventajas de esta integración:**
- La fuente de verdad del estado actual está en `Pedido`.
- El historial auditado está en `CambioEstado`.
- Se pueden consultar transiciones de estado sin afectar la tabla de pedidos.
- Permite implementar políticas de replicación async.

3. **Sin acoplamiento JPA:**
- Siempre se usa inyección de dependencias para acceder a `EstadoService`.
- La lógica de auditoría es transparente para el cliente.

### Ejemplo endpoint PATCH actual

```http
PATCH http://localhost:8080/api/pedidos/1/estado
Content-Type: application/json

{
  "estado": "PRODUCCION"
}
```

**Respuesta (200):**
```json
{
  "numeroPedido": 1,
  "clienteId": 1,
  "estado": "PRODUCCION",
  "tipoDespacho": "RETIRO",
  "monto": 25000.00,
  "fechaCreacion": "2026-04-29T09:00:00",
  "items": []
}
```

**Internamente:**
1. Se valida el pedido.
2. Se registra automáticamente un `CambioEstado`.
3. Se actualiza `Pedido.estado`.
4. Se retorna el pedido actualizado.

## 13. Cómo revisar los datos en H2

### 1. Acceder a la consola H2

**URL:**
```
http://localhost:8080/h2-console
```

**Credenciales:**
```
JDBC URL: jdbc:h2:mem:gestion_pedidos_db
User Name: sa
Password: (vacío)
```

### 2. Consultar la tabla de cambios de estado

**Query:**
```sql
SELECT * FROM cambios_estado;
```

**Resultado esperado:**
```
ID | NUMERO_PEDIDO | ESTADO_ANTERIOR | ESTADO_NUEVO | FECHA_CAMBIO              | OBSERVACION
---+---------------+-----------------+--------------+---------------------------+----------------------------------------
1  | 1             | NULL            | COLA         | 2026-04-29 09:00:00.123  | NULL
2  | 1             | COLA            | PRODUCCION   | 2026-04-29 10:30:00.456  | Personal de almacén...
3  | 1             | PRODUCCION      | LISTO        | 2026-04-29 12:00:00.789  | Pedido empaquetado...
```

### 3. Consultas útiles

#### Ver el último estado de cada pedido

```sql
SELECT 
    cm.id,
    cm.numero_pedido,
    cm.estado_anterior,
    cm.estado_nuevo,
    cm.fecha_cambio,
    cm.observacion
FROM cambios_estado cm
WHERE (cm.numero_pedido, cm.fecha_cambio) IN (
    SELECT numero_pedido, MAX(fecha_cambio)
    FROM cambios_estado
    GROUP BY numero_pedido
)
ORDER BY cm.numero_pedido;
```

#### Ver el historial de transiciones de un pedido específico

```sql
SELECT * FROM cambios_estado
WHERE numero_pedido = 1
ORDER BY fecha_cambio ASC;
```

#### Contar cambios de estado por pedido

```sql
SELECT numero_pedido, COUNT(*) as total_cambios
FROM cambios_estado
GROUP BY numero_pedido
ORDER BY total_cambios DESC;
```

#### Ver distribución de estados

```sql
SELECT 
    estado_nuevo,
    COUNT(*) as total_transiciones
FROM cambios_estado
GROUP BY estado_nuevo
ORDER BY total_transiciones DESC;
```

### 4. Exportar datos

**En la consola H2:**
1. Ejecuta la query deseada.
2. Haz clic en "Export".
3. Elige formato (CSV, SQL, etc.).
4. Descarga el archivo.

## 14. Próximos pasos

### Corto plazo (1-2 sprints)

1. **Tests de integración:**
   - Validar que `PATCH /api/pedidos/{numeroPedido}/estado` registre automáticamente un `CambioEstado`.
   - Agregar tests unitarios e integración para el historial.

2. **Enums de estado:**
   - Crear `enum EstadoPedido { COLA, PRODUCCION, LISTO, DESPACHADO, ENTREGADO, CANCELADO }`.
   - Validar contra este enum en `EstadoService`.
   - Sincronizar con la documentación.

3. **Tests unitarios e integración:**
   - Test para `registrarCambioEstado`.
   - Test para `listarCambiosPorPedido` (éxito y 404).
   - Test para `obtenerUltimoEstadoPorPedido`.
   - Test de validación en `CambioEstadoRequest`.

### Mediano plazo (2-3 sprints)

4. **Eventos de dominio:**
   - Crear evento `PedidoEstadoCambiadoEvent`.
   - Publicar evento desde `EstadoService`.
   - Implementar listeners para notificaciones por email/SMS.

5. **Métricas de procesamiento:**
   - Calcular tiempo promedio entre cambios de estado.
   - Identificar pedidos atascados.
   - Dashboard de KPIs por estado.

6. **Replicación asíncrona:**
   - Replicar cambios de estado a servicio de notifications.
   - Sincronizar con sistema de reporting.

### Largo plazo (3+ sprints)

7. **Separación como microservicio:**
   - Extraer módulo `estado` a repositorio independiente.
   - Crear `estado-service` con base de datos propia.
   - Integración vía mensajería (RabbitMQ, Kafka) o REST async.

8. **Auditoría avanzada:**
   - Registrar usuario responsable del cambio.
   - Registrar IP y User-Agent.
   - Anexar documentos o evidencias.
   - Trazas criptográficamente verificables.

9. **Análisis y BI:**
   - Exportar datos a data warehouse.
   - Crear reportes de tiempo de ciclo por cliente.
   - Análisis de patrones de comportamiento de pedidos.

---

## Resumen arquitectónico

El módulo `estado` implementa el patrón **Event Sourcing Lite** o **Audit Trail**:

```
┌──────────────────────────────────────────────────────────────┐
│                   REST API - /api/estados                    │
│                                                              │
│  POST   → registrarCambioEstado()              (201 Created) │
│  GET    → listarCambiosPorPedido()              (200 OK)     │
│  GET    → obtenerUltimoEstadoPorPedido()        (200 OK)     │
└──────────────────────────────────────────────────────────────┘
                         ↓
┌──────────────────────────────────────────────────────────────┐
│                   EstadoService                              │
│                                                              │
│ • registrarCambioEstado()                                   │
│ • listarCambiosPorPedido()                                  │
│ • obtenerUltimoEstadoPorPedido()                            │
└──────────────────────────────────────────────────────────────┘
                         ↓
┌──────────────────────────────────────────────────────────────┐
│                CambioEstadoRepository                        │
│                                                              │
│ • findByPedidoId()                                          │
│ • findByPedidoIdOrderByFechaCambioAsc()                     │
└──────────────────────────────────────────────────────────────┘
                         ↓
┌──────────────────────────────────────────────────────────────┐
│              Tabla H2: cambios_estado                        │
│                                                              │
│ [Registro inmutable de todos los cambios de estado]         │
└──────────────────────────────────────────────────────────────┘


Flujo de integración futura:

┌──────────────────┐       ┌──────────────────┐       ┌──────────────────┐
│  REST Client     │       │  EstadoService   │       │  CambioEstado TB │
│  (PATCH pedido)  │ ────→ │  (registra)      │ ────→ │  (persiste)      │
└──────────────────┘       └──────────────────┘       └──────────────────┘
        ↑                           ↓
        └───────────────────────────┘
        ↓
┌──────────────────────────────────────────────────────────────┐
│               PedidoService (futuro)                         │
│                                                              │
│ 1. Validar pedido                                           │
│ 2. Registrar cambio de estado (via EstadoService)           │
│ 3. Actualizar Pedido.estado                                 │
│ 4. Publicar evento (futura notificación)                    │
└──────────────────────────────────────────────────────────────┘
```

---

## Referencias

- **Spring Data JPA**: https://spring.io/projects/spring-data-jpa
- **Jakarta Validation**: https://jakarta.ee/specifications/validation/
- **Event Sourcing**: https://martinfowler.com/eaaDev/EventSourcing.html
- **Audit Trail Design**: https://en.wikipedia.org/wiki/Audit_trail

