package com.taskflow.service;

import com.taskflow.model.Usuario;
import com.taskflow.util.DatabaseManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Servicio de autenticacion de usuarios
 */
public class AuthService {

    private static AuthService instance;
    private DatabaseManager dbManager;
    private Usuario usuarioActual;

    private AuthService() {
        dbManager = DatabaseManager.getInstance();
    }

    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    /**
     * Autentica un usuario con email y contraseña
     * NOTA: En produccion, la contraseña debe estar hasheada con bcrypt
     * Para simplificar el desarrollo, se compara directamente
     */
    public boolean autenticar(String email, String password) {
        String sql = "SELECT id_usuario, nombre_completo, email, contraseña_hash, rol, " +
                     "foto_perfil, fecha_registro, telefono, activo " +
                     "FROM usuarios WHERE email = ? AND activo = true";

        try (PreparedStatement pstmt = dbManager.prepararConsulta(sql)) {
            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String passwordHash = rs.getString("contraseña_hash");

                    // TODO: Implementar verificacion bcrypt en produccion
                    // Por ahora, comparacion directa para desarrollo
                    if (password.equals(passwordHash)) {
                        usuarioActual = mapearUsuario(rs);
                        System.out.println("Autenticacion exitosa: " + usuarioActual.getNombreCompleto());
                        return true;
                    } else {
                        System.out.println("Contraseña incorrecta para: " + email);
                        return false;
                    }
                } else {
                    System.out.println("Usuario no encontrado: " + email);
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en autenticacion: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        return new Usuario(
            rs.getInt("id_usuario"),
            rs.getString("nombre_completo"),
            rs.getString("email"),
            rs.getString("contraseña_hash"),
            com.taskflow.model.Rol.valueOf(rs.getString("rol")),
            rs.getBytes("foto_perfil"),
            rs.getDate("fecha_registro").toLocalDate(),
            rs.getString("telefono"),
            rs.getBoolean("activo")
        );
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public void cerrarSesion() {
        usuarioActual = null;
        System.out.println("Sesion cerrada");
    }

    public boolean isAutenticado() {
        return usuarioActual != null;
    }

    public boolean isAdmin() {
        return usuarioActual != null && usuarioActual.getRol() == com.taskflow.model.Rol.admin;
    }
}
