# Despliegue Render

Este documento registra el despliegue remoto validado para la demo tecnica de Casa de la Impresion. No contiene secretos reales; `JWT_SECRET` debe configurarse en Render con un valor privado y aqui se documenta solo como placeholder.

Autor: Sebastian Valderrama.

## URLs Render Validadas

| Servicio | URL | Endpoint validado |
|---|---|---|
| `discovery-server` | `https://discovery-server-gjd0.onrender.com` | `https://discovery-server-gjd0.onrender.com/eureka/apps` |
| `api-gateway` | `https://api-gateway-c9qz.onrender.com` | `https://api-gateway-c9qz.onrender.com/actuator/health` |
| `cliente-service` | `https://cliente-service-6yfy.onrender.com` | `https://cliente-service-6yfy.onrender.com/api/clientes` |
| `producto-service` | `https://producto-service-ulv6.onrender.com` | `https://producto-service-ulv6.onrender.com/api/productos` |
| `estado-service` | `https://estado-service.onrender.com` | `https://estado-service.onrender.com/swagger-ui/index.html` |
| `pedido-service` | `https://pedido-service-47kn.onrender.com` | `https://pedido-service-47kn.onrender.com/api/pedidos` |

Endpoints Gateway Render validados:

```text
https://api-gateway-c9qz.onrender.com/api/clientes
https://api-gateway-c9qz.onrender.com/api/productos
https://api-gateway-c9qz.onrender.com/api/pedidos
```

Swagger Render validado:

```text
https://cliente-service-6yfy.onrender.com/swagger-ui/index.html
https://producto-service-ulv6.onrender.com/swagger-ui/index.html
https://estado-service.onrender.com/swagger-ui/index.html
https://pedido-service-47kn.onrender.com/swagger-ui/index.html
```

## Orden Recomendado De Despliegue

1. `discovery-server`
2. `cliente-service`
3. `producto-service`
4. `estado-service`
5. `pedido-service`
6. `api-gateway`

`discovery-server` debe existir primero. El Gateway se despliega al final porque depende del registry Eureka y enruta con `lb://` hacia servicios registrados.

## Configuracion Global

Todos los servicios se configuran como Web Service en Render desde el repositorio `https://github.com/sevalderramar/Casa-de-la-Impresion`.

Render Free puede dormir servicios por inactividad. Para la demo tecnica se recomienda despertar cada servicio con `/actuator/health`, revisar Eureka y despues probar Gateway.

Variables base para microservicios desplegados con perfil H2 de demo:

```properties
SPRING_PROFILES_ACTIVE=h2
JWT_SECRET=<JWT_SECRET_RENDER>
JWT_EXPIRATION_MS=86400000
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=https://discovery-server-gjd0.onrender.com/eureka/
EUREKA_CLIENT_REGISTER_WITH_EUREKA=true
EUREKA_CLIENT_FETCH_REGISTRY=true
```

No versionar el valor real de `JWT_SECRET`.

## Configuracion Por Servicio

### discovery-server

| Campo | Valor |
|---|---|
| Root Directory | `discovery-server` |
| Environment | Docker |
| Dockerfile Path | `Dockerfile` |
| Puerto local fallback | `8761` |
| URL Render | `https://discovery-server-gjd0.onrender.com` |

Variables:

```properties
PORT=<asignado por Render>
```

### cliente-service

| Campo | Valor |
|---|---|
| Root Directory | `cliente-service` |
| Environment | Docker |
| Dockerfile Path | `Dockerfile` |
| Puerto local fallback | `8082` |
| URL Render | `https://cliente-service-6yfy.onrender.com` |

Variables:

```properties
SPRING_PROFILES_ACTIVE=h2
JWT_SECRET=<JWT_SECRET_RENDER>
JWT_EXPIRATION_MS=86400000
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=https://discovery-server-gjd0.onrender.com/eureka/
EUREKA_CLIENT_REGISTER_WITH_EUREKA=true
EUREKA_CLIENT_FETCH_REGISTRY=true
EUREKA_INSTANCE_HOSTNAME=cliente-service-6yfy.onrender.com
EUREKA_INSTANCE_PREFER_IP_ADDRESS=false
EUREKA_INSTANCE_SECURE_PORT_ENABLED=true
EUREKA_INSTANCE_NON_SECURE_PORT_ENABLED=false
EUREKA_INSTANCE_SECURE_PORT=443
```

### producto-service

| Campo | Valor |
|---|---|
| Root Directory | `producto-service` |
| Environment | Docker |
| Dockerfile Path | `Dockerfile` |
| Puerto local fallback | `8083` |
| URL Render | `https://producto-service-ulv6.onrender.com` |

Variables:

```properties
SPRING_PROFILES_ACTIVE=h2
JWT_SECRET=<JWT_SECRET_RENDER>
JWT_EXPIRATION_MS=86400000
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=https://discovery-server-gjd0.onrender.com/eureka/
EUREKA_CLIENT_REGISTER_WITH_EUREKA=true
EUREKA_CLIENT_FETCH_REGISTRY=true
EUREKA_INSTANCE_HOSTNAME=producto-service-ulv6.onrender.com
EUREKA_INSTANCE_PREFER_IP_ADDRESS=false
EUREKA_INSTANCE_SECURE_PORT_ENABLED=true
EUREKA_INSTANCE_NON_SECURE_PORT_ENABLED=false
EUREKA_INSTANCE_SECURE_PORT=443
```

### estado-service

