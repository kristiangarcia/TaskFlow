package com.taskflow.model;

/**
 * Roles de usuario en el sistema TaskFlow
 * Los valores coinciden exactamente con la columna 'rol' de la tabla usuarios en Supabase
 */
public enum Rol {
    admin,      // Administrador del sistema
    empleado;   // Usuario empleado

    @Override
    public String toString() {
        return this.name();
    }
}
