# Estado Service

## 📋 Descripción General

**Estado Service** es un microservicio desarrollado con **Spring Boot 4.0.5** diseñado para gestionar y auditar los cambios de estado de los pedidos en el sistema de gestión de pedidos de DuoCUC.

### Objetivo del Microservicio

Este servicio proporciona un registro completo y auditable de todas las transiciones de estado por las que pasa cada pedido, desde su creación hasta su entrega o cancelación. Permite a otros microservicios y componentes del sistema consultar el historial de cambios y obtener el estado actual de cualquier pedido.

### Problema que Resuelve

- **Trazabilidad**: Mantiene un historial completo de cada cambio de estado
- **Auditoría**: Registra cuándo y a qué estado cambió cada pedido
- **Consultas eficientes**: Permite obtener rápidamente el estado actual o histórico de un pedido
- **Integración**: Actúa como servicio centralizado de estados, evitando duplicación de lógica

---

## 🏗️ Información de Despliegue

| Parámetro | Valor                              |
|-----------|------------------------------------|
| **Puerto** | 8086                               |
| **Contexto** | `/` (root)                         |
| **Base de Datos** | H2 (en memoria)                    |
| **Base de Datos (URL)** | `jdbc:h2:mem:estado_db`            |
| **Usuario BD** | `sa`                               |
| **Contraseña BD** | (vacía)                            |
| **H2 Console** | `http://localhost:8086/h2-console` |
| **JDK** | Java 25                            |
| **Spring Boot** | 4.0.5                              |

---

## 📦 Dependencias Principales

El microservicio utiliza las siguientes dependencias (definidas en `pom.xml`):

### Framework y Web
- **spring-boot-starter-webmvc**: Framework web para crear endpoints REST
- **spring-boot-starter-validation**: Validación de datos con anotaciones como `@NotNull`, `@NotBlank`

### Persistencia de Datos
- **spring-boot-starter-data-jpa**: Acceso a datos con JPA/Hibernate
- **com.h2database:h2**: Base de datos SQL embebida en memoria
- **com.mysql:mysql-connector-j**: Driver MySQL (disponible en runtime, permite cambiar a MySQL en producción)

### Herramientas de Desarrollo
- **spring-boot-h2console**: Consola H2 para visualizar/administrar la BD
- **spring-boot-devtools**: Recarga en caliente durante desarrollo
- **org.projectlombok:lombok**: Reduce boilerplate de getters/setters/constructores

### Integración con Otros Microservicios
- **spring-cloud-starter-openfeign**: Cliente HTTP declarativo para llamadas entre servicios
- **spring-cloud.version**: 2025.1.1

### Batch y Testing
- **spring-boot-starter-batch**: Procesamiento de trabajos por lotes
- **spring-boot-starter-batch-test**: Tests para batch
- **spring-boot-starter-data-jpa-test**: Tests para JPA
- **spring-boot-starter-webmvc-test**: Tests para web

---

## 🏛️ Arquitectura Interna

El microservicio sigue una arquitectura **en capas** con clara separación de responsabilidades:

```
cl.duocuc.estadoservice/
├── EstadoServiceApplication        # Clase raíz de la aplicación
├── controller/
│   └── EstadoController            # Endpoints REST
├── service/
│   └── EstadoService               # Lógica de negocio
├── repository/
│   └── CambioEstadoRepository      # Acceso a datos (JPA)
├── model/
│   └── CambioEstado                # Entidad JPA
├── dto/
│   ├── CambioEstadoRequest         # DTO para entrada
│   └── CambioEstadoResponse        # DTO para salida
└── common/
    └── exception/
        └── ResourceNotFoundException # Excepción personalizada
```

### Descripción de Componentes

#### 1. **EstadoServiceApplication**
- **Ubicación**: `cl.duocuc.estadoservice`
- **Responsabilidad**: Punto de entrada de la aplicación
- **Características**:
  - Anotada con `@SpringBootApplication`
  - Realiza el escaneo automático de componentes en el package raíz
  - Contiene el método `main()` para iniciar la aplicación

