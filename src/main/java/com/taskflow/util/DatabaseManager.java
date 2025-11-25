package com.taskflow.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * Gestor de conexiones a la base de datos Supabase PostgreSQL
 * Patrón Singleton para gestionar una única conexión
 */
public class DatabaseManager {

    private static DatabaseManager instance;
    private Connection conexion;
    private String url;
    private String user;
    private String password;

    /**
     * Constructor privado para patrón Singleton
     * Carga las credenciales desde database.properties
     */
    private DatabaseManager() {
        cargarConfiguracion();
    }

    /**
     * Obtiene la instancia única del DatabaseManager
     */
    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Carga la configuración de la base de datos desde database.properties
     */
    private void cargarConfiguracion() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                System.err.println("No se encontró database.properties");
                return;
            }
            props.load(input);

            url = props.getProperty("db.url");
            user = props.getProperty("db.user");
            password = props.getProperty("db.password");

        } catch (IOException e) {
            System.err.println("Error cargando configuración: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Realiza la conexión a la base de datos Supabase
     */
    public void realizarConexion() {
        try {
            // Mostrar drivers disponibles
            DriverManager.drivers().forEach(driver ->
                System.out.println("Driver disponible: " + driver.toString())
            );

            // Realizar conexión
            conexion = DriverManager.getConnection(url, user, password);
            System.out.println("Conexion a Supabase realizada exitosamente");

        } catch (SQLException e) {
            System.err.println("Error al conectar con Supabase: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Obtiene la conexión activa a la base de datos
     */
    public Connection getConexion() {
        try {
            // Verificar si la conexión está cerrada o es nula
            if (conexion == null || conexion.isClosed()) {
                realizarConexion();
            }
        } catch (SQLException e) {
            System.err.println("Error verificando conexión: " + e.getMessage());
            realizarConexion();
        }
        return conexion;
    }

    /**
     * Cierra la conexión a la base de datos
     */
    public void cerrarConexion() {
        if (conexion != null) {
            try {
                conexion.close();
                System.out.println("Conexion a Supabase cerrada");
            } catch (SQLException e) {
                System.err.println("Error cerrando conexión: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Ejecuta una consulta SELECT y devuelve el ResultSet
     * IMPORTANTE: El ResultSet debe cerrarse después de usarlo
     */
    public ResultSet ejecutarConsulta(String sql) throws SQLException {
        Statement st = getConexion().createStatement();
        return st.executeQuery(sql);
    }

    /**
     * Ejecuta una consulta SELECT con PreparedStatement (más seguro)
     * IMPORTANTE: El ResultSet debe cerrarse después de usarlo
     */
    public PreparedStatement prepararConsulta(String sql) throws SQLException {
        return getConexion().prepareStatement(sql);
    }

    /**
     * Ejecuta una actualización (INSERT, UPDATE, DELETE) con PreparedStatement
     * Retorna el número de filas afectadas
     */
    public int ejecutarActualizacion(PreparedStatement pstmt) throws SQLException {
        return pstmt.executeUpdate();
    }
}
