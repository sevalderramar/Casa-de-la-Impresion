# pedido-service

## 1. Nombre del microservicio
`pedido-service`

## 2. Descripcion breve
Microservicio para la gestion integral de pedidos.

## 3. Responsabilidad dentro del sistema
- Crear pedidos y calcular monto.
- Consultar pedidos por numero y por cliente.
- Actualizar estado y exponer historial.
- Coordinar validaciones con `cliente-service`, `producto-service` y `estado-service` via Feign.

## 4. Puerto
`8081`

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
- URL (h2): `jdbc:h2:mem:pedido_db`

## 8. H2 Console
- URL: `http://localhost:8081/h2-console`

## 9. Variables de entorno requeridas
| Variable | Requerida | Descripcion |
|---|---|---|
| `JWT_SECRET` | Si | Secreto JWT compartido |
| `JWT_EXPIRATION_MS` | No | Expiracion JWT |
| `CLIENTE_SERVICE_URL` | No | URL de `cliente-service` (prod/h2 segun config) |
| `PRODUCTO_SERVICE_URL` | No | URL de `producto-service` |
| `ESTADO_SERVICE_URL` | No | URL de `estado-service` |
| `DB_URL` | Solo prod | URL de BD |
| `DB_USERNAME` | Solo prod | Usuario BD |
| `DB_PASSWORD` | Solo prod | Password BD |
| `PORT` | No | Puerto prod (default `8081`) |

## 10. Seguridad JWT
Todos los endpoints de negocio requieren token Bearer.

```http
Authorization: Bearer <TOKEN_JWT>
```

## 11. Endpoints principales
| Metodo | Endpoint | Descripcion |
|---|---|---|
| POST | `/api/pedidos` | Crear pedido |
| GET | `/api/pedidos` | Listar pedidos |
| GET | `/api/pedidos/{numeroPedido}` | Obtener pedido por numero (Long) |
| GET | `/api/pedidos/numero/{numeroPedido}` | Obtener pedido por numero (String) |
| GET | `/api/pedidos/cliente/{clienteId}` | Listar pedidos por cliente |
| PATCH | `/api/pedidos/{numeroPedido}/estado` | Cambiar estado de pedido |
| GET | `/api/pedidos/{numeroPedido}/historial` | Historial de estados |
| DELETE | `/api/pedidos/{numeroPedido}` | Eliminar pedido |

## 12. Ejemplos de request JSON
```json
{
  "clienteId": 1,
  "estado": "PENDIENTE",
  "tipoDespacho": "RM",
  "items": [
    {
      "productoId": 10,
      "cantidad": 2
    }
  ]
}
```

## 13. Ejemplos de response JSON
```json
{
  "numeroPedido": 1001,
  "clienteId": 1,
  "estado": "PENDIENTE",
  "tipoDespacho": "RM",
  "monto": 7000.0,
  "fechaCreacion": "2026-05-22T10:00:00",
  "items": [
    {
      "productoId": 10,
      "cantidad": 2,
      "precioUnitario": 3500.0,
      "subtotal": 7000.0
    }
  ]
}
```

## 14. Comunicacion Feign si aplica
Clientes Feign usados:
- `ClienteFeignClient` -> `services.cliente.url`
- `ProductoFeignClient` -> `services.producto.url`
- `EstadoFeignClient` -> `services.estado.url`

## 15. Manejo global de errores Lesson 18
Manejo global de excepciones con respuestas 400/404/409/500 y payload JSON claro.

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
pedido-service/
  src/main/java/cl/duocuc/pedidoservice/
    client/
    controller/
    dto/
    entity/
    exception/
    repository/
    service/
  src/main/resources/
    application.properties
    application-h2.properties
    application-prod.properties
```

## 19. Estado actual del servicio
- Compila correctamente.
- JWT activo y comunicacion Feign operativa.
- Profile por defecto: `h2`.
