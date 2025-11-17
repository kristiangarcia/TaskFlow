package com.taskflow.model;

/**
 * Estados posibles de una tarea en el sistema TaskFlow
 * Los valores coinciden exactamente con la columna 'estado' de la tabla tareas en Supabase
 */
public enum EstadoTarea {
    abierta,        // Tarea nueva sin iniciar
    en_progreso,    // Tarea en desarrollo
    completada,     // Tarea finalizada
    retrasada;      // Tarea con fecha límite vencida

    @Override
    public String toString() {
        return this.name();
    }
}
