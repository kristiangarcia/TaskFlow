package com.taskflow.controller;

import com.taskflow.model.Rol;
import com.taskflow.model.Usuario;
import com.taskflow.util.AlertHelper;
import com.taskflow.util.Constants;
import com.taskflow.util.DataManager;
import com.taskflow.util.ValidadorFormulario;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.application.Platform;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;

public class ModalNuevoUsuarioController implements Initializable {

    @FXML private Label lblTitulo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelefono;
    @FXML private ComboBox<String> comboRol;
    @FXML private PasswordField txtPassword;
    @FXML private CheckBox checkActivo;
    @FXML private Button btnCancelar;
    @FXML private Label lblPasswordLabel;

    private DataManager dataManager;
    private Usuario usuarioEditar; // null si es nuevo, contiene el usuario si es edición

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dataManager = DataManager.getInstance();
        inicializarComboRol();
        checkActivo.setSelected(true);

        // Agregar eventos de teclado
        configurarEventosTeclado();
    }

    /**
     * Configura eventos de teclado: Enter=Guardar, Escape=Cancelar
     */
    private void configurarEventosTeclado() {
        // Listener para todos los TextFields
        EventHandler<KeyEvent> keyHandler = this::manejarEventoTeclado;
        txtNombre.setOnKeyPressed(keyHandler);
        txtEmail.setOnKeyPressed(keyHandler);
        txtTelefono.setOnKeyPressed(keyHandler);
        txtPassword.setOnKeyPressed(keyHandler);
        comboRol.setOnKeyPressed(keyHandler);
        checkActivo.setOnKeyPressed(keyHandler);
    }

    /**
     * Maneja eventos de teclado (Enter=Guardar, Escape=Cancelar)
     */
    private void manejarEventoTeclado(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleGuardar();
            event.consume();
        } else if (event.getCode() == KeyCode.ESCAPE) {
            handleCancelar();
            event.consume();
        }
    }

    /**
     * Configura el modal para editar un usuario existente
     */
    public void setUsuarioEditar(Usuario usuario) {
        this.usuarioEditar = usuario;
        if (lblTitulo != null) {
            lblTitulo.setText("Editar Usuario");
        }
        cargarDatosUsuario();
        // En modo edición, la contraseña es opcional (solo si se quiere cambiar)
        if (lblPasswordLabel != null) {
            lblPasswordLabel.setText("Contraseña (dejar vacío para mantener)");
        }
    }

    private void inicializarComboRol() {
        comboRol.getItems().addAll("admin", "empleado");
        comboRol.setValue("empleado");
    }

    /**
     * Carga los datos del usuario en el formulario (modo edición)
     */
    private void cargarDatosUsuario() {
        if (usuarioEditar != null) {
            txtNombre.setText(usuarioEditar.getNombreCompleto());
            txtEmail.setText(usuarioEditar.getEmail());
            txtTelefono.setText(usuarioEditar.getTelefono() != null ? usuarioEditar.getTelefono() : "");
            comboRol.setValue(usuarioEditar.getRol().name());
            checkActivo.setSelected(usuarioEditar.isActivo());
            // No cargamos la contraseña por seguridad
        }
    }

    @FXML
    void handleGuardar() {
        ValidadorFormulario validador = validarCampos();

        if (validador.tieneErrores()) {
            // Mostrar validación visual (campos en rojo) después de renderizar
            Platform.runLater(validador::validarVisualmente);

            // Mostrar alerta con errores
            AlertHelper.mostrarAdvertencia(Constants.TITULO_VALIDACION,
                validador.obtenerErroresFormateados());
            return;
        }

        boolean exito;
        if (usuarioEditar == null) {
            // Modo crear
            Usuario nuevoUsuario = crearUsuarioDesdeFormulario();
            exito = dataManager.insertarUsuario(nuevoUsuario);
        } else {
            // Modo editar
            Usuario usuarioActualizado = actualizarUsuarioDesdeFormulario();
            exito = dataManager.actualizarUsuario(usuarioActualizado);
        }

        if (exito) {
            String mensaje = usuarioEditar == null ? Constants.MSG_USUARIO_CREADO : "Usuario actualizado correctamente";
            AlertHelper.mostrarExito(Constants.TITULO_EXITO, mensaje);
            cerrarVentana();
        } else {
            AlertHelper.mostrarError(Constants.TITULO_ERROR, Constants.MSG_USUARIO_ERROR);
        }
    }

    private Usuario crearUsuarioDesdeFormulario() {
        String nombre = txtNombre.getText().trim();
        String email = txtEmail.getText().trim();
        String telefono = txtTelefono.getText().trim();
        // Si el teléfono está vacío, pasarlo como null para que se guarde NULL en BD
        if (telefono.isEmpty()) {
            telefono = null;
        }
        String password = txtPassword.getText();
        Rol rol = Rol.valueOf(comboRol.getValue());
        boolean activo = checkActivo.isSelected();

        // Hashear password con bcrypt
        String passwordHash = org.mindrot.jbcrypt.BCrypt.hashpw(password, org.mindrot.jbcrypt.BCrypt.gensalt());

        Usuario usuario = new Usuario(0, nombre, email, telefono, rol, activo);
        usuario.setContraseñaHash(passwordHash);
        return usuario;
    }

    private Usuario actualizarUsuarioDesdeFormulario() {
        String nombre = txtNombre.getText().trim();
        String email = txtEmail.getText().trim();
        String telefono = txtTelefono.getText().trim();
        // Si el teléfono está vacío, pasarlo como null para que se guarde NULL en BD
        if (telefono.isEmpty()) {
            telefono = null;
        }
        String password = txtPassword.getText();
        Rol rol = Rol.valueOf(comboRol.getValue());
        boolean activo = checkActivo.isSelected();

        Usuario usuario = new Usuario(usuarioEditar.getIdUsuario(), nombre, email, telefono, rol, activo);

        // Solo actualizar la contraseña si se proporcionó una nueva
        if (!password.isEmpty()) {
            String passwordHash = org.mindrot.jbcrypt.BCrypt.hashpw(password, org.mindrot.jbcrypt.BCrypt.gensalt());
            usuario.setContraseñaHash(passwordHash);
        } else {
            // Mantener la contraseña existente
            usuario.setContraseñaHash(usuarioEditar.getContraseñaHash());
        }

        return usuario;
    }

    private ValidadorFormulario validarCampos() {
        ValidadorFormulario validador = new ValidadorFormulario();

        // Registrar controles para validación visual
        validador.registrarControl("Nombre", txtNombre);
        validador.registrarControl("Email", txtEmail);
        validador.registrarControl("Teléfono", txtTelefono);
        validador.registrarControl("Rol", comboRol);
        validador.registrarControl("Contraseña", txtPassword);

        // Validar nombre (obligatorio, 3-100 caracteres)
        validador.validarNombre("Nombre", txtNombre.getText());

        // Validar email (obligatorio, formato válido)
        validador.validarEmail("Email", txtEmail.getText());

        // Validar teléfono (OPCIONAL - si proporciona debe ser válido)
        validador.validarTelefono("Teléfono", txtTelefono.getText());

        // Validar rol (obligatorio)
        validador.validarRol("Rol", comboRol.getValue());

        // Validar contraseña
        String password = txtPassword.getText();
        boolean passwordObligatorio = (usuarioEditar == null);
        validador.validarPassword("Contraseña", password, passwordObligatorio);

        // Retornar validador
        return validador;
    }

    @FXML
    void handleCancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        ((Stage) btnCancelar.getScene().getWindow()).close();
    }
}
