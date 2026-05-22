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

## 14. Comunicacion Feign si aplica
No consume otros microservicios por Feign en su capa de negocio actual.

## 15. Manejo global de errores Lesson 18
Manejo centralizado de errores 400/404/409/500 con JSON claro.

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
estado-service/
  src/main/java/cl/duocuc/estadoservice/
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
- JWT activo.
- Profile por defecto: `h2`.
