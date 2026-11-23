# Sistema de Control de Acceso UACM — Etapa 1

Implementación funcional de la **Etapa 1** del proyecto de Análisis y Modelamiento de Software:
seguridad, autenticación por roles, acceso institucional y bitácora digital.

> Documento base: `Etapa inicial - Sistema de Control de Access UACM` (RF-01, RF-09, RF-12, RF-13;
> CU-01, CU-09, CU-10). La Etapa 1 cubre la base de seguridad y trazabilidad sobre la que se
> construirán las etapas 2 a 5.

> **¿Solo quieres revisar la aplicación?** Sigue la
> [Guía de ejecución y revisión — Etapa 1](GUIA-CLIENTE.md). Incluye el paso a paso completo,
> capturas de pantalla y una lista de verificación.

## Inicio rápido (modo demostración, sin MySQL)

En Windows, con IntelliJ IDEA instalado:

1. Abre la carpeta del proyecto.
2. Haz doble clic en `ejecutar-demo.bat`.
3. Espera el mensaje `Started ControlAccesoApplication`.
4. Abre <http://localhost:8080>.

Este modo usa una base de datos H2 en memoria y carga los datos de demostración automáticamente.
No requiere instalar Java, Maven, MySQL ni Docker: utiliza el JDK y Maven incluidos en IntelliJ.
Los cambios se borran al apagar la aplicación, para poder repetir la revisión desde cero.

## Tecnologías

- Java 21
- Spring Boot 3.5.16
- Spring MVC + Thymeleaf
- Spring Security (form login, BCrypt, roles)
- Spring Data JPA + Hibernate
- MySQL 8 / MariaDB 10.6+ (H2 en memoria para el modo demostración)
- Bootstrap 5 (CDN)

## Alcance de esta etapa

| Requisito | Implementación |
|---|---|
| RF-01 Ingreso de estudiante, profesor y administrativo | Registro automático del acceso al escanear una credencial QR/código de barras: el sistema decide **Permitido** o **Denegado** según el estatus de la persona y la credencial, y muestra foto/iniciales, nombre, matrícula, tipo y estatus. Los casos restringidos permiten **autorizar con advertencia**. La validación manual por matrícula se conserva con confirmación y registro. |
| RF-09 Registro de bitácora digital | Bitácora de solo inserción con actor, evento, descripción, entidad, resultado e IP. |
| RF-12 Autenticación y control por roles | Login, BCrypt, roles, bloqueo tras 5 intentos, cierre por inactividad y bitácora de sesión. |
| RF-13 Gestión de usuarios | Crear, editar rol, desactivar, reactivar y desbloquear cuentas; cambio obligatorio de contraseña temporal. |
| CU-01, CU-09, CU-10 | Flujos principales y alternativos implementados en la interfaz web. |

## Requisitos previos

**Modo demostración:**

- Windows con IntelliJ IDEA instalado (aporta JDK 21+ y Maven).
- Un navegador web.

**Modo MySQL/MariaDB (entrega final):**

- JDK 21 o superior.
- Maven 3.9+ (o usar el Maven incluido en IntelliJ IDEA).
- MySQL 8 o MariaDB 10.6+ en `localhost:3306`.

## Puesta en marcha con MySQL/MariaDB

### 1. Crear la base de datos

Con MySQL/MariaDB instalado:

```bash
mysql -u root -p < database/init.sql
```

O con Docker:

```bash
docker compose up -d
```

Adminer queda disponible en <http://localhost:8081>:
- Sistema: MySQL
- Servidor: `mariadb` (o `localhost` si usas MySQL local)
- Usuario: `uacm`
- Contraseña: `uacm123`
- Base de datos: `control_acceso_uacm`

### 2. Configurar variables de entorno (opcional)

Los valores por defecto están en `src/main/resources/application.yml`:

