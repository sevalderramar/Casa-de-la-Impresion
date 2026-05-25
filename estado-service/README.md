# estado-service

## 1. Nombre del microservicio
`estado-service`

## 2. Descripcion breve
Microservicio para registrar y consultar cambios de estado de pedidos.

## 3. Responsabilidad dentro del sistema
- Registrar cambios de estado.
- Obtener historial de estados por pedido.
- Obtener ultimo estado de un pedido.

## 4. Puerto
`8086`

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
- URL (h2): `jdbc:h2:mem:estado_db`

## 8. H2 Console
- URL: `http://localhost:8086/h2-console`

## 9. Variables de entorno requeridas
| Variable | Requerida | Descripcion |
|---|---|---|
| `JWT_SECRET` | Si | Secreto JWT compartido |
| `JWT_EXPIRATION_MS` | No | Expiracion JWT |
| `DB_URL` | Solo prod | URL de BD |
| `DB_USERNAME` | Solo prod | Usuario BD |
| `DB_PASSWORD` | Solo prod | Password BD |
| `PORT` | No | Puerto prod (default `8086`) |

## 10. Seguridad JWT
Endpoints protegidos con Bearer token.

```http
Authorization: Bearer <TOKEN_JWT>
```

## 11. Endpoints principales
| Metodo | Endpoint | Descripcion |
|---|---|---|
| POST | `/api/estados` | Registrar cambio de estado |
| GET | `/api/estados/pedido/{numeroPedido}` | Listar cambios de estado |
| GET | `/api/estados/pedido/{numeroPedido}/ultimo` | Obtener ultimo estado |

## 12. Ejemplos de request JSON
```json
{
  "numeroPedido": 1001,
  "estadoAnterior": "PENDIENTE",
  "estadoNuevo": "EN_PROCESO",
  "observacion": "Pedido entra a produccion"
}
```

## 13. Ejemplos de response JSON
```json
{
  "id": 1,
  "numeroPedido": 1001,
  "estadoAnterior": "PENDIENTE",
  "estadoNuevo": "EN_PROCESO",
  "fechaCambio": "2026-05-22T10:00:00",
  "observacion": "Pedido entra a produccion"
}
```

## 14. Descripcion detallada del servicio

`estado-service` registra y expone el historial completo de cambios de estado de los pedidos:

- **Registro de Estados**: Persiste cada transición de estado (ej: PENDIENTE → EN_PROCESO → LISTO).
- **Historial**: Retorna toda la secuencia de cambios para un pedido específico.
- **Último Estado**: Endpoint rápido para obtener el estado actual de un pedido.
- **Trazabilidad**: Mantiene auditoría completa de quién y cuándo cambió el estado.

Los estados válidos son: `COLA`, `PRODUCCION`, `LISTO`, `DESPACHADO`, `ENTREGADO`.

Este servicio es crítico para el flujo del workflow de `pedido-service`.

## 15. Como compilar desde terminal
```powershell
cd .\estado-service
.\mvnw clean compile
```

## 16. Como ejecutar desde terminal
```powershell
cd .\estado-service
$env:JWT_SECRET="tu-secreto-base64-aqui"
.\mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=h2"
```

## 17. Como ejecutar desde IntelliJ IDEA

1. **Abrir el proyecto**: File → Open → carpeta `estado-service`
2. **Configurar variables de entorno**:
   - Edit Configurations (esquina superior)
   - Create new → Spring Boot
   - Name: `estado-service`
   - Main class: `cl.duocuc.estadoservice.EstadoServiceApplication`
   - Enviroment variables: `JWT_SECRET=tu-secreto-base64`
   - Active profiles: `h2`
3. **Ejecutar**: Run (▶) o Shift+F10
4. **Verificar**: `http://localhost:8086/h2-console`
   - Usuario: `sa`
   - Contraseña: (vacío)

## 18. Testear endpoints con Postman

### Obtener Token JWT primero (desde auth-service)
```http
POST http://localhost:8090/api/auth/login
Content-Type: application/json

{
  "email": "admin@casaimpresion.cl",
  "password": "123456"
}
```

