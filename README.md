# Sistema de Gestión de Pedidos

Backend REST construido con Spring Boot 4.0.5 y Java 25. El proyecto está organizado como **monolito modular**, es decir, una sola aplicación Spring Boot dividida por dominios de negocio.

## Arquitectura actual

```text
cl.duocuc.sistemagestionpedidos
├── cliente
│   ├── controller
│   ├── dto
│   ├── model
│   ├── repository
│   └── service
├── producto
│   ├── controller
│   ├── dto
│   ├── model
│   ├── repository
│   └── service
├── pedido
│   ├── controller
│   ├── dto
│   ├── model
│   ├── repository
│   └── service
├── estado
│   ├── controller
│   ├── dto
│   ├── model
│   ├── repository
│   └── service
└── common
    └── exception
```

## Por qué es modular

Antes el proyecto estaba organizado por capas generales. Ahora cada dominio tiene sus propias capas internas. Esto permite mantener separado el código de clientes y pedidos sin crear todavía microservicios Maven independientes.

## Módulo cliente

Gestiona los clientes del sistema.

### Entidad Cliente

- id
- nombre
- rut
- email
- telefono
- direccion
- comuna
- region
- fechaRegistro

### Endpoints cliente

```http
POST   /api/clientes
GET    /api/clientes
GET    /api/clientes/{id}
GET    /api/clientes/rut/{rut}
PUT    /api/clientes/{id}
DELETE /api/clientes/{id}
```

### Ejemplo POST cliente

```json
{
  "nombre": "Juan Perez",
  "rut": "12345678-9",
  "email": "juan@gmail.com",
  "telefono": "999999999",
  "direccion": "Av Siempre Viva 123",
  "comuna": "Santiago",
  "region": "Metropolitana"
}
```

## Módulo pedido

Gestiona pedidos e ítems de pedido. De momento no valida contra `cliente-service`; solo guarda `clienteId`, dejando el sistema preparado para futura separación real a microservicios.

### Entidad Pedido

- numeroPedido (Long, PK generado)
- clienteId
- estado
- tipoDespacho
- monto
- fechaCreacion
- items

### Entidad ItemPedido

- id
- productoId
- nombreProducto
- cantidad
- precioUnitario
- subtotal

### Endpoints pedido

```http
POST   /api/pedidos
GET    /api/pedidos
GET    /api/pedidos/{numeroPedido}
GET    /api/pedidos/numero/{numeroPedido}
GET    /api/pedidos/cliente/{clienteId}
PATCH  /api/pedidos/{numeroPedido}/estado   (body JSON: { "estado": "LISTO" })
GET    /api/pedidos/{numeroPedido}/historial
DELETE /api/pedidos/{numeroPedido}
```

### Ejemplo POST pedido

```json
{
  "clienteId": 1,
  "estado": "COLA",
  "tipoDespacho": "RETIRO",
  "items": [
    {
      "productoId": 1,
      "nombreProducto": "Etiqueta ecocuero",
      "cantidad": 100,
      "precioUnitario": 250
    },
    {
      "productoId": 2,
      "nombreProducto": "Sticker redondo",
      "cantidad": 50,
      "precioUnitario": 150
    }
  ]
}
```

El sistema calcula automáticamente:

```text
subtotal = cantidad * precioUnitario
monto = suma de subtotales
```

### Historial de estados

El módulo `estado` registra la auditoría operacional de los pedidos. Los cambios de estado pueden
consultarse desde:

```http
GET /api/pedidos/{numeroPedido}/historial
GET /api/estados/pedido/{numeroPedido}
```

## Módulo producto

Gestiona el catálogo de productos del sistema. Se integra de forma modular dentro de la misma
aplicación Spring Boot, sin convertirse todavía en un microservicio independiente.

### Entidad Producto

- id
- nombre
- descripcion
- categoria
- precio
- stock
- fechaCreacion

### Reglas principales

- No permite productos duplicados por nombre.
- `fechaCreacion` se asigna automáticamente.

### Endpoints producto

```http
POST   /api/productos
GET    /api/productos
GET    /api/productos/{id}
GET    /api/productos/nombre/{nombre}
GET    /api/productos/categoria/{categoria}
PUT    /api/productos/{id}
DELETE /api/productos/{id}
```

### Ejemplo POST producto

```json
{
  "nombre": "Etiqueta ecocuero",
  "descripcion": "Etiqueta premium para ropa",
  "categoria": "ETIQUETAS",
  "precio": 250.0,
  "stock": 1000
}
```

### Integración futura con pedido

El módulo `pedido` seguirá usando `productoId` y `nombreProducto` como snapshot histórico en cada
ítem. Esto permite conservar pedidos antiguos aunque el catálogo cambie y, a futuro, facilita separar
`producto` en un microservicio independiente.

## Módulo estado

Gestiona el historial y auditoría de cambios de estado de los pedidos.

### Qué guarda

- numeroPedido
- estadoAnterior
- estadoNuevo
- fechaCambio
- observacion

### Endpoints estado

```http
POST /api/estados
GET  /api/estados/pedido/{numeroPedido}
GET  /api/estados/pedido/{numeroPedido}/ultimo
```

### Integración con pedido

El estado actual del pedido se actualiza con `PATCH /api/pedidos/{numeroPedido}/estado` usando body JSON:

```json
{
  "estado": "LISTO"
}
```

Además, el historial puede consultarse desde:

```http
GET /api/pedidos/{numeroPedido}/historial
```

## Configuración de Ambientes

El proyecto utiliza **Spring Profiles** para gestionar diferentes configuraciones según el ambiente de ejecución.

### Archivos de Configuración

```text
src/main/resources/
├── application.properties          # Configuración base
├── application-dev.properties      # Perfil desarrollo
└── application-prod.properties     # Perfil producción
```

### Perfil de Desarrollo (dev)

Activo por defecto. Usa H2 en memoria para pruebas rápidas.

**Configuración:**
- Base de datos: H2 en memoria (`jdbc:h2:mem:gestion_pedidos_db`)
- Usuario: `sa`
- Contraseña: vacía
- Puerto: `8080`
- JPA DDL: `update` (actualiza esquema automáticamente)
- SQL logging: habilitado
- Consola H2: habilitada en `/h2-console`

**Para ejecutar en desarrollo:**
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

O simplemente (es el perfil predeterminado):
```bash
mvn spring-boot:run
```

### Perfil de Producción (prod)

Usa MySQL para persistencia real.

**Configuración:**
- Base de datos: MySQL (`jdbc:mysql://localhost:3306/gestion_pedidos`)
- Usuario: `root`
- Contraseña: vacía (modificar según ambiente)
- Puerto: `8080`
- JPA DDL: `validate` (valida que el esquema exista)
- SQL logging: deshabilitado

**Para ejecutar en producción:**
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"
```

## Base de datos H2 (Desarrollo)

La aplicación usa H2 para pruebas locales en el perfil development.

Consola H2:

```text
http://localhost:8080/h2-console
```

Datos de conexión:

```text
JDBC URL: jdbc:h2:mem:gestion_pedidos_db
User: sa
Password: vacio
```

