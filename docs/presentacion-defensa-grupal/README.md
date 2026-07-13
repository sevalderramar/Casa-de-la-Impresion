# Presentacion Defensa Grupal

## Nombre E Integrante

Proyecto: Casa de la Impresion - Microservicios.

Integrante: Sebastian Valderrama.

Proyecto desarrollado de forma individual por Sebastian Valderrama; por lo tanto, la evidencia de trabajo se concentra en commits tecnicos individuales y documentacion asociada.

## Problema

Casa de la Impresion requiere controlar pedidos, clientes, productos, estados, fabricacion, despacho y metricas sin concentrar toda la logica en una sola aplicacion. Tambien necesita trazabilidad, seguridad base y evidencia tecnica para mantener el sistema.

## Solucion

Se implemento una arquitectura Spring Boot con 10 microservicios de dominio, un API Gateway central y un Discovery Server Eureka real. El sistema expone APIs REST documentadas con Swagger/OpenAPI, pruebas automatizadas, JaCoCo y una demo Docker minima del flujo principal.

## Alcance

| Incluido | No incluido o pendiente |
|---|---|
| 10 microservicios de dominio | Render completo con Gateway y microservicios publicados |
| API Gateway central | Docker Compose con todos los servicios |
| Discovery Server Eureka | Base de datos productiva externa |
| JWT implementado | Endurecimiento final de seguridad productiva |
| Swagger en 10 microservicios |  |
| 452 tests pasando |  |

## Arquitectura

El `discovery-server` publica Eureka en `http://localhost:8761`. Los microservicios y el Gateway se registran como clientes Eureka. El Gateway recibe peticiones por `http://localhost:8080` y enruta con `lb://` a servicios registrados, manteniendo los prefijos `/api/**`. El servicio central del dominio es `pedido-service`, que se apoya en clientes, productos y estados. Otros servicios complementan el ciclo: fabricacion, despacho, metricas, transportistas, logs y autenticacion.

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
| `discovery-server` | Eureka Server para registro y descubrimiento |

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
2. Gateway consulta Eureka y enruta por path `/api/**` usando `lb://`.
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

El Gateway contiene rutas para los 10 dominios principales: auth, pedidos, clientes, productos, despachos, fabricacion, estados, metricas, transportistas y logs. Las rutas usan `lb://auth-service`, `lb://pedido-service`, `lb://cliente-service`, `lb://producto-service`, `lb://despacho-service`, `lb://fabricacion-service`, `lb://estado-service`, `lb://metrica-service`, `lb://transportista-service` y `lb://log-service`.

## Docker

Docker Compose corresponde a una demo minima validada del flujo principal con Eureka. Incluye `discovery-server`, Gateway, pedido, cliente, producto y estado. En la validacion, Eureka registro `API-GATEWAY`, `CLIENTE-SERVICE`, `PRODUCTO-SERVICE`, `PEDIDO-SERVICE` y `ESTADO-SERVICE`; los endpoints `/actuator/health`, `/api/clientes`, `/api/productos` y `/api/pedidos` respondieron 200 por Gateway. No representa el despliegue completo de los 10 microservicios.

## Render

Render esta avanzado parcialmente: `discovery-server` ya fue desplegado y validado en `https://discovery-server-gjd0.onrender.com`, con endpoint Eureka `https://discovery-server-gjd0.onrender.com/eureka/`. El Gateway y los microservicios siguen pendientes de publicacion con URLs reales.

## Feedback Corregido

| Feedback | Correccion |
|---|---|
| Tests insuficientes | 452 tests pasando |
| Swagger incompleto | Swagger en los 10 microservicios |
| Falta evidencia de cobertura | JaCoCo en 10 microservicios |
| Gateway limitado | Rutas centralizadas documentadas |
| Discovery formal | Eureka Server agregado y Gateway actualizado a `lb://` |
| Docker Compose con Eureka | Demo minima validada con discovery-server y Gateway `lb://` |
| Documentacion final faltante | Carpeta `docs/` creada |
| Falta coleccion REST | Archivo `.http` creado |

## Dificultades

| Dificultad | Solucion |
|---|---|
| Coordinar muchos microservicios | Separar responsabilidades y probar por modulo |
| Errores remotos Feign | Agregar manejo y pruebas de fallos |
| Cobertura en servicios con seguridad | Probar filtros, utilidades JWT y controllers |
| Swagger uniforme | Crear `OpenApiConfig` y anotaciones por servicio |
| Render y discovery | Agregar Eureka Server y documentar `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` con placeholders |

## Distribucion De Trabajo Individual

Todas las areas fueron abordadas por el mismo estudiante.

| Area | Responsabilidades documentadas |
|---|---|
| Arquitectura | Definicion de microservicios, responsabilidades y flujo principal |
| Backend | Implementacion y cierre de servicios de dominio |
| Gateway | Configuracion de rutas centralizadas con `lb://` y Eureka |
| Pruebas | Suite automatizada, JaCoCo y evidencias de cobertura |
| Swagger | Documentacion OpenAPI de los 10 microservicios |
| Documentacion | README, documentos formales, pruebas REST y preparacion de defensa |
