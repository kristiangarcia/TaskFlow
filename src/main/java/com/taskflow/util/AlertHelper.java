package com.taskflow.util;

import javafx.scene.control.Alert;

/**
 * Clase helper para mostrar alertas de manera centralizada
 */
public class AlertHelper {

    private AlertHelper() {
        throw new AssertionError("Cannot instantiate AlertHelper class");
    }

    /**
     * Muestra una alerta de informacion
     */
    public static void mostrarExito(String titulo, String mensaje) {
        mostrarAlerta(Alert.AlertType.INFORMATION, titulo, mensaje);
    }

    /**
     * Muestra una alerta de error
     */
    public static void mostrarError(String titulo, String mensaje) {
        mostrarAlerta(Alert.AlertType.ERROR, titulo, mensaje);
    }

    /**
     * Muestra una alerta de advertencia
     */
    public static void mostrarAdvertencia(String titulo, String mensaje) {
        mostrarAlerta(Alert.AlertType.WARNING, titulo, mensaje);
    }

    /**
     * Muestra una alerta generica
     */
    public static void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra una alerta de confirmacion y retorna la respuesta del usuario
     */
    public static boolean mostrarConfirmacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        return alert.showAndWait().filter(response -> response == javafx.scene.control.ButtonType.OK).isPresent();
    }
}
