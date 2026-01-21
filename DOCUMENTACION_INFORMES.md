# Documentación de Informes - TaskFlow

## Proyecto: TaskFlow - Sistema de Gestión de Tareas
**Alumno:** Kristian Olav García Paulsen
**Centro:** IES Politécnico Hermengildo Lanz
**Asignatura:** Desarrollo de Interfaces - 2º DAM
**Tema:** 3 - Informes con JasperReports

---

## 1. INTERFAZ DE USUARIO

### Ubicación de los controles

Los controles para generar informes se encuentran en la **pestaña "Dashboard Admin"** de la aplicación TaskFlow, en la sección "Informes".

### Controles disponibles:

1. **Botón "Informe de Tareas con Gráfica (Incrustado)"**
   - **Icono:** Gráfico de barras
   - **Estilo:** Botón azul primario (btn-primary)
   - **Ubicación:** Pestaña "Dashboard Admin", sección "Informes"
   - **Acción:** Genera y muestra el informe de tareas con gráfica de pastel incrustado en un WebView

2. **ComboBox "Filtrar por estado"**
   - **Opciones:** Todos, abierta, en_progreso, completada, retrasada
   - **Ubicación:** Entre los dos botones de informes
   - **Función:** Permite seleccionar el estado de las tareas para filtrar el informe de asignaciones

3. **Botón "Informe de Asignaciones (Ventana Nueva)"**
   - **Icono:** Documento de texto
   - **Estilo:** Botón verde (btn-assign)
   - **Ubicación:** Pestaña "Dashboard Admin", sección "Informes"
   - **Acción:** Genera el informe de asignaciones filtrado por el estado seleccionado y lo abre en una ventana externa

4. **WebView "Vista de Informe"**
   - **Ubicación:** Debajo de los botones de informes
   - **Función:** Muestra el informe incrustado (HTML) del informe de tareas con gráfica
   - **Borde:** Azul con padding de 10px

---

## 2. INFORME 1: TAREAS CON GRÁFICA (INCRUSTADO)

### Descripción
Informe que lista todas las tareas del sistema ordenadas por fecha límite y prioridad, mostrando en la sección resumen una **gráfica de pastel (pie chart)** con la distribución de tareas por estado.

### Características
- **Tipo:** Informe simple (tabla independiente: `tareas`)
- **Formato:** Incrustado en WebView (HTML)
- **Orientación:** Portrait (595x842 - A4)
- **Gráfica:** Pie chart mostrando distribución de tareas por estado
- **Colores:** Tema TaskFlow (azul #3498DB, gris oscuro #2C3E50)

### Cumple requisitos:
- ✓ **a) Informe simple** - Tabla independiente `tareas`
- ✓ **d) Gráficas** - Pie chart con distribución por estados
- ✓ **e) Incrustado** - Se muestra en WebView dentro de la aplicación

### SQL Principal (Listado de tareas)
```sql
SELECT
    id_tarea,
    titulo,
    proyecto_categoria,
    estado,
    prioridad,
    fecha_limite,
    tiempo_estimado_mins,
    imagen,
    fecha_creacion
FROM tareas
ORDER BY fecha_limite ASC, prioridad DESC
```

### SQL del Subdataset (Para la gráfica)
```sql
SELECT
    COALESCE(estado, 'Sin estado') AS estado,
    COUNT(*) AS cantidad
FROM tareas
GROUP BY estado
ORDER BY cantidad DESC
```

### Campos mostrados en el informe:
- **ID Tarea:** Identificador único
- **Título:** Nombre descriptivo de la tarea
- **Categoría:** Proyecto o categoría a la que pertenece
- **Estado:** Estado actual (abierta, en_progreso, completada, retrasada)
- **Prioridad:** Nivel de prioridad (baja, media, alta, urgente)
- **Fecha Límite:** Fecha de vencimiento
- **Tiempo Estimado:** Tiempo estimado en minutos

### Secciones del informe:
1. **Title:** Encabezado con logo y título "TaskFlow - Informe de Tareas"
2. **Column Header:** Encabezados de columnas con fondo azul oscuro
3. **Detail:** Filas de datos con fondo alternado (blanco/azul claro)
4. **Page Footer:** Número de página y nombre de la aplicación
5. **Summary:** Total de tareas y gráfica de pastel con distribución por estado

---

## 3. INFORME 2: ASIGNACIONES FILTRADO (NO INCRUSTADO)

### Descripción
Informe que relaciona las tres tablas del sistema (`tareas`, `asignaciones`, `usuarios`) mostrando información completa de las asignaciones. Incluye un **parámetro condicional** que permite filtrar por estado de tarea.

### Características
- **Tipo:** SQL compuesta con JOIN de 3 tablas + parámetro condicional
- **Formato:** No incrustado (se abre en aplicación externa)
- **Orientación:** Landscape (842x595 - A4 horizontal)
- **Parámetro:** `estado_filtro` - Filtra tareas por estado
- **Colores:** Tema TaskFlow (azul #3498DB, gris oscuro #2C3E50)

### Cumple requisitos:
- ✓ **b) Informe condicional** - Parámetro `estado_filtro` variable desde GUI
- ✓ **c) SQL compuesta** - JOIN de 3 tablas (tareas → asignaciones → usuarios)
- ✓ **e) No incrustado** - Se abre en aplicación externa (navegador/visor PDF)

