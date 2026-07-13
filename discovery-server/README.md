# Discovery Server

Servidor Eureka independiente para descubrimiento de servicios del ecosistema Casa de la Impresion.

## Proposito

Centralizar el registro y descubrimiento de microservicios usando Netflix Eureka Server.

## Puerto

- `8761`

## Consola Eureka

- `http://localhost:8761`

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

Los microservicios se registraran en Eureka en pasos posteriores. Este modulo solo agrega el servidor de discovery.
