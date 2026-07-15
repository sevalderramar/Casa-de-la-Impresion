# Casa de la Impresion - Microservicios

Sistema backend para la gestion de pedidos de Casa de la Impresion. El proyecto esta construido con microservicios Java/Spring Boot, Eureka Discovery, API Gateway, Swagger/OpenAPI, pruebas unitarias con JUnit/Mockito/JaCoCo, Docker Compose local y despliegue remoto en Render para la demo tecnica principal.

El proyecto fue desarrollado de forma individual por Sebastian Valderrama. La defensa tecnica se documenta como defensa individual en `docs/defensa-individual/valderrama-sebastian.md`.

Repositorio: https://github.com/sevalderramar/Casa-de-la-Impresion

## Arquitectura

La solucion separa responsabilidades en 10 microservicios de dominio, un `discovery-server` Eureka y un `api-gateway`. Los microservicios se registran como clientes Eureka. El Gateway tambien se registra en Eureka y enruta hacia los servicios usando URIs `lb://`.

| Servicio | Puerto local | Responsabilidad | URL Render |
|---|---:|---|---|
| `discovery-server` | 8761 | Servidor Eureka para service discovery | `https://discovery-server-gjd0.onrender.com` |
| `api-gateway` | 8080 | Entrada central y rutas hacia microservicios | `https://api-gateway-c9qz.onrender.com` |
| `auth-service` | 8090 | Autenticacion JWT y usuarios | Pendiente |
| `pedido-service` | 8081 | Gestion de pedidos e integracion con cliente, producto y estado | `https://pedido-service-47kn.onrender.com` |
| `cliente-service` | 8082 | Gestion de clientes | `https://cliente-service-6yfy.onrender.com` |
| `producto-service` | 8083 | Gestion de productos/catalogo | `https://producto-service-ulv6.onrender.com` |
| `despacho-service` | 8084 | Gestion de despachos | Pendiente |
| `fabricacion-service` | 8085 | Gestion de ordenes de fabricacion | Pendiente |
| `estado-service` | 8086 | Registro y consulta de cambios de estado | `https://estado-service.onrender.com` |
| `metrica-service` | 8087 | Metricas de clientes, productos y ventas | Pendiente |
| `transportista-service` | 8088 | Gestion de transportistas | Pendiente |
| `log-service` | 8089 | Registro y consulta de logs del sistema | Pendiente |

## Discovery Server / Eureka

Eureka local:

```text
http://localhost:8761
```

Eureka Render validado:

```text
https://discovery-server-gjd0.onrender.com
https://discovery-server-gjd0.onrender.com/eureka/apps
```

Variable Eureka local por defecto:

```properties
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://localhost:8761/eureka/
```

Variable Eureka para Render:

```properties
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=https://discovery-server-gjd0.onrender.com/eureka/
```

## API Gateway

El Gateway local escucha en `http://localhost:8080` y en Render en `https://api-gateway-c9qz.onrender.com`. Sus rutas principales estan definidas en `api-gateway/src/main/resources/application.yml`.

| Ruta Gateway | Servicio Eureka | URI interna |
|---|---|---|
| `/api/auth/**` | `auth-service` | `lb://auth-service` |
| `/api/pedidos/**` | `pedido-service` | `lb://pedido-service` |
| `/api/clientes/**` | `cliente-service` | `lb://cliente-service` |
| `/api/productos/**` | `producto-service` | `lb://producto-service` |
| `/api/despachos/**` | `despacho-service` | `lb://despacho-service` |
| `/api/fabricacion/**` | `fabricacion-service` | `lb://fabricacion-service` |
| `/api/estados/**` | `estado-service` | `lb://estado-service` |
| `/api/metricas/**` | `metrica-service` | `lb://metrica-service` |
| `/api/transportistas/**` | `transportista-service` | `lb://transportista-service` |
| `/api/logs/**` | `log-service` | `lb://log-service` |

Endpoints Gateway Render validados:

