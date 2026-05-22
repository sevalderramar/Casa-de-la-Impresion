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

## 14. Comunicacion Feign si aplica
No usa clientes Feign de negocio en la implementacion actual.

## 15. Manejo global de errores Lesson 18
Incluye manejo centralizado de 400/404/409/500 con payload JSON simple.

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
log-service/
  src/main/java/cl/duocuc/logservice/
    config/
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
- Seguridad JWT activa.
- Profile por defecto: `h2`.

