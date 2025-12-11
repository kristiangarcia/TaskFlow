package com.taskflow.util;

import com.taskflow.model.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestor de importación de datos desde CSV
 */
public class ImportManager {

    /**
     * Importa usuarios desde archivo CSV
     */
    public static List<Usuario> importarUsuariosCSV(String rutaArchivo) {
        List<Usuario> usuarios = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            int lineaNum = 0;

            while ((linea = reader.readLine()) != null) {
                lineaNum++;
                if (lineaNum == 1) continue; // Saltar encabezados

                String[] campos = parsearLineasCSV(linea);
                if (campos.length < 7) continue;

                try {
                    int id = Integer.parseInt(campos[0].trim());
                    String nombre = campos[1].trim();
                    String email = campos[2].trim();
                    String telefono = campos[3].trim();
                    Rol rol = Rol.valueOf(campos[4].trim());
                    boolean activo = campos[5].trim().equals("Sí");

                    Usuario usuario = new Usuario(id, nombre, email, telefono, rol, activo);

                    usuarios.add(usuario);
                } catch (Exception e) {
                    System.err.println("Error en línea " + lineaNum + ": " + e.getMessage());
                }
            }

            return usuarios;
        } catch (IOException e) {
            System.err.println("Error importando usuarios: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Importa tareas desde archivo CSV
     */
    public static List<Tarea> importarTareasCSV(String rutaArchivo) {
        List<Tarea> tareas = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            int lineaNum = 0;

            while ((linea = reader.readLine()) != null) {
                lineaNum++;
                if (lineaNum == 1) continue; // Saltar encabezados

                String[] campos = parsearLineasCSV(linea);
                if (campos.length < 9) continue;

                try {
                    int id = Integer.parseInt(campos[0].trim());
                    String titulo = campos[1].trim();
                    String descripcion = campos[2].trim();
                    String categoria = campos[3].trim();
                    EstadoTarea estado = EstadoTarea.valueOf(campos[4].trim());
                    Prioridad prioridad = Prioridad.valueOf(campos[5].trim());
                    LocalDate fechaLimite = !campos[6].trim().isEmpty() ? LocalDate.parse(campos[6].trim()) : null;
                    Integer tiempoEstimado = !campos[7].trim().isEmpty() ? Integer.parseInt(campos[7].trim()) : 0;
                    LocalDateTime fechaCreacion = !campos[8].trim().isEmpty() ? LocalDateTime.parse(campos[8].trim()) : LocalDateTime.now();

                    Tarea tarea = new Tarea(id, titulo, descripcion, categoria, estado, prioridad, fechaLimite, tiempoEstimado, null, fechaCreacion);

                    tareas.add(tarea);
                } catch (Exception e) {
                    System.err.println("Error en línea " + lineaNum + ": " + e.getMessage());
                }
            }

            return tareas;
        } catch (IOException e) {
            System.err.println("Error importando tareas: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Importa asignaciones desde archivo CSV
     */
    public static List<Asignacion> importarAsignacionesCSV(String rutaArchivo) {
        List<Asignacion> asignaciones = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            int lineaNum = 0;

            while ((linea = reader.readLine()) != null) {
                lineaNum++;
                if (lineaNum == 1) continue; // Saltar encabezados

                String[] campos = parsearLineasCSV(linea);
                if (campos.length < 8) continue;

                try {
                    int id = Integer.parseInt(campos[0].trim());
                    int tareaId = Integer.parseInt(campos[1].trim());
                    int usuarioId = Integer.parseInt(campos[2].trim());
                    String rol = campos[3].trim();
                    double horas = Double.parseDouble(campos[4].trim());
                    boolean completado = campos[5].trim().equals("Sí");
                    String notas = campos[6].trim();
                    LocalDate fechaAsignacion = !campos[7].trim().isEmpty() ? LocalDate.parse(campos[7].trim().substring(0, 10)) : LocalDate.now();

                    Asignacion asignacion = new Asignacion(id, tareaId, usuarioId, rol, horas, completado, notas, fechaAsignacion);

                    asignaciones.add(asignacion);
                } catch (Exception e) {
                    System.err.println("Error en línea " + lineaNum + ": " + e.getMessage());
                }
            }

            return asignaciones;
        } catch (IOException e) {
            System.err.println("Error importando asignaciones: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Parsea una línea CSV respetando comillas
     */
    private static String[] parsearLineasCSV(String linea) {
        List<String> campos = new ArrayList<>();
        StringBuilder campo = new StringBuilder();
        boolean dentroDeMillas = false;

        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);

            if (c == '"') {
                if (dentroDeMillas && i + 1 < linea.length() && linea.charAt(i + 1) == '"') {
                    // Comillas escapadas ""
                    campo.append('"');
                    i++;
                } else {
                    // Toggle de comillas
                    dentroDeMillas = !dentroDeMillas;
                }
            } else if (c == ',' && !dentroDeMillas) {
                // Separador de campo
                campos.add(campo.toString());
                campo = new StringBuilder();
            } else {
                campo.append(c);
            }
        }

        campos.add(campo.toString());
        return campos.toArray(new String[0]);
    }
}