| Campo | Valor |
|---|---|
| Root Directory | `estado-service` |
| Environment | Docker |
| Dockerfile Path | `Dockerfile` |
| Puerto local fallback | `8086` |
| URL Render | `https://estado-service.onrender.com` |

Variables:

```properties
SPRING_PROFILES_ACTIVE=h2
JWT_SECRET=<JWT_SECRET_RENDER>
JWT_EXPIRATION_MS=86400000
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=https://discovery-server-gjd0.onrender.com/eureka/
EUREKA_CLIENT_REGISTER_WITH_EUREKA=true
EUREKA_CLIENT_FETCH_REGISTRY=true
EUREKA_INSTANCE_HOSTNAME=estado-service.onrender.com
EUREKA_INSTANCE_PREFER_IP_ADDRESS=false
EUREKA_INSTANCE_SECURE_PORT_ENABLED=true
EUREKA_INSTANCE_NON_SECURE_PORT_ENABLED=false
EUREKA_INSTANCE_SECURE_PORT=443
```

### pedido-service

| Campo | Valor |
|---|---|
| Root Directory | `pedido-service` |
| Environment | Docker |
| Dockerfile Path | `Dockerfile` |
| Puerto local fallback | `8081` |
| URL Render | `https://pedido-service-47kn.onrender.com` |

Variables:

```properties
SPRING_PROFILES_ACTIVE=h2
JWT_SECRET=<JWT_SECRET_RENDER>
JWT_EXPIRATION_MS=86400000
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=https://discovery-server-gjd0.onrender.com/eureka/
EUREKA_CLIENT_REGISTER_WITH_EUREKA=true
EUREKA_CLIENT_FETCH_REGISTRY=true
EUREKA_INSTANCE_HOSTNAME=pedido-service-47kn.onrender.com
EUREKA_INSTANCE_PREFER_IP_ADDRESS=false
EUREKA_INSTANCE_SECURE_PORT_ENABLED=true
EUREKA_INSTANCE_NON_SECURE_PORT_ENABLED=false
EUREKA_INSTANCE_SECURE_PORT=443
CLIENTE_SERVICE_URL=https://cliente-service-6yfy.onrender.com
PRODUCTO_SERVICE_URL=https://producto-service-ulv6.onrender.com
ESTADO_SERVICE_URL=https://estado-service.onrender.com
FEIGN_CONNECT_TIMEOUT_MS=3000
FEIGN_READ_TIMEOUT_MS=5000
```

### api-gateway

| Campo | Valor |
|---|---|
| Root Directory | `api-gateway` |
| Environment | Docker |
| Dockerfile Path | `Dockerfile` |
| Puerto local fallback | `8080` |
| URL Render | `https://api-gateway-c9qz.onrender.com` |

Variables:

```properties
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=https://discovery-server-gjd0.onrender.com/eureka/
EUREKA_CLIENT_REGISTER_WITH_EUREKA=true
EUREKA_CLIENT_FETCH_REGISTRY=true
```

## Solucion De Errores Gateway 503/500

Si el Gateway responde `503 Service Unavailable` o `500` en Render, revisar en este orden:

| Causa | Diagnostico | Solucion |
|---|---|---|
| Servicios dormidos en Render Free | El health tarda o falla despues de inactividad | Abrir `/actuator/health` de cada microservicio y esperar a que despierte |
| Eureka sin servicios registrados | `eureka/apps` no muestra `CLIENTE-SERVICE`, `PRODUCTO-SERVICE`, `ESTADO-SERVICE`, `PEDIDO-SERVICE` | Despertar servicios y confirmar `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` |
| Hostname interno de Render | Eureka muestra instancias, pero Gateway no puede llamarlas | Configurar `EUREKA_INSTANCE_HOSTNAME`, `EUREKA_INSTANCE_SECURE_PORT_ENABLED=true`, `EUREKA_INSTANCE_NON_SECURE_PORT_ENABLED=false`, `EUREKA_INSTANCE_SECURE_PORT=443` |
| Pedido no encuentra dependencias | `pedido-service` falla al crear pedidos | Configurar `CLIENTE_SERVICE_URL`, `PRODUCTO_SERVICE_URL`, `ESTADO_SERVICE_URL` con URLs Render reales |
| Registry aun refrescando | Error justo despues de levantar servicios | Esperar 20-60 segundos y reintentar |

## Flujo De Validacion Remota

1. Abrir `https://discovery-server-gjd0.onrender.com`.
2. Despertar `cliente-service`, `producto-service`, `estado-service`, `pedido-service` y `api-gateway` con `/actuator/health` cuando aplique.
3. Revisar `https://discovery-server-gjd0.onrender.com/eureka/apps`.
4. Probar `https://api-gateway-c9qz.onrender.com/api/clientes`.
5. Probar `https://api-gateway-c9qz.onrender.com/api/productos`.
6. Probar `https://api-gateway-c9qz.onrender.com/api/pedidos`.
7. Abrir Swagger directo en cliente, producto, estado y pedido.

Nota: la ruta raiz `/` de cada microservicio no es endpoint funcional obligatorio. Puede responder `404` o `500`; validar con `/actuator/health`, Swagger, `/v3/api-docs` y `/api/**`.

## Base De Datos

La demo Render usa `SPRING_PROFILES_ACTIVE=h2`. H2 en Render sirve para demostracion temporal; la persistencia puede perderse si el contenedor se recicla. Para produccion real, configurar una base externa por servicio, revisar `application-prod.properties`, migraciones Flyway y `ddl-auto=validate`.