| Variable | Valor por defecto |
|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/control_acceso_uacm?...` |
| `DB_USER` | `uacm` |
| `DB_PASSWORD` | `uacm123` |
| `PORT` | `8080` |
| `APP_SEED` | `true` |

### 3. Ejecutar

Con Maven:

```bash
mvn spring-boot:run
```

O desde IntelliJ IDEA: abrir `pom.xml` como proyecto Maven y ejecutar
`ControlAccesoApplication`.

La aplicación queda en <http://localhost:8080>.

> Si usas el JDK incluido en IntelliJ, configura `JAVA_HOME` apuntando a
> `C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.4\jbr` antes de ejecutar Maven.

## Cuentas de demostración

| Usuario | Contraseña | Rol |
|---|---|---|
| `direccion` | `Direccion123!` | Dirección de Enlace |
| `guardia` | `Guardia123!` | Guardia de Seguridad |
| `emisor` | `Emisor123!` | Personal Administrativo (emisor) |

## Datos de prueba

| Matrícula | Persona | Tipo | Credencial | Caso de prueba |
|---|---|---|---|---|
| `20260001` | Ana López García | Estudiante | `UACM-20260001` | Identidad activa |
| `20260002` | Carlos Pérez Ruiz | Estudiante | `UACM-20260002` | Identidad activa (código de barras) |
| `20260003` | Luis Hernández Soto | Estudiante | `UACM-20260003` | Estatus con prohibición de acceso |
| `20260004` | Sofía Torres Vega | Estudiante | `UACM-20260004` | Credencial vencida |
| `20260005` | Pedro Ramírez Luna | Estudiante | `UACM-20260005` | Credencial bloqueada |
| `DOC-001` | María Sánchez Díaz | Profesora | `UACM-DOC-001` | Identidad activa |
| `ADM-001` | José Ramírez Cruz | Administrativo | `UACM-ADM-001` | Identidad activa |
| `20260008` | Diego Flores Nava | Estudiante | Sin credencial | Validación manual por matrícula |

Para probar el escaneo sin lector físico, usa la hoja imprimible
[`docs/credenciales-qr.pdf`](docs/credenciales-qr.pdf) y la guía
[`docs/COMO-USAR-TU-TELEFONO-COMO-LECTOR.md`](docs/COMO-USAR-TU-TELEFONO-COMO-LECTOR.md).

## Flujos para probar

1. **Acceso con credencial:** entrar como `guardia`, ir a *Revisar identidad*, escanear
   `UACM-20260001` y presionar Enter. El sistema registra el acceso como **Permitido** y muestra
   la identidad (foto/iniciales, nombre, matrícula, tipo y estatus). El evento queda en la
   bitácora y en *Mis últimos accesos registrados*.
2. **Casos restringidos:** escanear `UACM-20260003` (prohibición), `UACM-20260004` (credencial
   vencida) o `UACM-20260005` (credencial bloqueada). El sistema registra el acceso como
   **Denegado** y muestra el botón **Permitir con advertencia**. Si el Guardia lo usa, se agrega
   un evento **Acceso permitido con advertencia** y se conserva el motivo original.
3. **Validación manual:** capturar la matrícula `20260008`, revisar los datos mostrados y
   confirmar. Se registra como `MANUAL_MATRICULA`.
4. **Bloqueo de cuenta:** cerrar sesión, intentar entrar 5 veces con una contraseña incorrecta
   para `emisor`; la cuenta queda bloqueada. Entrar como `direccion`, ir a *Usuarios* y
   desbloquearla. El emisor deberá cambiar su contraseña.
5. **Bitácora:** entrar como `direccion` o `guardia`, abrir *Bitácora* y filtrar por fecha,
   tipo de evento o texto.
6. **Gestión de usuarios:** como `direccion`, crear una cuenta, asignar rol, desactivar,
   reactivar o desbloquear. No se permite desactivar la propia cuenta.

## Estructura del proyecto

```
control-acceso-uacm/
├── database/init.sql                 # Creación de BD y usuario
├── docker-compose.yml                # MariaDB + Adminer
├── docs/                             # Capturas, QR de prueba y guías de apoyo
├── pom.xml
├── src/main/java/mx/uacm/controlacceso/
│   ├── config/                       # Seguridad, MVC y datos de demostración
│   ├── dominio/                      # Entidades y enums
│   ├── repositorio/                  # Spring Data JPA
│   ├── seguridad/                    # UserDetails, handlers e interceptor
│   ├── servicio/                     # Reglas de negocio
│   └── web/                          # Controladores MVC y DTOs
├── src/main/resources/
│   ├── application.yml
│   ├── static/css/app.css
│   └── templates/                    # Vistas Thymeleaf
└── src/test/                         # Pruebas unitarias y de contexto
```

## Arquitectura

La implementación respeta la arquitectura MVC del diagrama de paquetes:

- **Presentación:** plantillas Thymeleaf (`templates/`).
- **Controlador:** clases `*Controller` en `web/`.
- **Modelo:** entidades y reglas en `dominio/` y `servicio/`.
- **Persistencia:** repositorios Spring Data JPA en `repositorio/`.

La seguridad se aplica con Spring Security: sesión, roles, CSRF, BCrypt y bloqueo de cuentas.

## Decisiones de diseño

- **Contraseñas:** se almacenan con BCrypt; nunca en texto plano.
- **Bitácora:** solo inserción, sin endpoints de edición o borrado.
- **Cuentas:** no se eliminan; se desactivan para conservar el historial.
- **Roles:** `GUARDIA`, `PERSONAL_EMISOR` y `DIRECCION_ENLACE`.
- **Fotografías:** en este prototipo se muestran iniciales; la integración con el directorio
  institucional de la UACM queda pendiente. La entidad ya tiene el campo `fotografiaUrl`.
- **Base de datos:** `ddl-auto=update` para desarrollo. Para producción se recomienda migrar a
  Flyway o Liquibase y usar `ddl-auto=validate`.
- **Datos de demostración:** se cargan solo si la tabla de cuentas está vacía. Desactivar con
  `APP_SEED=false`.

## Pruebas

```bash
mvn test
```

Las pruebas usan H2 en memoria y no requieren MySQL.

## Próximas etapas

1. **Etapa 2:** visitantes y proveedores, pases temporales QR, validación y salida.
2. **Etapa 3:** tarjetón vehicular, control de acceso vehicular e inspección de cajuela.
3. **Etapa 4:** incidentes, notificaciones por correo/panel y rondines.
4. **Etapa 5:** reportes históricos, exportación a PDF/Excel e integración final.

## Notas para la entrega

- El documento original no especifica lenguaje ni framework; esta implementación usa Java +
  Spring Boot por decisión del equipo.
- La aplicación es web y funciona en el navegador de la caseta; un lector USB de QR/código de
  barras se comporta como teclado y escribe el código en el campo del lector, donde el sistema
  registra el acceso automáticamente.
- Para el despliegue real se debe integrar el directorio institucional de la UACM para obtener
  fotografía, matrícula y vigencia de credenciales.
