# Guía rápida - Módulo Pedido-Service

## 🚀 Inicio rápido

### 1. Ejecutar la aplicación
```bash
cd C:\Users\sebyv\IdeaProjects\DSY1103-011V-Api-Pedidos-JDK25-corregido
mvnw.cmd spring-boot:run
```

### 2. Acceso a H2 Console
```
URL: http://localhost:8080/h2-console
Usuario: sa
Contraseña: (vacío)
JDBC URL: jdbc:h2:mem:gestion_pedidos_db
```

---

## 📝 Flujo típico de uso

### Paso 1: Crear cliente
```bash
curl -X POST http://localhost:8080/api/clientes \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan García López",
    "rut": "18765432-9",
    "email": "juan@ejemplo.cl",
    "telefono": "+56987654321",
    "direccion": "Av. Providencia 123",
    "comuna": "Providencia",
    "region": "Región Metropolitana"
  }'
```

**Respuesta:**
```json
{
  "id": 1,
  "nombre": "Juan García López",
  "rut": "18765432-9"
}
```

### Paso 2: Crear pedido (usando clienteId: 1)
```bash
curl -X POST http://localhost:8080/api/pedidos \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": 1,
    "estado": "COLA",
    "tipoDespacho": "DOMICILIO",
    "items": [
      {
        "productoId": 5,
        "nombreProducto": "Laptop ASUS",
        "cantidad": 1,
        "precioUnitario": 899.99
      },
      {
        "productoId": 12,
        "nombreProducto": "Mouse Logitech",
        "cantidad": 2,
        "precioUnitario": 29.99
      }
    ]
  }'
```

**Respuesta:**
```json
{
  "numeroPedido": 1,
  "clienteId": 1,
  "estado": "COLA",
  "tipoDespacho": "DOMICILIO",
  "monto": 959.97,
  "fechaCreacion": "2026-04-23T15:30:45",
  "items": [
    {
      "id": 1,
      "productoId": 5,
      "nombreProducto": "Laptop ASUS",
      "cantidad": 1,
      "precioUnitario": 899.99,
      "subtotal": 899.99
    },
    {
      "id": 2,
      "productoId": 12,
      "nombreProducto": "Mouse Logitech",
      "cantidad": 2,
      "precioUnitario": 29.99,
      "subtotal": 59.98
    }
  ]
}
```

### Paso 3: Listar pedidos
```bash
curl http://localhost:8080/api/pedidos
```

### Paso 4: Obtener pedido específico
```bash
curl http://localhost:8080/api/pedidos/1
```

### Paso 5: Listar pedidos de cliente específico
```bash
curl http://localhost:8080/api/pedidos/cliente/1
```

### Paso 6: Actualizar estado del pedido
```bash
curl -X PATCH "http://localhost:8080/api/pedidos/1/estado" \
  -H "Content-Type: application/json" \
  -d '{"estado":"LISTO"}'
```

### Paso 7: Eliminar pedido (eliminación física)
```bash
curl -X DELETE http://localhost:8080/api/pedidos/1
```

---

## 📊 Estructura de datos

### Pedido
```json
{
  "numeroPedido": 1,
  "clienteId": 1,
  "estado": "COLA",
  "tipoDespacho": "DOMICILIO",
  "monto": 959.97,
  "fechaCreacion": "2026-04-23T15:30:45",
  "items": []
}
```

### ItemPedido
```json
{
  "id": 1,
  "productoId": 5,
  "nombreProducto": "Laptop ASUS",
  "cantidad": 1,
  "precioUnitario": 899.99,
  "subtotal": 899.99
}
```

---

## ⚡ Características clave

### 1. Cálculo automático de monto
```
Pedido.monto = ItemPedido[1].subtotal + ItemPedido[2].subtotal + ...

Ejemplo:
- Item 1: 1 × 899.99 = 899.99
- Item 2: 2 × 29.99 = 59.98
- Total: 959.97
```

### 2. Cálculo automático de subtotal
```
ItemPedido.subtotal = cantidad × precioUnitario
```

### 3. Eliminación física
```
DELETE /api/pedidos/1
→ se elimina físicamente el pedido y sus items (cascade)
→ GET /api/pedidos ya no devolverá el pedido eliminado
```