#### 2. **EstadoController**
- **Ubicación**: `cl.duocuc.estadoservice.controller`
- **Responsabilidad**: Maneja las solicitudes HTTP y devuelve respuestas
- **Endpoints**:
  - `POST /api/estados`: Crear un nuevo cambio de estado
  - `GET /api/estados/pedido/{numeroPedido}`: Listar todos los cambios de un pedido
  - `GET /api/estados/pedido/{numeroPedido}/ultimo`: Obtener el último estado de un pedido
- **Técnicas usadas**:
  - Inyección de dependencias del `EstadoService`
  - Validación con `@Valid`
  - Códigos HTTP apropiados (201 para POST, 200 para GET)

#### 3. **EstadoService**
- **Ubicación**: `cl.duocuc.estadoservice.service`
- **Responsabilidad**: Lógica de negocio
- **Funciones principales**:
  - `registrarCambioEstado()`: Persiste un nuevo cambio de estado
  - `listarCambiosPorPedido()`: Consulta todos los cambios ordenados por fecha
  - `obtenerUltimoEstadoPorPedido()`: Obtiene el estado más reciente
- **Mapeo de DTOs**: Convierte entidades JPA a objetos de respuesta

#### 4. **CambioEstado** (Modelo)
- **Ubicación**: `cl.duocuc.estadoservice.model`
- **Responsabilidad**: Entidad JPA que representa un cambio de estado
- **Campos principales**:
  - `id`: Identificador único (auto-generado)
  - `numeroPedido`: Referencia al pedido
  - `estadoAnterior`: Estado previo (nullable)
  - `estadoNuevo`: Nuevo estado (requerido)
  - `fechaCambio`: Marca de tiempo automática
  - `observacion`: Comentario adicional (hasta 500 caracteres)
- **Tabla**: `cambios_estado`
- **Hooks JPA**: `@PrePersist` para auto-asignar fecha si no existe

#### 5. **CambioEstadoRepository**
- **Ubicación**: `cl.duocuc.estadoservice.repository`
- **Responsabilidad**: Acceso a datos (Data Access Object)
- **Métodos**:
  - `findByNumeroPedido()`: Busca cambios por número de pedido
  - `findByNumeroPedidoOrderByFechaCambioAsc()`: Lista ordenada por fecha ascendente
  - Heredado de `JpaRepository`: save(), findById(), delete(), etc.

#### 6. **DTOs (Data Transfer Objects)**

**CambioEstadoRequest** (Entrada):
```
numeroPedido      (Long, obligatorio)
estadoAnterior    (String, opcional)
estadoNuevo       (String, obligatorio)
observacion       (String, opcional)
```

**CambioEstadoResponse** (Salida):
```
id                (Long)
numeroPedido      (Long)
estadoAnterior    (String)
estadoNuevo       (String)
fechaCambio       (LocalDateTime)
observacion       (String)
```

#### 7. **ResourceNotFoundException**
- **Ubicación**: `cl.duocuc.estadoservice.common.exception`
- **Responsabilidad**: Excepción personalizada lanzada cuando no se encuentran cambios de estado
- **Herencia**: Extiende `RuntimeException`
- **Constructores**: Message, message + cause

---

## 🔌 Endpoints Disponibles

### 1. Crear un Nuevo Cambio de Estado
```http
POST /api/estados
Content-Type: application/json

{
  "numeroPedido": 1001,
  "estadoAnterior": "PENDIENTE",
  "estadoNuevo": "CONFIRMADO",
  "observacion": "Pedido confirmado y pagado"
}
```

**Respuesta (201 Created)**:
```json
{
  "id": 1,
  "numeroPedido": 1001,
  "estadoAnterior": "PENDIENTE",
  "estadoNuevo": "CONFIRMADO",
  "fechaCambio": "2026-05-11T03:30:00",
  "observacion": "Pedido confirmado y pagado"
}
```

---

### 2. Listar Cambios de Estado de un Pedido
```http
GET /api/estados/pedido/1001
```

