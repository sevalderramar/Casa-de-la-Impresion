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

## 14. Descripcion detallada del servicio

`metrica-service` proporciona inteligencia de negocio y análisis de datos:

- **Ranking de Clientes**: Top clientes por cantidad de pedidos/monto total.
- **Top Productos**: Productos más vendidos en rango de fechas.
- **Resumen de Ventas**: Monto total y cantidad de pedidos por período.
- **Métricas Individuales**: Estadísticas detalladas de cliente específico.
- **Integración Feign**: Consulta datos de `pedido-service` y `cliente-service`.

Útil para dashboards, reportes y decisiones comerciales.

## 15. Como compilar desde terminal
```powershell
cd .\metrica-service
.\mvnw clean compile
```

## 16. Como ejecutar desde terminal
```powershell
cd .\metrica-service
$env:JWT_SECRET="tu-secreto-base64-aqui"
.\mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=h2"
```

## 17. Como ejecutar desde IntelliJ IDEA

1. **Abrir el proyecto**: File → Open → carpeta `metrica-service`
2. **Dependencias**: Asegurar que `pedido-service` (8081) y `cliente-service` (8082) están ejecutándose
3. **Configurar variables de entorno**:
   - Edit Configurations (esquina superior)
   - Create new → Spring Boot
   - Name: `metrica-service`
   - Main class: `cl.duocuc.metricaservice.MetricaServiceApplication`
   - Enviroment variables: `JWT_SECRET=tu-secreto-base64`
   - Active profiles: `h2`
4. **Ejecutar**: Run (▶) o Shift+F10
5. **Verificar**: `http://localhost:8087/h2-console`
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

### 1. Obtener Métricas de Cliente Específico
```http
GET http://localhost:8087/api/metricas/clientes/1
Authorization: Bearer <TOKEN_JWT>
```
**Respuesta esperada (200)**:
```json
{
  "mensaje": "Métricas del cliente obtenidas correctamente",
  "data": {
    "clienteId": 1,
    "nombreCliente": "Maria Perez",
    "cantidadPedidos": 5,
    "montoTotal": 150000.0,
    "montoPromedio": 30000.0,
    "estadoMasComun": "ENTREGADO",
    "fechaPrimerPedido": "2026-05-01",
    "fechaUltimoPedido": "2026-05-22"
  },
  "exitoso": true,
  "timestamp": "2026-05-22T10:00:00"
}
```

### 2. Obtener Ranking de Clientes (Top 10)
```http
GET http://localhost:8087/api/metricas/clientes/ranking?limite=10
Authorization: Bearer <TOKEN_JWT>
```
**Respuesta esperada (200)**:
```json
{
  "mensaje": "Ranking obtenido correctamente",
  "data": [
    {
      "clienteId": 1,
      "nombreCliente": "Maria Perez",
      "cantidadPedidos": 15,
      "montoTotal": 450000.0,
      "posicion": 1
    },
    {
      "clienteId": 3,
      "nombreCliente": "Juan Garcia",
      "cantidadPedidos": 12,
      "montoTotal": 380000.0,
      "posicion": 2
    }
  ],
  "exitoso": true,
  "timestamp": "2026-05-22T10:00:00"
}
```

### 3. Obtener Top Productos en Rango de Fechas
```http
GET http://localhost:8087/api/metricas/productos/top?desde=2026-05-01&hasta=2026-05-31&limite=10
Authorization: Bearer <TOKEN_JWT>
```
**Respuesta esperada (200)**:
```json
{
  "mensaje": "Top productos obtenido",
  "data": [
    {
      "productoId": 1,
      "nombreProducto": "Resma Carta Blanca",
      "categoria": "Papel",
      "cantidadVendida": 250,
      "montoTotal": 875000.0,
      "posicion": 1
    },
    {
      "productoId": 5,
      "nombreProducto": "Tinta Epson Negro",
      "categoria": "Tinta",
      "cantidadVendida": 180,
      "montoTotal": 4500000.0,
      "posicion": 2
    }
  ],
  "exitoso": true,
  "timestamp": "2026-05-22T10:00:00"
}
```

