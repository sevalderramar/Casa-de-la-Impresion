# fabricacion-service

## 1. Nombre del microservicio
`fabricacion-service`

## 2. Descripcion breve
Microservicio para crear y actualizar ordenes de fabricacion vinculadas a pedidos.

## 3. Responsabilidad dentro del sistema
- Crear orden de fabricacion por pedido.
- Consultar orden por ID.
- Actualizar estado de fabricacion.
- Notificar estados a `pedido-service` via Feign.

## 4. Puerto
`8085`

## 5. Tecnologias usadas
- Java 21
- Spring Boot 4.0.5
- Maven
- Spring Web MVC
- Spring Data JPA
- Spring Security + JWT
- H2
- OpenFeign

## 6. Profiles disponibles
- `h2`
- `prod`

## 7. Base de datos H2
- URL (h2): `jdbc:h2:mem:fabricacion_db`

## 8. H2 Console
- URL: `http://localhost:8085/h2-console`

## 9. Variables de entorno requeridas
| Variable | Requerida | Descripcion |
|---|---|---|
| `JWT_SECRET` | Si | Secreto JWT compartido |
| `JWT_EXPIRATION_MS` | No | Expiracion JWT |
| `PEDIDO_SERVICE_URL` | No | URL de `pedido-service` |
| `DB_URL` | Solo prod | URL de BD |
| `DB_USERNAME` | Solo prod | Usuario BD |
| `DB_PASSWORD` | Solo prod | Password BD |
| `PORT` | No | Puerto prod (default `8085`) |

## 10. Seguridad JWT
Endpoints protegidos con Bearer token.

```http
Authorization: Bearer <TOKEN_JWT>
```

## 11. Endpoints principales
| Metodo | Endpoint | Descripcion |
|---|---|---|
| GET | `/api/fabricacion/ping` | Healthcheck |
| POST | `/api/fabricacion` | Crear orden de fabricacion |
| GET | `/api/fabricacion/{id}` | Obtener orden por ID |
| PATCH | `/api/fabricacion/{id}/estado` | Actualizar estado de orden |

## 12. Ejemplos de request JSON
```json
{
  "numeroPedido": 1001,
  "usuarioResponsable": "operador-01",
  "descripcionEstado": "Inicio de fabricacion"
}
```

## 13. Ejemplos de response JSON
```json
{
  "mensaje": "Orden creada",
  "data": {
    "id": 1,
    "numeroPedido": 1001,
    "estadoFabricacion": "EN_PROCESO",
    "fechaInicio": "2026-05-22T10:00:00",
    "fechaFin": null,
    "fechaCreacion": "2026-05-22T10:00:00",
    "fechaActualizacion": null,
    "descripcionEstado": "Inicio de fabricacion",
    "usuarioResponsable": "operador-01"
  },
  "exitoso": true,
  "timestamp": "2026-05-22T10:00:00"
}
```

## 14. Descripcion detallada del servicio

`fabricacion-service` gestiona las órdenes de fabricación producidas por pedidos:

- **Creación de Órdenes**: Crea una orden de fabricación ligada a un pedido via Feign.
- **Estados**: Transita entre EN_PROCESO → EN_PAUSA → COMPLETADO.
- **Historial**: Mantiene registro de todos los cambios de estado.
- **Notificación**: Informa a `pedido-service` el progreso de fabricación.
- **Validaciónde Pedidos**: Verifica que el pedido exista antes de crear orden.

Este servicio es usado por el área de producción para trackear órdenes de fabricación.

## 15. Como compilar desde terminal
```powershell
cd .\fabricacion-service
.\mvnw clean compile
```

## 16. Como ejecutar desde terminal
```powershell
cd .\fabricacion-service
$env:JWT_SECRET="tu-secreto-base64-aqui"
.\mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=h2"
```

## 17. Como ejecutar desde IntelliJ IDEA

1. **Abrir el proyecto**: File → Open → carpeta `fabricacion-service`
2. **Dependencia**: Asegurar que `pedido-service` (8081) está ejecutándose
3. **Configurar variables de entorno**:
   - Edit Configurations (esquina superior)
   - Create new → Spring Boot
   - Name: `fabricacion-service`
   - Main class: `cl.duocuc.fabricacion.FabricacionServiceApplication`
   - Enviroment variables: `JWT_SECRET=tu-secreto-base64`
   - Active profiles: `h2`
4. **Ejecutar**: Run (▶) o Shift+F10
5. **Verificar**: `http://localhost:8085/h2-console`
   - Usuario: `sa`
   - Contraseña: (vacío)

## 18. Testear endpoints con Postman

### 0. Obtener Token JWT primero (desde auth-service)
```http
POST http://localhost:8090/api/auth/login
Content-Type: application/json

{
  "email": "admin@casaimpresion.cl",
  "password": "123456"
}
```

### 1. Healthcheck (Público)
```http
GET http://localhost:8085/api/fabricacion/ping
```
**Respuesta esperada (200)**:
```json
{
  "mensaje": "pong",
  "data": "fabricacion-service",
  "exitoso": true,
  "timestamp": "2026-05-22T10:00:00"
}
```