**Respuesta (200 OK)**:
```json
[
  {
    "id": 1,
    "numeroPedido": 1001,
    "estadoAnterior": null,
    "estadoNuevo": "CREADO",
    "fechaCambio": "2026-05-11T03:20:00",
    "observacion": null
  },
  {
    "id": 2,
    "numeroPedido": 1001,
    "estadoAnterior": "CREADO",
    "estadoNuevo": "CONFIRMADO",
    "fechaCambio": "2026-05-11T03:25:00",
    "observacion": "Confirmado por cliente"
  },
  {
    "id": 3,
    "numeroPedido": 1001,
    "estadoAnterior": "CONFIRMADO",
    "estadoNuevo": "ENVIADO",
    "fechaCambio": "2026-05-11T03:30:00",
    "observacion": "Enviado al almacén"
  }
]
```

---

### 3. Obtener el Último Estado de un Pedido
```http
GET /api/estados/pedido/1001/ultimo
```

**Respuesta (200 OK)**:
```json
{
  "id": 3,
  "numeroPedido": 1001,
  "estadoAnterior": "CONFIRMADO",
  "estadoNuevo": "ENVIADO",
  "fechaCambio": "2026-05-11T03:30:00",
  "observacion": "Enviado al almacén"
}
```

---

## 📝 Ejemplos Prácticos con Postman

### Ejemplo 1: Crear Primer Cambio (Estado Inicial)
```bash
# Request
POST http://localhost:8086/api/estados
Content-Type: application/json

{
  "numeroPedido": 5001,
  "estadoAnterior": null,
  "estadoNuevo": "CREADO",
  "observacion": "Pedido creado en el sistema"
}

# Response (201)
{
  "id": 1,
  "numeroPedido": 5001,
  "estadoAnterior": null,
  "estadoNuevo": "CREADO",
  "fechaCambio": "2026-05-11T10:15:32.123456",
  "observacion": "Pedido creado en el sistema"
}
```

### Ejemplo 2: Cambiar Estado a Confirmado
```bash
# Request
POST http://localhost:8086/api/estados
Content-Type: application/json

{
  "numeroPedido": 5001,
  "estadoAnterior": "CREADO",
  "estadoNuevo": "CONFIRMADO",
  "observacion": "Pago procesado correctamente"
}

# Response (201)
{
  "id": 2,
  "numeroPedido": 5001,
  "estadoAnterior": "CREADO",
  "estadoNuevo": "CONFIRMADO",
  "fechaCambio": "2026-05-11T10:18:45.654321",
  "observacion": "Pago procesado correctamente"
}
```

### Ejemplo 3: Consultar Historial Completo
```bash
# Request
GET http://localhost:8086/api/estados/pedido/5001

# Response (200)
[
  {
    "id": 1,
    "numeroPedido": 5001,
    "estadoAnterior": null,
    "estadoNuevo": "CREADO",
    "fechaCambio": "2026-05-11T10:15:32.123456",
    "observacion": "Pedido creado en el sistema"
  },
  {
    "id": 2,
    "numeroPedido": 5001,
    "estadoAnterior": "CREADO",
    "estadoNuevo": "CONFIRMADO",
    "fechaCambio": "2026-05-11T10:18:45.654321",
    "observacion": "Pago procesado correctamente"
  }
]
```

### Ejemplo 4: Obtener Estado Actual
```bash
# Request
GET http://localhost:8086/api/estados/pedido/5001/ultimo

# Response (200)
{
  "id": 2,
  "numeroPedido": 5001,
  "estadoAnterior": "CREADO",
  "estadoNuevo": "CONFIRMADO",
  "fechaCambio": "2026-05-11T10:18:45.654321",
  "observacion": "Pago procesado correctamente"
}
```

### Ejemplo 5: Error - Validación Fallida
```bash
# Request (falta estadoNuevo)
POST http://localhost:8086/api/estados
Content-Type: application/json

{
  "numeroPedido": 5002,
  "estadoAnterior": "CREADO"
}

# Response (400 Bad Request)
{
  "timestamp": "2026-05-11T10:20:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed: estadoNuevo es obligatorio",
  "path": "/api/estados"
}
```

### Ejemplo 6: Error - Pedido No Encontrado
```bash
# Request
GET http://localhost:8086/api/estados/pedido/99999

# Response (404 Not Found)
{
  "timestamp": "2026-05-11T10:21:00",
  "status": 404,
  "error": "Not Found",
  "message": "No se encontraron cambios de estado para el pedido con numero: 99999",
  "path": "/api/estados/pedido/99999"
}
```

---

## 💾 Verificar Datos en H2 Console

