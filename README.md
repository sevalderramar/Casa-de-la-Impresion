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
├── pedido
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
- estado

El campo `estado` reemplaza a `activo`.

- `ACTIVO`: cliente habilitado.
- `INACTIVO`: cliente eliminado de forma lógica.

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

- id
- numeroPedido
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
GET    /api/pedidos/{id}
GET    /api/pedidos/numero/{numeroPedido}
GET    /api/pedidos/cliente/{clienteId}
PATCH  /api/pedidos/{id}/estado?estado=LISTO
GET    /api/pedidos/filtro?estado=PENDIENTE
GET    /api/pedidos/filtro-combinado?estado=LISTO&tipoDespacho=RETIRO
DELETE /api/pedidos/{id}
```

### Ejemplo POST pedido

```json
{
  "numeroPedido": "PED-001",
  "clienteId": 1,
  "estado": "PENDIENTE",
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
- estado
- fechaCreacion

El campo `estado` reemplaza a `activo`.

- `ACTIVO`: producto disponible en el catálogo.
- `INACTIVO`: producto eliminado de forma lógica.

### Reglas principales

- No permite productos duplicados por nombre.
- `fechaCreacion` se asigna automáticamente.
- `estado` se inicializa como `ACTIVO`.
- La eliminación es lógica y cambia el estado a `INACTIVO`.
- No conecta todavía con `pedido` mediante relaciones JPA.

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

## Base de datos H2

La aplicación usa H2 para pruebas locales.

Consola H2:

```text
http://localhost:8080/h2-console
```

Datos de conexión:

```text
JDBC URL: jdbc:h2:mem:gestion_pedidos_db
User: sa
Password: vacío
```

