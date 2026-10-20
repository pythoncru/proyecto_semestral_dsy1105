# 📘 Bitácora Escolar

Aplicación móvil académica desarrollada para la asignatura **DSY1105 – Desarrollo de Aplicaciones Móviles**, de Duoc UC. Su objetivo es facilitar el registro, seguimiento y revisión de las actividades de aprendizaje de los estudiantes mediante bitácoras digitales.

## 🎯 Objetivo del proyecto

Bitácora Escolar propone una herramienta sencilla para que los docentes administren bitácoras mensuales y los estudiantes registren sus actividades, guarden borradores y consulten la retroalimentación recibida.

El proyecto corresponde a un **Producto Mínimo Viable (MVP) académico** y utiliza información ficticia para sus pruebas.

## 👥 Integrantes

- Darío Silva
- Matías Navarrete
- Diego Norambuena

## 🛠️ Tecnologías utilizadas

- **Lenguaje:** Kotlin
- **Entorno de desarrollo:** Android Studio
- **Interfaz:** Jetpack Compose
- **Diseño visual:** Material Design 3
- **Arquitectura:** MVVM
- **Persistencia local:** Room / SQLite
- **Gestión de estados:** ViewModel y StateFlow
- **Control de versiones:** Git y GitHub

## 📱 Módulos de la aplicación

### Docente
- Acceso mediante cuenta de prueba.
- Consulta y administración de bitácoras.
- Seguimiento de registros de los estudiantes.
- Revisión de aportes y entrega de retroalimentación.

### Estudiante
- Acceso mediante cuenta de prueba.
- Consulta de las bitácoras disponibles.
- Creación y gestión de registros de aprendizaje.
- Consulta del estado de los registros y de la retroalimentación.

### Datos y persistencia
- Organización de entidades, DAO y base de datos local.
- Separación de responsabilidades mediante repositorios y ViewModels.
- Uso de datos ficticios para las pruebas académicas.

> **Nota:** Los módulos y las funciones descritas deben verificarse en la versión entregada. La disponibilidad de cada función depende de su implementación efectiva en el proyecto.

## 🏗️ Estructura del proyecto

La organización principal del código se encuentra dentro del paquete `cl.duoc.bitacoraescolar`.

- `data/auth/`: gestión de la sesión.
- `data/dao/`: acceso a los datos.
- `data/database/`: configuración de la base de datos.
- `data/model/`: entidades y modelos.
- `data/repository/`: acceso a los datos desde la lógica de la aplicación.
- `ui/auth/`: interfaz y lógica de inicio de sesión.
- `ui/bitacora/`: creación de bitácoras.
- `ui/dashboard/`: paneles de docente y estudiante.
- `ui/theme/`: colores, tipografía y tema visual.

## 🚀 Cómo ejecutar el proyecto

1. Instalar Android Studio.
2. Descargar o clonar este repositorio.
3. Abrir el proyecto desde Android Studio.
4. Esperar a que termine la sincronización de Gradle.
5. Configurar un emulador Android o conectar un dispositivo compatible.
6. Ejecutar la aplicación mediante el botón **Run**.

La primera ejecución puede requerir descargar las dependencias del proyecto.

## 🔐 Seguridad y privacidad

- Utilizar exclusivamente cuentas y datos ficticios.
- Restringir el acceso según el rol del usuario.
- Evitar incorporar información personal real de estudiantes.
- No utilizar GPS, micrófono ni información biométrica.
- No asignar calificaciones automáticas ni tomar decisiones pedagógicas automatizadas.

## 🧪 Pruebas

Se deben comprobar, como mínimo, el inicio y cierre de sesión, el acceso según el rol, las operaciones con bitácoras y registros, la persistencia de los datos y las validaciones de los formularios.

Las funciones adicionales deben verificarse en la versión final antes de considerarse completadas.

## 🤝 Trabajo colaborativo

El proyecto utiliza Git y GitHub para el control de versiones y el seguimiento de los cambios. La planificación de tareas y la distribución del trabajo se documentan mediante Trello, cuando corresponda.

## 📚 Contexto académico

**Asignatura:** DSY1105 – Desarrollo de Aplicaciones Móviles  
**Institución:** Duoc UC  
**Proyecto:** Bitácora Escolar  
**Tipo de entrega:** Producto Mínimo Viable académico

---

Proyecto desarrollado con fines educativos.
