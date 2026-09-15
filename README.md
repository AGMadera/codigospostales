# Códigos Postales México 🇲🇽

API REST para consultar asentamientos (colonias, fraccionamientos, etc.) por código postal en México, a partir del archivo oficial de **SEPOMEX**. Construida con **Spring Boot 4**, arquitectura **hexagonal** y caché en memoria con **Caffeine**.

---

## ✨ Características

- Consulta de asentamientos por código postal (`GET /api/v1/codigos-postales/{cp}`).
- Carga del archivo SEPOMEX en memoria al arrancar la aplicación.
- Índice por CP (`Map<CP, List<Asentamiento>>`) para búsquedas O(1).
- Recarga programada del archivo todos los días a las 3:00 AM.
- Caché de resultados con **Caffeine** (hasta 10 000 CPs, expiración de 24 h).
- Manejo de errores con **RFC 7807 (Problem Details)**.
- Documentación interactiva con **Swagger / OpenAPI 3**.
- Validación de entrada con **Jakarta Bean Validation**.
- Lectura del archivo en `ISO-8859-1` (encoding oficial de SEPOMEX).

---

## 🧱 Arquitectura

El proyecto sigue **arquitectura hexagonal (puertos y adaptadores)**:

```
┌───────────────────────────────────────────────────────────────┐
│                      infrastructure                           │
│                                                               │
│  ┌─────────────┐    ┌─────────────────┐    ┌───────────────┐  │
│  │ REST Adapter│───▶│  Input Ports    │    │ Output Ports  │  │
│  │ Controller  │    │  (Use Cases)    │    │ (Repositories)│  │
│  └─────────────┘    └─────────────────┘    └───────────────┘  │
│                            ▲                       ▲          │
│                            │                       │          │
│                     ┌──────┴──────┐         ┌──────┴───────┐  │
│                     │   domain    │         │  File Adapter│  │
│                     │  services   │         │  Cache Adapt.│  │
│                     └─────────────┘         └──────────────┘  │
└───────────────────────────────────────────────────────────────┘
```

### Capas

| Capa | Responsabilidad |
|------|-----------------|
| `domain.model` | Modelos de negocio puros (`Asentamiento`, `CodigoPostalInfo`). |
| `domain.port.in` | Puertos de entrada (casos de uso). |
| `domain.port.out` | Puertos de salida (repositorios). |
| `domain.service` | Lógica de negocio (`CodigoPostalService`). |
| `infrastructure.adapter.in.rest` | Controladores, DTOs y manejo de errores. |
| `infrastructure.adapter.out.file` | Lectura del archivo SEPOMEX. |
| `infrastructure.adapter.out.cache` | Envoltorio con Caffeine. |
| `infrastructure.config` | Configuración de beans y caché. |

---

## 🛠 Stack tecnológico

- **Java 21**
- **Spring Boot 4.1.1**
- **Spring Web MVC** (`spring-boot-starter-webmvc`)
- **Spring Cache** + **Caffeine**
- **Bean Validation (Jakarta)**
- **springdoc-openapi 3.1.1** (Swagger UI)
- **SLF4J + Logback**
- **Maven**

---

## 📋 Requisitos

