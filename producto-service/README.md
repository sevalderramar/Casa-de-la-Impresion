# producto-service

## 1. Nombre del microservicio
`producto-service`

## 2. Descripcion breve
Microservicio para administrar el catalogo de productos.

## 3. Responsabilidad dentro del sistema
- Crear, consultar, actualizar y eliminar productos.
- Buscar por nombre y filtrar por categoria.

## 4. Puerto
`8083`

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
- URL (h2): `jdbc:h2:mem:producto_db`

## 8. H2 Console
- URL: `http://localhost:8083/h2-console`

## 9. Variables de entorno requeridas
| Variable | Requerida | Descripcion |
|---|---|---|
| `JWT_SECRET` | Si | Secreto JWT compartido |
| `JWT_EXPIRATION_MS` | No | Expiracion del token |
| `DB_URL` | Solo prod | URL de BD |
| `DB_USERNAME` | Solo prod | Usuario BD |
| `DB_PASSWORD` | Solo prod | Password BD |
| `PORT` | No | Puerto prod (default `8083`) |

## 10. Seguridad JWT
Endpoints de negocio protegidos con Bearer token.

```http
Authorization: Bearer <TOKEN_JWT>
```

## 11. Endpoints principales
| Metodo | Endpoint | Descripcion |
|---|---|---|
| POST | `/api/productos` | Crear producto |
| GET | `/api/productos` | Listar productos |
| GET | `/api/productos/{id}` | Obtener producto por ID |
| GET | `/api/productos/nombre/{nombre}` | Buscar por nombre |
| GET | `/api/productos/categoria/{categoria}` | Listar por categoria |
| PUT | `/api/productos/{id}` | Actualizar producto |
| DELETE | `/api/productos/{id}` | Eliminar producto |

## 12. Ejemplos de request JSON
```json
{
  "nombre": "Resma Carta",
  "descripcion": "Resma de papel tamano carta",
  "categoria": "Papel",
  "precio": 3500.0,
  "stock": 120
}
```

## 13. Ejemplos de response JSON
```json
{
  "id": 10,
  "nombre": "Resma Carta",
  "descripcion": "Resma de papel tamano carta",
  "categoria": "Papel",
  "precio": 3500.0,
  "stock": 120,
  "fechaCreacion": "2026-05-22T10:00:00"
}
```

## 14. Comunicacion Feign si aplica
No consume otros microservicios via Feign en la implementacion actual.

## 15. Manejo global de errores Lesson 18
Manejo global de 400/404/409/500 con payload JSON estandar.

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
producto-service/
  src/main/java/cl/duocuc/productoservice/
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
- Seguridad JWT activa.
- Profile por defecto: `h2`.
