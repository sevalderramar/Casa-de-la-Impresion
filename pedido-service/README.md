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

## 15. Descripcion detallada del servicio

`pedido-service` es el corazón del sistema, orquestando el flujo completo de pedidos:

- **Creación de Pedidos**: Valida cliente, productos y crea orden con items.
- **Cálculo de Monto**: Suma automática de precios unitarios × cantidades.
- **Búsqueda Flexible**: Por número (Long o String) y por cliente.
- **Cambio de Estados**: Transita entre COLA → PRODUCCION → LISTO → DESPACHADO → ENTREGADO.
- **Historial Completo**: Expone toda la secuencia de cambios vía `estado-service`.
- **Coordinación Feign**: Valida clientes, productos y estados con otros servicios.

### Flujo de creación:
1. POST `/api/pedidos` con datos y items.
2. Valida cliente via Feign (`cliente-service`).
3. Valida cada producto via Feign (`producto-service`).
4. Crea orden y registra estado inicial en `estado-service`.
5. Retorna pedido con número y monto calculado.

## 16. Como compilar desde terminal
```powershell
cd .\pedido-service
.\mvnw clean compile
```

## 17. Como ejecutar desde terminal
```powershell
cd .\pedido-service
$env:JWT_SECRET="tu-secreto-base64-aqui"
.\mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=h2"
```

## 18. Como ejecutar desde IntelliJ IDEA

1. **Abrir el proyecto**: File → Open → carpeta `pedido-service`
2. **IMPORTANTE**: Primero asegúrate de que `cliente-service` (8082), `producto-service` (8083), y `estado-service` (8086) están ejecutándose
3. **Configurar variables de entorno**:
   - Edit Configurations (esquina superior)
   - Create new → Spring Boot
   - Name: `pedido-service`
   - Main class: `cl.duocuc.pedidoservice.PedidoServiceApplication`
   - Enviroment variables: `JWT_SECRET=tu-secreto-base64`
   - Active profiles: `h2`
   - VM options (si es necesario): `-Dservices.cliente.url=http://localhost:8082 -Dservices.producto.url=http://localhost:8083 -Dservices.estado.url=http://localhost:8086`
4. **Ejecutar**: Run (▶) o Shift+F10
5. **Verificar**: `http://localhost:8081/h2-console`
   - Usuario: `sa`
   - Contraseña: (vacío)

## 19. Testear endpoints con Postman

### 0. Obtener Token JWT primero (desde auth-service)
```http
POST http://localhost:8090/api/auth/login
Content-Type: application/json

{
  "email": "admin@casaimpresion.cl",
  "password": "123456"
}
```

### 1. Crear Pedido (requiere cliente y productos existentes)
```http
POST http://localhost:8081/api/pedidos
Authorization: Bearer <TOKEN_JWT>
Content-Type: application/json

{
  "clienteId": 1,
  "estado": "COLA",
  "tipoDespacho": "RM",
  "items": [
    {
      "productoId": 1,
      "cantidad": 5
    },
    {
      "productoId": 2,
      "cantidad": 3
    }
  ]
}
```
**Respuesta esperada (201)**:
```json
{
  "numeroPedido": 1001,
  "clienteId": 1,
  "estado": "COLA",
  "tipoDespacho": "RM",
  "monto": 22500.0,
  "fechaCreacion": "2026-05-22T10:00:00",
  "items": [
    {
      "id": 1,
      "productoId": 1,
      "cantidad": 5,
      "precioUnitario": 3500.0,
      "subtotal": 17500.0
    },
    {
      "id": 2,
      "productoId": 2,
      "cantidad": 3,
      "precioUnitario": 1666.67,
      "subtotal": 5000.0
    }
  ]
}
```

### 2. Listar Todos los Pedidos
```http
GET http://localhost:8081/api/pedidos
Authorization: Bearer <TOKEN_JWT>
```

### 3. Obtener Pedido por Número (Long)
```http
GET http://localhost:8081/api/pedidos/1001
Authorization: Bearer <TOKEN_JWT>
```

### 4. Obtener Pedido por Número (String)
```http
GET http://localhost:8081/api/pedidos/numero/1001
Authorization: Bearer <TOKEN_JWT>
```

