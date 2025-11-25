package com.taskflow.controller;

import com.taskflow.model.EstadoTarea;
import com.taskflow.model.Prioridad;
import com.taskflow.model.Tarea;
import com.taskflow.util.AlertHelper;
import com.taskflow.util.Constants;
import com.taskflow.util.DataManager;
import com.taskflow.util.Validaciones;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ModalNuevaTareaController implements Initializable {

    @FXML private TextField txtTitulo;
    @FXML private TextArea txtDescripcion;
    @FXML private TextField txtCategoria;
    @FXML private ComboBox<String> comboPrioridad;
    @FXML private ComboBox<String> comboEstado;
    @FXML private DatePicker dateFechaLimite;
    @FXML private TextField txtTiempoEstimado;
    @FXML private Button btnCancelar;

    private DataManager dataManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dataManager = DataManager.getInstance();
        inicializarCombos();
    }

    private void inicializarCombos() {
        comboPrioridad.getItems().addAll("alta", "media", "baja");
        comboPrioridad.setValue("media");
        comboEstado.getItems().addAll("abierta", "en_progreso", "completada", "retrasada");
        comboEstado.setValue("abierta");
    }

    @FXML
    void handleGuardar() {
        String errores = validarCampos();
        if (errores != null) {
            AlertHelper.mostrarAdvertencia(Constants.TITULO_VALIDACION, errores);
            return;
        }

        Tarea nuevaTarea = crearTareaDesdeFormulario();
        boolean exito = dataManager.insertarTarea(nuevaTarea);

        if (exito) {
            AlertHelper.mostrarExito(Constants.TITULO_EXITO, Constants.MSG_TAREA_CREADA);
            cerrarVentana();
        } else {
            AlertHelper.mostrarError(Constants.TITULO_ERROR, Constants.MSG_TAREA_ERROR);
        }
    }

    private Tarea crearTareaDesdeFormulario() {
        String titulo = txtTitulo.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        String categoria = txtCategoria.getText().trim();
        Prioridad prioridad = Prioridad.valueOf(comboPrioridad.getValue());
        EstadoTarea estado = EstadoTarea.valueOf(comboEstado.getValue());
        LocalDate fechaLimite = dateFechaLimite.getValue();

        Tarea tarea = new Tarea(0, titulo, descripcion, categoria, estado, prioridad, fechaLimite);

        Integer tiempoEstimado = obtenerTiempoEstimado();
        if (tiempoEstimado != null) {
            tarea.setTiempoEstimadoMins(tiempoEstimado);
        }

        return tarea;
    }

    private Integer obtenerTiempoEstimado() {
        String tiempoTexto = txtTiempoEstimado.getText();
        if (!Validaciones.esTextoVacio(tiempoTexto)) {
            try {
                return Integer.parseInt(tiempoTexto.trim());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private String validarCampos() {
        StringBuilder errores = new StringBuilder();

        validarTitulo(errores);
        validarDescripcion(errores);
        validarCategoria(errores);
        validarPrioridad(errores);
        validarEstado(errores);
        validarFechaLimite(errores);
        validarTiempoEstimado(errores);

        return errores.length() > 0 ? errores.toString() : null;
    }

    private void validarTitulo(StringBuilder errores) {
        if (Validaciones.esTextoVacio(txtTitulo.getText())) {
            errores.append("- ").append(Constants.MSG_TITULO_OBLIGATORIO).append("\n");
        }
    }

    private void validarDescripcion(StringBuilder errores) {
        if (Validaciones.esTextoVacio(txtDescripcion.getText())) {
            errores.append("- ").append(Constants.MSG_DESCRIPCION_OBLIGATORIA).append("\n");
        }
    }

    private void validarCategoria(StringBuilder errores) {
        if (Validaciones.esTextoVacio(txtCategoria.getText())) {
            errores.append("- ").append(Constants.MSG_CATEGORIA_OBLIGATORIA).append("\n");
        }
    }

    private void validarPrioridad(StringBuilder errores) {
        if (comboPrioridad.getValue() == null) {
            errores.append("- ").append(Constants.MSG_PRIORIDAD_OBLIGATORIA).append("\n");
        }
    }

    private void validarEstado(StringBuilder errores) {
        if (comboEstado.getValue() == null) {
            errores.append("- ").append(Constants.MSG_ESTADO_OBLIGATORIO).append("\n");
        }
    }

    private void validarFechaLimite(StringBuilder errores) {
        LocalDate fecha = dateFechaLimite.getValue();
        if (fecha == null) {
            errores.append("- ").append(Constants.MSG_FECHA_OBLIGATORIA).append("\n");
        } else if (fecha.isBefore(LocalDate.now())) {
            errores.append("- ").append(Constants.MSG_FECHA_INVALIDA).append("\n");
        }
    }

    private void validarTiempoEstimado(StringBuilder errores) {
        String tiempo = txtTiempoEstimado.getText();
        if (!Validaciones.esTextoVacio(tiempo)) {
            if (!Validaciones.esNumeroPositivo(tiempo.trim())) {
                errores.append("- ").append(Constants.MSG_TIEMPO_POSITIVO).append("\n");
            }
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