### 1. Registrar Cambio de Estado
```http
POST http://localhost:8086/api/estados
Authorization: Bearer <TOKEN_JWT>
Content-Type: application/json

{
  "numeroPedido": 1001,
  "estadoAnterior": "COLA",
  "estadoNuevo": "PRODUCCION",
  "observacion": "Pedido entra a producción. Personal asignado correctamente."
}
```
**Respuesta esperada (201)**:
```json
{
  "id": 1,
  "numeroPedido": 1001,
  "estadoAnterior": "COLA",
  "estadoNuevo": "PRODUCCION",
  "fechaCambio": "2026-05-22T10:30:45",
  "observacion": "Pedido entra a producción. Personal asignado correctamente."
}
```

### 2. Registrar otro cambio de estado
```http
POST http://localhost:8086/api/estados
Authorization: Bearer <TOKEN_JWT>
Content-Type: application/json

{
  "numeroPedido": 1001,
  "estadoAnterior": "PRODUCCION",
  "estadoNuevo": "LISTO",
  "observacion": "Producción completada. Control de calidad pasado."
}
```

### 3. Listar Cambios de Estado de un Pedido
```http
GET http://localhost:8086/api/estados/pedido/1001
Authorization: Bearer <TOKEN_JWT>
```
**Respuesta esperada (200)**:
```json
[
  {
    "id": 1,
    "numeroPedido": 1001,
    "estadoAnterior": "COLA",
    "estadoNuevo": "PRODUCCION",
    "fechaCambio": "2026-05-22T10:30:45",
    "observacion": "Pedido entra a producción. Personal asignado correctamente."
  },
  {
    "id": 2,
    "numeroPedido": 1001,
    "estadoAnterior": "PRODUCCION",
    "estadoNuevo": "LISTO",
    "fechaCambio": "2026-05-22T14:15:20",
    "observacion": "Producción completada. Control de calidad pasado."
  }
]
```

### 4. Obtener Último Estado de un Pedido
```http
GET http://localhost:8086/api/estados/pedido/1001/ultimo
Authorization: Bearer <TOKEN_JWT>
```
**Respuesta esperada (200)**:
```json
{
  "id": 2,
  "numeroPedido": 1001,
  "estadoAnterior": "PRODUCCION",
  "estadoNuevo": "LISTO",
  "fechaCambio": "2026-05-22T14:15:20",
  "observacion": "Producción completada. Control de calidad pasado."
}
```

## 19. Estados válidos del pedido
| Estado | Descripción |
|--------|-------------|
| `COLA` | Pedido recibido y en espera de procesamiento |
| `PRODUCCION` | Pedido en proceso de fabricación |
| `LISTO` | Producción completada, listo para despacho |
| `DESPACHADO` | Enviado al cliente |
| `ENTREGADO` | Recibido por el cliente |

## 20. Estructura de carpetas
```
estado-service/
├── pom.xml
├── README.md
├── src/
│   └── main/
│       ├── java/cl/duocuc/estadoservice/
│       │   ├── controller/        (EstadoController)
│       │   ├── dto/               (CambioEstadoRequest, CambioEstadoResponse)
│       │   ├── entity/            (CambioEstado)
│       │   ├── exception/         (ResourceNotFoundException)
│       │   ├── handler/           (GlobalExceptionHandler)
│       │   ├── repository/        (CambioEstadoRepository)
│       │   ├── service/           (EstadoService)
│       │   └── EstadoServiceApplication.java
│       └── resources/
│           ├── application.properties
│           ├── application-h2.properties
│           └── application-prod.properties
└── data/
    └── estado_db.mv.db
```

## 21. Flujo de transición de estados recomendado
```
COLA
  ↓
PRODUCCION
  ↓
LISTO
  ↓
DESPACHADO
  ↓
ENTREGADO
```

## 22. Notas importantes
- Todos los endpoints de negocio requieren JWT válido.
- Cada cambio de estado se registra con timestamp automático.
- El campo `observacion` permite documentar detalles del cambio.
- No se validadas transiciones descendentes (se permite cualquier cambio).

## 23. Estado actual del servicio
- ✅ Compila correctamente con Java 21 y Spring Boot 4.0.5.
- ✅ Seguridad JWT activa.
- ✅ H2 Console accesible en `http://localhost:8086/h2-console`.
- ✅ Endpoints de consulta y registro operativos.
- ✅ Profile por defecto: `h2`.
