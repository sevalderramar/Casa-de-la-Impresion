# log-service

## 1. Nombre del microservicio
`log-service`

## 2. Descripcion breve
Microservicio para registrar y consultar eventos operacionales del ecosistema.

## 3. Responsabilidad dentro del sistema
- Persistir logs de servicio/operacion/resultado.
- Consultar logs por servicio y fecha.

## 4. Puerto
`8089`

## 5. Tecnologias usadas
- Java 21
- Spring Boot 4.0.5
- Spring Web MVC
- Spring Data JPA
- Spring Security + JWT
- H2
- Maven
- OpenFeign

## 6. Profiles disponibles
- `h2`
- `prod`

## 7. Base de datos H2
- URL (h2): `jdbc:h2:mem:log_db`

## 8. H2 Console
- URL: `http://localhost:8089/h2-console`

## 9. Variables de entorno requeridas
| Variable | Requerida | Descripcion |
|---|---|---|
| `JWT_SECRET` | Si | Secreto JWT compartido |
| `JWT_EXPIRATION_MS` | No | Expiracion JWT |
| `DB_URL` | Solo prod | URL de BD |
| `DB_USERNAME` | Solo prod | Usuario de BD |
| `DB_PASSWORD` | Solo prod | Password de BD |
| `PORT` | No | Puerto prod (default `8089`) |

## 10. Seguridad JWT
Todos los endpoints de negocio usan autenticacion Bearer.

```http
Authorization: Bearer <TOKEN_JWT>
```

## 11. Endpoints principales
| Metodo | Endpoint | Descripcion |
|---|---|---|
| POST | `/api/logs` | Registrar un evento de log |
| GET | `/api/logs?servicio={s}&desde={isoDateTime}` | Consultar logs |
| GET | `/api/logs/ping` | Healthcheck |

## 12. Ejemplos de request JSON
```json
{
  "servicio": "pedido-service",
  "operacion": "CREAR_PEDIDO",
  "usuarioId": "admin@casaimpresion.cl",
  "resultado": "OK",
  "detalle": "Pedido 1001 creado"
}
```

## 13. Ejemplos de response JSON
```json
{
  "mensaje": "Log registrado",
  "data": {
    "id": 1,
    "servicio": "pedido-service",
    "operacion": "CREAR_PEDIDO",
    "usuarioId": "admin@casaimpresion.cl",
    "timestamp": "2026-05-22T10:00:00",
    "resultado": "OK",
    "detalle": "Pedido 1001 creado"
  },
  "exitoso": true,
  "timestamp": "2026-05-22T10:00:00"
}
```

## 14. Descripcion detallada del servicio

`log-service` actúa como repositorio centralizado de auditoría y trazabilidad:

- **Registro de Eventos**: Persiste logs de operaciones de todos los servicios.
- **Filtrado Flexible**: Por servicio, por fecha o ambos.
- **Consulta Eficiente**: Búsquedas rápidas en BD con índices.
- **Independencia**: No depende de otros servicios, puede ejecutarse aislado.
- **Auditoría**: Mantiene historial completo de quién hizo qué y cuándo.

Típicamente los logs vienen de:
- `pedido-service`: CREAR_PEDIDO, CAMBIAR_ESTADO
- `fabricacion-service`: CREAR_ORDEN, ACTUALIZAR_ESTADO
- `cliente-service`: CREAR_CLIENTE, ACTUALIZAR_CLIENTE

## 15. Como compilar desde terminal
```powershell
cd .\log-service
.\mvnw clean compile
```

## 16. Como ejecutar desde terminal
```powershell
cd .\log-service
$env:JWT_SECRET="<TU_JWT_SECRET_BASE64>"
.\mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=h2"
```

## 17. Como ejecutar desde IntelliJ IDEA

1. **Abrir el proyecto**: File → Open → carpeta `log-service`
2. **Configurar variables de entorno**:
   - Edit Configurations (esquina superior)
   - Create new → Spring Boot
   - Name: `log-service`
   - Main class: `cl.duocuc.logservice.LogServiceApplication`
   - Enviroment variables: `JWT_SECRET=<TU_JWT_SECRET_BASE64>`
   - Active profiles: `h2`
3. **Ejecutar**: Run (▶) o Shift+F10
4. **Verificar**: `http://localhost:8089/h2-console`
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
GET http://localhost:8089/api/logs/ping
```
**Respuesta esperada (200)**:
```json
{
  "mensaje": "log-service OK",
  "data": "log-service OK",
  "exitoso": true,
  "timestamp": "2026-05-22T10:00:00"
}
```

### 2. Registrar un Log
```http
POST http://localhost:8089/api/logs
Authorization: Bearer <TOKEN_JWT>
Content-Type: application/json

