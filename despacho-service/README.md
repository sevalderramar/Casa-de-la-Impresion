# despacho-service

## 1. Nombre del microservicio
`despacho-service`

## 2. Descripcion breve
Microservicio para registrar y administrar despachos de pedidos.

## 3. Responsabilidad dentro del sistema
- Crear despachos asociados a pedidos existentes.
- Listar y filtrar despachos por tipo.
- Consultar despacho por numero de pedido y actualizar registros.

## 4. Puerto
`8084`

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
- URL (h2): `jdbc:h2:mem:despacho_db`

## 8. H2 Console
- URL: `http://localhost:8084/h2-console`

## 8.1 Swagger/OpenAPI
- URL: `http://localhost:8084/swagger-ui/index.html`
- Documentación estándar: `http://localhost:8084/v3/api-docs`

## 9. Variables de entorno requeridas
| Variable | Requerida | Descripcion |
|---|---|---|
| `JWT_SECRET` | Si | Secreto JWT compartido |
| `JWT_EXPIRATION_MS` | No | Expiracion JWT |
| `PEDIDO_SERVICE_URL` | No | URL de `pedido-service` |
| `DB_URL` | Solo prod | URL de BD |
| `DB_USERNAME` | Solo prod | Usuario BD |
| `DB_PASSWORD` | Solo prod | Password BD |
| `PORT` | No | Puerto prod (default `8084`) |

## 10. Seguridad JWT
Endpoints de negocio protegidos con Bearer token.

```http
Authorization: Bearer <TOKEN_JWT>
```

## 11. Endpoints principales
| Metodo | Endpoint | Descripcion |
|---|---|---|
| POST | `/api/despachos` | Crear despacho |
| GET | `/api/despachos` | Listar despachos (`?tipo=` opcional) |
| GET | `/api/despachos/{numeroPedido}` | Obtener despacho por numero de pedido |
| PUT | `/api/despachos/{id}` | Actualizar despacho |

## 12. Ejemplos de request JSON
```json
{
  "numeroPedido": 1001,
  "tipoDespacho": "RM",
  "transportista": "Transporte Central",
  "trackingCode": "TRK-1001"
}
```

## 13. Ejemplos de response JSON
```json
{
  "id": 1,
  "numeroPedido": 1001,
  "tipoDespacho": "RM",
  "transportista": "Transporte Central",
  "fechaDespacho": "2026-05-22T10:00:00",
  "trackingCode": "TRK-1001"
}
```

## 14. Descripcion detallada del servicio

`despacho-service` gestiona el envío y trazabilidad de pedidos:

- **Creación de Despachos**: Registra envío vinculado a pedido específico.
- **Tipos de Despacho**: RM (Región Metropolitana), Regiones, Internacional.
- **Filtrado**: Busca despachos por tipo (RM, Regiones, etc).
- **Consulta por Pedido**: Obtener despacho asociado a un número de pedido.
- **Actualización**: Modifica datos de despacho (transportista, tracking).

Este servicio es crítico para el tracking de órdenes en tránsito.

## 15. Como compilar desde terminal
```powershell
cd .\despacho-service
.\mvnw clean compile
```

## 16. Como ejecutar desde terminal
```powershell
cd .\despacho-service
$env:JWT_SECRET="<TU_JWT_SECRET_BASE64>"
.\mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=h2"
```

## 17. Como ejecutar desde IntelliJ IDEA

1. **Abrir el proyecto**: File → Open → carpeta `despacho-service`
2. **Dependencias**: Asegurar que `pedido-service` (8081) está ejecutándose
3. **Configurar variables de entorno**:
   - Edit Configurations (esquina superior)
   - Create new → Spring Boot
   - Name: `despacho-service`
   - Main class: `cl.duocuc.despachoservice.DespachoServiceApplication`
   - Enviroment variables: `JWT_SECRET=<TU_JWT_SECRET_BASE64>`
   - Active profiles: `h2`
4. **Ejecutar**: Run (▶) o Shift+F10
5. **Verificar**: `http://localhost:8084/h2-console`
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

### 1. Crear Despacho
```http
POST http://localhost:8084/api/despachos
Authorization: Bearer <TOKEN_JWT>
Content-Type: application/json

{
  "numeroPedido": 1001,
  "tipoDespacho": "RM",
  "transportista": "Transporte Central",
  "trackingCode": "TRK-2026-05-22-001"
}
```
**Respuesta esperada (201)**:
```json
{
  "id": 1,
  "numeroPedido": 1001,
  "tipoDespacho": "RM",
  "transportista": "Transporte Central",
  "fechaDespacho": "2026-05-22T10:00:00",
  "trackingCode": "TRK-2026-05-22-001"
}
```