```text
https://api-gateway-c9qz.onrender.com/actuator/health
https://api-gateway-c9qz.onrender.com/api/clientes
https://api-gateway-c9qz.onrender.com/api/productos
https://api-gateway-c9qz.onrender.com/api/pedidos
```

El Gateway no expone Swagger propio porque no contiene controladores de negocio; Swagger/OpenAPI vive en los microservicios de dominio.

Flujo funcional remoto validado:

1. Cliente creado y consultado.
2. Producto creado y consultado.
3. Pedido creado con `clienteId` y `productoId` existentes.
4. Pedido consultado desde `pedido-service`.
5. Cliente, producto y pedido consultados desde API Gateway.
6. Eureka muestra servicios registrados en estado `UP`.
7. `estado-service` validado con `/actuator/health`.
8. Swagger usado para cliente, producto y pedido.

## Swagger/OpenAPI

Swagger/OpenAPI esta configurado en los 10 microservicios de dominio mediante Springdoc. URLs locales principales:

| Servicio | Swagger local | OpenAPI local | Swagger Render |
|---|---|---|---|
| `auth-service` | `http://localhost:8090/swagger-ui/index.html` | `http://localhost:8090/v3/api-docs` | Pendiente |
| `cliente-service` | `http://localhost:8082/swagger-ui/index.html` | `http://localhost:8082/v3/api-docs` | `https://cliente-service-6yfy.onrender.com/swagger-ui/index.html` |
| `producto-service` | `http://localhost:8083/swagger-ui/index.html` | `http://localhost:8083/v3/api-docs` | `https://producto-service-ulv6.onrender.com/swagger-ui/index.html` |
| `pedido-service` | `http://localhost:8081/swagger-ui/index.html` | `http://localhost:8081/v3/api-docs` | `https://pedido-service-47kn.onrender.com/swagger-ui/index.html` |
| `estado-service` | `http://localhost:8086/swagger-ui/index.html` | `http://localhost:8086/v3/api-docs` | `https://estado-service.onrender.com/swagger-ui/index.html` |
| `despacho-service` | `http://localhost:8084/swagger-ui/index.html` | `http://localhost:8084/v3/api-docs` | Pendiente |
| `fabricacion-service` | `http://localhost:8085/swagger-ui/index.html` | `http://localhost:8085/v3/api-docs` | Pendiente |
| `metrica-service` | `http://localhost:8087/swagger-ui/index.html` | `http://localhost:8087/v3/api-docs` | Pendiente |
| `transportista-service` | `http://localhost:8088/swagger-ui/index.html` | `http://localhost:8088/v3/api-docs` | Pendiente |
| `log-service` | `http://localhost:8089/swagger-ui/index.html` | `http://localhost:8089/v3/api-docs` | Pendiente |

Nota de validacion: no se documenta como obligatorio que la ruta raiz `/` funcione. Las raices de microservicios pueden responder `404` o `500` porque no son endpoints funcionales. Validar con `/actuator/health`, `/swagger-ui/index.html`, `/v3/api-docs` y rutas `/api/**`.

## Variables De Entorno

No versionar secretos reales. Usar placeholders seguros en documentacion y configurar valores reales solo en el entorno local o en Render.

Variables base para desarrollo local/demo Render H2:

```properties
SPRING_PROFILES_ACTIVE=h2
JWT_SECRET=<TU_JWT_SECRET>
JWT_EXPIRATION_MS=86400000
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://localhost:8761/eureka/
```

Variables Render para microservicios desplegados:

```properties
SPRING_PROFILES_ACTIVE=h2
JWT_SECRET=<JWT_SECRET_RENDER>
JWT_EXPIRATION_MS=86400000
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=https://discovery-server-gjd0.onrender.com/eureka/
EUREKA_CLIENT_REGISTER_WITH_EUREKA=true
EUREKA_CLIENT_FETCH_REGISTRY=true
```

`pedido-service` usa URLs directas para Feign en esta etapa:

