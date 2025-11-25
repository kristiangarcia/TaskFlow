package com.taskflow.controller;

import com.taskflow.service.AuthService;
import com.taskflow.util.AlertHelper;
import com.taskflow.util.Constants;
import com.taskflow.util.Validaciones;
import com.taskflow.view.ViewManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnLogin;
    @FXML private Label lblError;

    private AuthService authService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        authService = AuthService.getInstance();
        configurarEventos();
    }

    private void configurarEventos() {
        txtPassword.setOnKeyPressed(this::handleKeyPressed);
        txtEmail.setOnKeyPressed(this::handleKeyPressed);
    }

    private void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleLogin();
        }
    }

    @FXML
    void handleLogin() {
        lblError.setVisible(false);

        String errores = validarCampos();
        if (errores != null) {
            mostrarError(errores);
            return;
        }

        String email = txtEmail.getText().trim();
        String password = txtPassword.getText();

        btnLogin.setDisable(true);
        btnLogin.setText("Iniciando sesion...");

        boolean autenticado = authService.autenticar(email, password);

        btnLogin.setDisable(false);
        btnLogin.setText("Iniciar Sesion");

        if (autenticado) {
            abrirVentanaPrincipal();
        } else {
            mostrarError("Email o contraseña incorrectos");
        }
    }

    private String validarCampos() {
        StringBuilder errores = new StringBuilder();

        if (Validaciones.esTextoVacio(txtEmail.getText())) {
            errores.append("El email es obligatorio\n");
        } else if (!Validaciones.esEmailValido(txtEmail.getText().trim())) {
            errores.append("El formato del email no es valido\n");
        }

        if (Validaciones.esTextoVacio(txtPassword.getText())) {
            errores.append("La contraseña es obligatoria\n");
        }

        return errores.length() > 0 ? errores.toString() : null;
    }

    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisible(true);
    }

    private void abrirVentanaPrincipal() {
        try {
            ViewManager.getInstance().cambiarVista("/fxml/MainView.fxml", Constants.APP_TITLE);
        } catch (Exception e) {
            System.err.println("Error al abrir ventana principal: " + e.getMessage());
            e.printStackTrace();
            AlertHelper.mostrarError(Constants.TITULO_ERROR, "No se pudo abrir la ventana principal");
        }
    }
}
