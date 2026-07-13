# api-gateway

## Proposito

Servicio de entrada central que enruta solicitudes HTTP hacia los microservicios del sistema mediante Spring Cloud Gateway y Eureka Discovery.

## Puerto

`8080`

## Rutas principales

El Gateway se registra como cliente Eureka y usa URIs `lb://` para resolver servicios registrados. Requiere que `discovery-server` este levantado antes de iniciar el Gateway.

| Ruta | Servicio Eureka | URI Gateway |
|---|---|
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

Las rutas mantienen los prefijos `/api/**`; el destino se resuelve desde el registry de Eureka.

## Variables y perfil

- `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE`: URL del servidor Eureka. En local usa `http://localhost:8761/eureka/`.
- En Docker o Render debe apuntar al `discovery-server` del entorno.
- `SPRING_PROFILES_ACTIVE`: perfil de ejecucion cuando aplica.

## Docker

Participa en la demo Docker junto con los servicios de dominio. Para operar con rutas `lb://`, la demo debe levantar primero `discovery-server` y configurar `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE`.

## Swagger

El Gateway no tiene Swagger propio. La documentacion OpenAPI se consulta directamente en cada microservicio, por ejemplo `http://localhost:8081/swagger-ui/index.html` para `pedido-service`.

## Tests

No tiene pruebas unitarias propias actualmente. El Gateway compila y su responsabilidad principal es enrutar solicitudes declarativamente.

## Ejecucion local

```powershell
cd api-gateway
.\mvnw spring-boot:run
```

Antes de probar rutas por Gateway, verificar la consola Eureka en `http://localhost:8761` y esperar unos segundos a que el registry refresque los servicios.
