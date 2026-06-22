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

## 14. Descripcion detallada del servicio

`transportista-service` mantiene el catálogo de empresas transportistas:

- **Registro**: Crear nuevas empresas transportistas con cobertura geográfica.
- **Listado Activos**: Filtrar solo transportistas con estado activo.
- **Consulta Individual**: Obtener datos de transportista por ID.
- **Actualización**: Modificar datos y cambiar estado (activo/inactivo).
- **Control de Duplicados**: Prevenir transportistas duplicados por código interno.

Este servicio es consultado por `despacho-service` al registrar envíos.

## 15. Como compilar desde terminal
```powershell
cd .\transportista-service
.\mvnw clean compile
```

## 16. Como ejecutar desde terminal
```powershell
cd .\transportista-service
$env:JWT_SECRET="<TU_JWT_SECRET_BASE64>"
.\mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=h2"
```

## 17. Como ejecutar desde IntelliJ IDEA

1. **Abrir el proyecto**: File → Open → carpeta `transportista-service`
2. **Configurar variables de entorno**:
   - Edit Configurations (esquina superior)
   - Create new → Spring Boot
   - Name: `transportista-service`
   - Main class: `cl.duocuc.transportistaservice.TransportistaServiceApplication`
   - Enviroment variables: `JWT_SECRET=<TU_JWT_SECRET_BASE64>`
   - Active profiles: `h2`
3. **Ejecutar**: Run (▶) o Shift+F10
4. **Verificar**: `http://localhost:8088/h2-console`
   - Usuario: `sa`
   - Contraseña: (vacío)

## 18. Testear endpoints con Postman

### 0. Obtener Token JWT primero (desde auth-service)
```http
POST http://localhost:8090/api/auth/login
Content-Type: application/json

{
  "email": "admin@casaimpresion.cl",
  "password": "123456"
}
```

### 1. Healthcheck (Público)
```http
GET http://localhost:8088/api/transportistas/ping
```
**Respuesta esperada (200)**:
```json
{
  "mensaje": "transportista-service activo",
  "data": null,
  "exitoso": true,
  "timestamp": "2026-05-22T10:00:00"
}
```

### 2. Registrar Transportista
```http
POST http://localhost:8088/api/transportistas
Authorization: Bearer <TOKEN_JWT>
Content-Type: application/json

{
  "nombre": "Transporte Central S.A.",
  "codigoInterno": "TR-001",
  "contacto": "+56 9 1234 5678",
  "regionesCobertura": "RM,Valparaiso,OHiggins"
}
```
**Respuesta esperada (201)**:
```json
{
  "mensaje": "Transportista registrado correctamente",
  "data": {
    "id": 1,
    "nombre": "Transporte Central S.A.",
    "codigoInterno": "TR-001",
    "contacto": "+56 9 1234 5678",
    "regionesCobertura": "RM,Valparaiso,OHiggins",
    "activo": true
  },
  "exitoso": true,
  "timestamp": "2026-05-22T10:00:00"
}
```

### 3. Registrar otro Transportista
```http
POST http://localhost:8088/api/transportistas
Authorization: Bearer <TOKEN_JWT>
Content-Type: application/json

{
  "nombre": "Starken Express",
  "codigoInterno": "STK-001",
  "contacto": "+56 2 2800 8000",
  "regionesCobertura": "I,II,III,IV,V,VI,VII,VIII,IX,X,XI,XII,RM,XIV,XV,XVI"
}
```

### 4. Listar Transportistas Activos
```http
GET http://localhost:8088/api/transportistas
Authorization: Bearer <TOKEN_JWT>
```
**Respuesta esperada (200)**:
```json
{
  "mensaje": "Listado obtenido",
  "data": [
    {
      "id": 1,
      "nombre": "Transporte Central S.A.",
      "codigoInterno": "TR-001",
      "contacto": "+56 9 1234 5678",
      "regionesCobertura": "RM,Valparaiso,OHiggins",
      "activo": true
    },
    {
      "id": 2,
      "nombre": "Starken Express",
      "codigoInterno": "STK-001",
      "contacto": "+56 2 2800 8000",
      "regionesCobertura": "I,II,III,IV,V,VI,VII,VIII,IX,X,XI,XII,RM,XIV,XV,XVI",
      "activo": true
    }
  ],
  "exitoso": true,
  "timestamp": "2026-05-22T10:00:00"
}
```

### 5. Obtener Transportista por ID
```http
GET http://localhost:8088/api/transportistas/1
Authorization: Bearer <TOKEN_JWT>
```

