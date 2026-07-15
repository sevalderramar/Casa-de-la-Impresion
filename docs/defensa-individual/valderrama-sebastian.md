# Defensa Individual - Sebastian Valderrama

## Identificación

| Campo | Valor |
|---|---|
| Estudiante | Sebastian Valderrama |
| Modalidad | Proyecto individual |
| Rol | Desarrollador único y responsable integral del proyecto |
| Repositorio | `https://github.com/sevalderramar/Casa-de-la-Impresion` |

Mi sistema administra el ciclo completo de pedidos de Casa de la Impresión: desde el registro de clientes y productos, pasando por la creación del pedido, el cambio y seguimiento de estado, la fabricación, el despacho, las métricas operacionales y el registro de auditoría (logs). Lo separé en microservicios para que cada dominio se pueda mantener, probar y desplegar de forma independiente.

Definí 14 RF documentados en `docs/matriz-requerimientos.md`: autenticar usuarios (RF-01), gestionar usuarios (RF-02), gestionar clientes (RF-03), gestionar productos (RF-04), crear/consultar pedidos (RF-05), cambiar estado (RF-06), consultar historial (RF-07), gestión de despacho (RF-08), fabricación (RF-09), métricas (RF-10), transportistas (RF-11), logs (RF-12) y autenticación (RF-14). Cada uno tiene su servicio responsable y sus endpoints principales.

## Participación Por Módulo

Trabajé en todos los módulos del proyecto: `discovery-server`, `api-gateway`, `auth-service`, `cliente-service`, `producto-service`, `pedido-service`, `estado-service`, `despacho-service`, `fabricacion-service`, `metrica-service`, `transportista-service` y `log-service`.

| Área | Tareas realizadas |
|---|---|
| Arquitectura | Separación en microservicios, Eureka Discovery y Gateway |
| Backend | Endpoints REST, DTOs, servicios, repositorios y configuración |
| Integración | Feign/REST entre pedido, cliente, producto y estado |
| Seguridad | JWT por variable de entorno, filtros y configuración demo |
| Documentación | README, docs técnicos, funcionales, matriz, Render y defensa |
| Pruebas | JUnit, Mockito, JaCoCo y pruebas REST manuales |
| Despliegue | Docker Compose local y Render para el flujo principal |

## Commits Relevantes

| Hash | Commit | Aporte |
|---|---|---|
| `30344ca` | `docs: actualizar despliegue render y validacion tecnica` | Documentación final de Render y validación técnica |
| `7faebb0` | `fix: usar PORT en servicios base para render` | Soporte de variable `PORT` para Render |
| `ff8fa9f` | `docs: documentar discovery server en render` | URL real de Eureka en Render |
| `b3fe782` | `fix: usar PORT en discovery server para render` | Discovery compatible con Render |
| `8b33a8d` | `docs: documentar validacion docker compose con eureka` | Evidencia de Docker/Eureka local |
| `8f27e7c` | `chore: configurar docker compose con eureka discovery` | Demo Docker mínima con Eureka |
| `0a5377d` | `feat: enrutar gateway mediante eureka` | Gateway con rutas `lb://` |
| `53d688a` | `feat: registrar servicios restantes en eureka` | Microservicios como clientes Eureka |
| `b7a3477` | `feat: agregar discovery server con eureka` | Servidor Eureka |

## Feedback Corregido

| Feedback | Corrección aplicada | Evidencia |
|---|---|---|
| Poca cobertura | Tests ampliados en 10 microservicios | 452 tests documentados, JaCoCo sobre 80% |
| Swagger incompleto | Swagger/OpenAPI en microservicios de dominio | `/swagger-ui/index.html`, `/v3/api-docs` |
| Falta Discovery | Eureka Server y clientes Eureka | `discovery-server`, Eureka apps en Render |
| Gateway limitado | Rutas `lb://` centralizadas | `api-gateway/application.yml` |
| Render pendiente | Flujo principal desplegado | Discovery, Gateway, cliente, producto, estado, pedido |
| Evidencias faltantes | Estructura `docs/evidencias/` creada | READMEs de evidencia |

## Endpoints Y Flujos Asociados

| Flujo | Endpoint o evidencia |
|---|---|
| Eureka Render | `https://discovery-server-gjd0.onrender.com/eureka/apps` |
| Gateway clientes | `https://api-gateway-c9qz.onrender.com/api/clientes` |
| Gateway productos | `https://api-gateway-c9qz.onrender.com/api/productos` |
| Gateway pedidos | `https://api-gateway-c9qz.onrender.com/api/pedidos` |
| Swagger cliente | `https://cliente-service-6yfy.onrender.com/swagger-ui/index.html` |
| Swagger producto | `https://producto-service-ulv6.onrender.com/swagger-ui/index.html` |
| Swagger estado | `https://estado-service.onrender.com/swagger-ui/index.html` |
| Swagger pedido | `https://pedido-service-47kn.onrender.com/swagger-ui/index.html` |

## Pruebas Unitarias Y REST

| Evidencia | Resultado |
|---|---|
| Suite documentada | 452 tests |
| Cobertura | 10 microservicios sobre 80% |
| REST local/remoto | `docs/pruebas-rest/casa-de-la-impresion.http` |
| Gateway remoto | Clientes, productos y pedidos validados |
| Estado remoto | `/actuator/health` validado |

## Regla De Negocio Que Domino

Un pedido debe relacionarse con un cliente y productos existentes. El flujo principal valida cliente y producto, crea el pedido y registra/consulta su estado. Esta regla conecta `pedido-service`, `cliente-service`, `producto-service` y `estado-service`.

## Relación De BD Que Domino

| Relación | Explicación |
|---|---|
| Pedido - Cliente | El pedido referencia un cliente existente |
| Pedido - Producto | El pedido contiene productos y cantidades |
| Pedido - Estado | El pedido mantiene historial de estados |
| Pedido - Despacho | El despacho está asociado a un número de pedido |
| Servicio - Log | Las operaciones son trazables por servicio y resultado |

## Comunicación Entre Servicios Que Domino

El usuario entra por el API Gateway. El Gateway consulta a Eureka y enruta con `lb://`. `pedido-service` usa variables Feign para llamar a cliente, producto y estado en Render. Eureka requiere un hostname público y `securePort` 443 para evitar que se registren hostnames internos de Render.

## Dificultad Técnica Personal Y Solución

| Dificultad | Solución |
|---|---|
| Gateway devolvía 503/500 en Render | Despertar los servicios, revisar Eureka y configurar hostnames públicos con puerto seguro 443 |
| Servicios dormidos en Render Free | Documentar el flujo de "despertar" con `/actuator/health` antes de la demo |
| Eureka registraba direcciones internas | Usar `EUREKA_INSTANCE_HOSTNAME`, `EUREKA_INSTANCE_SECURE_PORT_ENABLED=true`, `EUREKA_INSTANCE_NON_SECURE_PORT_ENABLED=false`, `EUREKA_INSTANCE_SECURE_PORT=443` |
| Persistencia remota en H2 | Documentar H2 como demo temporal, no productiva |