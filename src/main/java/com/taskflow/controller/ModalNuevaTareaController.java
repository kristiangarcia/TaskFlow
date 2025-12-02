package com.taskflow.controller;

import com.taskflow.model.EstadoTarea;
import com.taskflow.model.Prioridad;
import com.taskflow.model.Tarea;
import com.taskflow.util.AlertHelper;
import com.taskflow.util.Constants;
import com.taskflow.util.DataManager;
import com.taskflow.util.Validaciones;
import com.taskflow.util.ValidadorFormulario;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ModalNuevaTareaController implements Initializable {

    @FXML private Label lblTitulo;
    @FXML private TextField txtTitulo;
    @FXML private TextArea txtDescripcion;
    @FXML private TextField txtCategoria;
    @FXML private ComboBox<String> comboPrioridad;
    @FXML private ComboBox<String> comboEstado;
    @FXML private DatePicker dateFechaLimite;
    @FXML private TextField txtTiempoEstimado;
    @FXML private Button btnCancelar;

    private DataManager dataManager;
    private Tarea tareaEditar; // null si es nueva, contiene la tarea si es edición

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dataManager = DataManager.getInstance();
        inicializarCombos();
    }

    /**
     * Configura el modal para editar una tarea existente
     */
    public void setTareaEditar(Tarea tarea) {
        this.tareaEditar = tarea;
        if (lblTitulo != null) {
            lblTitulo.setText("Editar Tarea");
        }
        cargarDatosTarea();
    }

    private void inicializarCombos() {
        comboPrioridad.getItems().addAll("alta", "media", "baja");
        comboPrioridad.setValue("media");
        comboEstado.getItems().addAll("abierta", "en_progreso", "completada", "retrasada");
        comboEstado.setValue("abierta");
    }

    /**
     * Carga los datos de la tarea en el formulario (modo edición)
     */
    private void cargarDatosTarea() {
        if (tareaEditar != null) {
            txtTitulo.setText(tareaEditar.getTitulo());
            txtDescripcion.setText(tareaEditar.getDescripcion());
            txtCategoria.setText(tareaEditar.getProyectoCategoria() != null ? tareaEditar.getProyectoCategoria() : "");
            comboPrioridad.setValue(tareaEditar.getPrioridad().name());
            comboEstado.setValue(tareaEditar.getEstado().name());
            dateFechaLimite.setValue(tareaEditar.getFechaLimite());
            if (tareaEditar.getTiempoEstimadoMins() > 0) {
                txtTiempoEstimado.setText(String.valueOf(tareaEditar.getTiempoEstimadoMins()));
            }
        }
    }

    @FXML
    void handleGuardar() {
        String errores = validarCampos();
        if (errores != null) {
            AlertHelper.mostrarAdvertencia(Constants.TITULO_VALIDACION, errores);
            return;
        }

        boolean exito;
        if (tareaEditar == null) {
            // Modo crear
            Tarea nuevaTarea = crearTareaDesdeFormulario();
            exito = dataManager.insertarTarea(nuevaTarea);
        } else {
            // Modo editar
            Tarea tareaActualizada = actualizarTareaDesdeFormulario();
            exito = dataManager.actualizarTarea(tareaActualizada);
        }

        if (exito) {
            String mensaje = tareaEditar == null ? Constants.MSG_TAREA_CREADA : "Tarea actualizada correctamente";
            AlertHelper.mostrarExito(Constants.TITULO_EXITO, mensaje);
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

    private Tarea actualizarTareaDesdeFormulario() {
        String titulo = txtTitulo.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        String categoria = txtCategoria.getText().trim();
        Prioridad prioridad = Prioridad.valueOf(comboPrioridad.getValue());
        EstadoTarea estado = EstadoTarea.valueOf(comboEstado.getValue());
        LocalDate fechaLimite = dateFechaLimite.getValue();

        Tarea tarea = new Tarea(tareaEditar.getIdTarea(), titulo, descripcion, categoria, estado, prioridad, fechaLimite);

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
        ValidadorFormulario validador = new ValidadorFormulario();

        // Validar título (obligatorio)
        validador.validarNoVacio("Título", txtTitulo.getText(), "Es obligatorio");

        // Validar descripción (obligatorio)
        validador.validarNoVacio("Descripción", txtDescripcion.getText(), "Es obligatoria");

        // Validar categoría (obligatorio)
        validador.validarNoVacio("Categoría", txtCategoria.getText(), "Es obligatoria");

        // Validar prioridad (obligatorio)
        validador.validarNoVacio("Prioridad", comboPrioridad.getValue(), "Es obligatoria");

        // Validar estado (obligatorio)
        validador.validarNoVacio("Estado", comboEstado.getValue(), "Es obligatorio");

        // Validar fecha límite (obligatorio, no puede ser en el pasado)
        LocalDate fecha = dateFechaLimite.getValue();
        if (fecha == null) {
            validador.agregarError("Fecha límite", "Es obligatoria");
        } else if (fecha.isBefore(LocalDate.now())) {
            validador.agregarError("Fecha límite", "No puede ser anterior a hoy");
        } else {
            validador.removerError("Fecha límite");
        }

        // Validar tiempo estimado (OPCIONAL - si proporciona debe ser positivo)
        validador.validarNumeroPositivo("Tiempo estimado", txtTiempoEstimado.getText(), false);

        // Retornar errores si existen
        return validador.obtenerErroresFormateados();
    }

    @FXML
    void handleCancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        ((Stage) btnCancelar.getScene().getWindow()).close();
    }
}
