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

    // Validaciones
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MIN_TELEFONO_LENGTH = 9;
    public static final int MAX_TELEFONO_LENGTH = 15;

    // Mensajes de validacion
    public static final String MSG_NOMBRE_OBLIGATORIO = "El nombre es obligatorio";
    public static final String MSG_EMAIL_OBLIGATORIO = "El email es obligatorio";
    public static final String MSG_EMAIL_INVALIDO = "El formato del email no es valido";
    public static final String MSG_TELEFONO_OBLIGATORIO = "El telefono es obligatorio";
    public static final String MSG_TELEFONO_INVALIDO = "El telefono debe contener solo numeros";
    public static final String MSG_PASSWORD_OBLIGATORIO = "La contraseña es obligatoria";
    public static final String MSG_PASSWORD_CORTO = "La contraseña debe tener al menos %d caracteres";
    public static final String MSG_ROL_OBLIGATORIO = "Debe seleccionar un rol";
    public static final String MSG_TITULO_OBLIGATORIO = "El titulo es obligatorio";
    public static final String MSG_DESCRIPCION_OBLIGATORIA = "La descripcion es obligatoria";
    public static final String MSG_CATEGORIA_OBLIGATORIA = "La categoria es obligatoria";
    public static final String MSG_PRIORIDAD_OBLIGATORIA = "Debe seleccionar una prioridad";
    public static final String MSG_ESTADO_OBLIGATORIO = "Debe seleccionar un estado";
    public static final String MSG_FECHA_OBLIGATORIA = "La fecha limite es obligatoria";
    public static final String MSG_FECHA_INVALIDA = "La fecha limite no puede ser anterior a hoy";
    public static final String MSG_TIEMPO_POSITIVO = "El tiempo estimado debe ser mayor a 0";
    public static final String MSG_TIEMPO_INVALIDO = "El tiempo estimado debe ser un numero valido";

    // Titulos de alertas
    public static final String TITULO_EXITO = "Exito";
    public static final String TITULO_ERROR = "Error";
    public static final String TITULO_VALIDACION = "Validacion de campos";

    // Mensajes de exito/error
    public static final String MSG_USUARIO_CREADO = "Usuario creado correctamente";
    public static final String MSG_USUARIO_ERROR = "No se pudo crear el usuario";
    public static final String MSG_TAREA_CREADA = "Tarea creada correctamente";
    public static final String MSG_TAREA_ERROR = "No se pudo crear la tarea";

    // Constructor privado para prevenir instanciación
    private Constants() {
        throw new AssertionError("Cannot instantiate Constants class");
    }
}
