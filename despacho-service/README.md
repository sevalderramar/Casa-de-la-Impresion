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

## 14. Comunicacion Feign si aplica
Cliente Feign usado:
- `PedidoFeignClient` -> `services.pedido.url`

## 15. Manejo global de errores Lesson 18
Manejo centralizado de errores 400/404/409/500 con payload JSON claro.

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
despacho-service/
  src/main/java/cl/duocuc/despachoservice/
    client/
    controller/
    dto/
    entity/
    exception/
    service/
  src/main/resources/
    application.properties
    application-h2.properties
    application-prod.properties
```

## 19. Estado actual del servicio
- Compila correctamente.
- JWT activo y validacion de pedido por Feign operativa.
- Profile por defecto: `h2`.
