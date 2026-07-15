# Plan De Cierre De Feedback

Este plan resume observaciones corregidas para la entrega final transversal DSY1103. El proyecto es individual y fue desarrollado por Sebastian Valderrama.

| ID | Observacion o feedback | Accion realizada | Archivo(s) modificados | Evidencia de verificacion | Estado |
|---|---|---|---|---|---|
| FB-01 | Tests solo en algunos servicios | Se ampliaron pruebas a los 10 microservicios de dominio | `src/test/java` por microservicio | 452 tests documentados y reportes JaCoCo | Cerrado |
| FB-02 | Cobertura insuficiente | Se agrego JaCoCo y se validaron coberturas sobre 80% | `pom.xml`, tests y reportes `target/site/jacoco` | 10 microservicios sobre 80% line coverage | Cerrado |
| FB-03 | Swagger incompleto | Se completo Swagger/OpenAPI en microservicios de dominio | `OpenApiConfig`, controllers y documentacion | `/swagger-ui/index.html` y `/v3/api-docs` por servicio | Cerrado |
| FB-04 | Gateway con rutas limitadas | Se centralizaron rutas `/api/**` en API Gateway con `lb://` | `api-gateway/src/main/resources/application.yml` | Gateway Render valida `/api/clientes`, `/api/productos`, `/api/pedidos` | Cerrado |
| FB-05 | Feign y errores remotos poco cubiertos | Se agregaron pruebas para clientes Feign y errores remotos | Tests de pedido, metrica, fabricacion y despacho | Tests service/controller pasando | Cerrado |
| FB-06 | Docker local incompleto | Se valido Docker Compose como demo minima con Eureka real | `docker-compose.yml`, README y docs | Demo levanta discovery, gateway, cliente, producto, pedido y estado | Cerrado para demo minima |
| FB-07 | Render pendiente o parcial | Se desplego y valido el flujo principal en Render | `README.md`, `docs/render-deploy.md`, `.http` | Discovery, Gateway, cliente, producto, estado y pedido con URLs reales | Cerrado para flujo tecnico principal |
| FB-08 | Eureka/discovery | Se agrego `discovery-server`, clientes Eureka y Gateway con `lb://` | `discovery-server/`, configuraciones Eureka, Gateway | Eureka muestra servicios `UP`; Gateway enruta cliente/producto/pedido | Cerrado |
| FB-09 | Errores 503/500 en Gateway Render | Se documento causa y solucion: servicios dormidos, Eureka vacio, hostname interno | `docs/render-deploy.md` | Uso de `EUREKA_INSTANCE_HOSTNAME`, securePort 443 y health checks | Cerrado documental y operativo |
| FB-10 | Falta matriz de requerimientos | Se actualizo matriz con RF/RNF, evidencias y pruebas | `docs/matriz-requerimientos.md` | Cada requerimiento implementado tiene evidencia y prueba asociada | Cerrado |
| FB-11 | Falta documentacion funcional | Se documento problema, actores, reglas, flujos y datos de prueba | `docs/documentacion-funcional.md` | Flujo cliente -> producto -> pedido -> estado -> gateway documentado | Cerrado |
| FB-12 | Falta documentacion tecnica desde cero | Se agrego guia tecnica con local, Docker, Render, Swagger y pruebas | `docs/documentacion-tecnica.md` | Seccion `Ejecucion desde cero` y comandos Windows/Linux | Cerrado |
| FB-13 | Falta evidencia REST remota | Se agregaron variables y requests Render al archivo `.http` | `docs/pruebas-rest/casa-de-la-impresion.http` | Requests Gateway, directos, invalidos y recursos inexistentes | Cerrado |
| FB-14 | Falta presentacion tecnica de defensa | Se aclara que el proyecto es individual y se crea presentacion tecnica individual | `docs/presentacion-defensa-grupal.md` | Documento indica modalidad individual desde el inicio | Cerrado |
| FB-15 | Falta defensa individual | Se actualiza defensa individual con rol integral y dificultad Render/Eureka | `docs/defensa-individual/valderrama-sebastian.md` | Tareas, commits, endpoints, reglas y evidencias pendientes | Cerrado |
| FB-16 | No exponer credenciales | Se usan placeholders y `.env.example` seguro | `.env.example`, README, docs | Sin secretos reales versionados | Cerrado |
| FB-17 | Evidencias/screenshots no incorporadas | Se crea estructura para adjuntar capturas finales | `docs/evidencias/**/README.md` | Carpetas versionables listas para informe final | Pendiente de adjuntar capturas |

## Estado Render Final

Render principal esta implementado y validado para el flujo tecnico principal: `discovery-server`, `api-gateway`, `cliente-service`, `producto-service`, `estado-service` y `pedido-service`.

| Evidencia | URL |
|---|---|
| Discovery Server | `https://discovery-server-gjd0.onrender.com` |
| Eureka apps | `https://discovery-server-gjd0.onrender.com/eureka/apps` |
| Gateway health | `https://api-gateway-c9qz.onrender.com/actuator/health` |
| Gateway clientes | `https://api-gateway-c9qz.onrender.com/api/clientes` |
| Gateway productos | `https://api-gateway-c9qz.onrender.com/api/productos` |
| Gateway pedidos | `https://api-gateway-c9qz.onrender.com/api/pedidos` |
| Swagger cliente | `https://cliente-service-6yfy.onrender.com/swagger-ui/index.html` |
| Swagger producto | `https://producto-service-ulv6.onrender.com/swagger-ui/index.html` |
| Swagger estado | `https://estado-service.onrender.com/swagger-ui/index.html` |
| Swagger pedido | `https://pedido-service-47kn.onrender.com/swagger-ui/index.html` |

## Limitaciones Documentadas

Render Free puede dormir servicios por inactividad; antes de la demo se deben despertar con `/actuator/health`. H2 remoto se usa como demo temporal, no como base productiva persistente. Las capturas finales quedan pendientes de incorporar en `docs/evidencias/` o informe final.
