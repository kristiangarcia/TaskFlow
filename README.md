# TaskFlow

Sistema de gestión de tareas desarrollado con JavaFX para el proyecto intermodular de 2º DAM.

## Descripción

TaskFlow es una aplicación de escritorio para la gestión integral de tareas, usuarios y asignaciones. Permite administrar proyectos mediante un sistema de tareas con estados, prioridades y asignación de usuarios, con capacidad de importación/exportación de datos y generación de informes.

## Características principales

- **Gestión de Tareas**: Creación, edición y eliminación de tareas con estados (abierta, en progreso, completada, retrasada)
- **Gestión de Usuarios**: Administración completa de usuarios con roles (admin/usuario)
- **Sistema de Asignaciones**: Relación muchos a muchos entre tareas y usuarios
- **Dashboard Administrativo**: Visualización de estadísticas con gráficos de barras y próximos vencimientos
- **Importación/Exportación CSV**: Carga y descarga masiva de datos
- **Informes con JasperReports**: Generación de informes en PDF/HTML con gráficas
- **Búsqueda en tiempo real**: Filtrado instantáneo en todas las tablas
- **Interfaz moderna**: Diseño con tema azul profesional y FontAwesome icons

## Tecnologías utilizadas

- **Java 21**: Lenguaje de programación
- **JavaFX 25**: Framework para la interfaz gráfica
- **Gradle 9.0**: Sistema de construcción y gestión de dependencias
- **PostgreSQL/Supabase**: Base de datos relacional en la nube
- **JasperReports 7.0.1**: Motor de generación de informes
- **OpenCSV**: Procesamiento de archivos CSV
- **BCrypt**: Encriptación de contraseñas
- **FontAwesomeFX**: Iconos para la interfaz

## Requisitos previos

- **JDK 21** o superior
- **Gradle 9.0** o superior (incluido en el wrapper)
- Conexión a Internet (para acceder a la base de datos Supabase)

## Instalación

1. Clonar el repositorio:
```bash
git clone <url-del-repositorio>
cd TaskFlow
```

2. Compilar el proyecto:
```bash
./gradlew build
```

3. Compilar los informes en Jaspersoft Studio (opcional):
   - Abrir los archivos `.jrxml` en `src/main/resources/reports/`
   - Compilar cada uno para generar los `.jasper`

## Ejecución

### Desde línea de comandos:
```bash
./gradlew run
```

### Desde VS Code:
Usar la configuración de lanzamiento incluida (`.vscode/launch.json`)

### Desde IntelliJ IDEA:
Ejecutar la clase `com.taskflow.App`

## Estructura del proyecto

```
TaskFlow/
├── src/main/
│   ├── java/com/taskflow/
│   │   ├── App.java                    # Punto de entrada
│   │   ├── controller/                 # Controladores JavaFX
│   │   │   └── MainController.java
│   │   ├── model/                      # Modelos de datos
│   │   │   ├── Usuario.java
│   │   │   ├── Tarea.java
│   │   │   └── Asignacion.java
│   │   ├── util/                       # Utilidades
│   │   │   ├── DatabaseManager.java    # Gestión BD
│   │   │   ├── CSVManager.java         # Import/Export
│   │   │   └── ReportManager.java      # Informes
│   │   └── enums/                      # Enumeraciones
│   │       ├── RolUsuario.java
│   │       ├── EstadoTarea.java
│   │       └── Prioridad.java
│   └── resources/
│       ├── fxml/
│       │   └── MainView.fxml           # Interfaz principal
│       ├── css/
│       │   └── styles.css              # Estilos
│       └── reports/                     # Informes JasperReports
│           ├── informe_tareas_grafica.jrxml
│           └── informe_asignaciones_filtrado.jrxml
├── informes/                            # Informes generados
├── build.gradle                         # Configuración Gradle
└── README.md
```

## Funcionalidades por pestaña

### 1. Dashboard Admin
- Estadísticas generales del sistema
- Gráfico de barras de tareas por categoría
- Tabla de próximos vencimientos (10 tareas)
- **Sección de Informes**:
  - Informe de tareas con gráfica (incrustado)
  - Informe de asignaciones filtrado (ventana externa)

