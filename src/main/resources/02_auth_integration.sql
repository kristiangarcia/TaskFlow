-- ============================================
-- ACTUALIZACION SCHEMA PARA SUPABASE AUTH
-- ============================================

-- Agregar columna auth_id para vincular con auth.users
ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS auth_id UUID UNIQUE;

-- Crear indice para busquedas rapidas
CREATE INDEX IF NOT EXISTS idx_usuarios_auth_id ON usuarios(auth_id);

-- ============================================
-- TRIGGER PARA SINCRONIZAR AUTH.USERS
-- ============================================

-- Funcion que se ejecuta cuando se crea un usuario en auth.users
CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO public.usuarios (
        auth_id,
        nombre_completo,
        email,
        rol,
        telefono,
        activo,
        fecha_registro
    )
    VALUES (
        NEW.id,
        COALESCE(NEW.raw_user_meta_data->>'nombre_completo', split_part(NEW.email, '@', 1)),
        NEW.email,
        COALESCE((NEW.raw_user_meta_data->>'rol')::text, 'empleado'),
        NEW.raw_user_meta_data->>'telefono',
        true,
        CURRENT_DATE
    );
    RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- Crear trigger para INSERT
DROP TRIGGER IF EXISTS on_auth_user_created ON auth.users;
CREATE TRIGGER on_auth_user_created
AFTER INSERT ON auth.users
FOR EACH ROW
EXECUTE FUNCTION public.handle_new_user();

-- ============================================
-- TRIGGER PARA SINCRONIZAR ELIMINACION
-- ============================================

-- Funcion que se ejecuta cuando se elimina un usuario en auth.users
CREATE OR REPLACE FUNCTION public.handle_user_deleted()
RETURNS TRIGGER AS $$
BEGIN
    DELETE FROM public.usuarios WHERE auth_id = OLD.id;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- Crear trigger para DELETE
DROP TRIGGER IF EXISTS on_auth_user_deleted ON auth.users;
CREATE TRIGGER on_auth_user_deleted
AFTER DELETE ON auth.users
FOR EACH ROW
EXECUTE FUNCTION public.handle_user_deleted();

-- ============================================
-- FUNCION HELPER PARA OBTENER ID_USUARIO
-- ============================================

-- Funcion para obtener id_usuario desde auth.uid()
CREATE OR REPLACE FUNCTION public.get_usuario_id()
RETURNS INTEGER AS $$
DECLARE
    user_id INTEGER;
BEGIN
    SELECT id_usuario INTO user_id
    FROM usuarios
    WHERE auth_id = auth.uid();

    RETURN user_id;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;
