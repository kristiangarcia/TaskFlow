package com.taskflow.util;

import com.taskflow.model.Asignacion;
import com.taskflow.model.Tarea;
import com.taskflow.model.Usuario;
import javafx.collections.ObservableList;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Gestor de exportación de datos a CSV
 */
public class ExportManager {

    /**
     * Exporta lista de usuarios a archivo CSV
     */
    public static boolean exportarUsuariosCSV(ObservableList<Usuario> usuarios, String rutaArchivo) {
        try (FileWriter writer = new FileWriter(rutaArchivo)) {
            // Encabezados
            writer.append("ID,Nombre,Email,Teléfono,Rol,Activo,Fecha Registro\n");

            // Datos
            for (Usuario usuario : usuarios) {
                writer.append(escaparCSV(String.valueOf(usuario.getIdUsuario()))).append(",");
                writer.append(escaparCSV(usuario.getNombreCompleto())).append(",");
                writer.append(escaparCSV(usuario.getEmail())).append(",");
                writer.append(escaparCSV(usuario.getTelefono() != null ? usuario.getTelefono() : "")).append(",");
                writer.append(escaparCSV(usuario.getRol().name())).append(",");
                writer.append(usuario.isActivo() ? "Sí" : "No").append(",");
                writer.append(usuario.getFechaRegistro() != null ? usuario.getFechaRegistro().toString() : "").append("\n");
            }

            return true;
        } catch (IOException e) {
            System.err.println("Error exportando usuarios: " + e.getMessage());
            return false;
        }
    }

    /**
     * Exporta lista de tareas a archivo CSV
     */
    public static boolean exportarTareasCSV(ObservableList<Tarea> tareas, String rutaArchivo) {
        try (FileWriter writer = new FileWriter(rutaArchivo)) {
            // Encabezados
            writer.append("ID,Título,Descripción,Categoría,Estado,Prioridad,Fecha Límite,Tiempo Estimado (mins),Fecha Creación\n");

            // Datos
            for (Tarea tarea : tareas) {
                writer.append(escaparCSV(String.valueOf(tarea.getIdTarea()))).append(",");
                writer.append(escaparCSV(tarea.getTitulo())).append(",");
                writer.append(escaparCSV(tarea.getDescripcion())).append(",");
                writer.append(escaparCSV(tarea.getProyectoCategoria() != null ? tarea.getProyectoCategoria() : "")).append(",");
                writer.append(escaparCSV(tarea.getEstado().name())).append(",");
                writer.append(escaparCSV(tarea.getPrioridad().name())).append(",");
                writer.append(tarea.getFechaLimite() != null ? tarea.getFechaLimite().toString() : "").append(",");
                writer.append(tarea.getTiempoEstimadoMins() > 0 ? String.valueOf(tarea.getTiempoEstimadoMins()) : "").append(",");
                writer.append(tarea.getFechaCreacion() != null ? tarea.getFechaCreacion().toString() : "").append("\n");
            }

            return true;
        } catch (IOException e) {
            System.err.println("Error exportando tareas: " + e.getMessage());
            return false;
        }
    }

    /**
     * Exporta lista de asignaciones a archivo CSV
     */
    public static boolean exportarAsignacionesCSV(ObservableList<Asignacion> asignaciones, String rutaArchivo) {
        try (FileWriter writer = new FileWriter(rutaArchivo)) {
            // Encabezados
            writer.append("ID,Tarea ID,Usuario ID,Rol,Horas Asignadas,Completado,Notas,Fecha Asignación\n");

            // Datos
            for (Asignacion asignacion : asignaciones) {
                writer.append(escaparCSV(String.valueOf(asignacion.getIdAsignacion()))).append(",");
                writer.append(escaparCSV(String.valueOf(asignacion.getTareaId()))).append(",");
                writer.append(escaparCSV(String.valueOf(asignacion.getUsuarioId()))).append(",");
                writer.append(escaparCSV(asignacion.getRolAsignacion())).append(",");
                writer.append(String.valueOf(asignacion.getHorasAsignadas())).append(",");
                writer.append(asignacion.isCompletado() ? "Sí" : "No").append(",");
                writer.append(escaparCSV(asignacion.getNotas() != null ? asignacion.getNotas() : "")).append(",");
                writer.append(asignacion.getFechaAsignacion() != null ? asignacion.getFechaAsignacion().toString() : "").append("\n");
            }

            return true;
        } catch (IOException e) {
            System.err.println("Error exportando asignaciones: " + e.getMessage());
            return false;
        }
    }

    /**
     * Escapa comillas y saltos de línea en campos CSV
     */
    private static String escaparCSV(String campo) {
        if (campo == null || campo.isEmpty()) {
            return "";
        }

        if (campo.contains(",") || campo.contains("\"") || campo.contains("\n")) {
            return "\"" + campo.replace("\"", "\"\"") + "\"";
        }

        return campo;
    }
}