### 2. Gestión de Usuarios
- Tabla con todos los usuarios
- CRUD completo (Crear, Editar, Eliminar)
- Búsqueda en tiempo real
- Asignación de roles (admin/usuario)
- Contraseñas encriptadas con BCrypt

### 3. Gestión de Tareas
- Tabla con todas las tareas
- CRUD completo con validaciones
- Estados: abierta, en_progreso, completada, retrasada
- Prioridades: baja, media, alta, urgente
- Campos: título, descripción, categoría, fechas, tiempo estimado
- Búsqueda en tiempo real
- Importación desde CSV
- Exportación a CSV

### 4. Asignaciones
- Tabla de relaciones tarea-usuario
- Asignación de usuarios a tareas
- Control de horas asignadas y rol
- Estado de completado por asignación

## Base de datos

### Esquema
El sistema utiliza 3 tablas principales con relación muchos a muchos:

**usuarios**
- id_usuario (PK)
- nombre_completo
- email
- contrasena (bcrypt)
- rol (admin/usuario)
- telefono
- fecha_registro

**tareas**
- id_tarea (PK)
- titulo
- descripcion
- proyecto_categoria
- estado (abierta/en_progreso/completada/retrasada)
- prioridad (baja/media/alta/urgente)
- fecha_creacion
- fecha_limite
- tiempo_estimado_mins
- imagen

**asignaciones** (tabla intermedia)
- id_asignacion (PK)
- tarea_id (FK → tareas)
- usuario_id (FK → usuarios)
- rol_asignacion
- horas_asignadas
- completado
- fecha_asignacion

### Configuración
La conexión a la base de datos se configura en `DatabaseManager.java`:
- Host: Supabase (PostgreSQL en la nube)
- Conexión SSL requerida

## Informes con JasperReports

### Informe 1: Tareas con Gráfica (Incrustado)
- **Tipo**: Informe simple
- **Visualización**: Incrustado en WebView
- **Contenido**: Listado de todas las tareas + Pie chart de distribución por estado
- **Formato**: PDF y HTML
- **SQL**: Query simple sobre tabla `tareas`

### Informe 2: Asignaciones Filtrado (No Incrustado)
- **Tipo**: SQL compuesta con parámetro condicional
- **Visualización**: Ventana externa (navegador/visor)
- **Contenido**: Relación completa de tareas → asignaciones → usuarios
- **Parámetro**: Filtro por estado de tarea (variable desde ComboBox)
- **Formato**: PDF y HTML, orientación Landscape
- **SQL**: JOIN de 3 tablas con cláusula WHERE condicional

Todos los informes generados se guardan en la carpeta `informes/` del proyecto.

## Importación/Exportación CSV

### Formato de importación de tareas
```csv
titulo,descripcion,proyecto_categoria,estado,prioridad,fecha_limite,tiempo_estimado_mins
Tarea ejemplo,Descripción,Categoría,abierta,media,2024-12-31,120
```

### Exportación
Las tareas se pueden exportar a CSV con todos sus campos para respaldo o análisis externo.

## Notas de desarrollo

### Configuration Cache
El `configuration-cache` de Gradle está deshabilitado debido a incompatibilidades con el plugin JavaFX 0.1.0 en Gradle 9.

### JVM Args
La aplicación requiere argumentos JVM específicos para JavaFX:
```
--enable-native-access=ALL-UNNAMED
--enable-native-access=javafx.graphics
```

Estos están configurados automáticamente en `build.gradle`.

## Proyecto académico

Este proyecto ha sido desarrollado como parte del **Proyecto Intermodular** de 2º curso del Ciclo Superior de Desarrollo de Aplicaciones Multiplataforma (DAM), integrando conocimientos de:

- **Desarrollo de Interfaces (DI)**: JavaFX, CSS, UX/UI
- **Acceso a Datos (AD)**: PostgreSQL, JDBC, importación/exportación
- **Programación de Servicios y Procesos (PSP)**: Conexiones, manejo de recursos

## Autor

**Kristian Olav García Paulsen**
Ciclo Superior DAM - 2º Curso
IES Politécnico Hermengildo Lanz

## Licencia

Este proyecto es de carácter académico y no tiene licencia de distribución comercial.
