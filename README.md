# Escuela Colombiana de Ingeniería Julio Garavito
## Arquitectura de Software – ARSW
### Laboratorio – Parte 2: BluePrints API con Seguridad JWT (OAuth 2.0)

Este laboratorio extiende la **Parte 1** ([Lab_P1_BluePrints_Java21_API](https://github.com/DECSIS-ECI/Lab_P1_BluePrints_Java21_API)) agregando **seguridad a la API** usando **Spring Boot 3, Java 21 y JWT (OAuth 2.0)**.  
El API se convierte en un **Resource Server** protegido por tokens Bearer firmados con **RS256**.  
Incluye un endpoint didáctico `/auth/login` que emite el token para facilitar las pruebas.

---

## Objetivos
- Implementar seguridad en servicios REST usando **OAuth2 Resource Server**.
- Configurar emisión y validación de **JWT**.
- Proteger endpoints con **roles y scopes** (`blueprints.read`, `blueprints.write`).
- Integrar la documentación de seguridad en **Swagger/OpenAPI**.

---

## Requisitos
- JDK 21
- Maven 3.9+
- Git

---

## Ejecución del proyecto
1. Clonar o descomprimir el proyecto:
   ```bash
   git clone https://github.com/DECSIS-ECI/Lab_P2_BluePrints_Java21_API_Security_JWT.git
   cd Lab_P2_BluePrints_Java21_API_Security_JWT
   ```
   ó si el profesor entrega el `.zip`, descomprimirlo y entrar en la carpeta.

2. Ejecutar con Maven:
   ```bash
   mvn -q -DskipTests spring-boot:run
   ```

3. Verificar que la aplicación levante en `http://localhost:8080`.

---

## Endpoints principales

### 1. Login (emite token)
```
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "username": "student",
  "password": "student123"
}
```
Respuesta:
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer",
  "expires_in": 3600
}
```

### 2. Consultar blueprints (requiere scope `blueprints.read`)
```
GET http://localhost:8080/api/blueprints
Authorization: Bearer <ACCESS_TOKEN>
```

### 3. Crear blueprint (requiere scope `blueprints.write`)
```
POST http://localhost:8080/api/blueprints
Authorization: Bearer <ACCESS_TOKEN>
Content-Type: application/json

{
  "name": "Nuevo Plano"
}
```

---

## Swagger UI
- URL: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- Pulsa **Authorize**, ingresa el token en el formato:
  ```
  Bearer eyJhbGciOi...
  ```

---

## Estructura del proyecto
```
src/main/java/co/edu/eci/blueprints/
  ├── api/BlueprintController.java       # Endpoints protegidos
  ├── auth/AuthController.java           # Login didáctico para emitir tokens
  ├── config/OpenApiConfig.java          # Configuración Swagger + JWT
  └── security/
       ├── SecurityConfig.java
       ├── MethodSecurityConfig.java
       ├── JwtKeyProvider.java
       ├── InMemoryUserService.java
       └── RsaKeyProperties.java
src/main/resources/
  └── application.yml
```

---

## Actividades propuestas
1. Revisar el código de configuración de seguridad (`SecurityConfig`) e identificar cómo se definen los endpoints públicos y protegidos.
2. Explorar el flujo de login y analizar las claims del JWT emitido.
3. Extender los scopes (`blueprints.read`, `blueprints.write`) para controlar otros endpoints de la API, del laboratorio P1 trabajado.
4. Modificar el tiempo de expiración del token y observar el efecto.
5. Documentar en Swagger los endpoints de autenticación y de negocio.

---

## Lecturas recomendadas
- [Spring Security Reference – OAuth2 Resource Server](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/index.html)
- [Spring Boot – Securing Web Applications](https://spring.io/guides/gs/securing-web/)
- [JSON Web Tokens – jwt.io](https://jwt.io/introduction)

---

## Licencia
Proyecto educativo con fines académicos – Escuela Colombiana de Ingeniería Julio Garavito.

---
# Laboratorio – Parte 2: BluePrints API con Seguridad JWT (OAuth 2.0)
## Escuela Colombiana de Ingeniería Julio Garavito – Arquitectura de Software

Este documento describe la implementación de seguridad realizada sobre la API de Blueprints (migrada de la Parte 1), usando **Spring Security OAuth2 Resource Server** con tokens **JWT firmados en RS256**.

---

## 1. Integrantes y Distribución del Trabajo

| Integrante | Responsabilidad |
|---|---|
| _[Laura Castillo]_ | Migración de las clases de la Parte 1 (modelo, persistencia, servicios, filtros, `ApiResponse`, `GlobalExceptionHandler`) al nuevo repositorio, adaptando el paquete a `co.edu.eci.blueprints`. |
| _[Miguel Sandoval]_ | Implementación y verificación de la capa de seguridad: emisión y validación de JWT, control de acceso por scopes, expiración de tokens y documentación en Swagger. |

---

## 2. Arquitectura de Seguridad

```
src/main/java/co/edu/eci/blueprints/
  ├── auth/
  │    └── AuthController.java          # Endpoint /auth/login: valida credenciales y emite el JWT
  ├── security/
  │    ├── SecurityConfig.java          # Filtro de seguridad: rutas públicas/protegidas, beans JWT
  │    ├── MethodSecurityConfig.java    # Habilita @PreAuthorize a nivel de método
  │    ├── JwtKeyProvider.java          # Genera el par de llaves RSA (firma/verificación)
  │    ├── InMemoryUserService.java     # Usuarios de prueba y sus scopes
  │    └── RsaKeyProperties.java        # Propiedades configurables: issuer y TTL del token
  ├── controllers/
  │    ├── BlueprintsAPIController.java # Endpoints de negocio, protegidos por scope
  │    └── GlobalExceptionHandler.java  # Manejo global de errores (heredado de la Parte 1)
  └── config/
       └── OpenApiConfig.java          # Configuración de Swagger + esquema de seguridad Bearer JWT
```

**Flujo general:**
1. El cliente hace `POST /auth/login` con usuario y contraseña.
2. `AuthController` valida las credenciales contra `InMemoryUserService` y, si son correctas, emite un JWT firmado con la llave privada RSA generada por `JwtKeyProvider`, incluyendo como claim `scope` los permisos del usuario.
3. El cliente usa ese token como header `Authorization: Bearer <token>` en cada petición a `/api/**`.
4. `SecurityConfig` valida la firma del token con la llave pública y verifica, según el método HTTP, que el token tenga el scope requerido.

---

## 3. Usuarios y Scopes

| Usuario | Contraseña | Scopes |
|---|---|---|
| `student` | `student123` | `blueprints.read` |
| `assistant` | `assistant123` | `blueprints.read`, `blueprints.write` |

La asignación de scopes por usuario se definió en `InMemoryUserService`, de modo que cada login emite un token con únicamente los permisos correspondientes a ese usuario — no todos los usuarios reciben los mismos scopes.

---

## 4. Endpoints Protegidos

### Rutas públicas (sin autenticación)
- `POST /auth/login`
- `/actuator/health`
- `/v3/api-docs/**`, `/swagger-ui/**`

### Rutas protegidas — `/api/blueprints/**`

| Método | Path | Scope requerido |
|---|---|---|
| GET | `/api/blueprints` | `blueprints.read` |
| GET | `/api/blueprints/{author}` | `blueprints.read` |
| GET | `/api/blueprints/{author}/{name}` | `blueprints.read` |
| POST | `/api/blueprints` | `blueprints.write` |
| PUT | `/api/blueprints/{author}/{name}/points` | `blueprints.write` |

Esta distinción se implementó en dos capas, como defensa en profundidad:
- **A nivel de filtro HTTP**, en `SecurityConfig`, usando `requestMatchers(HttpMethod.X, "/api/**").hasAuthority(...)`.
- **A nivel de método**, con `@PreAuthorize("hasAuthority('SCOPE_...')")` en cada endpoint del controlador, habilitado por `@EnableMethodSecurity` en `MethodSecurityConfig`.

---

## 5. Códigos de Respuesta Relacionados con Seguridad

| Código | Significado | Cuándo ocurre |
|---|---|---|
| 200 / 201 / 202 | Éxito | Token válido y con el scope requerido |
| 401 Unauthorized | No autenticado | No se envía token, el token es inválido, o el token expiró |
| 403 Forbidden | Sin permiso | El token es válido pero no tiene el scope necesario para esa operación |
| 404 Not Found | Recurso no encontrado | Heredado de la lógica de negocio de la Parte 1 |

- petición sin token → 401:
<img width="1744" height="852" alt="Captura de pantalla 2026-09-14 170500" src="https://github.com/user-attachments/assets/b151aa58-f354-4c29-a936-497d379f35fd" />

- login con `student`, luego POST → 403:
<img width="1742" height="862" alt="Captura de pantalla 2026-09-14 165656" src="https://github.com/user-attachments/assets/f770e93d-88a8-402f-b47a-328254379ce5" />

- login con `assistant`, luego POST → 201:
<img width="1751" height="916" alt="Captura de pantalla 2026-09-14 163725" src="https://github.com/user-attachments/assets/1b6c68eb-145c-4211-b6d0-db5b0b7a1cd0" />


---

## 6. Flujo de Login y Claims del JWT

Ejemplo de petición de login:

```
POST /auth/login
Content-Type: application/json

{
  "username": "assistant",
  "password": "assistant123"
}
```

Respuesta:

```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer",
  "expires_in": 3600
}
```

Al decodificar el token en [jwt.io](https://jwt.io), se observan los siguientes claims:

| Claim | Descripción |
|---|---|
| `iss` | Emisor del token, configurado en `blueprints.security.issuer` |
| `sub` | Username del usuario autenticado |
| `scope` | Permisos otorgados (`blueprints.read`, `blueprints.write`, o ambos) |
| `iat` | Fecha/hora de emisión |
| `exp` | Fecha/hora de expiración |


---

## 7. Prueba de Expiración del Token

Se configuró temporalmente `blueprints.security.token-ttl-seconds` en `10` segundos para observar el efecto de la expiración:

1. Se generó un token con TTL de 10 segundos.
2. Se esperó más de 10 segundos sin usar el token.
3. Al intentar acceder a un endpoint protegido con ese token vencido, la API respondió `401 Unauthorized`, con el header `WWW-Authenticate` indicando `error="invalid_token"` y la razón de expiración.

Esto confirma que la validación de expiración del JWT es efectiva y que un token vencido no puede reutilizarse, incluso si su firma sigue siendo válida.

- Modificacion del token:
<img width="1751" height="837" alt="Captura de pantalla 2026-09-14 170227" src="https://github.com/user-attachments/assets/4df34822-7613-477f-831f-300d89f9af76" />


El valor de `token-ttl-seconds` se restableció posteriormente a `3600` segundos para el uso normal de la aplicación.

---

## 8. Documentación en Swagger / OpenAPI

Se configuró `OpenApiConfig` con un esquema de seguridad `bearer-jwt` (tipo `http`, esquema `bearer`, formato `JWT`), lo que permite:
- Ver el candado 🔒 en cada endpoint protegido.
- Autenticarse desde la interfaz con el botón **Authorize**, pegando el token obtenido del login.
- Ejecutar peticiones autenticadas directamente desde Swagger UI.

Cada endpoint fue anotado con `@Operation` (resumen) y `@ApiResponses` (códigos de respuesta posibles, incluyendo 401/403 según corresponda), tanto en `AuthController` como en `BlueprintsAPIController`.

Accesos:
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

- vista general de Swagger UI con los grupos `blueprints-api-controller` y `auth-controller`:
<img width="1752" height="856" alt="Captura de pantalla 2026-09-14 171558" src="https://github.com/user-attachments/assets/acb2950d-d7d4-49da-a7be-efd61a911612" />


<img width="1719" height="914" alt="Captura de pantalla 2026-09-14 171633" src="https://github.com/user-attachments/assets/3c9e92cb-258b-4266-8491-7526533731fa" />

---

## 9. Ejecución del Proyecto

```bash
mvn clean install
mvn spring-boot:run
```

La aplicación levanta en `http://localhost:8080`. Ejemplos de prueba:

```bash
# Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"assistant","password":"assistant123"}'

# Consulta protegida
curl http://localhost:8080/api/blueprints \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```
