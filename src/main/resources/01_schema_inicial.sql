-- ============================================
-- TASKFLOW - SENTENCIAS SQL DDL (CORREGIDO)
-- Base de datos: PostgreSQL (Supabase)
-- ============================================

-- ============================================
-- TABLA: USUARIOS
-- ============================================
CREATE TABLE usuarios (
    id_usuario SERIAL PRIMARY KEY,
    nombre_completo VARCHAR(100) NOT NULL CHECK (LENGTH(nombre_completo) >= 3),
    email VARCHAR(150) NOT NULL UNIQUE CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    rol VARCHAR(20) NOT NULL CHECK (rol IN ('admin', 'empleado')),
    foto_perfil BYTEA, -- BLOB en PostgreSQL es BYTEA
    fecha_registro DATE NOT NULL DEFAULT CURRENT_DATE,
    telefono VARCHAR(15) CHECK (telefono ~* '^\+?[0-9\s\-()]+$'),
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

-- Índices para optimizar búsquedas
CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_usuarios_rol ON usuarios(rol);
CREATE INDEX idx_usuarios_activo ON usuarios(activo);

-- Comentarios descriptivos
COMMENT ON TABLE usuarios IS 'Empleados y administradores del sistema TaskFlow';
COMMENT ON COLUMN usuarios.foto_perfil IS 'Imagen de perfil en formato binario (JPG, PNG, max 5MB)';
COMMENT ON COLUMN usuarios.activo IS 'Indica si el usuario puede acceder al sistema';

-- ============================================
-- TABLA: TAREAS
-- ============================================
CREATE TABLE tareas (
    id_tarea SERIAL PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL CHECK (LENGTH(titulo) >= 3),
    descripcion TEXT CHECK (LENGTH(descripcion) <= 1000),
    proyecto_categoria VARCHAR(100) NOT NULL, -- Campo de TEXTO LIBRE, NO foreign key
    estado VARCHAR(20) NOT NULL CHECK (estado IN ('abierta', 'en_progreso', 'completada', 'retrasada')),
    prioridad VARCHAR(10) NOT NULL CHECK (prioridad IN ('alta', 'media', 'baja')),
    fecha_limite DATE NOT NULL, -- Sin CHECK de fecha futura (se valida en trigger)
    tiempo_estimado_mins INTEGER NOT NULL CHECK (tiempo_estimado_mins BETWEEN 15 AND 999),
    imagen BYTEA, -- Imagen adjunta opcional
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices para optimizar filtros y búsquedas
CREATE INDEX idx_tareas_estado ON tareas(estado);
CREATE INDEX idx_tareas_prioridad ON tareas(prioridad);
CREATE INDEX idx_tareas_categoria ON tareas(proyecto_categoria);
CREATE INDEX idx_tareas_fecha_limite ON tareas(fecha_limite);
CREATE INDEX idx_tareas_fecha_creacion ON tareas(fecha_creacion);

-- Comentarios descriptivos
COMMENT ON TABLE tareas IS 'Unidades de trabajo individuales con tiempos estimados y deadlines';
COMMENT ON COLUMN tareas.proyecto_categoria IS 'Categoría o proyecto (TEXTO LIBRE para agrupar tareas, ej: "Desarrollo Web", "Marketing Digital") - NO es foreign key';
COMMENT ON COLUMN tareas.tiempo_estimado_mins IS 'Tiempo estimado en minutos (usado por IA para predicciones)';
COMMENT ON COLUMN tareas.imagen IS 'Imagen adjunta a la tarea (JPG, PNG, max 5MB)';
COMMENT ON COLUMN tareas.fecha_limite IS 'Fecha límite de finalización (debe ser posterior a fecha_creacion, validado por trigger)';

-- ============================================
-- TABLA: ASIGNACIONES (Intermedia M:N)
-- ============================================
CREATE TABLE asignaciones (
    id_asignacion SERIAL PRIMARY KEY,
    usuario_id INTEGER NOT NULL,
    tarea_id INTEGER NOT NULL,
    rol_asignacion VARCHAR(30) NOT NULL CHECK (rol_asignacion IN ('responsable_principal', 'colaborador', 'revisor')),
    fecha_asignacion DATE NOT NULL DEFAULT CURRENT_DATE,
    horas_asignadas DECIMAL(5,2) CHECK (horas_asignadas BETWEEN 0.5 AND 40.0),
    completado BOOLEAN NOT NULL DEFAULT FALSE,
    notas TEXT CHECK (LENGTH(notas) <= 500),

    -- Claves foráneas con DELETE CASCADE
    CONSTRAINT fk_asignaciones_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id_usuario)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_asignaciones_tarea
        FOREIGN KEY (tarea_id)
        REFERENCES tareas(id_tarea)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    -- RESTRICCION UNICA: un usuario no puede estar asignado dos veces a la misma tarea
    CONSTRAINT unique_usuario_tarea UNIQUE (usuario_id, tarea_id)
);

-- Índices para optimizar consultas de asignaciones
CREATE INDEX idx_asignaciones_usuario ON asignaciones(usuario_id);
CREATE INDEX idx_asignaciones_tarea ON asignaciones(tarea_id);
CREATE INDEX idx_asignaciones_rol ON asignaciones(rol_asignacion);
CREATE INDEX idx_asignaciones_completado ON asignaciones(completado);

-- Comentarios descriptivos
COMMENT ON TABLE asignaciones IS 'Tabla intermedia que relaciona usuarios con tareas (relación M:N: 1 usuario → N asignaciones, 1 tarea → M asignaciones)';
COMMENT ON COLUMN asignaciones.rol_asignacion IS 'Rol del usuario en la tarea específica';
COMMENT ON COLUMN asignaciones.horas_asignadas IS 'Horas semanales dedicadas a esta tarea';
COMMENT ON COLUMN asignaciones.completado IS 'Indica si el usuario completó su parte de la tarea';

-- ============================================
-- TRIGGERS PARA VALIDACIONES AVANZADAS
-- ============================================

-- Trigger: Validar que fecha_limite sea posterior a fecha_creacion SOLO EN INSERT
CREATE OR REPLACE FUNCTION validar_fecha_limite_insert()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.fecha_limite <= NEW.fecha_creacion::DATE THEN
        RAISE EXCEPTION 'La fecha límite (%) debe ser posterior a la fecha de creación (%)',
            NEW.fecha_limite, NEW.fecha_creacion::DATE;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_validar_fecha_limite_insert
BEFORE INSERT ON tareas
FOR EACH ROW
EXECUTE FUNCTION validar_fecha_limite_insert();

-- Trigger: En UPDATE, permitir cualquier fecha_limite pero advertir si es pasada
CREATE OR REPLACE FUNCTION validar_fecha_limite_update()
RETURNS TRIGGER AS $$
BEGIN
    -- Si se actualiza fecha_limite a una fecha pasada, actualizar estado a 'retrasada'
    IF NEW.fecha_limite < CURRENT_DATE AND NEW.estado NOT IN ('completada', 'retrasada') THEN
        NEW.estado := 'retrasada';
        RAISE NOTICE 'La tarea "%" tiene fecha límite pasada. Estado actualizado a "retrasada".', NEW.titulo;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_validar_fecha_limite_update
BEFORE UPDATE ON tareas
FOR EACH ROW
WHEN (OLD.fecha_limite IS DISTINCT FROM NEW.fecha_limite)
EXECUTE FUNCTION validar_fecha_limite_update();

-- ============================================
-- FUNCIONES ÚTILES
-- ============================================

-- Función: Actualizar estado de tareas retrasadas automáticamente
CREATE OR REPLACE FUNCTION actualizar_tareas_retrasadas()
RETURNS INTEGER AS $$
DECLARE
    tareas_actualizadas INTEGER;
BEGIN
    UPDATE tareas
    SET estado = 'retrasada'
    WHERE fecha_limite < CURRENT_DATE
    AND estado NOT IN ('completada', 'retrasada');

    GET DIAGNOSTICS tareas_actualizadas = ROW_COUNT;
    RETURN tareas_actualizadas;
END;
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION actualizar_tareas_retrasadas() IS 'Ejecutar diariamente para marcar tareas vencidas como retrasadas. Retorna número de tareas actualizadas.';