### 2. Crear Despacho Regional
```http
POST http://localhost:8084/api/despachos
Authorization: Bearer <TOKEN_JWT>
Content-Type: application/json

{
  "numeroPedido": 1002,
  "tipoDespacho": "REGIONES",
  "transportista": "Starken",
  "trackingCode": "STK-2026-05-22-100"
}
```

### 3. Listar Todos los Despachos
```http
GET http://localhost:8084/api/despachos
Authorization: Bearer <TOKEN_JWT>
```

### 4. Filtrar Despachos por Tipo (RM)
```http
GET http://localhost:8084/api/despachos?tipo=RM
Authorization: Bearer <TOKEN_JWT>
```
**Respuesta esperada (200)**:
```json
[
  {
    "id": 1,
    "numeroPedido": 1001,
    "tipoDespacho": "RM",
    "transportista": "Transporte Central",
    "fechaDespacho": "2026-05-22T10:00:00",
    "trackingCode": "TRK-2026-05-22-001"
  }
]
```

### 5. Filtrar Despachos por Tipo (REGIONES)
```http
GET http://localhost:8084/api/despachos?tipo=REGIONES
Authorization: Bearer <TOKEN_JWT>
```

### 6. Obtener Despacho por Número de Pedido
```http
GET http://localhost:8084/api/despachos/1001
Authorization: Bearer <TOKEN_JWT>
```

### 7. Actualizar Despacho
```http
PUT http://localhost:8084/api/despachos/1
Authorization: Bearer <TOKEN_JWT>
Content-Type: application/json

{
  "numeroPedido": 1001,
  "tipoDespacho": "RM",
  "transportista": "Transporte Central - Ruta Nueva",
  "trackingCode": "TRK-2026-05-22-001-UPD"
}
```
**Respuesta esperada (200)**:
```json
{
  "id": 1,
  "numeroPedido": 1001,
  "tipoDespacho": "RM",
  "transportista": "Transporte Central - Ruta Nueva",
  "fechaDespacho": "2026-05-22T10:00:00",
  "trackingCode": "TRK-2026-05-22-001-UPD"
}
```

## 19. Tipos de Despacho
| Tipo | Descripción |
|------|-------------|
| `RM` | Región Metropolitana (Santiago) - Envío local rápido |
| `REGIONES` | Resto de Chile - Envío nacional |
| `INTERNACIONAL` | Envío al extranjero |

## 20. Dependencias inter-servicios
| Servicio | URL | Puerto | Usado para |
|----------|-----|--------|-----------|
| `pedido-service` | http://localhost:8081 | 8081 | Validar existencia de pedido |

## 21. Estructura de carpetas
```
despacho-service/
├── pom.xml
├── README.md
├── src/
│   └── main/
│       ├── java/cl/duocuc/despachoservice/
│       │   ├── client/           (PedidoFeignClient)
│       │   ├── controller/       (DespachoController)
│       │   ├── dto/              (DespachoRequest, DespachoResponse)
│       │   ├── entity/           (Despacho)
│       │   ├── exception/        
│       │   ├── service/          (DespachoService)
│       │   └── DespachoServiceApplication.java
│       └── resources/
│           ├── application.properties
│           ├── application-h2.properties
│           └── application-prod.properties
└── data/
    └── despacho_db.mv.db
```

## 22. Validaciones aplicadas
- **Número de Pedido**: Debe existir en `pedido-service`.
- **Tipo Despacho**: RM, REGIONES, o INTERNACIONAL.
- **Transportista**: No vacío, máx 200 caracteres.
- **Tracking Code**: Código único por despacho.

## 23. Notas importantes
- ✅ Todos los endpoints de negocio requieren JWT válido.
- ✅ Integración Feign con `pedido-service` para validación.
- ✅ Los despachos no se pueden duplicar para un mismo pedido.
- ✅ Cada creación registra automáticamente la fecha/hora.
- ✅ Timeout Feign: 2s connection, 5s read.

## 24. Campos del Despacho
| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | Long | Identificador único del despacho |
| `numeroPedido` | Long | Referencia al pedido en pedido-service |
| `tipoDespacho` | String | RM, REGIONES, INTERNACIONAL |
| `transportista` | String | Nombre de la empresa transportista |
| `fechaDespacho` | LocalDateTime | Cuándo se creó el despacho |
| `trackingCode` | String | Código de seguimiento del envío |

## 25. Estado actual del servicio
- ✅ Compila correctamente con Java 21 y Spring Boot 4.0.5.
- ✅ Seguridad JWT activa.
- ✅ Integración Feign con `pedido-service` operativa.
- ✅ H2 Console accesible en `http://localhost:8084/h2-console`.
- ✅ Filtrado y búsqueda de despachos operacional.
- ✅ Profile por defecto: `h2`.