### Acceso a H2 Console

1. Abre tu navegador e ingresa a: **`http://localhost:8086/h2-console`**
2. Verifica la configuración de conexión:
   - **JDBC URL**: `jdbc:h2:mem:estado_db`
   - **User Name**: `sa`
   - **Password**: (dejar vacío)
3. Haz clic en **"Connect"**

### Consultas SQL Útiles

```sql
-- Ver todos los cambios de estado
SELECT * FROM cambios_estado ORDER BY fecha_cambio ASC;

-- Ver cambios de un pedido específico
SELECT * FROM cambios_estado WHERE numero_pedido = 1001 ORDER BY fecha_cambio ASC;

-- Ver el último estado de cada pedido
SELECT DISTINCT ON (numero_pedido) * FROM cambios_estado 
ORDER BY numero_pedido, fecha_cambio DESC;

-- Contar cambios por pedido
SELECT numero_pedido, COUNT(*) as cantidad_cambios 
FROM cambios_estado GROUP BY numero_pedido;

-- Ver estructura de la tabla
DESCRIBE cambios_estado;
```

---

## ⚠️ Gestión de Errores

El servicio retorna los siguientes códigos HTTP y mensajes de error:

### 400 - Bad Request (Validación Fallida)
Se lanza cuando los datos del request no cumplen las restricciones:
- `numeroPedido` es null
- `estadoNuevo` está en blanco o es null

**Ejemplo**:
```json
{
  "timestamp": "2026-05-11T10:25:00",
  "status": 400,
  "error": "Bad Request",
  "trace": "[ValidationError...]"
}
```

### 404 - Not Found (Recurso No Encontrado)
Se lanza cuando se consulta un pedido que no tiene cambios de estado registrados.
- Lanzado por `EstadoService` mediante `ResourceNotFoundException`
- Ocurre en:
  - `GET /api/estados/pedido/{numeroPedido}`
  - `GET /api/estados/pedido/{numeroPedido}/ultimo`

**Ejemplo**:
```json
{
  "timestamp": "2026-05-11T10:26:00",
  "status": 404,
  "error": "Not Found",
  "message": "No se encontraron cambios de estado para el pedido con numero: 99999"
}
```

### 409 - Conflict (Conflicto de Negocio)
Actualmente no se valida conflicto de transiciones en la lógica base, pero puede agregarse:
- Ejemplo: Intentar pasar de "CANCELADO" a cualquier otro estado

### 500 - Internal Server Error (Error Interno)
Errores no controlados:
- Problemas de conexión a BD
- Excepciones no manejadas en el código

**Ejemplo**:
```json
{
  "timestamp": "2026-05-11T10:27:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Error al acceder a la base de datos"
}
```

---

## 🚀 Cómo Ejecutar el Microservicio

### Requisitos Previos
- **JDK 25** instalado
- **Maven 3.8.1+** instalado (o usar `mvnw.cmd` del proyecto)
- **Puerto 8086** disponible

### Paso 1: Compilar el Proyecto
```bash
cd C:\Users\sebyv\IdeaProjects\estado-service
mvn clean compile
```

⏱️ Tiempo estimado: 10-15 segundos (primera vez: 30-60 segundos)

### Paso 2: Ejecutar en Modo Desarrollo
```bash
mvn spring-boot:run
```

**Salida esperada**:
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v4.0.5)

2026-05-11T03:25:37.000-04:00  INFO ... : Starting EstadoServiceApplication
...
2026-05-11T03:25:40.000-04:00  INFO ... : Started EstadoServiceApplication in 2.5 seconds
2026-05-11T03:25:40.000-04:00  INFO ... : H2 console available at '/h2-console'
```

### Paso 3: Compilar y Empaquetar (JAR)
```bash
mvn clean package -DskipTests
```

**Salida**: `target/estado-service-0.0.1-SNAPSHOT.jar`

### Paso 4: Ejecutar el JAR
```bash
java -jar target/estado-service-0.0.1-SNAPSHOT.jar
```

### Detener el Servicio
- Presiona **Ctrl + C** en la terminal

---

## 🔗 Integración con Otros Microservicios

### Arquitectura del Sistema

El sistema está compuesto por los siguientes microservicios:

```
┌─────────────────────────────────────────────────────────┐
│                     API Gateway                         │
└──────────┬──────────────┬──────────────┬────────────────┘
           │              │              │
    ┌──────▼────┐  ┌─────▼─────┐  ┌────▼──────┐
    │ cliente   │  │ producto  │  │  pedido   │
    │ service   │  │  service  │  │  service  │
    │ (8081)    │  │  (8082)   │  │  (8083)   │
    └───────────┘  └───────────┘  └────┬──────┘
                                        │
                                   ┌────▼──────┐
                                   │  estado   │
                                   │  service  │
                                   │  (8086)   │
                                   └───────────┘