```properties
CLIENTE_SERVICE_URL=https://cliente-service-6yfy.onrender.com
PRODUCTO_SERVICE_URL=https://producto-service-ulv6.onrender.com
ESTADO_SERVICE_URL=https://estado-service.onrender.com
FEIGN_CONNECT_TIMEOUT_MS=3000
FEIGN_READ_TIMEOUT_MS=5000
```

## Ejecucion Local Con Maven

Requisitos:

| Herramienta | Version recomendada |
|---|---|
| Java | 21 |
| Maven Wrapper | Incluido por servicio |
| PowerShell | Para comandos Windows del proyecto |

Configurar variables iniciales:

```powershell
$env:SPRING_PROFILES_ACTIVE="h2"
$env:JWT_SECRET="<TU_JWT_SECRET>"
$env:JWT_EXPIRATION_MS="86400000"
```

Orden recomendado para el flujo principal:

1. `discovery-server`
2. `cliente-service`
3. `producto-service`
4. `estado-service`
5. `pedido-service`
6. `api-gateway`

Ejemplo:

```powershell
cd discovery-server
.\mvnw.cmd spring-boot:run
```

```powershell
cd cliente-service
.\mvnw.cmd spring-boot:run
```

Para `pedido-service` local:

```powershell
$env:CLIENTE_SERVICE_URL="http://localhost:8082"
$env:PRODUCTO_SERVICE_URL="http://localhost:8083"
$env:ESTADO_SERVICE_URL="http://localhost:8086"
.\mvnw.cmd spring-boot:run
```

## Docker Compose Local

`docker-compose.yml` implementa una demo minima validada del flujo principal con Eureka real. Incluye:

| Servicio | Rol |
|---|---|
| `discovery-server` | Eureka local |
| `api-gateway` | Rutas Gateway locales |
| `cliente-service` | Clientes |
| `producto-service` | Productos |
| `pedido-service` | Pedidos |
| `estado-service` | Estados |

- `discovery-server`
- `api-gateway`
- `cliente-service`
- `producto-service`
- `pedido-service`
- `estado-service`

Servicios registrados en Eureka durante la validacion Docker:

- `API-GATEWAY`
- `CLIENTE-SERVICE`
- `PRODUCTO-SERVICE`
- `PEDIDO-SERVICE`
- `ESTADO-SERVICE`

Servicios fuera de esta demo Docker inicial:

- `auth-service`
- `despacho-service`
- `fabricacion-service`
- `metrica-service`
- `transportista-service`
- `log-service`

Comandos Docker validados:

```powershell
docker compose config
docker compose build
docker compose up -d
docker compose ps
```

Validacion local por Gateway:

```powershell
curl.exe http://localhost:8761
curl.exe http://localhost:8080/actuator/health
curl.exe http://localhost:8080/api/clientes
curl.exe http://localhost:8080/api/productos
curl.exe http://localhost:8080/api/pedidos
```

Detener demo:

```powershell
docker compose down
```

## Render

Servicios Render principales ya desplegados y validados:

| Servicio | Health/API validada |
|---|---|
| `discovery-server` | `https://discovery-server-gjd0.onrender.com/eureka/apps` |
| `api-gateway` | `https://api-gateway-c9qz.onrender.com/actuator/health` |
| `cliente-service` | `https://cliente-service-6yfy.onrender.com/api/clientes` |
| `producto-service` | `https://producto-service-ulv6.onrender.com/api/productos` |
| `estado-service` | `https://estado-service.onrender.com/actuator/health` y `https://estado-service.onrender.com/swagger-ui/index.html` |
| `pedido-service` | `https://pedido-service-47kn.onrender.com/api/pedidos` |

Render Free puede dormir servicios por inactividad. Para la demo tecnica, despertar primero cada servicio con `/actuator/health`, revisar Eureka y luego probar el Gateway.

H2 remoto se usa solo como persistencia temporal de demo. No representa una base productiva ni persistencia garantizada ante reciclaje de contenedores.

Flujo de validacion tecnica remota:

