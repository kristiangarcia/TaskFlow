-- ============================================
-- TASKFLOW - ROW LEVEL SECURITY (RLS)
-- Politicas de seguridad a nivel de fila
-- ============================================

-- Habilitar RLS en todas las tablas
ALTER TABLE usuarios ENABLE ROW LEVEL SECURITY;
ALTER TABLE tareas ENABLE ROW LEVEL SECURITY;
ALTER TABLE asignaciones ENABLE ROW LEVEL SECURITY;

-- ============================================
-- POLITICAS PARA TABLA USUARIOS
-- ============================================

-- Admin puede ver todos los usuarios
CREATE POLICY admin_view_all_usuarios ON usuarios
FOR SELECT
TO authenticated
USING (
    EXISTS (
        SELECT 1 FROM usuarios
        WHERE id_usuario = auth.uid()::integer
        AND rol = 'admin'
    )
);

-- Admin puede insertar usuarios
CREATE POLICY admin_insert_usuarios ON usuarios
FOR INSERT
TO authenticated
WITH CHECK (
    EXISTS (
        SELECT 1 FROM usuarios
        WHERE id_usuario = auth.uid()::integer
        AND rol = 'admin'
    )
);

-- Admin puede actualizar usuarios
CREATE POLICY admin_update_usuarios ON usuarios
FOR UPDATE
TO authenticated
USING (
    EXISTS (
        SELECT 1 FROM usuarios
        WHERE id_usuario = auth.uid()::integer
        AND rol = 'admin'
    )
);

-- Admin puede eliminar usuarios
CREATE POLICY admin_delete_usuarios ON usuarios
FOR DELETE
TO authenticated
USING (
    EXISTS (
        SELECT 1 FROM usuarios
        WHERE id_usuario = auth.uid()::integer
        AND rol = 'admin'
    )
);

-- Empleados pueden ver su propio perfil
CREATE POLICY empleado_view_own_usuario ON usuarios
FOR SELECT
TO authenticated
USING (id_usuario = auth.uid()::integer);

-- Empleados pueden actualizar su propio perfil
CREATE POLICY empleado_update_own_usuario ON usuarios
FOR UPDATE
TO authenticated
USING (id_usuario = auth.uid()::integer)
WITH CHECK (id_usuario = auth.uid()::integer);

-- ============================================
-- POLITICAS PARA TABLA TAREAS
-- ============================================

-- Admin puede ver todas las tareas
CREATE POLICY admin_view_all_tareas ON tareas
FOR SELECT
TO authenticated
USING (
    EXISTS (
        SELECT 1 FROM usuarios
        WHERE id_usuario = auth.uid()::integer
        AND rol = 'admin'
    )
);

-- Admin puede insertar tareas
CREATE POLICY admin_insert_tareas ON tareas
FOR INSERT
TO authenticated
WITH CHECK (
    EXISTS (
        SELECT 1 FROM usuarios
        WHERE id_usuario = auth.uid()::integer
        AND rol = 'admin'
    )
);

-- Admin puede actualizar tareas
CREATE POLICY admin_update_tareas ON tareas
FOR UPDATE
TO authenticated
USING (
    EXISTS (
        SELECT 1 FROM usuarios
        WHERE id_usuario = auth.uid()::integer
        AND rol = 'admin'
    )
);

-- Admin puede eliminar tareas
CREATE POLICY admin_delete_tareas ON tareas
FOR DELETE
TO authenticated
USING (
    EXISTS (
        SELECT 1 FROM usuarios
        WHERE id_usuario = auth.uid()::integer
        AND rol = 'admin'
    )
);

-- Empleados pueden ver tareas asignadas a ellos
CREATE POLICY empleado_view_assigned_tareas ON tareas
FOR SELECT
TO authenticated
USING (
    EXISTS (
        SELECT 1 FROM asignaciones
        WHERE tarea_id = id_tarea
        AND usuario_id = auth.uid()::integer
    )
);

-- Empleados pueden actualizar solo estado y notas de sus tareas
CREATE POLICY empleado_update_assigned_tareas ON tareas
FOR UPDATE
TO authenticated
USING (
    EXISTS (
        SELECT 1 FROM asignaciones
        WHERE tarea_id = id_tarea
        AND usuario_id = auth.uid()::integer
    )
);

-- ============================================
-- POLITICAS PARA TABLA ASIGNACIONES
-- ============================================

-- Admin puede ver todas las asignaciones
CREATE POLICY admin_view_all_asignaciones ON asignaciones
FOR SELECT
TO authenticated
USING (
    EXISTS (
        SELECT 1 FROM usuarios
        WHERE id_usuario = auth.uid()::integer
        AND rol = 'admin'
    )
);

-- Admin puede insertar asignaciones
CREATE POLICY admin_insert_asignaciones ON asignaciones
FOR INSERT
TO authenticated
WITH CHECK (
    EXISTS (
        SELECT 1 FROM usuarios
        WHERE id_usuario = auth.uid()::integer
        AND rol = 'admin'
    )
);

-- Admin puede actualizar asignaciones
CREATE POLICY admin_update_asignaciones ON asignaciones
FOR UPDATE
TO authenticated
USING (
    EXISTS (
        SELECT 1 FROM usuarios
        WHERE id_usuario = auth.uid()::integer
        AND rol = 'admin'
    )
);

-- Admin puede eliminar asignaciones
CREATE POLICY admin_delete_asignaciones ON asignaciones
FOR DELETE
TO authenticated
USING (
    EXISTS (
        SELECT 1 FROM usuarios
        WHERE id_usuario = auth.uid()::integer
        AND rol = 'admin'
    )
);

-- Empleados pueden ver sus propias asignaciones
CREATE POLICY empleado_view_own_asignaciones ON asignaciones
FOR SELECT
TO authenticated
USING (usuario_id = auth.uid()::integer);

-- Empleados pueden actualizar sus propias asignaciones (completado, notas)
CREATE POLICY empleado_update_own_asignaciones ON asignaciones
FOR UPDATE
TO authenticated
USING (usuario_id = auth.uid()::integer)
WITH CHECK (usuario_id = auth.uid()::integer);
