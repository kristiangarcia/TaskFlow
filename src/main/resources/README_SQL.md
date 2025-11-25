# Scripts SQL para Supabase

Ejecutar en este orden en el SQL Editor de Supabase:

## 1. Schema Inicial
**Archivo:** `01_schema_inicial.sql`

Crea todas las tablas, triggers, funciones y datos de ejemplo:
- Tabla usuarios
- Tabla tareas
- Tabla asignaciones
- Triggers de validacion
- Vistas y funciones utiles
- Datos de ejemplo para testing

## 2. Integracion con Supabase Auth
**Archivo:** `02_auth_integration.sql`

Integra Supabase Authentication con la tabla usuarios:
- Agrega columna `auth_id` (UUID) a usuarios
- Crea trigger para sincronizar auth.users con usuarios
- Cuando se crea un usuario en Supabase Auth, automaticamente se crea en la tabla usuarios
- Incluye funcion helper para obtener id_usuario desde auth.uid()

## 3. Row Level Security (RLS)
**Archivo:** `03_row_level_security.sql`

Configura las politicas de seguridad a nivel de fila:
- Admins pueden ver/editar/eliminar todo
- Empleados solo pueden ver sus propias tareas y asignaciones
- Empleados pueden actualizar su perfil
- Todas las politicas usan `auth_id` para vincularse con Supabase Auth

## Orden de Ejecucion

```sql
-- 1. Ejecutar schema inicial
-- (copiar y pegar contenido de 01_schema_inicial.sql)

-- 2. Ejecutar integracion auth
-- (copiar y pegar contenido de 02_auth_integration.sql)

-- 3. Ejecutar RLS
-- (copiar y pegar contenido de 03_row_level_security.sql)
```

## Crear Usuario Admin para Testing

Despues de ejecutar los 3 archivos, crear un usuario admin:

```sql
-- Insertar admin inicial
INSERT INTO usuarios (
    nombre_completo,
    email,
    contraseña_hash,
    rol,
    telefono,
    activo
) VALUES (
    'Administrador',
    'admin@taskflow.com',
    'admin123',
    'admin',
    '+34600000000',
    true
);
```

Credenciales para login:
- Email: `admin@taskflow.com`
- Password: `admin123`

## Notas Importantes

- La columna `auth_id` se usa para vincular con Supabase Auth
- Por ahora usamos contraseñas en texto plano para desarrollo
- En produccion debes usar Supabase Auth completamente o bcrypt
- El trigger sincroniza automaticamente nuevos usuarios de auth.users
