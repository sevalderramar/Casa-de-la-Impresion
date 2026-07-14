# Documentacion Tecnica

## Arquitectura General

Casa de la Impresion usa una arquitectura de microservicios Spring Boot con Eureka Discovery, API Gateway, persistencia H2 para desarrollo/demo, seguridad JWT por variable de entorno, Swagger/OpenAPI y pruebas automatizadas. El sistema se organiza en 10 microservicios de dominio, un `discovery-server` y un `api-gateway`.

El servicio principal del dominio es `pedido-service`, porque concentra la operacion de crear, consultar y evolucionar pedidos. Se apoya en `cliente-service`, `producto-service` y `estado-service`. Los demas servicios cubren autenticacion, despacho, fabricacion, metricas, transportistas y logs.

## Estructura Del Repositorio

```text
Casa-de-la-impresion/
  discovery-server/
  api-gateway/
  auth-service/
  cliente-service/
  producto-service/
  pedido-service/
  estado-service/
  despacho-service/
  fabricacion-service/
  metrica-service/
  transportista-service/
  log-service/
  docs/
  docker-compose.yml
  .env.example
```

## Servicios

| Servicio | Puerto local | Responsabilidad | Render |
|---|---:|---|---|
| `discovery-server` | 8761 | Eureka Server | `https://discovery-server-gjd0.onrender.com` |
| `api-gateway` | 8080 | Gateway WebFlux con rutas `lb://` | `https://api-gateway-c9qz.onrender.com` |
| `auth-service` | 8090 | Autenticacion JWT y usuarios | Pendiente |
| `pedido-service` | 8081 | Pedidos e integraciones | `https://pedido-service-47kn.onrender.com` |
| `cliente-service` | 8082 | Clientes | `https://cliente-service-6yfy.onrender.com` |
| `producto-service` | 8083 | Productos/catalogo | `https://producto-service-ulv6.onrender.com` |
| `despacho-service` | 8084 | Despachos | Pendiente |
| `fabricacion-service` | 8085 | Ordenes de fabricacion | Pendiente |
| `estado-service` | 8086 | Cambios de estado | `https://estado-service.onrender.com` |
| `metrica-service` | 8087 | Metricas | Pendiente |
| `transportista-service` | 8088 | Transportistas | Pendiente |
| `log-service` | 8089 | Logs operacionales | Pendiente |

## Discovery Eureka

`discovery-server` ejecuta Eureka Server en el puerto local `8761`. Los microservicios y el Gateway se registran como clientes Eureka.

Eureka local:

```text
http://localhost:8761
http://localhost:8761/eureka/apps
```

Eureka Render validado:

```text
https://discovery-server-gjd0.onrender.com
https://discovery-server-gjd0.onrender.com/eureka/apps
```

Variable por entorno:

```properties
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://localhost:8761/eureka/
```

En Render:

```properties
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=https://discovery-server-gjd0.onrender.com/eureka/
```

## API Gateway

El Gateway usa Spring Cloud Gateway WebFlux. Las rutas estan declaradas en `api-gateway/src/main/resources/application.yml` y usan `lb://` para resolver servicios por Eureka.

| Ruta | Servicio Eureka | URI interna |
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

El Gateway no tiene Swagger propio porque no expone controladores de negocio. Swagger se valida directamente en cada microservicio.

## Comunicacion Entre Servicios

`pedido-service` usa OpenFeign/REST para consultar cliente, producto y estado. En la demo Render actual conserva URLs directas por variables:

```properties
CLIENTE_SERVICE_URL=https://cliente-service-6yfy.onrender.com
PRODUCTO_SERVICE_URL=https://producto-service-ulv6.onrender.com
ESTADO_SERVICE_URL=https://estado-service.onrender.com
FEIGN_CONNECT_TIMEOUT_MS=3000
FEIGN_READ_TIMEOUT_MS=5000
```

El Gateway, en cambio, no usa URLs directas para el enrutamiento: resuelve por Eureka y rutas `lb://`.

## Perfiles, H2 Y JWT

Los microservicios usan `application.properties`, `application-h2.properties` y `application-prod.properties` donde aplica. El perfil `h2` permite ejecucion local y demo en Render sin base externa. H2 en Render debe considerarse temporal porque el contenedor puede reciclarse.

Variables seguras de ejemplo:

```properties
SPRING_PROFILES_ACTIVE=h2
JWT_SECRET=<TU_JWT_SECRET>
JWT_EXPIRATION_MS=86400000
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://localhost:8761/eureka/
```

`JWT_SECRET` se inyecta como variable de entorno mediante `jwt.secret=${JWT_SECRET}`. No se debe versionar ningun secreto real.

## Swagger/OpenAPI

Los 10 microservicios de dominio tienen Swagger/OpenAPI con Springdoc.

