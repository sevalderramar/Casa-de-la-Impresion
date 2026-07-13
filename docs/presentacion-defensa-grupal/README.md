# Presentacion Defensa Grupal

## Nombre E Integrante

Proyecto: Casa de la Impresion - Microservicios.

Integrante: Sebastian Valderrama.

Proyecto desarrollado de forma individual por Sebastian Valderrama; por lo tanto, la evidencia de trabajo se concentra en commits tecnicos individuales y documentacion asociada.

## Problema

Casa de la Impresion requiere controlar pedidos, clientes, productos, estados, fabricacion, despacho y metricas sin concentrar toda la logica en una sola aplicacion. Tambien necesita trazabilidad, seguridad base y evidencia tecnica para mantener el sistema.

## Solucion

Se implemento una arquitectura Spring Boot con 10 microservicios de dominio y un API Gateway central. El sistema expone APIs REST documentadas con Swagger/OpenAPI, pruebas automatizadas, JaCoCo y una demo Docker minima del flujo principal.

## Alcance

| Incluido | No incluido o pendiente |
|---|---|
| 10 microservicios de dominio | Eureka real |
| API Gateway central | Render con URLs publicas reales confirmadas |
| JWT implementado | Docker Compose con todos los servicios |
| Swagger en 10 microservicios | Base de datos productiva externa |
| 452 tests pasando | Endurecimiento final de seguridad productiva |

## Arquitectura

El Gateway recibe peticiones por `http://localhost:8080` y enruta a servicios internos. El servicio central del dominio es `pedido-service`, que se apoya en clientes, productos y estados. Otros servicios complementan el ciclo: fabricacion, despacho, metricas, transportistas, logs y autenticacion.

## Microservicios

| Servicio | Rol en la solucion |
|---|---|
| `auth-service` | Autenticacion JWT y usuarios |
| `cliente-service` | Gestion de clientes |
| `producto-service` | Catalogo de productos |
| `pedido-service` | Gestion principal de pedidos |
| `estado-service` | Cambios e historial de estado |
| `despacho-service` | Entrega y seguimiento |
| `fabricacion-service` | Ordenes de fabricacion |
| `metrica-service` | Reportes y metricas |
| `transportista-service` | Transportistas disponibles |
| `log-service` | Auditoria de eventos |
| `api-gateway` | Rutas centralizadas |

## Flujo Funcional

1. Se registra cliente.
2. Se registra o consulta producto.
3. Se crea pedido.
4. Se cambia estado y se consulta historial.
5. Se crea orden de fabricacion.
6. Se registra despacho.
7. Se consultan metricas y logs.

## Flujo Tecnico

1. Cliente HTTP llama al Gateway.
2. Gateway enruta por path `/api/**`.
3. Microservicio procesa request y valida datos.
4. Si requiere datos remotos, usa REST/OpenFeign.
5. Responde JSON y, cuando corresponde, registra eventos o metricas.

## Seguridad

JWT esta implementado con filtros y utilidades de seguridad. Para la demo local, varios endpoints se mantienen accesibles o con `permitAll` para facilitar evaluacion, healthchecks, H2 y Swagger. En produccion se recomienda endurecer reglas de acceso por rol.

## Pruebas

Resultado final: 452 tests pasando. Hay pruebas de controllers, services, handlers, JWT, Feign y DTOs. Los 10 microservicios tienen JaCoCo y superan 80% de cobertura de lineas.

## Swagger

Los 10 microservicios exponen Swagger UI y OpenAPI JSON. Ver tabla completa de Swagger en `README.md` y `docs/documentacion-tecnica.md`.

## Gateway

El Gateway contiene rutas para los 10 dominios principales: auth, pedidos, clientes, productos, despachos, fabricacion, estados, metricas, transportistas y logs.

## Docker

Docker Compose esta definido como demo minima del flujo principal con Gateway, pedido, cliente, producto y estado. No representa el despliegue completo de los 10 microservicios.

## Render

Render esta documentado como configuracion preparada y pendiente de publicacion final. No se deben inventar URLs publicas; si se despliega antes de AVA, se deben reemplazar los placeholders por URLs reales.

## Feedback Corregido

| Feedback | Correccion |
|---|---|
| Tests insuficientes | 452 tests pasando |
| Swagger incompleto | Swagger en los 10 microservicios |
| Falta evidencia de cobertura | JaCoCo en 10 microservicios |
| Gateway limitado | Rutas centralizadas documentadas |
| Documentacion final faltante | Carpeta `docs/` creada |
| Falta coleccion REST | Archivo `.http` creado |

## Dificultades

| Dificultad | Solucion |
|---|---|
| Coordinar muchos microservicios | Separar responsabilidades y probar por modulo |
| Errores remotos Feign | Agregar manejo y pruebas de fallos |
| Cobertura en servicios con seguridad | Probar filtros, utilidades JWT y controllers |
| Swagger uniforme | Crear `OpenApiConfig` y anotaciones por servicio |
| Render y discovery | Documentar rutas estaticas/env y placeholders |

## Distribucion De Trabajo Individual

Todas las areas fueron abordadas por el mismo estudiante.

| Area | Responsabilidades documentadas |
|---|---|
| Arquitectura | Definicion de microservicios, responsabilidades y flujo principal |
| Backend | Implementacion y cierre de servicios de dominio |
| Gateway | Configuracion de rutas centralizadas |
| Pruebas | Suite automatizada, JaCoCo y evidencias de cobertura |
| Swagger | Documentacion OpenAPI de los 10 microservicios |
| Documentacion | README, documentos formales, pruebas REST y preparacion de defensa |
