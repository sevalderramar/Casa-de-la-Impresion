# Modelo de datos - auth-service

## Entidad principal: `Usuario`
Tabla: `usuarios`

| Campo | Tipo | Restricciones | Descripcion |
|---|---|---|---|
| `id` | `Long` | PK, identity | Identificador unico |
| `nombre` | `String` | NOT NULL, max 100 | Nombre del usuario |
| `email` | `String` | NOT NULL, UNIQUE, max 150 | Usuario de login |
| `password` (`password_hash`) | `String` | max 255 | Hash BCrypt |
| `rol` | `Enum` | NOT NULL | Rol de autorizacion |
| `activo` | `boolean` | NOT NULL | Estado activo/inactivo |

## Roles soportados
- `ADMIN`
- `ENCARGADO_PEDIDOS`
- `ENCARGADO_DESPACHO`
- `COMERCIAL`

## Observaciones
- Las credenciales nunca se almacenan en texto plano.
- Usuario inactivo no debe operar endpoints protegidos.
