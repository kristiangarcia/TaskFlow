package com.taskflow.controller;

import com.taskflow.model.Rol;
import com.taskflow.model.Usuario;
import com.taskflow.util.AlertHelper;
import com.taskflow.util.Constants;
import com.taskflow.util.DataManager;
import com.taskflow.util.Validaciones;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ModalNuevoUsuarioController implements Initializable {

    @FXML private TextField txtNombre;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelefono;
    @FXML private ComboBox<String> comboRol;
    @FXML private PasswordField txtPassword;
    @FXML private CheckBox checkActivo;
    @FXML private Button btnCancelar;

    private DataManager dataManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dataManager = DataManager.getInstance();
        inicializarComboRol();
        checkActivo.setSelected(true);
    }

    private void inicializarComboRol() {
        comboRol.getItems().addAll("admin", "empleado");
        comboRol.setValue("empleado");
    }

    @FXML
    void handleGuardar() {
        String errores = validarCampos();
        if (errores != null) {
            AlertHelper.mostrarAdvertencia(Constants.TITULO_VALIDACION, errores);
            return;
        }

        Usuario nuevoUsuario = crearUsuarioDesdeFormulario();
        boolean exito = dataManager.insertarUsuario(nuevoUsuario);

        if (exito) {
            AlertHelper.mostrarExito(Constants.TITULO_EXITO, Constants.MSG_USUARIO_CREADO);
            cerrarVentana();
        } else {
            AlertHelper.mostrarError(Constants.TITULO_ERROR, Constants.MSG_USUARIO_ERROR);
        }
    }

    private Usuario crearUsuarioDesdeFormulario() {
        String nombre = txtNombre.getText().trim();
        String email = txtEmail.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String password = txtPassword.getText();
        Rol rol = Rol.valueOf(comboRol.getValue());
        boolean activo = checkActivo.isSelected();

        Usuario usuario = new Usuario(0, nombre, email, telefono, rol, activo);
        usuario.setContraseñaHash(password);
        return usuario;
    }

    private String validarCampos() {
        StringBuilder errores = new StringBuilder();

        validarNombre(errores);
        validarEmail(errores);
        validarTelefono(errores);
        validarPassword(errores);
        validarRol(errores);

        return errores.length() > 0 ? errores.toString() : null;
    }

    private void validarNombre(StringBuilder errores) {
        if (Validaciones.esTextoVacio(txtNombre.getText())) {
            errores.append("- ").append(Constants.MSG_NOMBRE_OBLIGATORIO).append("\n");
        }
    }

    private void validarEmail(StringBuilder errores) {
        String email = txtEmail.getText();
        if (Validaciones.esTextoVacio(email)) {
            errores.append("- ").append(Constants.MSG_EMAIL_OBLIGATORIO).append("\n");
        } else if (!Validaciones.esEmailValido(email.trim())) {
            errores.append("- ").append(Constants.MSG_EMAIL_INVALIDO).append("\n");
        }
    }

    private void validarTelefono(StringBuilder errores) {
        String telefono = txtTelefono.getText();
        if (Validaciones.esTextoVacio(telefono)) {
            errores.append("- ").append(Constants.MSG_TELEFONO_OBLIGATORIO).append("\n");
        } else if (!Validaciones.esTelefonoValido(telefono.trim())) {
            errores.append("- ").append(Constants.MSG_TELEFONO_INVALIDO).append("\n");
        }
    }

    private void validarPassword(StringBuilder errores) {
        String password = txtPassword.getText();
        if (Validaciones.esTextoVacio(password)) {
            errores.append("- ").append(Constants.MSG_PASSWORD_OBLIGATORIO).append("\n");
        } else if (!Validaciones.longitudMinima(password, Constants.MIN_PASSWORD_LENGTH)) {
            errores.append("- ").append(String.format(Constants.MSG_PASSWORD_CORTO, Constants.MIN_PASSWORD_LENGTH)).append("\n");
        }
    }

    private void validarRol(StringBuilder errores) {
        if (comboRol.getValue() == null) {
            errores.append("- ").append(Constants.MSG_ROL_OBLIGATORIO).append("\n");
        }
    }

    @FXML
    void handleCancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        ((Stage) btnCancelar.getScene().getWindow()).close();
    }
}
