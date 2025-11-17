package com.taskflow.util;

import com.taskflow.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Gestor de datos que carga información desde Supabase
 * Utiliza DatabaseManager para las conexiones
 */
public class DataManager {
    private static DataManager instance;
    private DatabaseManager dbManager;

    private ObservableList<Usuario> usuarios;
    private ObservableList<Tarea> tareas;
    private ObservableList<Asignacion> asignaciones;

    /**
     * Constructor privado (Singleton)
     */
    private DataManager() {
        dbManager = DatabaseManager.getInstance();
        dbManager.realizarConexion();

        usuarios = FXCollections.observableArrayList();
        tareas = FXCollections.observableArrayList();
        asignaciones = FXCollections.observableArrayList();

        cargarDatos();
    }

    /**
     * Obtiene la instancia única del DataManager
     */
    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    /**
     * Carga todos los datos desde Supabase
     */
    private void cargarDatos() {
        cargarUsuarios();
        cargarTareas();
        cargarAsignaciones();
    }

    /**
     * Carga usuarios desde la tabla 'usuarios' en Supabase
     */
    private void cargarUsuarios() {
        String sql = "SELECT id_usuario, nombre_completo, email, contraseña_hash, rol, foto_perfil, " +
                     "fecha_registro, telefono, activo FROM usuarios ORDER BY id_usuario";

        try (PreparedStatement pstmt = dbManager.prepararConsulta(sql);
             ResultSet rs = pstmt.executeQuery()) {

            usuarios.clear();
            while (rs.next()) {
                int idUsuario = rs.getInt("id_usuario");
                String nombreCompleto = rs.getString("nombre_completo");
                String email = rs.getString("email");
                String contraseñaHash = rs.getString("contraseña_hash");
                String rolStr = rs.getString("rol");
                byte[] fotoPerfil = rs.getBytes("foto_perfil");
                Date fechaRegistroSQL = rs.getDate("fecha_registro");
                String telefono = rs.getString("telefono");
                boolean activo = rs.getBoolean("activo");

                Rol rol = Rol.valueOf(rolStr);
                LocalDate fechaRegistro = fechaRegistroSQL != null ? fechaRegistroSQL.toLocalDate() : LocalDate.now();

                Usuario usuario = new Usuario(idUsuario, nombreCompleto, email, contraseñaHash != null ? contraseñaHash : "",
                        rol, fotoPerfil, fechaRegistro, telefono, activo);
                usuarios.add(usuario);
            }

            System.out.println("✅ Cargados " + usuarios.size() + " usuarios desde Supabase");

        } catch (SQLException e) {
            System.err.println("❌ Error cargando usuarios: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Carga tareas desde la tabla 'tareas' en Supabase
     */
    private void cargarTareas() {
        String sql = "SELECT id_tarea, titulo, descripcion, proyecto_categoria, estado, prioridad, " +
                     "fecha_limite, tiempo_estimado_mins, imagen, fecha_creacion FROM tareas ORDER BY id_tarea";

        try (PreparedStatement pstmt = dbManager.prepararConsulta(sql);
             ResultSet rs = pstmt.executeQuery()) {

            tareas.clear();
            while (rs.next()) {
                int idTarea = rs.getInt("id_tarea");
                String titulo = rs.getString("titulo");
                String descripcion = rs.getString("descripcion");
                String proyectoCategoria = rs.getString("proyecto_categoria");
                String estadoStr = rs.getString("estado");
                String prioridadStr = rs.getString("prioridad");
                Date fechaLimiteSQL = rs.getDate("fecha_limite");
                Integer tiempoEstimadoMins = rs.getObject("tiempo_estimado_mins", Integer.class);
                byte[] imagen = rs.getBytes("imagen");
                Timestamp fechaCreacionTS = rs.getTimestamp("fecha_creacion");

                EstadoTarea estado = EstadoTarea.valueOf(estadoStr);
                Prioridad prioridad = Prioridad.valueOf(prioridadStr);
                LocalDate fechaLimite = fechaLimiteSQL != null ? fechaLimiteSQL.toLocalDate() : null;
                LocalDateTime fechaCreacion = fechaCreacionTS != null ? fechaCreacionTS.toLocalDateTime() : LocalDateTime.now();

                Tarea tarea = new Tarea(idTarea, titulo, descripcion, proyectoCategoria, estado,
                        prioridad, fechaLimite, tiempoEstimadoMins, imagen, fechaCreacion);
                tareas.add(tarea);
            }

            System.out.println("✅ Cargadas " + tareas.size() + " tareas desde Supabase");

        } catch (SQLException e) {
            System.err.println("❌ Error cargando tareas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Carga asignaciones desde la tabla 'asignaciones' en Supabase
     */
    private void cargarAsignaciones() {
        String sql = "SELECT id_asignacion, tarea_id, usuario_id, rol_asignacion, horas_asignadas, " +
                     "completado, notas, fecha_asignacion FROM asignaciones ORDER BY id_asignacion";

        try (PreparedStatement pstmt = dbManager.prepararConsulta(sql);
             ResultSet rs = pstmt.executeQuery()) {

            asignaciones.clear();
            while (rs.next()) {
                int idAsignacion = rs.getInt("id_asignacion");
                int tareaId = rs.getInt("tarea_id");
                int usuarioId = rs.getInt("usuario_id");
                String rolAsignacion = rs.getString("rol_asignacion");
                double horasAsignadas = rs.getDouble("horas_asignadas");
                boolean completado = rs.getBoolean("completado");
                String notas = rs.getString("notas");
                Date fechaAsignacionSQL = rs.getDate("fecha_asignacion");

                LocalDate fechaAsignacion = fechaAsignacionSQL != null ? fechaAsignacionSQL.toLocalDate() : LocalDate.now();

                Asignacion asignacion = new Asignacion(idAsignacion, tareaId, usuarioId,
                        rolAsignacion, horasAsignadas, completado, notas, fechaAsignacion);
                asignaciones.add(asignacion);
            }

            System.out.println("✅ Cargadas " + asignaciones.size() + " asignaciones desde Supabase");

        } catch (SQLException e) {
            System.err.println("❌ Error cargando asignaciones: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==================== MÉTODOS DE INSERCIÓN ====================

    /**
     * Inserta un nuevo usuario en Supabase
     */
    public boolean insertarUsuario(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nombre_completo, email, contraseña_hash, rol, foto_perfil, " +
                     "telefono, activo) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = dbManager.prepararConsulta(sql)) {
            pstmt.setString(1, usuario.getNombreCompleto());
            pstmt.setString(2, usuario.getEmail());
            pstmt.setString(3, usuario.getContraseñaHash());
            pstmt.setString(4, usuario.getRol().name());
            pstmt.setBytes(5, usuario.getFotoPerfil());
            pstmt.setString(6, usuario.getTelefono());
            pstmt.setBoolean(7, usuario.isActivo());

            int filasAfectadas = pstmt.executeUpdate();
            if (filasAfectadas > 0) {
                cargarUsuarios(); // Recargar lista
                System.out.println("✅ Usuario insertado correctamente");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error insertando usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Inserta una nueva tarea en Supabase
     */
    public boolean insertarTarea(Tarea tarea) {
        String sql = "INSERT INTO tareas (titulo, descripcion, proyecto_categoria, estado, prioridad, " +
                     "fecha_limite, tiempo_estimado_mins, imagen) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = dbManager.prepararConsulta(sql)) {
            pstmt.setString(1, tarea.getTitulo());
            pstmt.setString(2, tarea.getDescripcion());
            pstmt.setString(3, tarea.getProyectoCategoria());
            pstmt.setString(4, tarea.getEstado().name());
            pstmt.setString(5, tarea.getPrioridad().name());
            pstmt.setDate(6, tarea.getFechaLimite() != null ? Date.valueOf(tarea.getFechaLimite()) : null);
            pstmt.setObject(7, tarea.getTiempoEstimadoMins() > 0 ? tarea.getTiempoEstimadoMins() : null);
            pstmt.setBytes(8, tarea.getImagen());

            int filasAfectadas = pstmt.executeUpdate();
            if (filasAfectadas > 0) {
                cargarTareas(); // Recargar lista
                System.out.println("✅ Tarea insertada correctamente");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error insertando tarea: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Inserta una nueva asignación en Supabase
     */
    public boolean insertarAsignacion(Asignacion asignacion) {
        String sql = "INSERT INTO asignaciones (tarea_id, usuario_id, rol_asignacion, horas_asignadas, " +
                     "completado, notas) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = dbManager.prepararConsulta(sql)) {
            pstmt.setInt(1, asignacion.getTareaId());
            pstmt.setInt(2, asignacion.getUsuarioId());
            pstmt.setString(3, asignacion.getRolAsignacion());
            pstmt.setDouble(4, asignacion.getHorasAsignadas());
            pstmt.setBoolean(5, asignacion.isCompletado());
            pstmt.setString(6, asignacion.getNotas());

            int filasAfectadas = pstmt.executeUpdate();
            if (filasAfectadas > 0) {
                cargarAsignaciones(); // Recargar lista
                System.out.println("✅ Asignación insertada correctamente");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error insertando asignación: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ==================== GETTERS ====================

    public ObservableList<Usuario> getUsuarios() {
        return usuarios;
    }

    public ObservableList<Tarea> getTareas() {
        return tareas;
    }

    public ObservableList<Asignacion> getAsignaciones() {
        return asignaciones;
    }

    /**
     * Cuenta las tareas por estado
     */
    public long countTareasByEstado(EstadoTarea estado) {
        return tareas.stream()
            .filter(t -> t.getEstado() == estado)
            .count();
    }

    /**
     * Cuenta los usuarios activos
     */
    public long countUsuariosActivos() {
        return usuarios.stream()
            .filter(Usuario::isActivo)
            .count();
    }

    /**
     * Recarga todos los datos desde Supabase
     */
    public void recargarDatos() {
        cargarDatos();
    }

    /**
     * Cierra la conexión a la base de datos
     */
    public void cerrarConexion() {
        dbManager.cerrarConexion();
    }
}
