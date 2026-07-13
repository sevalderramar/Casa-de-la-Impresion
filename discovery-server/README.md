# Discovery Server

Servidor Eureka independiente para registro y descubrimiento de servicios del ecosistema Casa de la Impresion.

## Proposito

Centralizar el registro y descubrimiento de los 10 microservicios de dominio y del `api-gateway` usando Netflix Eureka Server.

## Puerto

- `8761`

## Consola Eureka

- `http://localhost:8761`

## Endpoint Eureka

- Local: `http://localhost:8761/eureka/`
- Docker/Render: configurar `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` apuntando al servidor Eureka del entorno.

En Docker Compose validado, la variable usada por Gateway y servicios demo es:

```properties
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://discovery-server:8761/eureka/
```

## Ejecucion local

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
./mvnw spring-boot:run
```

## Registro de servicios

Los 10 microservicios de dominio se registran como clientes Eureka:

- `auth-service`
- `pedido-service`
- `cliente-service`
- `producto-service`
- `despacho-service`
- `fabricacion-service`
- `estado-service`
- `metrica-service`
- `transportista-service`
- `log-service`

El `api-gateway` tambien se registra como cliente Eureka y enruta hacia esos servicios mediante URIs `lb://`.

## Demo Docker Validada

Docker Compose corresponde a una demo minima validada del flujo principal. Incluye `discovery-server`, `api-gateway`, `cliente-service`, `producto-service`, `pedido-service` y `estado-service`.

Durante la validacion Docker, Eureka mostro registrados y en estado `UP`:

- `API-GATEWAY`
- `CLIENTE-SERVICE`
- `PRODUCTO-SERVICE`
- `PEDIDO-SERVICE`
- `ESTADO-SERVICE`