### 6. Actualizar Transportista (Cambiar región)
```http
PUT http://localhost:8088/api/transportistas/1
Authorization: Bearer <TOKEN_JWT>
Content-Type: application/json

{
  "nombre": "Transporte Central S.A.",
  "contacto": "+56 9 1234 5678",
  "regionesCobertura": "RM,Valparaiso,OHiggins,Maule,BioBio",
  "activo": true
}
```
**Respuesta esperada (200)**:
```json
{
  "mensaje": "Transportista actualizado",
  "data": {
    "id": 1,
    "nombre": "Transporte Central S.A.",
    "codigoInterno": "TR-001",
    "contacto": "+56 9 1234 5678",
    "regionesCobertura": "RM,Valparaiso,OHiggins,Maule,BioBio",
    "activo": true
  },
  "exitoso": true,
  "timestamp": "2026-05-22T11:00:00"
}
```

### 7. Desactivar Transportista
```http
PUT http://localhost:8088/api/transportistas/2
Authorization: Bearer <TOKEN_JWT>
Content-Type: application/json

{
  "nombre": "Starken Express",
  "contacto": "+56 2 2800 8000",
  "regionesCobertura": "I,II,III,IV,V,VI,VII,VIII,IX,X,XI,XII,RM,XIV,XV,XVI",
  "activo": false
}
```

## 19. Regiones de Chile Soportadas
| Código | Región |
|--------|--------|
| `I` | Región de Arica y Parinacota |
| `II` | Región de Tarapacá |
| `III` | Región de Atacama |
| `IV` | Región de Coquimbo |
| `V` | Región de Valparaíso |
| `RM` | Región Metropolitana |
| `VI` | Región del Libertador General Bernardo O'Higgins |
| `VII` | Región del Maule |
| `VIII` | Región de Bío-Bío |
| `IX` | Región de La Araucanía |
| `X` | Región de Los Lagos |
| `XI` | Región de Aysén |
| `XII` | Región de Magallanes |
| `XIV` | Región de Los Ríos |
| `XV` | Región de Arica y Parinacota |
| `XVI` | Región Metropolitana |

## 20. Estructura de carpetas
```
transportista-service/
├── pom.xml
├── README.md
├── src/
│   └── main/
│       ├── java/cl/duocuc/transportistaservice/
│       │   ├── config/          (Configuración)
│       │   ├── controller/      (TransportistaController)
│       │   ├── dto/             (TransportistaRequestDTO, TransportistaResponseDTO)
│       │   ├── exception/       (ResourceNotFoundException, ConflictException)
│       │   ├── model/           (Transportista)
│       │   ├── repository/      (TransportistaRepository)
│       │   ├── response/        (ApiResponse)
│       │   ├── service/         (TransportistaService, TransportistaServiceImpl)
│       │   └── TransportistaServiceApplication.java
│       └── resources/
│           ├── application.properties
│           ├── application-h2.properties
│           └── application-prod.properties
└── data/
    └── transportista_db.mv.db
```

## 21. Validaciones aplicadas
- **Nombre**: No vacío, máx 200 caracteres.
- **Código Interno**: No vacío, máx 50 caracteres, **ÚNICO**.
- **Contacto**: No vacío, máx 100 caracteres.
- **Regiones**: No vacío, formato separado por comas.
- **Duplicados**: No se permiten dos transportistas con mismo código.

## 22. Notas importantes
- ✅ Todos los endpoints de negocio requieren JWT válido.
- ✅ Este servicio NO consume otros microservicios (sin Feign).
- ✅ El endpoint `GET /ping` es PÚBLICO (sin token).
- ✅ Los transportistas pueden estar activos o inactivos.
- ✅ Solo se listan transportistas con `activo=true`.

## 23. Campos del Transportista
| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| `id` | Long | Auto | Identificador único |
| `nombre` | String | Sí | Nombre legal de la empresa |
| `codigoInterno` | String | Sí | Código único (ej: TR-001) |
| `contacto` | String | Sí | Teléfono o email de contacto |
| `regionesCobertura` | String | Sí | Regiones que atiende (RM,V,VI,...) |
| `activo` | Boolean | No | Estado de actividad (default: true) |

## 24. Estado actual del servicio
- ✅ Compila correctamente con Java 21 y Spring Boot 4.0.5.
- ✅ Seguridad JWT activa.
- ✅ H2 Console accesible en `http://localhost:8088/h2-console`.
- ✅ Gestión completa de transportistas (CRUD).
- ✅ Validación de duplicados operacional.
- ✅ Profile por defecto: `h2`.

