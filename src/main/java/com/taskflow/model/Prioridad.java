package com.taskflow.model;

/**
 * Prioridades de las tareas en el sistema TaskFlow
 * Los valores coinciden exactamente con la columna 'prioridad' de la tabla tareas en Supabase
 */
public enum Prioridad {
    alta,    // Prioridad alta (urgente)
    media,   // Prioridad media (importante)
    baja;    // Prioridad baja (puede esperar)

    @Override
    public String toString() {
        return this.name();
    }
}