### 4. Obtener Resumen de Ventas por Período
```http
GET http://localhost:8087/api/metricas/ventas?desde=2026-05-01&hasta=2026-05-31
Authorization: Bearer <TOKEN_JWT>
```
**Respuesta esperada (200)**:
```json
{
  "mensaje": "Resumen de ventas obtenido",
  "data": {
    "desde": "2026-05-01",
    "hasta": "2026-05-31",
    "montoTotal": 2500000.0,
    "cantidadPedidos": 42,
    "montoPromedioPorPedido": 59523.81,
    "cantidadClientes": 8,
    "productosVendidos": 1250
  },
  "exitoso": true,
  "timestamp": "2026-05-22T10:00:00"
}
```

### 5. Variante: Sin fechas (usa primer día del mes a hoy)
```http
GET http://localhost:8087/api/metricas/ventas
Authorization: Bearer <TOKEN_JWT>
```

## 19. Parámetros soportados
| Parámetro | Tipo | Requerido | Descripción |
|-----------|------|-----------|-------------|
| `desde` | LocalDate (yyyy-MM-dd) | No | Fecha inicial (default: primer día del mes) |
| `hasta` | LocalDate (yyyy-MM-dd) | No | Fecha final (default: hoy) |
| `limite` | Integer | No | Número de registros a retornar (default: 10) |

## 20. Dependencias inter-servicios
| Servicio | URL | Puerto | Usado para |
|----------|-----|--------|-----------|
| `pedido-service` | http://localhost:8081 | 8081 | Obtener datos de pedidos y ventas |
| `cliente-service` | http://localhost:8082 | 8082 | Obtener información de clientes |

## 21. Estructura de carpetas
```
metrica-service/
├── pom.xml
├── README.md
├── src/
│   └── main/
│       ├── java/cl/duocuc/metricaservice/
│       │   ├── client/
│       │   │   ├── ClienteFeignClient.java
│       │   │   └── PedidoFeignClient.java
│       │   ├── controller/      (MetricaController)
│       │   ├── dto/             (Responses y DTOs de analítica)
│       │   ├── exception/       (ConflictException)
│       │   ├── handler/         (GlobalExceptionHandler)
│       │   ├── service/         (MetricaService)
│       │   └── MetricaServiceApplication.java
│       └── resources/
│           ├── application.properties
│           ├── application-h2.properties
│           └── application-prod.properties
└── data/
    └── metrica_db.mv.db
```

## 22. Validaciones aplicadas
- **Fechas**: `desde` no puede ser posterior a `hasta`.
- **Límite**: Máximo 100 registros para evitar sobrecarga.
- **Cliente ID**: Debe existir en `cliente-service` si se consulta métrica individual.

## 23. Notas importantes
- ✅ Todos los endpoints de negocio requieren JWT válido.
- ✅ Integración Feign con 2 servicios es CRÍTICA.
- ✅ Los datos se agregan en tiempo real desde otros servicios.
- ✅ Las consultas sin caché para siempre tener datos actualizados.
- ✅ Timeouts configurados: 2s connection, 5s read.

## 24. Casos de uso
- **Dashboard**: Mostrar ranking de mejores clientes.
- **Reportería**: Generar reportes de ventas mensuales.
- **Análisis**: Identificar productos más vendidos por período.
- **KPIs**: Monitorear monto promedio por pedido.

## 25. Estado actual del servicio
- ✅ Compila correctamente con Java 21 y Spring Boot 4.0.5.
- ✅ Seguridad JWT activa.
- ✅ Integración Feign con 2 servicios operativa.
- ✅ H2 Console accesible en `http://localhost:8087/h2-console`.
- ✅ Endpoints de analítica todos funcionales.
- ✅ Profile por defecto: `h2`.