### 2. Crear Orden de Fabricación
```http
POST http://localhost:8085/api/fabricacion
Authorization: Bearer <TOKEN_JWT>
Content-Type: application/json

{
  "numeroPedido": 1001,
  "usuarioResponsable": "operador-01",
  "descripcionEstado": "Inicio de fabricación. Recursos listos."
}
```
**Respuesta esperada (201)**:
```json
{
  "mensaje": "Orden creada",
  "data": {
    "id": 1,
    "numeroPedido": 1001,
    "estadoFabricacion": "EN_PROCESO",
    "fechaInicio": "2026-05-22T10:00:00",
    "fechaFin": null,
    "fechaCreacion": "2026-05-22T10:00:00",
    "fechaActualizacion": null,
    "descripcionEstado": "Inicio de fabricación. Recursos listos.",
    "usuarioResponsable": "operador-01"
  },
  "exitoso": true,
  "timestamp": "2026-05-22T10:00:00"
}
```

### 3. Obtener Orden por ID
```http
GET http://localhost:8085/api/fabricacion/1
Authorization: Bearer <TOKEN_JWT>
```

### 4. Actualizar Estado de Orden
```http
PATCH http://localhost:8085/api/fabricacion/1/estado
Authorization: Bearer <TOKEN_JWT>
Content-Type: application/json

{
  "estadoFabricacion": "COMPLETADO",
  "descripcionEstado": "Fabricación completada. Control de calidad aprobado."
}
```
**Respuesta esperada (200)**:
```json
{
  "mensaje": "Estado actualizado",
  "data": {
    "id": 1,
    "numeroPedido": 1001,
    "estadoFabricacion": "COMPLETADO",
    "fechaInicio": "2026-05-22T10:00:00",
    "fechaFin": "2026-05-22T15:30:00",
    "fechaCreacion": "2026-05-22T10:00:00",
    "fechaActualizacion": "2026-05-22T15:30:00",
    "descripcionEstado": "Fabricación completada. Control de calidad aprobado.",
    "usuarioResponsable": "operador-01"
  },
  "exitoso": true,
  "timestamp": "2026-05-22T15:30:00"
}
```

## 19. Estados válidos de fabricación
| Estado | Descripción |
|--------|-------------|
| `EN_PROCESO` | Orden recibida, fabricación iniciada |
| `EN_PAUSA` | Fabricación pausada (falta material, máquina, etc) |
| `COMPLETADO` | Fabricación finalizada correctamente |

## 20. Dependencias inter-servicios
| Servicio | URL | Puerto | Usado para |
|----------|-----|--------|-----------|
| `pedido-service` | http://localhost:8081 | 8081 | Validar existencia de pedido y notificar progreso |

## 21. Estructura de carpetas
```
fabricacion-service/
├── pom.xml
├── README.md
├── src/
│   └── main/
│       ├── java/cl/duocuc/fabricacion/
│       │   ├── client/
│       │   │   ├── PedidoFeignClient.java
│       │   │   └── PedidoServiceClient.java
│       │   ├── config/          (GlobalExceptionHandler)
│       │   ├── controller/      (OrdenFabricacionController)
│       │   ├── dto/             (OrdenFabricacionRequest, OrdenFabricacionResponse)
│       │   ├── entity/          (OrdenFabricacion, HistorialFabricacion, EstadoFabricacion)
│       │   ├── exception/       (ConflictException, ResourceNotFoundException)
│       │   ├── handler/         
│       │   ├── repository/      (OrdenFabricacionRepository, HistorialFabricacionRepository)
│       │   ├── service/         (OrdenFabricacionService)
│       │   └── FabricacionServiceApplication.java
│       └── resources/
│           ├── application.properties
│           ├── application-h2.properties
│           └── application-prod.properties
└── data/
    └── fabricacion_db.mv.db
```

## 22. Validaciones aplicadas
- **Número de Pedido**: Debe existir en `pedido-service`.
- **No Duplicados**: No se puede crear dos órdenes para el mismo pedido.
- **Estado Válido**: Solo EN_PROCESO, EN_PAUSA, COMPLETADO.
- **Usuario**: Responsable debe ser no vacío.

## 23. Notas importantes
- ✅ Todos los endpoints de negocio requieren JWT válido.
- ✅ Cada creación/actualización es transaccional.
- ✅ El historial se registra automáticamente en cada cambio de estado.
- ✅ Integración Feign con `pedido-service` es CRÍTICA.
- ✅ Timeout configurado para Feign: 2s connection, 5s read.

## 24. Estado actual del servicio
- ✅ Compila correctamente con Java 21 y Spring Boot 4.0.5.
- ✅ Seguridad JWT activa.
- ✅ Integración Feign con `pedido-service` operativa.
- ✅ H2 Console accesible en `http://localhost:8085/h2-console`.
- ✅ Micrometer e historial de fabricación incluidos.
- ✅ Profile por defecto: `h2`.
