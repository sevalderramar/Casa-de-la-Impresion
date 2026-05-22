# cliente-service

## 1. Nombre del microservicio
`cliente-service`

## 2. Descripcion breve
Microservicio para administracion de clientes.

## 3. Responsabilidad dentro del sistema
- Registrar clientes.
- Consultar clientes por ID y RUT.
- Actualizar y eliminar clientes.

## 4. Puerto
`8082`

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
- URL (h2): `jdbc:h2:mem:cliente_db`

## 8. H2 Console
- URL: `http://localhost:8082/h2-console`

## 9. Variables de entorno requeridas
| Variable | Requerida | Descripcion |
|---|---|---|
| `JWT_SECRET` | Si | Secreto JWT compartido |
| `JWT_EXPIRATION_MS` | No | Expiracion del token |
| `DB_URL` | Solo prod | URL de BD |
| `DB_USERNAME` | Solo prod | Usuario BD |
| `DB_PASSWORD` | Solo prod | Password BD |
| `PORT` | No | Puerto prod (default `8082`) |

## 10. Seguridad JWT
Endpoints protegidos con Bearer token.

```http
Authorization: Bearer <TOKEN_JWT>
```

## 11. Endpoints principales
| Metodo | Endpoint | Descripcion |
|---|---|---|
| POST | `/api/clientes` | Crear cliente |
| GET | `/api/clientes` | Listar clientes |
| GET | `/api/clientes/{id}` | Obtener cliente por ID |
| GET | `/api/clientes/rut/{rut}` | Obtener cliente por RUT |
| PUT | `/api/clientes/{id}` | Actualizar cliente |
| DELETE | `/api/clientes/{id}` | Eliminar cliente |

## 12. Ejemplos de request JSON
```json
{
  "nombre": "Maria Perez",
  "rut": "12.345.678-9",
  "email": "maria@correo.cl",
  "telefono": "+56 9 8765 4321",
  "direccion": "Av. Siempre Viva 123",
  "comuna": "Santiago",
  "region": "Metropolitana"
}
```

## 13. Ejemplos de response JSON
```json
{
  "id": 1,
  "nombre": "Maria Perez",
  "rut": "12.345.678-9",
  "email": "maria@correo.cl",
  "telefono": "+56 9 8765 4321",
  "direccion": "Av. Siempre Viva 123",
  "comuna": "Santiago",
  "region": "Metropolitana",
  "fechaRegistro": "2026-05-22"
}
```

## 14. Comunicacion Feign si aplica
No consume otros servicios via Feign en su capa de negocio actual.

## 15. Manejo global de errores Lesson 18
Manejo centralizado para 400/404/409/500 con JSON simple (`mensaje`, `status`).

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
cliente-service/
  src/main/java/cl/duocuc/clienteservice/
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
- JWT activo.
- Profile por defecto: `h2`.