- JDK 21 o superior.
- Maven 3.9+.
- Archivo **SEPOMEX** en formato TXT (ver sección [Formato del archivo](#-formato-del-archivo-sepomex)).

---

## 🚀 Instalación y ejecución

### 1. Clonar el repositorio

```bash
git clone https://github.com/<tu-usuario>/codigospostalesmex.git
cd codigospostalesmex
```

### 2. Descargar el archivo SEPOMEX

Descarga el catálogo nacional de códigos postales desde el portal oficial de SEPOMEX o desde el dataset de [https://www.correosdemexico.gob.mx/SSLServicios/ConsultaCP/CodigoPostal_Exportar.aspx](https://www.correosdemexico.gob.mx/SSLServicios/ConsultaCP/CodigoPostal_Exportar.aspx). El archivo debe estar en formato TXT delimitado por `|` y con codificación `ISO-8859-1`.

### 3. Configurar la ruta del archivo

En `src/main/resources/application.properties`:

```properties
app.sepomex.archivo=/ruta/a/CPdescarga.txt
```

También puedes usar una variable de entorno:

```bash
export APP_SEPOMEX_ARCHIVO=/ruta/a/CPdescarga.txt
```

### 4. Compilar y ejecutar

```bash
mvn clean spring-boot:run
```

La aplicación arranca en `http://localhost:8080`.

### 5. Verificar la carga

En los logs deberías ver algo como:

```
Cargados 145678 asentamientos en 32164 CPs desde /ruta/a/CPdescarga.txt (1850 ms)
```

---

## ⚙️ Configuración

| Propiedad | Descripción | Valor por defecto |
|-----------|-------------|-------------------|
| `app.sepomex.archivo` | Ruta al archivo TXT de SEPOMEX. | *(obligatorio)* |
| `server.port` | Puerto del servidor. | `8080` |
| `spring.cache.type` | Tipo de caché. | `caffeine` |

### Ejemplo de `application.properties`

```properties
# Nombre de la aplicación
spring.application.name=codigospostalesmex

# Ruta al archivo SEPOMEX
app.sepomex.archivo=/ruta/a/CPdescarga.txt

# Puerto del servidor (opcional)
server.port=8080

# Tipo de caché
spring.cache.type=caffeine
```

### Perfiles

Si más adelante necesitas configuración por entorno, puedes usar `application-dev.properties`, `application-prod.properties`, etc., y activarlos con:

```properties
spring.profiles.active=dev
```

O por variable de entorno:

```bash
export SPRING_PROFILES_ACTIVE=prod
```

### Caché

Configurada en `CacheConfiguration`:

- Nombre del cache: `asentamientosPorCp`
- Tamaño máximo: `10_000` entradas.
- Expiración: `24 h` después de la escritura.

Ajustable según tu tráfico.

---

## 🌐 API

### `GET /api/v1/codigos-postales/{cp}`

Devuelve la información de un código postal y la lista de asentamientos asociados.

**Path params**

| Parámetro | Tipo | Descripción | Ejemplo |
|-----------|------|-------------|---------|
| `cp` | `string` | Código postal de 5 dígitos. | `01000` |

**Ejemplo de petición**

```bash
curl http://localhost:8080/api/v1/codigos-postales/01000
```

**Respuesta 200**

```json
{
  "codigoPostal": "01000",
  "estado": "Ciudad de México",
  "municipio": "Álvaro Obregón",
  "colonias": [
    { "nombre": "San Ángel", "tipo": "Colonia" },
    { "nombre": "Guadalupe Inn", "tipo": "Colonia" }
  ]
}
```

**Respuesta 404 (Problem Details)**

```json
{
  "type": "/errors/cp-no-encontrado",
  "title": "Código postal no encontrado",
  "status": 404,
  "detail": "No se encontraron asentamientos para el código postal 99999",
  "instance": "/api/v1/codigos-postales/99999",
  "codigoPostal": "99999",
  "timestamp": "2025-01-15T10:23:45"
}
```

**Respuesta 400**

```json
{
  "type": "/errors/solicitud-invalida",
  "title": "Solicitud inválida",
  "status": 400,
  "detail": "El código postal debe tener 5 dígitos"
}
```

### Swagger UI

Una vez arrancada la aplicación:

- Swagger UI: [`http://localhost:8080/swagger-ui.html`](http://localhost:8080/swagger-ui.html)
- OpenAPI JSON: [`http://localhost:8080/v3/api-docs`](http://localhost:8080/v3/api-docs)

---

## 🗂 Formato del archivo SEPOMEX

El archivo es un TXT delimitado por `|` con codificación **ISO-8859-1**. La primera línea es el encabezado y debe ser ignorada.

Ejemplo:

```
d_codigo|d_asenta|d_tipo_asenta|D_mnpio|d_estado|d_ciudad|d_CP|c_estado|c_oficina|c_CP|c_tipo_asenta|c_mnpio|id_asenta_cpcons|d_zona|c_cve_ciudad
01000|San Ángel|Colonia|Álvaro Obregón|Ciudad de México|Ciudad de México|01001|09|01001||09|010|0001|Urbano|01
```

### Columnas utilizadas

| Índice | Campo | Descripción |
|--------|-------|-------------|
| 0 | `d_codigo` | Código postal |
| 1 | `d_asenta` | Nombre del asentamiento |
| 2 | `d_tipo_asenta` | Tipo (Colonia, Fraccionamiento, etc.) |
| 3 | `D_mnpio` | Municipio |
| 4 | `d_estado` | Estado |
| 13 | `d_zona` | Zona (Urbano / Rural) |

---

## 📁 Estructura del proyecto

```
src/main/java/com/agmadera/codigospostalesmex
├── domain
│   ├── exception
│   │   └── CodigoPostalNoEncontradoException.java
│   ├── model
│   │   ├── Asentamiento.java
│   │   └── CodigoPostalInfo.java
│   ├── port
│   │   ├── in
│   │   │   └── ConsultarCodigoPostalCaseUse.java
│   │   └── out
│   │       └── AsentamientoRepositoryPort.java
│   └── service
│       └── CodigoPostalService.java
└── infrastructure
    ├── adapter
    │   ├── in.rest
    │   │   ├── CodigoPostalController.java
    │   │   ├── GlobalExceptionHandler.java
    │   │   └── dto
    │   │       ├── CodigoPostalResponse.java
    │   │       └── ColoniaDTO.java
    │   └── out
    │       ├── cache
    │       │   └── CachedAsentamientoRepositoryAdapter.java
    │       └── file
    │           └── TxtFileAsentamientoAdapter.java
    └── config
        ├── BeanConfiguration.java
        └── CacheConfiguration.java
```

---

## 🧪 Tests

Ejecutar la suite completa:

```bash
mvn test
```

El proyecto usa `spring-boot-starter-webmvc-test` para pruebas de integración con `MockMvc`.

Cobertura sugerida:

- Parser de líneas SEPOMEX (líneas válidas, inválidas y con campos vacíos).
- Carga del archivo en un directorio temporal.
- Búsqueda de CP existente, inexistente y con formato inválido.
- Recarga programada (reemplazando el archivo).
- Controller con `MockMvc`.
- Manejo de excepciones en `GlobalExceptionHandler`.

---

## 🔁 Recarga de datos

El adaptador de archivo recarga el catálogo automáticamente todos los días a las **3:00 AM** (cron: `0 0 3 * * *`). Si la recarga falla, se conserva el índice anterior y se registra el error en el log.

Si necesitas forzar la recarga, puedes exponer un endpoint administrativo o reiniciar la aplicación.

---

## 🤝 Contribuciones

Las contribuciones son bienvenidas. Por favor:

1. Haz un fork del repositorio.
2. Crea una rama (`git checkout -b feature/mi-mejora`).
3. Realiza tus cambios y agrega tests.
4. Envía un Pull Request.

---

## 📄 Licencia

Este proyecto está bajo la licencia **MIT**. Consulta el archivo [LICENSE](LICENSE) para más detalles.

---

## 🙏 Créditos

- Datos: **Correos de México (SEPOMEX)** — Catálogo Nacional de Códigos Postales.
- Inspiración arquitectónica: **Alistair Cockburn** (Arquitectura Hexagonal).

---

## 📬 Contacto

- **Autor:** AGMadera
- **Repositorio:** [github.com/AGMadera/codigospostales](https://github.com/AGMadera/codigospostales)
- **Correo:** agmadera@protonmail.com
