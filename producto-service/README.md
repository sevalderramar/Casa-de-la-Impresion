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

## 14. Descripcion detallada del servicio

`producto-service` administra el catálogo de productos de la plataforma:

- **Creación**: Permite registrar nuevos productos con nombre, descripción, categoría, precio y stock.
- **Listado**: Expone todos los productos disponibles en el sistema.
- **Búsqueda por ID**: Consulta rápida de producto individual.
- **Búsqueda por Nombre**: Búsqueda exacta (case-insensitive) de productos.
- **Filtrado por Categoría**: Agrupa productos por tipo (Papel, Tinta, Sobres, etc).
- **Actualización**: Modifica datos existentes de productos.
- **Eliminación**: Borra registros de producto.

Este servicio es usado por `pedido-service` al validar items al crear órdenes.

## 15. Como compilar desde terminal
```powershell
cd .\producto-service
.\mvnw clean compile
```

## 16. Como ejecutar desde terminal
```powershell
cd .\producto-service
$env:JWT_SECRET="tu-secreto-base64-aqui"
.\mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=h2"
```

## 17. Como ejecutar desde IntelliJ IDEA

1. **Abrir el proyecto**: File → Open → carpeta `producto-service`
2. **Configurar variables de entorno**:
   - Edit Configurations (esquina superior)
   - Create new → Spring Boot
   - Name: `producto-service`
   - Main class: `cl.duocuc.productoservice.ProductoServiceApplication`
   - Enviroment variables: `JWT_SECRET=tu-secreto-base64`
   - Active profiles: `h2`
3. **Ejecutar**: Run (▶) o Shift+F10
4. **Verificar**: `http://localhost:8083/h2-console`
   - Usuario: `sa`
   - Contraseña: (vacío)

## 18. Testear endpoints con Postman

### Obtener Token JWT primero (desde auth-service)
```http
POST http://localhost:8090/api/auth/login
Content-Type: application/json

{
  "email": "admin@casaimpresion.cl",
  "password": "123456"
}
```

### 1. Crear Producto
```http
POST http://localhost:8083/api/productos
Authorization: Bearer <TOKEN_JWT>
Content-Type: application/json

{
  "nombre": "Resma Carta Blanca",
  "descripcion": "Resma de papel blanco tamaño carta de alta calidad",
  "categoria": "Papel",
  "precio": 3500.0,
  "stock": 150
}
```
**Respuesta esperada (201)**:
```json
{
  "id": 1,
  "nombre": "Resma Carta Blanca",
  "descripcion": "Resma de papel blanco tamaño carta de alta calidad",
  "categoria": "Papel",
  "precio": 3500.0,
  "stock": 150,
  "fechaCreacion": "2026-05-22T10:00:00"
}
```

### 2. Crear otro producto (Tinta)
```http
POST http://localhost:8083/api/productos
Authorization: Bearer <TOKEN_JWT>
Content-Type: application/json

{
  "nombre": "Tinta Epson Negro",
  "descripcion": "Cartucho de tinta negra para impresoras Epson",
  "categoria": "Tinta",
  "precio": 25000.0,
  "stock": 50
}
```

### 3. Listar Todos los Productos
```http
GET http://localhost:8083/api/productos
Authorization: Bearer <TOKEN_JWT>
```

### 4. Obtener Producto por ID
```http
GET http://localhost:8083/api/productos/1
Authorization: Bearer <TOKEN_JWT>
```

### 5. Buscar por Nombre
```http
GET http://localhost:8083/api/productos/nombre/Resma Carta Blanca
Authorization: Bearer <TOKEN_JWT>
```

### 6. Filtrar por Categoría
```http
GET http://localhost:8083/api/productos/categoria/Papel
Authorization: Bearer <TOKEN_JWT>
```
**Respuesta esperada (200)**:
```json
[
  {
    "id": 1,
    "nombre": "Resma Carta Blanca",
    "descripcion": "Resma de papel blanco tamaño carta de alta calidad",
    "categoria": "Papel",
    "precio": 3500.0,
    "stock": 150,
    "fechaCreacion": "2026-05-22T10:00:00"
  }
]
```

### 7. Actualizar Producto
```http
PUT http://localhost:8083/api/productos/1
Authorization: Bearer <TOKEN_JWT>
Content-Type: application/json

{
  "nombre": "Resma Carta Premium",
  "descripcion": "Resma premium de papel blanco tamaño carta",
  "categoria": "Papel",
  "precio": 4500.0,
  "stock": 100
}
```

### 8. Eliminar Producto
```http
DELETE http://localhost:8083/api/productos/1
Authorization: Bearer <TOKEN_JWT>
```
**Respuesta esperada (204 No Content)**

## 19. Validaciones aplicadas a los productos
- **Nombre**: No vacío, máx 255 caracteres.
- **Descripción**: No vacía, máx 1000 caracteres.
- **Categoría**: No vacía, normalizada a mayúsculas.
- **Precio**: Debe ser mayor a 0.
- **Stock**: No puede ser negativo.

## 20. Estructura de carpetas
```
producto-service/
├── pom.xml
├── README.md
├── src/
│   └── main/
│       ├── java/cl/duocuc/productoservice/
│       │   ├── common/
│       │   │   ├── exception/
│       │   │   └── handler/
│       │   ├── controller/        (ProductoController)
│       │   ├── dto/               (ProductoRequest, ProductoResponse)
│       │   ├── model/             (Producto)
│       │   ├── repository/        (ProductoRepository)
│       │   ├── service/           (ProductoService)
│       │   └── ProductoServiceApplication.java
│       └── resources/
│           ├── application.properties
│           ├── application-h2.properties
│           └── application-prod.properties
└── data/
    └── producto_db.mv.db
```

## 21. Notas importantes
- Todos los endpoints de negocio requieren JWT válido.
- Los nombres de categoría se normalizan a mayúsculas (Papel, Tinta, Sobres, etc).
- Las búsquedas por nombre son case-insensitive.
- El stock se actualiza cuando se crean/eliminan productos.

## 22. Estado actual del servicio
- ✅ Compila correctamente con Java 21 y Spring Boot 4.0.5.
- ✅ Seguridad JWT activa.
- ✅ H2 Console disponible en `http://localhost:8083/h2-console`.
- ✅ Todos los CRUD operativos.
- ✅ Profile por defecto: `h2`.
