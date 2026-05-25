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

## 14. Descripcion detallada del servicio

`cliente-service` gestiona el ciclo completo de vida de los clientes en el sistema Casa de la Impresión:

- **Registro**: Permite crear nuevos clientes con validaciones de RUT, email único y datos requeridos.
- **Consulta**: Búsqueda por ID, RUT, con caché en memoria para optimizar lecturasrepetidas.
- **Actualización**: Modifica datos de clientes existentes.
- **Eliminación**: Borra registros de clientes (soft o hard delete según configuración).
- **Búsqueda por RUT**: Valida y busca usando el Rut del cliente, normalizado internamente.

Este servicio es fundamental para `pedido-service` que valida clientes en cada creación de orden.

## 15. Como compilar desde terminal
```powershell
cd .\cliente-service
.\mvnw clean compile
```

## 16. Como ejecutar desde terminal
```powershell
cd .\cliente-service
$env:JWT_SECRET="tu-secreto-base64-aqui"
.\mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=h2"
```

## 17. Como ejecutar desde IntelliJ IDEA

1. **Abrir el proyecto**: File → Open → selecciona la carpeta `cliente-service`
2. **Configurar variables de entorno**:
   - Edit Configurations (esquina superior derecha)
   - Create new → Spring Boot
   - Name: `cliente-service`
   - Main class: `cl.duocuc.clienteservice.ClienteServiceApplication`
   - Enviroment variables: `JWT_SECRET=tu-secreto-base64`
   - Active profiles: `h2`
3. **Ejecutar**: Presiona Run (▶) o Shift+F10
4. **Verificar**: Navega a `http://localhost:8082/h2-console`
   - Usuario: `sa`
   - Contraseña: (dejar vacío)

## 18. Testear endpoints con Postman

### Obtener Token JWT (desde auth-service primero)
```http
POST http://localhost:8090/api/auth/login
Content-Type: application/json

{
  "email": "admin@casaimpresion.cl",
  "password": "123456"
}
```

### 1. Crear Cliente
```http
POST http://localhost:8082/api/clientes
Authorization: Bearer <TOKEN_JWT>
Content-Type: application/json

{
  "nombre": "Maria Perez Gonzalez",
  "rut": "12.345.678-9",
  "email": "maria.perez@empresa.com",
  "telefono": "+56 9 8765 4321",
  "direccion": "Av. Siempre Viva 123, Departamento 45",
  "comuna": "Santiago",
  "region": "Metropolitana"
}
```
**Respuesta esperada (201)**:
```json
{
  "id": 1,
  "nombre": "Maria Perez Gonzalez",
  "rut": "12.345.678-9",
  "email": "maria.perez@empresa.com",
  "telefono": "+56 9 8765 4321",
  "direccion": "Av. Siempre Viva 123, Departamento 45",
  "comuna": "Santiago",
  "region": "Metropolitana",
  "fechaRegistro": "2026-05-22"
}
```

### 2. Listar Clientes
```http
GET http://localhost:8082/api/clientes
Authorization: Bearer <TOKEN_JWT>
```

### 3. Obtener por ID
```http
GET http://localhost:8082/api/clientes/1
Authorization: Bearer <TOKEN_JWT>
```

### 4. Obtener por RUT
```http
GET http://localhost:8082/api/clientes/rut/12.345.678-9
Authorization: Bearer <TOKEN_JWT>
```

### 5. Actualizar Cliente
```http
PUT http://localhost:8082/api/clientes/1
Authorization: Bearer <TOKEN_JWT>
Content-Type: application/json

{
  "nombre": "Maria Perez Garcia",
  "rut": "12.345.678-9",
  "email": "maria.nueva@empresa.com",
  "telefono": "+56 9 9999 9999",
  "direccion": "Av. Nueva 999",
  "comuna": "Providencia",
  "region": "Metropolitana"
}
```

### 6. Eliminar Cliente
```http
DELETE http://localhost:8082/api/clientes/1
Authorization: Bearer <TOKEN_JWT>
```
**Respuesta esperada (204 No Content)**

## 19. Estructura de carpetas
```
cliente-service/
├── pom.xml
├── README.md
├── src/
│   └── main/
│       ├── java/cl/duocuc/clienteservice/
│       │   ├── controller/        (ClienteController)
│       │   ├── dto/               (ClienteRequest, ClienteResponse)
│       │   ├── entity/            (Cliente)
│       │   ├── exception/         (ResourceNotFoundException, ConflictException)
│       │   ├── repository/        (ClienteRepository)
│       │   ├── service/           (ClienteService)
│       │   └── ClienteServiceApplication.java
│       └── resources/
│           ├── application.properties
│           ├── application-h2.properties
│           └── application-prod.properties
└── data/
    └── cliente_db.mv.db
```

## 20. Notas de seguridad
- Todos los endpoints de negocio requieren token JWT válido.
- El email debe ser único en el sistema (validado en BD).
- El RUT se normaliza internamente (espacios y guiones se preservan).
- Solo roles autenticados pueden operar en este servicio.

## 21. Estado actual del servicio
- ✅ Compila correctamente con Java 21 y Spring Boot 4.0.5.
- ✅ Seguridad JWT activa.
- ✅ Caché en memoria para optimizaciones de lectura.
- ✅ H2 Console accesible en `http://localhost:8082/h2-console`.
- ✅ Profile por defecto: `h2`.
