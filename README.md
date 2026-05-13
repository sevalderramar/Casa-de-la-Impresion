# Estado actual del proyecto

## Arquitectura implementada

El proyecto actualmente se encuentra desarrollado bajo arquitectura de microservicios utilizando Spring Boot.

## Microservicios implementados

| Microservicio    | Estado       | Puerto |
| ---------------- | ------------ | ------ |
| cliente-service  | Implementado | 8081   |
| producto-service | Implementado | 8082   |
| pedido-service   | Implementado | 8083   |
| despacho-service | Implementado | 8084   |
| estado-service   | Implementado | 8086   |

## Microservicios pendientes

| Microservicio         | Estado    |
| --------------------- | --------- |
| fabricacion-service   | Pendiente |
| transportista-service | Pendiente |
| metrica-service       | Pendiente |
| log-service           | Pendiente |
| auth-service          | Pendiente |

## Lessons implementadas

### Lesson 12

* Relaciones JPA
* OneToMany
* ManyToOne

### Lesson 13

* Historial de estados
* CambioEstado
* Auditoría básica

### Lesson 14

* Arquitectura de microservicios
* Comunicación entre servicios
* FeignClient
* Bases de datos separadas

### Lesson 15

* Flyway Migrations
* ddl-auto=validate
* Migraciones SQL versionadas

## Tecnologías implementadas

* Java 25
* Spring Boot 4.0.5
* Maven
* FeignClient
* H2 Database
* Flyway
* Lombok
* Spring Data JPA
* Validation

## Estado general

El sistema actualmente permite:

* Gestión de clientes
* Gestión de productos
* Gestión de pedidos
* Gestión de estados
* Gestión de despachos
* Historial de cambios de estado
* Comunicación entre microservicios mediante FeignClient
* Migraciones SQL mediante Flyway

## Estado del desarrollo

El proyecto se encuentra funcional y estable hasta la Lesson 15.
Actualmente se continúa el desarrollo de los microservicios restantes y mejoras de arquitectura.