### 5. Listar Pedidos por Cliente
```http
GET http://localhost:8081/api/pedidos/cliente/1
Authorization: Bearer <TOKEN_JWT>
```

### 6. Cambiar Estado del Pedido
```http
PATCH http://localhost:8081/api/pedidos/1001/estado
Authorization: Bearer <TOKEN_JWT>
Content-Type: application/json

{
  "estado": "PRODUCCION"
}
```
**Respuesta esperada (200)**:
```json
{
  "numeroPedido": 1001,
  "clienteId": 1,
  "estado": "PRODUCCION",
  "tipoDespacho": "RM",
  "monto": 22500.0,
  "fechaCreacion": "2026-05-22T10:00:00",
  "items": [...]
}
```

### 7. Obtener Historial de Estados del Pedido
```http
GET http://localhost:8081/api/pedidos/1001/historial
Authorization: Bearer <TOKEN_JWT>
```
**Respuesta esperada (200)**:
```json
[
  {
    "id": 1,
    "numeroPedido": 1001,
    "estadoAnterior": "SIN_ESTADO",
    "estadoNuevo": "COLA",
    "fechaCambio": "2026-05-22T10:00:00"
  },
  {
    "id": 2,
    "numeroPedido": 1001,
    "estadoAnterior": "COLA",
    "estadoNuevo": "PRODUCCION",
    "fechaCambio": "2026-05-22T11:00:00"
  }
]
```

### 8. Eliminar Pedido
```http
DELETE http://localhost:8081/api/pedidos/1001
Authorization: Bearer <TOKEN_JWT>
```
**Respuesta esperada (204 No Content)**

## 20. Dependencias inter-servicios
| Servicio | URL | Puerto | Usado para |
|----------|-----|--------|-----------|
| `cliente-service` | http://localhost:8082 | 8082 | Validar existencia de cliente |
| `producto-service` | http://localhost:8083 | 8083 | Validar existencia y precio de producto |
| `estado-service` | http://localhost:8086 | 8086 | Registrar cambios de estado |

## 21. Estructura de carpetas
```
pedido-service/
├── pom.xml
├── README.md
├── src/
│   └── main/
│       ├── java/cl/duocuc/pedidoservice/
│       │   ├── client/
│       │   │   ├── ClienteFeignClient.java
│       │   │   ├── ProductoFeignClient.java
│       │   │   └── EstadoFeignClient.java
│       │   ├── controller/        (PedidoController)
│       │   ├── dto/               (PedidoRequest, PedidoResponse, ItemPedidoRequest/Response)
│       │   ├── entity/            (Pedido, ItemPedido)
│       │   ├── exception/         
│       │   ├── repository/        (PedidoRepository, ItemPedidoRepository)
│       │   ├── service/           (PedidoService)
│       │   ├── common/            (ApiResponse, excepciones globales)
│       │   └── PedidoServiceApplication.java
│       └── resources/
│           ├── application.properties
│           ├── application-h2.properties
│           └── application-prod.properties
└── data/
    └── pedido_db.mv.db
```

## 22. Estados válidos de pedido
| Estado | Descripción |
|--------|-------------|
| `COLA` | Pedido recibido, esperando procesamiento |
| `PRODUCCION` | En fase de fabricación |
| `LISTO` | Producción completada |
| `DESPACHADO` | Enviado al cliente |
| `ENTREGADO` | Recibido por el cliente |

## 23. Validaciones aplicadas
- **Cliente**: Debe existir en `cliente-service`.
- **Productos**: Cada ID debe existir y estar disponible en `producto-service`.
- **Cantidad**: Mayor a 0 para cada item.
- **Tipo Despacho**: Normalizado a mayúsculas (RM, Regiones, Internacional).

## 24. Notas importantes
- ✅ Todos los endpoints de negocio requieren JWT válido.
- ✅ Las direcciones Feign se configuram en `application-h2.properties`.
- ✅ El caché de pedidos en memoria optimiza lecturas repetidas.
- ✅ La creación de pedido es transaccional (todo o nada).

## 25. Estado actual del servicio
- ✅ Compila correctamente con Java 21 y Spring Boot 4.0.5.
- ✅ Seguridad JWT activa.
- ✅ Integración Feign con 3 servicios operativa.
- ✅ H2 Console accesible en `http://localhost:8081/h2-console`.
- ✅ Profile por defecto: `h2`.
