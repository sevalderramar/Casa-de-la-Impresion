# Casa de la Impresión — Arquitectura de Microservicios

Sistema de gestión de pedidos con arquitectura de microservicios, Spring Boot, JWT, OpenFeign, H2, Swagger/OpenAPI y preparación para Docker/API Gateway.

---

# Arquitectura

El sistema está compuesto por múltiples microservicios independientes que se comunican mediante OpenFeign.

| Microservicio | Puerto | Responsabilidad |
|---|---|---|
| auth-service | 8090 | Autenticación JWT y usuarios |
| pedido-service | 8081 | Gestión de pedidos |
| cliente-service | 8082 | Gestión de clientes |
| producto-service | 8083 | Gestión de productos |
| despacho-service | 8084 | Gestión de despachos |
| fabricacion-service | 8085 | Gestión de fabricación |
| estado-service | 8086 | Historial y estados |
| metrica-service | 8087 | Métricas del sistema |
| transportista-service | 8088 | Gestión de transportistas |
| log-service | 8089 | Registro de logs |

---

# Tecnologías utilizadas

- Java 21
- Spring Boot 4.0.5
- Maven
- Spring Data JPA
- Spring Security
- JWT
- OpenFeign
- H2 Database
- Lombok

---

# Seguridad JWT

La autenticación del sistema se basa en JWT.

El token es generado por:

```text
/auth/login
```

Los demás microservicios validan el token mediante filtros JWT.

## Variables de entorno requeridas

### JWT_SECRET (REQUERIDA)

```powershell
$env:JWT_SECRET="<TU_JWT_SECRET_BASE64>"
```

Debe ser una cadena Base64 de al menos 32 caracteres para garantizar seguridad en producción.

### URLs de Feign (Para microservicios con dependencias)

Cada servicio que usa OpenFeign debe configurar las URLs de los servicios dependientes:

```powershell
$env:CLIENTE_SERVICE_URL="http://localhost:8082"
$env:PRODUCTO_SERVICE_URL="http://localhost:8083"
$env:PEDIDO_SERVICE_URL="http://localhost:8081"
$env:ESTADO_SERVICE_URL="http://localhost:8086"
$env:DESPACHO_SERVICE_URL="http://localhost:8084"
$env:FABRICACION_SERVICE_URL="http://localhost:8085"
$env:METRICA_SERVICE_URL="http://localhost:8087"
$env:LOG_SERVICE_URL="http://localhost:8089"
```

Estas pueden ser configuradas en `application-h2.properties` o `application.properties`.

---

# Profiles

## Desarrollo

```properties
spring.profiles.active=h2
```

## Producción

```properties
spring.profiles.active=prod
```

---

# Ejecución

## Compilar

```powershell
.\mvnw clean compile
```

## Ejecutar

```powershell
.\mvnw spring-boot:run
```

---

# H2 Console

Cada microservicio posee su propia consola H2 para gestionar la base de datos embebida.

Para acceder, usa:
- **Usuario**: `sa`
- **Password**: (vacío)

Ejemplo por servicio:
| Servicio | URL |
|---|---|
| auth-service | http://localhost:8090/h2-console |
| pedido-service | http://localhost:8081/h2-console |
| cliente-service | http://localhost:8082/h2-console |
| producto-service | http://localhost:8083/h2-console |
| despacho-service | http://localhost:8084/h2-console |
| fabricacion-service | http://localhost:8085/h2-console |
| estado-service | http://localhost:8086/h2-console |
| metrica-service | http://localhost:8087/h2-console |
| transportista-service | http://localhost:8088/h2-console |
| log-service | http://localhost:8089/h2-console |

---

# Swagger/OpenAPI

Documentación interactiva de APIs disponible en los siguientes servicios:
| Servicio | Swagger UI | API Docs |
|---|---|---|
| pedido-service | http://localhost:8081/swagger-ui/index.html | http://localhost:8081/v3/api-docs |
| cliente-service | http://localhost:8082/swagger-ui/index.html | http://localhost:8082/v3/api-docs |
| producto-service | http://localhost:8083/swagger-ui/index.html | http://localhost:8083/v3/api-docs |
| despacho-service | http://localhost:8084/swagger-ui/index.html | http://localhost:8084/v3/api-docs |
| fabricacion-service | http://localhost:8085/swagger-ui/index.html | http://localhost:8085/v3/api-docs |
| metrica-service | http://localhost:8087/swagger-ui/index.html | http://localhost:8087/v3/api-docs |

---

# Comunicación entre Microservicios (OpenFeign)

Los microservicios se comunican mediante OpenFeign usando URLs configurables desde variables de entorno. Cada servicio que tiene dependencias de otros debe usar la siguiente estructura en `application.properties`:

```properties
# Ejemplo en application-h2.properties
services.cliente.url=${CLIENTE_SERVICE_URL:http://localhost:8082}
services.producto.url=${PRODUCTO_SERVICE_URL:http://localhost:8083}
services.pedido.url=${PEDIDO_SERVICE_URL:http://localhost:8081}
services.estado.url=${ESTADO_SERVICE_URL:http://localhost:8086}
services.despacho.url=${DESPACHO_SERVICE_URL:http://localhost:8084}
services.fabricacion.url=${FABRICACION_SERVICE_URL:http://localhost:8085}
services.transportista.url=${TRANSPORTISTA_SERVICE_URL:http://localhost:8088}
services.log.url=${LOG_SERVICE_URL:http://localhost:8089}
```

Los valores por defecto apuntan a `localhost` en desarrollo. Para producción, configurar las variables de entorno con las URLs reales de los servicios.

---

# Flujo general

1. Usuario inicia sesión en auth-service
2. auth-service genera JWT
3. El cliente envía Authorization Bearer Token
4. Los microservicios validan el token
5. Los servicios se comunican mediante OpenFeign

---

# Orden recomendado para demo

1. auth-service
2. cliente-service
3. producto-service
4. pedido-service
5. estado-service
6. despacho-service
7. fabricacion-service
8. metrica-service
9. transportista-service
10. log-service

---

# Estructura general

```text
controller
service
repository
dto
entity
exception
handler
config
client
```