1. Despertar servicios con `/actuator/health`.
2. Revisar Eureka en `https://discovery-server-gjd0.onrender.com/eureka/apps`.
3. Probar Gateway: `/api/clientes`, `/api/productos`, `/api/pedidos`.
4. Probar Swagger directo en microservicios.
5. Crear cliente, producto y pedido para evidenciar el flujo funcional.

## Pruebas Unitarias Y Cobertura

La suite final documentada tiene 452 tests pasando. Los 10 microservicios de dominio tienen JaCoCo configurado y superan 80% de cobertura de lineas.

| Servicio | Tests | Cobertura lineas | JaCoCo |
|---|---:|---:|---|
| `auth-service` | 50 | 86.38% | Si |
| `cliente-service` | 36 | 83.33% | Si |
| `producto-service` | 42 | 86.39% | Si |
| `pedido-service` | 44 | 83.67% | Si |
| `estado-service` | 33 | 91.97% | Si |
| `despacho-service` | 48 | 95.88% | Si |
| `fabricacion-service` | 76 | 96.76% | Si |
| `metrica-service` | 46 | 92.82% | Si |
| `transportista-service` | 40 | 90.65% | Si |
| `log-service` | 37 | 91.22% | Si |
| `api-gateway` | 0 | N/A | No aplica |

Comandos principales de validacion:

```powershell
cd cliente-service; .\mvnw.cmd clean verify
cd ..\producto-service; .\mvnw.cmd clean verify
cd ..\estado-service; .\mvnw.cmd clean verify
cd ..\pedido-service; .\mvnw.cmd clean verify
cd ..\api-gateway; .\mvnw.cmd clean package -DskipTests
cd ..\discovery-server; .\mvnw.cmd clean package -DskipTests
```

## Evidencias Sugeridas Para Screenshots

| Evidencia | URL o comando |
|---|---|
| GitHub actualizado | Repositorio remoto en `main` |
| Eureka Render | `https://discovery-server-gjd0.onrender.com/eureka/apps` |
| Gateway health | `https://api-gateway-c9qz.onrender.com/actuator/health` |
| Gateway clientes | `https://api-gateway-c9qz.onrender.com/api/clientes` |
| Gateway productos | `https://api-gateway-c9qz.onrender.com/api/productos` |
| Gateway pedidos | `https://api-gateway-c9qz.onrender.com/api/pedidos` |
| Swagger cliente | `https://cliente-service-6yfy.onrender.com/swagger-ui/index.html` |
| Swagger producto | `https://producto-service-ulv6.onrender.com/swagger-ui/index.html` |
| Swagger estado | `https://estado-service.onrender.com/swagger-ui/index.html` |
| Swagger pedido | `https://pedido-service-47kn.onrender.com/swagger-ui/index.html` |
| Docker local | `docker compose ps` |
| Tests y cobertura | `target/site/jacoco/index.html` por microservicio |

## Evidencias Pendientes Por Incorporar

Las capturas finales todavia deben incorporarse en `docs/evidencias/` o en el informe final de entrega. Evidencias esperadas:

| Evidencia | Carpeta sugerida |
|---|---|
| Render dashboard con servicios desplegados | `docs/evidencias/render/` |
| Eureka con servicios `UP` | `docs/evidencias/eureka/` |
| Swagger cliente, producto, pedido y estado | `docs/evidencias/swagger/` |
| Gateway clientes/productos/pedidos | `docs/evidencias/gateway/` |
| GitHub con ultimo commit en `main` | `docs/evidencias/github/` |
| README con URLs Render finales | `docs/evidencias/github/` |

## Tecnologias

- Java 21
- Spring Boot 4.0.5
- Spring Cloud Gateway
- Spring Cloud Netflix Eureka
- Spring Data JPA
- Spring Security y JWT por variable de entorno
- OpenFeign para integraciones entre servicios
- H2 para perfil local/demo
- Swagger/OpenAPI con Springdoc
- Docker Compose
- JUnit 5, Mockito y JaCoCo