### SQL Compuesta
```sql
SELECT
    t.id_tarea,
    t.titulo,
    t.proyecto_categoria,
    t.estado,
    t.prioridad,
    t.fecha_limite,
    t.tiempo_estimado_mins,
    u.nombre_completo AS usuario_asignado,
    u.email AS usuario_email,
    u.telefono AS usuario_telefono,
    a.rol_asignacion,
    a.horas_asignadas,
    a.completado AS asignacion_completada,
    a.fecha_asignacion
FROM tareas t
LEFT JOIN asignaciones a ON t.id_tarea = a.tarea_id
LEFT JOIN usuarios u ON a.usuario_id = u.id_usuario
WHERE ($P{estado_filtro} IS NULL OR t.estado = $P{estado_filtro})
ORDER BY t.fecha_limite ASC, t.prioridad DESC, t.id_tarea
```

### Parámetro condicional:
- **Nombre:** `estado_filtro`
- **Tipo:** `java.lang.String`
- **Valor por defecto:** `null` (muestra todas las tareas)
- **Valores posibles:** `abierta`, `en_progreso`, `completada`, `retrasada`, o `null` (Todos)
- **Control en GUI:** ComboBox en la sección de Informes

### Campos mostrados en el informe:
- **ID:** Identificador de tarea
- **Título Tarea:** Nombre de la tarea
- **Categoría:** Proyecto/categoría
- **Estado:** Estado de la tarea
- **Prioridad:** Nivel de prioridad
- **Fecha Límite:** Fecha de vencimiento
- **Usuario Asignado:** Nombre completo del usuario
- **Email:** Correo electrónico del usuario
- **Rol:** Rol en la asignación
- **Horas:** Horas asignadas al usuario

### Secciones del informe:
1. **Title:** Encabezado azul con título, subtítulo, fecha y filtro aplicado
2. **Column Header:** Encabezados de 10 columnas con fondo azul oscuro
3. **Detail:** Filas de datos con fondo alternado
4. **Page Footer:** Numeración de páginas
5. **Summary:** Total de registros y suma de horas asignadas

---

## 4. ARCHIVOS GENERADOS

La carpeta `informes/` contiene los siguientes archivos:

### Informe de Tareas con Gráfica:
- `informe_tareas_grafica.pdf` - Versión PDF
- `informe_tareas_grafica.html` - Versión HTML (para WebView)
- `informe_tareas_grafica.html_files/` - Recursos del HTML (imágenes de la gráfica)

### Informe de Asignaciones Filtrado:
- `informe_asignaciones_filtrado.pdf` - Versión PDF
- `informe_asignaciones_filtrado.html` - Versión HTML

---

## 5. CÓDIGO RELEVANTE

### ReportManager.java
Clase singleton que gestiona la generación de informes:

```java
public class ReportManager {
    // Método para informe incrustado con gráfica
    public void generarInformeTareasGrafica(WebView embeddedWebView)

    // Método para informe filtrado no incrustado
    public void generarInformeAsignacionesFiltrado(String estadoFiltro)
}
```

### MainController.java
Handlers de los botones:

```java
@FXML
private void handleInformeTareasGrafica() {
    ReportManager.getInstance().generarInformeTareasGrafica(webViewInforme);
}

@FXML
private void handleInformeAsignaciones() {
    String estadoFiltro = comboEstadoFiltro.getSelectionModel().getSelectedItem();
    if ("Todos".equals(estadoFiltro)) estadoFiltro = null;
    ReportManager.getInstance().generarInformeAsignacionesFiltrado(estadoFiltro);
}
```

---

## 6. DEPENDENCIAS UTILIZADAS

```gradle
// JasperReports para generación de informes
implementation 'net.sf.jasperreports:jasperreports:7.0.1'
implementation 'net.sf.jasperreports:jasperreports-pdf:7.0.0'
implementation 'net.sf.jasperreports:jasperreports-fonts:7.0.1'
implementation 'net.sf.jasperreports:jasperreports-charts:7.0.1'

// JavaFX Web para WebView (incrustado)
javafx.web
```

---

## 7. RESUMEN DE CUMPLIMIENTO DE REQUISITOS

| Requisito | Cumplimiento | Informe | Detalle |
|-----------|--------------|---------|---------|
| **a) Informe simple** | ✓ | Tareas con Gráfica | Tabla independiente `tareas` |
| **b) Informe condicional** | ✓ | Asignaciones Filtrado | Parámetro `estado_filtro` variable |
| **c) SQL compuesta** | ✓ | Asignaciones Filtrado | JOIN de 3 tablas |
| **d) Gráficas** | ✓ | Tareas con Gráfica | Pie chart con distribución |
| **e) Incrustado** | ✓ | Tareas con Gráfica | WebView en Dashboard |
| **e) No incrustado** | ✓ | Asignaciones Filtrado | Abre en app externa |

---
