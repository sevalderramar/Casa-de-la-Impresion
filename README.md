# Casa de la Impresión — Arquitectura de Microservicios

Sistema de gestión de pedidos basado en arquitectura de microservicios desarrollado con Spring Boot 4.0.5 y Java 21.

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

## Variable de entorno requerida

```powershell
$env:JWT_SECRET="clave-super-secreta"
```

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

Cada microservicio posee su propia consola H2.

Ejemplo:

```text
http://localhost:8081/h2-console
```

Usuario:

```text
sa
```

Password:

```text
(vacío)
```

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
