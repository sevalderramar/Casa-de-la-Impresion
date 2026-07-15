# Documentacion Tecnica

## Arquitectura General

Casa de la Impresion usa microservicios Spring Boot con Eureka Discovery, API Gateway, H2 para desarrollo/demo, JWT por variable de entorno, Swagger/OpenAPI y pruebas automatizadas. El proyecto fue desarrollado individualmente por Sebastian Valderrama.

El flujo principal validado es cliente -> producto -> pedido -> estado -> gateway. `pedido-service` integra cliente, producto y estado; `api-gateway` centraliza la entrada HTTP y resuelve servicios registrados en Eureka mediante rutas `lb://`.

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

## Microservicios Y Responsabilidades

| Servicio | Puerto | Responsabilidad | Render |
|---|---:|---|---|
| `discovery-server` | 8761 | Eureka Server | `https://discovery-server-gjd0.onrender.com` |
| `api-gateway` | 8080 | Gateway WebFlux con rutas `lb://` | `https://api-gateway-c9qz.onrender.com` |
| `auth-service` | 8090 | Autenticacion JWT y usuarios | Pendiente remoto |
| `cliente-service` | 8082 | Gestion de clientes | `https://cliente-service-6yfy.onrender.com` |
| `producto-service` | 8083 | Gestion de productos | `https://producto-service-ulv6.onrender.com` |
| `pedido-service` | 8081 | Pedidos e integraciones | `https://pedido-service-47kn.onrender.com` |
| `estado-service` | 8086 | Cambios de estado e historial | `https://estado-service.onrender.com` |
| `despacho-service` | 8084 | Despachos | Pendiente remoto |
| `fabricacion-service` | 8085 | Ordenes de fabricacion | Pendiente remoto |
| `metrica-service` | 8087 | Metricas | Pendiente remoto |
| `transportista-service` | 8088 | Transportistas | Pendiente remoto |
| `log-service` | 8089 | Logs operacionales | Pendiente remoto |

## Modelo De Datos Resumido

| Servicio | Datos principales |
|---|---|
| `auth-service` | Usuario, rol, credencial hasheada, JWT |
| `cliente-service` | Cliente, RUT, email, telefono, direccion |
| `producto-service` | Producto, categoria, precio, stock |
| `pedido-service` | Pedido, detalle, clienteId, productoId, numeroPedido |
| `estado-service` | Cambio de estado, numeroPedido, estado anterior/nuevo, fecha |
| `despacho-service` | Despacho, numeroPedido, transportista, tracking |
| `fabricacion-service` | Orden de fabricacion, estado productivo, historial |
| `metrica-service` | Rankings, ventas, productos top |
| `transportista-service` | Transportista, codigo, contacto, regiones |
| `log-service` | Evento, servicio, operacion, usuario, resultado |

## Relaciones Principales

| Relacion | Descripcion |
|---|---|
| Pedido - Cliente | Un pedido referencia un cliente existente |
| Pedido - Producto | Un pedido contiene productos y cantidades |
| Pedido - Estado | Un pedido tiene historial de cambios de estado |
| Pedido - Despacho | Un despacho se asocia a un numero de pedido |
| Pedido - Fabricacion | Una orden de fabricacion representa avance productivo |
| Servicio - Log | Cada operacion relevante puede registrarse como log |

## Discovery Eureka

Eureka local:

```text
http://localhost:8761
http://localhost:8761/eureka/apps
```

Eureka Render:

```text
https://discovery-server-gjd0.onrender.com
https://discovery-server-gjd0.onrender.com/eureka/apps
```

Los microservicios y el Gateway se registran como clientes Eureka. En Render se usa `EUREKA_INSTANCE_HOSTNAME` con hostname publico, `EUREKA_INSTANCE_SECURE_PORT_ENABLED=true`, `EUREKA_INSTANCE_NON_SECURE_PORT_ENABLED=false` y `EUREKA_INSTANCE_SECURE_PORT=443` para evitar hostnames internos no resolubles por Gateway.

## API Gateway

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

Gateway Render validado:

```text
https://api-gateway-c9qz.onrender.com/api/clientes
https://api-gateway-c9qz.onrender.com/api/productos
https://api-gateway-c9qz.onrender.com/api/pedidos
```

## Perfiles De Configuracion

| Perfil | Uso |
|---|---|
| `h2` | Desarrollo local, Docker demo y Render demo temporal |
| `prod` | Base externa por variables `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` donde aplica |

H2 en Render se usa solo como persistencia temporal de demo. No es base productiva.

## Variables De Entorno

```properties
SPRING_PROFILES_ACTIVE=h2
JWT_SECRET=<TU_JWT_SECRET>
JWT_EXPIRATION_MS=86400000
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://localhost:8761/eureka/
```

Render:

```properties
SPRING_PROFILES_ACTIVE=h2
JWT_SECRET=<JWT_SECRET_RENDER>
JWT_EXPIRATION_MS=86400000
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=https://discovery-server-gjd0.onrender.com/eureka/
EUREKA_CLIENT_REGISTER_WITH_EUREKA=true
EUREKA_CLIENT_FETCH_REGISTRY=true
```

`pedido-service` Render:

