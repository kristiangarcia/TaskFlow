package com.taskflow.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ModalNuevaTareaController implements Initializable {

    @FXML
    private TextField txtTitulo;

    @FXML
    private TextArea txtDescripcion;

    @FXML
    private TextField txtCategoria;

    @FXML
    private ComboBox<String> comboPrioridad;

    @FXML
    private ComboBox<String> comboEstado;

    @FXML
    private DatePicker dateFechaLimite;

    @FXML
    private TextField txtTiempoEstimado;

    @FXML
    private CheckBox checkAsignarAhora;

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnGuardar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Poblar comboPrioridad
        comboPrioridad.getItems().addAll("alta", "media", "baja");
        comboPrioridad.setValue("media");

        // Poblar comboEstado
        comboEstado.getItems().addAll("abierta", "en_progreso", "completada", "retrasada");
        comboEstado.setValue("abierta");
    }

    @FXML
    void handleGuardar() {
        // Funcionalidad en desarrollo
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Nueva Tarea");
        alert.setHeaderText(null);
        alert.setContentText("Funcionalidad para crear tarea aún no está programada");
        alert.showAndWait();
    }

    @FXML
    void handleCancelar() {
        // Cerrar ventana
        ((Stage) btnCancelar.getScene().getWindow()).close();
    }
}
