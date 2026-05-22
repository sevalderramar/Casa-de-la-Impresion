# transportista-service

## 1. Nombre del microservicio
`transportista-service`

## 2. Descripcion breve
Microservicio para administrar transportistas activos del sistema.

## 3. Responsabilidad dentro del sistema
- Registrar transportistas.
- Listar transportistas activos.
- Consultar y actualizar transportistas por ID.

## 4. Puerto
`8088`

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
- URL (h2): `jdbc:h2:mem:transportista_db`

## 8. H2 Console
- URL: `http://localhost:8088/h2-console`

## 9. Variables de entorno requeridas
| Variable | Requerida | Descripcion |
|---|---|---|
| `JWT_SECRET` | Si | Secreto JWT compartido |
| `JWT_EXPIRATION_MS` | No | Expiracion JWT |
| `DB_URL` | Solo prod | URL de BD |
| `DB_USERNAME` | Solo prod | Usuario de BD |
| `DB_PASSWORD` | Solo prod | Password de BD |
| `PORT` | No | Puerto prod (default `8088`) |

## 10. Seguridad JWT
Endpoints de negocio protegidos con Bearer token.

```http
Authorization: Bearer <TOKEN_JWT>
```

## 11. Endpoints principales
| Metodo | Endpoint | Descripcion |
|---|---|---|
| POST | `/api/transportistas` | Registrar transportista |
| GET | `/api/transportistas` | Listar activos |
| GET | `/api/transportistas/{id}` | Obtener por ID |
| PUT | `/api/transportistas/{id}` | Actualizar transportista |
| GET | `/api/transportistas/ping` | Healthcheck |

## 12. Ejemplos de request JSON
```json
{
  "nombre": "Transporte Central",
  "codigoInterno": "TR-001",
  "contacto": "+56 9 1234 5678",
  "regionesCobertura": "RM,Valparaiso"
}
```

## 13. Ejemplos de response JSON
```json
{
  "mensaje": "Transportista registrado correctamente",
  "data": {
    "id": 1,
    "nombre": "Transporte Central",
    "codigoInterno": "TR-001",
    "contacto": "+56 9 1234 5678",
    "regionesCobertura": "RM,Valparaiso",
    "activo": true
  },
  "exitoso": true,
  "timestamp": "2026-05-22T10:00:00"
}
```

## 14. Comunicacion Feign si aplica
No expone clientes Feign en su capa de negocio actual.

## 15. Manejo global de errores Lesson 18
Manejo centralizado para 400/404/409/500 con respuesta JSON clara.

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
transportista-service/
  src/main/java/cl/duocuc/transportistaservice/
    config/
    controller/
    dto/
    exception/
    response/
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