```properties
CLIENTE_SERVICE_URL=https://cliente-service-6yfy.onrender.com
PRODUCTO_SERVICE_URL=https://producto-service-ulv6.onrender.com
ESTADO_SERVICE_URL=https://estado-service.onrender.com
FEIGN_CONNECT_TIMEOUT_MS=3000
FEIGN_READ_TIMEOUT_MS=5000
```

## Seguridad JWT

Los servicios inyectan `jwt.secret=${JWT_SECRET}`. No se versionan secretos reales. La documentacion usa placeholders seguros como `<TU_JWT_SECRET>` y `<JWT_SECRET_RENDER>`. Algunos endpoints permanecen permisivos para demo academica, especialmente Swagger, H2 y health checks.

## Comunicacion Entre Servicios

`pedido-service` consulta cliente, producto y estado mediante OpenFeign/REST configurado por variables. El Gateway enruta por Eureka con `lb://`, no por URLs HTTP fijas. Los errores remotos 400/404/500/503 se cubren con pruebas unitarias y escenarios REST.

## Manejo De Errores

Los servicios usan validaciones DTO, excepciones de negocio y handlers globales donde aplica. En Render, `503` o `500` desde Gateway suele indicar servicio dormido, servicio ausente en Eureka o hostname interno de Render.

## Logs

`log-service` registra eventos con servicio, operacion, usuario, resultado, detalle y fecha. Es apoyo de auditoria operacional y trazabilidad, validado localmente con pruebas unitarias y REST.

## Pruebas

La suite documentada tiene 452 tests. Se usan JUnit 5, Mockito y JaCoCo. Los 10 microservicios de dominio superan 80% de cobertura de lineas.

## Swagger/OpenAPI

| Servicio | Swagger local | Swagger Render |
|---|---|---|
| `auth-service` | `http://localhost:8090/swagger-ui/index.html` | Pendiente remoto |
| `cliente-service` | `http://localhost:8082/swagger-ui/index.html` | `https://cliente-service-6yfy.onrender.com/swagger-ui/index.html` |
| `producto-service` | `http://localhost:8083/swagger-ui/index.html` | `https://producto-service-ulv6.onrender.com/swagger-ui/index.html` |
| `pedido-service` | `http://localhost:8081/swagger-ui/index.html` | `https://pedido-service-47kn.onrender.com/swagger-ui/index.html` |
| `estado-service` | `http://localhost:8086/swagger-ui/index.html` | `https://estado-service.onrender.com/swagger-ui/index.html` |
| `despacho-service` | `http://localhost:8084/swagger-ui/index.html` | Pendiente remoto |
| `fabricacion-service` | `http://localhost:8085/swagger-ui/index.html` | Pendiente remoto |
| `metrica-service` | `http://localhost:8087/swagger-ui/index.html` | Pendiente remoto |
| `transportista-service` | `http://localhost:8088/swagger-ui/index.html` | Pendiente remoto |
| `log-service` | `http://localhost:8089/swagger-ui/index.html` | Pendiente remoto |

## Ejecución desde cero

### Requisitos Previos

| Requisito | Version/uso |
|---|---|
| Java | 21 |
| Maven Wrapper | `mvnw.cmd` en Windows, `./mvnw` en Linux/macOS |
| Docker | Opcional para demo local |
| Git | Clonar rama `main` |

### Variables De Entorno

PowerShell:

```powershell
$env:SPRING_PROFILES_ACTIVE="h2"
$env:JWT_SECRET="<TU_JWT_SECRET>"
$env:JWT_EXPIRATION_MS="86400000"
```

Linux/macOS:

```bash
export SPRING_PROFILES_ACTIVE=h2
export JWT_SECRET=<TU_JWT_SECRET>
export JWT_EXPIRATION_MS=86400000
```

### Orden De Arranque Local

1. `discovery-server`
2. `cliente-service`
3. `producto-service`
4. `estado-service`
5. `pedido-service`
6. `api-gateway`

Windows:

```powershell
cd discovery-server
.\mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
cd discovery-server
./mvnw spring-boot:run
```

Para `pedido-service` local:

```powershell
$env:CLIENTE_SERVICE_URL="http://localhost:8082"
$env:PRODUCTO_SERVICE_URL="http://localhost:8083"
$env:ESTADO_SERVICE_URL="http://localhost:8086"
.\mvnw.cmd spring-boot:run
```

### Docker Compose

```powershell
docker compose config
docker compose build
docker compose up -d
docker compose ps
```

### Verificaciones Locales

```powershell
curl.exe http://localhost:8761
curl.exe http://localhost:8080/actuator/health
curl.exe http://localhost:8080/api/clientes
curl.exe http://localhost:8080/api/productos
curl.exe http://localhost:8080/api/pedidos
```

### Comandos De Pruebas

```powershell
cd cliente-service; .\mvnw.cmd clean verify
cd ..\producto-service; .\mvnw.cmd clean verify
cd ..\estado-service; .\mvnw.cmd clean verify
cd ..\pedido-service; .\mvnw.cmd clean verify
cd ..\api-gateway; .\mvnw.cmd clean package -DskipTests
cd ..\discovery-server; .\mvnw.cmd clean package -DskipTests
```

### Despliegue Render

La configuracion final esta en `docs/render-deploy.md`. En Render Free se deben despertar servicios con `/actuator/health`, revisar Eureka y luego probar Gateway. Las rutas raiz `/` pueden responder `404` o `500`; validar con `/actuator/health`, Swagger, `/v3/api-docs` y rutas `/api/**`.