{
  "servicio": "pedido-service",
  "operacion": "CREAR_PEDIDO",
  "usuarioId": "admin@casaimpresion.cl",
  "resultado": "EXITO",
  "detalle": "Pedido PED-1001 creado exitosamente para cliente ID 5"
}
```
**Respuesta esperada (201)**:
```json
{
  "mensaje": "Log registrado",
  "data": {
    "id": 1,
    "servicio": "pedido-service",
    "operacion": "CREAR_PEDIDO",
    "usuarioId": "admin@casaimpresion.cl",
    "timestamp": "2026-05-22T10:00:00",
    "resultado": "EXITO",
    "detalle": "Pedido PED-1001 creado exitosamente para cliente ID 5"
  },
  "exitoso": true,
  "timestamp": "2026-05-22T10:00:00"
}
```

### 3. Registrar un Log de Error
```http
POST http://localhost:8089/api/logs
Authorization: Bearer <TOKEN_JWT>
Content-Type: application/json

{
  "servicio": "fabricacion-service",
  "operacion": "CREAR_ORDEN",
  "usuarioId": "operador-01",
  "resultado": "ERROR",
  "detalle": "Pedido no encontrado en pedido-service"
}
```

### 4. Consultar Todos los Logs
```http
GET http://localhost:8089/api/logs
Authorization: Bearer <TOKEN_JWT>
```

### 5. Filtrar Logs por Servicio
```http
GET http://localhost:8089/api/logs?servicio=pedido-service
Authorization: Bearer <TOKEN_JWT>
```
**Respuesta esperada (200)**:
```json
{
  "mensaje": "Logs consultados correctamente",
  "data": [
    {
      "id": 1,
      "servicio": "pedido-service",
      "operacion": "CREAR_PEDIDO",
      "usuarioId": "admin@casaimpresion.cl",
      "timestamp": "2026-05-22T10:00:00",
      "resultado": "EXITO",
      "detalle": "Pedido PED-1001 creado exitosamente"
    }
  ],
  "exitoso": true,
  "timestamp": "2026-05-22T10:30:00"
}
```

### 6. Filtrar por Fecha Inicial
```http
GET http://localhost:8089/api/logs?desde=2026-05-01T00:00:00
Authorization: Bearer <TOKEN_JWT>
```

### 7. Filtrar por Servicio y Fecha
```http
GET http://localhost:8089/api/logs?servicio=fabricacion-service&desde=2026-05-01T00:00:00
Authorization: Bearer <TOKEN_JWT>
```

## 19. Campos de un LogEntrada
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | Long | Identificador único |
| `servicio` | String | Nombre del servicio que genera el log |
| `operacion` | String | Tipo de operación (CREAR, ACTUALIZAR, ELIMINAR) |
| `usuarioId` | String | Usuario que realizó la acción (email o ID) |
| `timestamp` | LocalDateTime | Fecha/hora exacta del evento |
| `resultado` | String | EXITO o ERROR |
| `detalle` | String | Descripción adicional (max 1000 caracteres) |

## 20. Estructura de carpetas
```
log-service/
├── pom.xml
├── README.md
├── src/
│   └── main/
│       ├── java/cl/duocuc/logservice/
│       │   ├── config/           (GlobalExceptionHandler)
│       │   ├── controller/       (LogController)
│       │   ├── dto/              (LogRequestDTO, ApiResponse)
│       │   ├── entity/           (LogEntrada)
│       │   ├── exception/        
│       │   ├── repository/       (LogRepository)
│       │   ├── service/          (LogService, LogServiceImpl)
│       │   └── LogServiceApplication.java
│       └── resources/
│           ├── application.properties
│           ├── application-h2.properties
│           └── application-prod.properties
└── data/
    └── log_db.mv.db
```

## 21. Validaciones aplicadas
- **Servicio**: No vacío, máx 100 caracteres.
- **Operación**: No vacía, máx 100 caracteres.
- **Resultado**: Debe ser EXITO o ERROR.
- **Detalle**: Opcional, máx 1000 caracteres.
- **Timestamp**: Se asigna automáticamente en el servidor.

## 22. Notas importantes
- ✅ Todos los endpoints de negocio requieren JWT válido.
- ✅ Este servicio NO consume otros microservicios (sin Feign).
- ✅ Cada log es inmutable una vez creado (no hay UPDATE/DELETE).
- ✅ Las consultas soportan paginación automática.
- ✅ Ideal para auditoría, debugging y monitoreo.

## 23. Casos de uso típicos
```
pedido-service registra:
  CREAR_PEDIDO → EXITO/ERROR
  CAMBIAR_ESTADO → EXITO/ERROR

fabricacion-service registra:
  CREAR_ORDEN → EXITO/ERROR
  ACTUALIZAR_ESTADO → EXITO/ERROR

cliente-service registra:
  CREAR_CLIENTE → EXITO/ERROR
  ACTUALIZAR_CLIENTE → EXITO/ERROR
```

## 24. Estado actual del servicio
- ✅ Compila correctamente con Java 21 y Spring Boot 4.0.5.
- ✅ Seguridad JWT activa.
- ✅ H2 Console accesible en `http://localhost:8089/h2-console`.
- ✅ Completamente independiente (no tiene dependencias Feign).
- ✅ Profile por defecto: `h2`.