```

### Cómo estado-service se Conecta

**estado-service** es consumido por:
1. **pedido-service**: Consulta el estado de un pedido antes de permitir ciertas operaciones
2. **Clientes/Frontend**: Consultan el historial de estados

**estado-service** actualmente no hace llamadas a otros servicios, pero puede integrarse usando **OpenFeign** si es necesario en el futuro.

### Ejemplo Futuro: Llamada a estado-service desde pedido-service
```java
@FeignClient(name = "estado-service", url = "http://localhost:8086")
public interface EstadoServiceClient {
    
    @GetMapping("/api/estados/pedido/{numeroPedido}/ultimo")
    CambioEstadoResponse obtenerUltimoEstado(@PathVariable Long numeroPedido);
    
    @PostMapping("/api/estados")
    CambioEstadoResponse registrarCambio(@RequestBody CambioEstadoRequest request);
}
```

---

## 📋 Orden de Ejecución de Microservicios

Para un correcto funcionamiento del ecosistema de microservicios, ejecuta los servicios en este orden:

| Orden | Servicio | Puerto | Comando |
|-------|----------|--------|---------|
| 1 | cliente-service | 8081 | `mvn spring-boot:run` |
| 2 | producto-service | 8082 | `mvn spring-boot:run` |
| 3 | **estado-service** | **8086** | `mvn spring-boot:run` ← Este servicio |
| 4 | pedido-service | 8083 | `mvn spring-boot:run` |

**Nota:** Aunque `estado-service` es "stateless" y puede ejecutarse en cualquier orden, es recomendable iniciarlo antes que `pedido-service` para evitar errores de conexión al intentar consultar estados.

---

## 🧪 Validación Rápida de Funcionalidad

Una vez ejecutado el servicio, prueba con estos comandos en PowerShell:

```powershell
# Test 1: Crear un cambio de estado
$body = @{
    numeroPedido = 1001
    estadoAnterior = $null
    estadoNuevo = "CREADO"
    observacion = "Pedido creado"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:/api/estados" `
    -Method POST `
    -ContentType "application/json" `
    -Body $body

# Test 2: Consultar historial
Invoke-RestMethod -Uri "http://localhost:8086/api/estados/pedido/1001" `
    -Method GET

# Test 3: Obtener último estado
Invoke-RestMethod -Uri "http://localhost:8086/api/estados/pedido/1001/ultimo" `
    -Method GET

# Test 4: H2 Console (abre en navegador)
Start-Process "http://localhost:8086/h2-console"
```

---

## 📚 Tecnologías y Frameworks

| Componente | Versión | Propósito |
|-----------|---------|----------|
| Spring Boot | 4.0.5 | Framework de desarrollo automático |
| Spring Cloud | 2025.1.1 | Orquestación de microservicios |
| JPA/Hibernate | 7.2.7 | ORM y persistencia |
| H2 | 2.4.240 | Base de datos en memoria |
| Lombok | Latest | Reducción de boilerplate |
| Jakarta Validation | Latest | Validación de datos |
| Java | 25 | Lenguaje de programación |

---

## 📞 Contacto y Soporte

Para reportar errores o sugerencias, contacta con el equipo de desarrollo:
- **Equipo**: DuoCUC Development Team
- **Sistema**: Sistema de Gestión de Pedidos
- **Versión**: 0.0.1-SNAPSHOT

---

## 📄 Licencia

Este proyecto es parte del Sistema de Gestión de Pedidos de DuoCUC y está sujeto a las políticas de licencia de la institución.

---

**Última actualización**: 11 de mayo de 2026

