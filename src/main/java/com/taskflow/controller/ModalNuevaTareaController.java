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
        StringBuilder errores = new StringBuilder();

        // Validar título (obligatorio)
        String titulo = txtTitulo.getText();
        if (Validaciones.esTextoVacio(titulo)) {
            errores.append("- El título es obligatorio\n");
        }

        // Validar descripción (obligatorio)
        String descripcion = txtDescripcion.getText();
        if (Validaciones.esTextoVacio(descripcion)) {
            errores.append("- La descripción es obligatoria\n");
        }

        // Validar categoría (obligatorio)
        String categoria = txtCategoria.getText();
        if (Validaciones.esTextoVacio(categoria)) {
            errores.append("- La categoría es obligatoria\n");
        }

        // Validar prioridad (obligatorio)
        String prioridad = comboPrioridad.getValue();
        if (Validaciones.esTextoVacio(prioridad)) {
            errores.append("- La prioridad es obligatoria\n");
        }

        // Validar estado (obligatorio)
        String estado = comboEstado.getValue();
        if (Validaciones.esTextoVacio(estado)) {
            errores.append("- El estado es obligatorio\n");
        }

        // Validar fecha límite (obligatorio, no puede ser en el pasado)
        LocalDate fecha = dateFechaLimite.getValue();
        if (fecha == null) {
            errores.append("- La fecha límite es obligatoria\n");
        } else if (fecha.isBefore(LocalDate.now())) {
            errores.append("- La fecha límite no puede ser anterior a hoy\n");
        }

        // Validar tiempo estimado (OPCIONAL - si proporciona debe ser positivo)
        String tiempo = txtTiempoEstimado.getText();
        if (!Validaciones.esTextoVacio(tiempo)) {
            if (!Validaciones.esNumeroPositivo(tiempo)) {
                errores.append("- El tiempo estimado debe ser un número positivo (opcional)\n");
            }
        }

        return errores.length() > 0 ? errores.toString() : null;
    }

    @FXML
    void handleCancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        ((Stage) btnCancelar.getScene().getWindow()).close();
    }
}
