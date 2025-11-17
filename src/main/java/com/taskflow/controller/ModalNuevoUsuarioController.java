package com.taskflow.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ModalNuevoUsuarioController implements Initializable {

    @FXML
    private Button btnSeleccionarFoto;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtTelefono;

    @FXML
    private ComboBox<String> comboRol;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private CheckBox checkActivo;

    @FXML
    private TextArea txtNotas;

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnGuardar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Poblar comboRol con roles
        comboRol.getItems().addAll("admin", "empleado");
        comboRol.setValue("empleado");

        // Activo por defecto
        checkActivo.setSelected(true);
    }

    @FXML
    void handleGuardar() {
        // Funcionalidad en desarrollo
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Nuevo Usuario");
        alert.setHeaderText(null);
        alert.setContentText("Funcionalidad para crear usuario aún no está programada");
        alert.showAndWait();
    }

    @FXML
    void handleCancelar() {
        // Cerrar ventana
        ((Stage) btnCancelar.getScene().getWindow()).close();
    }
}
