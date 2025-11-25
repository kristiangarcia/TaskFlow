-- ============================================
-- SINCRONIZACION DE AUTH.USERS CON USUARIOS
-- Para Supabase Authentication
-- ============================================

-- Funcion para sincronizar cuando se crea un usuario en auth.users
CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO public.usuarios (
        id_usuario,
        nombre_completo,
        email,
        contraseña_hash,
        rol,
        telefono,
        activo,
        fecha_registro
    )
    VALUES (
        NEW.id::integer,
        COALESCE(NEW.raw_user_meta_data->>'nombre_completo', split_part(NEW.email, '@', 1)),
        NEW.email,
        '',  -- La contraseña ya esta en auth.users, no la duplicamos
        COALESCE((NEW.raw_user_meta_data->>'rol')::text, 'empleado'),
        NEW.raw_user_meta_data->>'telefono',
        true,
        CURRENT_DATE
    );
    RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- Trigger que se ejecuta cuando se crea un usuario
CREATE TRIGGER on_auth_user_created
AFTER INSERT ON auth.users
FOR EACH ROW
EXECUTE FUNCTION public.handle_new_user();

-- ============================================
-- FUNCIONES DE AUTENTICACION
-- ============================================

-- Funcion para verificar credenciales (alternativa mas segura)
CREATE OR REPLACE FUNCTION public.verificar_credenciales(
    p_email TEXT,
    p_password TEXT
)
RETURNS TABLE (
    id_usuario INTEGER,
    nombre_completo VARCHAR,
    email VARCHAR,
    rol VARCHAR,
    telefono VARCHAR,
    activo BOOLEAN
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        u.id_usuario,
        u.nombre_completo,
        u.email,
        u.rol,
        u.telefono,
        u.activo
    FROM usuarios u
    WHERE u.email = p_email
    AND u.activo = true
    AND u.contraseña_hash = crypt(p_password, u.contraseña_hash);
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;