### 4. Número de pedido único
```
No puedes crear 2 pedidos con el mismo numeroPedido
→ Error 409 Conflict
```

### 5. ClienteId solo (sin relación)
```
Pedido almacena clienteId: 1
No valida que cliente exista (aún)
Preparado para futura integración con cliente-service
```

---

## 🔍 Búsqueda de pedidos

### Por numeroPedido (identificador único)
```bash
GET /api/pedidos/1
```

### Por cliente
```bash
GET /api/pedidos/cliente/1
```

### Todos los pedidos
```bash
GET /api/pedidos
```

---

### Error: Pedido sin items
```json
{
  "status": 400,
  "message": "Error de validación",
  "errors": {
    "items": "El pedido debe contener al menos un item"
  }
}
```

**Solución:** Agrega al menos 1 item al pedido

### Error: Cantidad o precio inválidos
```json
{
  "status": 400,
  "message": "Error de validación",
  "errors": {
    "cantidad": "La cantidad debe ser mayor a 0",
    "precioUnitario": "El precio unitario debe ser mayor a 0"
  }
}
```

**Solución:** Usa valores positivos

### Error: Pedido no encontrado
```json
{
  "status": 404,
  "message": "Pedido no encontrado"
}
```

## 📋 Estados válidos de Pedido

```
COLA         → Pedido acabado de crear
PRODUCCION   → Pedido en producción/procesamiento
LISTO        → Pedido listo para despacho
DESPACHADO   → Pedido despachado
ENTREGADO    → Pedido entregado al cliente
```

### Cambiar estado
```bash
curl -X PATCH "http://localhost:8080/api/pedidos/1/estado" \
  -H "Content-Type: application/json" \
  -d '{"estado":"LISTO"}'
```
---

## 🧪 Testing con Postman

### Importar colección
1. Abre Postman
2. File → Import
3. Pega esta JSON en "Raw text":

```json
{
  "info": {
    "name": "API Pedidos - Pedido-Service",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "POST - Crear pedido",
      "request": {
        "method": "POST",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "url": {"raw": "http://localhost:8080/api/pedidos", "protocol": "http", "host": ["localhost"], "port": ["8080"], "path": ["api", "pedidos"]}
      }
    },
    {
      "name": "GET - Listar todos",
      "request": {
        "method": "GET",
        "url": {"raw": "http://localhost:8080/api/pedidos", "protocol": "http", "host": ["localhost"], "port": ["8080"], "path": ["api", "pedidos"]}
      }
    },
    {
      "name": "GET - Por ID",
      "request": {
        "method": "GET",
        "url": {"raw": "http://localhost:8080/api/pedidos/1", "protocol": "http", "host": ["localhost"], "port": ["8080"], "path": ["api", "pedidos", "1"]}
      }
    },
    {
      "name": "PATCH - Actualizar estado",
      "request": {
        "method": "PATCH",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "url": {"raw": "http://localhost:8080/api/pedidos/1/estado", "protocol": "http", "host": ["localhost"], "port": ["8080"], "path": ["api", "pedidos", "1", "estado"]},
        "body": {"mode": "raw", "raw": "{\"estado\": \"LISTO\"}"}
      }
    },
    {
      "name": "DELETE - Eliminar",
      "request": {
        "method": "DELETE",
        "url": {"raw": "http://localhost:8080/api/pedidos/1", "protocol": "http", "host": ["localhost"], "port": ["8080"], "path": ["api", "pedidos", "1"]}
      }
    }
  ]
}
```

---

## 🔗 Próxima integración

### Cuando cliente-service sea microservicio
```
Cliente-Service: http://localhost:8081
Pedido-Service: http://localhost:8082

Pedido-Service llamará a Cliente-Service para validar clienteId
```

---

## 📞 Comandos útiles

### Health check
```bash
curl http://localhost:8080/actuator/health
```

### Ver todas las rutas
```bash
curl http://localhost:8080/actuator/mappings
```

---

**Versión:** 0.0.1-SNAPSHOT  
**Última actualización:** 23 de abril de 2026  
**Módulo:** pedido-service
