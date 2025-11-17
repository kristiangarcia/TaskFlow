package com.taskflow.util;

public class Constants {
    // Constantes de aplicación
    public static final String APP_TITLE = "TaskFlow";
    public static final String APP_VERSION = "1.0.0";

    // Constantes de interfaz de usuario
    public static final int WINDOW_WIDTH = 1200;
    public static final int WINDOW_HEIGHT = 800;

    // Nombres de columnas de tabla
    public static final String COL_ID = "ID";
    public static final String COL_TITULO = "Título";
    public static final String COL_NOMBRE = "Nombre";
    public static final String COL_EMAIL = "Email";
    public static final String COL_ROL = "Rol";
    public static final String COL_ACTIVO = "Activo";
    public static final String COL_ESTADO = "Estado";
    public static final String COL_PRIORIDAD = "Prioridad";
    public static final String COL_FECHA_LIMITE = "Fecha Límite";
    public static final String COL_CATEGORIA = "Categoría";
    public static final String COL_ASIGNADOS = "Asignados";
    public static final String COL_ACCIONES = "Acciones";
    public static final String COL_ACCION = "Acción";

    // Opciones de filtro
    public static final String FILTRO_TODOS = "Todos";

    // Constructor privado para prevenir instanciación
    private Constants() {
        throw new AssertionError("Cannot instantiate Constants class");
    }
}
