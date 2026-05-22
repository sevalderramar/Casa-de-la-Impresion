# metrica-service

## 1. Nombre del microservicio
`metrica-service`

## 2. Descripcion breve
Microservicio de analitica para consolidar metricas de clientes, productos y ventas.

## 3. Responsabilidad dentro del sistema
- Exponer ranking de clientes.
- Exponer top de productos.
- Exponer resumen de ventas por rango de fechas.
- Consultar datos de `pedido-service` y `cliente-service` via Feign.

## 4. Puerto
`8087`

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
- URL (h2): `jdbc:h2:mem:metrica_db`

## 8. H2 Console
- URL: `http://localhost:8087/h2-console`

## 9. Variables de entorno requeridas
| Variable | Requerida | Descripcion |
|---|---|---|
| `JWT_SECRET` | Si | Secreto JWT compartido |
| `JWT_EXPIRATION_MS` | No | Expiracion JWT |
| `PEDIDO_SERVICE_URL` | No | URL de `pedido-service` |
| `CLIENTE_SERVICE_URL` | No | URL de `cliente-service` |
| `DB_URL` | Solo prod | URL de BD |
| `DB_USERNAME` | Solo prod | Usuario de BD |
| `DB_PASSWORD` | Solo prod | Password de BD |
| `PORT` | No | Puerto prod (default `8087`) |

## 10. Seguridad JWT
Todos los endpoints de negocio requieren token Bearer.

```http
Authorization: Bearer <TOKEN_JWT>
```

## 11. Endpoints principales
| Metodo | Endpoint | Descripcion |
|---|---|---|
| GET | `/api/metricas/clientes/{id}` | Metricas de un cliente |
| GET | `/api/metricas/clientes/ranking?limite=10` | Ranking de clientes |
| GET | `/api/metricas/productos/top?desde=yyyy-MM-dd&hasta=yyyy-MM-dd&limite=10` | Top productos |
| GET | `/api/metricas/ventas?desde=yyyy-MM-dd&hasta=yyyy-MM-dd` | Resumen de ventas |
| GET | `/api/metricas/ping` | Healthcheck simple |

## 12. Ejemplos de request JSON
Aunque los endpoints son `GET`, este es el equivalente de parametros en formato JSON:

```json
{
  "desde": "2026-05-01",
  "hasta": "2026-05-31",
  "limite": 10
}
```

Ejemplo real en URL:

```http
GET /api/metricas/productos/top?desde=2026-05-01&hasta=2026-05-31&limite=10
```

## 13. Ejemplos de response JSON
```json
{
  "mensaje": "Resumen de ventas obtenido",
  "data": {
    "desde": "2026-05-01",
    "hasta": "2026-05-31",
    "montoTotal": 1250000.0,
    "cantidadPedidos": 42
  },
  "exitoso": true,
  "timestamp": "2026-05-22T10:00:00"
}
```

## 14. Comunicacion Feign si aplica
Clientes Feign usados:
- `pedido-service` (`services.pedido.url`)
- `cliente-service` (`services.cliente.url`)

## 15. Manejo global de errores Lesson 18
`GlobalExceptionHandler` maneja errores 400/404/409/500 con payload simple.

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
metrica-service/
  src/main/java/cl/duocuc/metricaservice/
    client/
    controller/
    dto/
    exception/
    handler/
    service/
  src/main/resources/
    application.properties
    application-h2.properties
    application-prod.properties
```

## 19. Estado actual del servicio
- Compila correctamente.
- JWT activo y Feign operativo.
- Profile por defecto: `h2`.
