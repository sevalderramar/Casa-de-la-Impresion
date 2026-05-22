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

## 14. Comunicacion Feign si aplica
Cliente Feign usado:
- `PedidoFeignClient` -> `services.pedido.url`

## 15. Manejo global de errores Lesson 18
Manejo global de excepciones centralizado (400/404/409/500).

## 16. Como compilar
```powershell
.\mvnw clean compile
```

## 17. Como ejecutar
```powershell
$env:JWT_SECRET="BASE64_SECRET"
.\mvnw spring-boot:run
```

## 18. Estructura de carpetas
```text
fabricacion-service/
  src/main/java/cl/duocuc/fabricacion/
    client/
    config/
    controller/
    dto/
    entity/
    exception/
    handler/
    repository/
    service/
  src/main/resources/
    application.properties
    application-h2.properties
    application-prod.properties
```

## 19. Estado actual del servicio
- Compila correctamente.
- Seguridad JWT activa y comunicacion Feign con `pedido-service` operativa.
- Profile por defecto: `h2`.