| Servicio | Swagger local | Swagger Render |
|---|---|---|
| `auth-service` | `http://localhost:8090/swagger-ui/index.html` | Pendiente |
| `cliente-service` | `http://localhost:8082/swagger-ui/index.html` | `https://cliente-service-6yfy.onrender.com/swagger-ui/index.html` |
| `producto-service` | `http://localhost:8083/swagger-ui/index.html` | `https://producto-service-ulv6.onrender.com/swagger-ui/index.html` |
| `pedido-service` | `http://localhost:8081/swagger-ui/index.html` | `https://pedido-service-47kn.onrender.com/swagger-ui/index.html` |
| `estado-service` | `http://localhost:8086/swagger-ui/index.html` | `https://estado-service.onrender.com/swagger-ui/index.html` |
| `despacho-service` | `http://localhost:8084/swagger-ui/index.html` | Pendiente |
| `fabricacion-service` | `http://localhost:8085/swagger-ui/index.html` | Pendiente |
| `metrica-service` | `http://localhost:8087/swagger-ui/index.html` | Pendiente |
| `transportista-service` | `http://localhost:8088/swagger-ui/index.html` | Pendiente |
| `log-service` | `http://localhost:8089/swagger-ui/index.html` | Pendiente |

Validar tambien `/v3/api-docs` por servicio cuando se requiera el JSON OpenAPI.

## Modelo De Datos Resumido

| Servicio | Datos principales |
|---|---|
| `auth-service` | Usuarios, credenciales hasheadas, roles, JWT |
| `cliente-service` | Cliente, RUT, contacto, direccion |
| `producto-service` | Producto, nombre, categoria, precio, stock |
| `pedido-service` | Pedido, detalle, cliente, productos, numero de pedido |
| `estado-service` | Cambio de estado, numero de pedido, estado, timestamp |
| `despacho-service` | Despacho, numero de pedido, transportista, tracking |
| `fabricacion-service` | Orden de fabricacion, historial productivo |
| `metrica-service` | Rankings, ventas, productos top |
| `transportista-service` | Transportista, codigo, contacto, regiones |
| `log-service` | Log de servicio, operacion, usuario, resultado |

## Manejo De Errores

Los servicios incluyen validaciones DTO, excepciones de negocio y handlers globales donde aplica. Las pruebas cubren entradas invalidas, recursos inexistentes y errores remotos Feign. En Render, un `503` desde Gateway normalmente indica servicio dormido, no registrado en Eureka o registro con hostname interno.

## Docker Compose

`docker-compose.yml` implementa una demo minima local del flujo principal con Eureka real:

| Servicio | Incluido |
|---|---|
| `discovery-server` | Si |
| `api-gateway` | Si |
| `cliente-service` | Si |
| `producto-service` | Si |
| `pedido-service` | Si |
| `estado-service` | Si |

Comandos validados:

```powershell
docker compose config
docker compose build
docker compose up -d
docker compose ps
curl.exe http://localhost:8761
curl.exe http://localhost:8080/actuator/health
curl.exe http://localhost:8080/api/clientes
curl.exe http://localhost:8080/api/productos
curl.exe http://localhost:8080/api/pedidos
docker compose down
```

## Render

Render esta validado para `discovery-server`, `api-gateway`, `cliente-service`, `producto-service`, `estado-service` y `pedido-service`. La configuracion detallada esta en `docs/render-deploy.md`.

Render Free puede dormir servicios por inactividad. Flujo recomendado:

1. Despertar cada servicio con `/actuator/health`.
2. Revisar `https://discovery-server-gjd0.onrender.com/eureka/apps`.
3. Probar Gateway en `/api/clientes`, `/api/productos`, `/api/pedidos`.
4. Probar Swagger directo.
5. Crear cliente, producto y pedido.

Las rutas raiz `/` de microservicios pueden responder `404` o `500`; no son endpoints funcionales obligatorios.

## Pruebas Y Cobertura

La suite final documentada tiene 452 tests pasando. Los 10 microservicios de dominio tienen JaCoCo y superan 80% de cobertura de lineas.

| Servicio | Tests | Cobertura lineas |
|---|---:|---:|
| `auth-service` | 50 | 86.38% |
| `cliente-service` | 36 | 83.33% |
| `producto-service` | 42 | 86.39% |
| `pedido-service` | 44 | 83.67% |
| `estado-service` | 33 | 91.97% |
| `despacho-service` | 48 | 95.88% |
| `fabricacion-service` | 76 | 96.76% |
| `metrica-service` | 46 | 92.82% |
| `transportista-service` | 40 | 90.65% |
| `log-service` | 37 | 91.22% |

Comandos principales:

```powershell
cd cliente-service; .\mvnw.cmd clean verify
cd ..\producto-service; .\mvnw.cmd clean verify
cd ..\estado-service; .\mvnw.cmd clean verify
cd ..\pedido-service; .\mvnw.cmd clean verify
cd ..\api-gateway; .\mvnw.cmd clean package -DskipTests
cd ..\discovery-server; .\mvnw.cmd clean package -DskipTests
```
