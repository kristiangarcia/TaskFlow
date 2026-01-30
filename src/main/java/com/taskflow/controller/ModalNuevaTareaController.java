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
import javafx.application.Platform;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;

public class ModalNuevaTareaController implements Initializable {

    @FXML private Label lblTitulo;
    @FXML private TextField txtTitulo;
    @FXML private TextArea txtDescripcion;
    @FXML private ComboBox<String> comboCategoria;
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

        // Agregar eventos de teclado
        configurarEventosTeclado();
    }

    /**
     * Configura eventos de teclado: Enter=Guardar, Escape=Cancelar
     */
    private void configurarEventosTeclado() {
        // Listener para todos los campos
        EventHandler<KeyEvent> keyHandler = this::manejarEventoTeclado;
        txtTitulo.setOnKeyPressed(keyHandler);
        txtDescripcion.setOnKeyPressed(keyHandler);
        comboCategoria.setOnKeyPressed(keyHandler);
        comboPrioridad.setOnKeyPressed(keyHandler);
        comboEstado.setOnKeyPressed(keyHandler);
        dateFechaLimite.setOnKeyPressed(keyHandler);
        txtTiempoEstimado.setOnKeyPressed(keyHandler);
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
        // Inicializar categorías predefinidas + categorías existentes de la BD
        comboCategoria.getItems().addAll(
            "Desarrollo Web",
            "Desarrollo Móvil",
            "Diseño UI/UX",
            "Marketing",
            "Administración",
            "Soporte",
            "QA/Testing",
            "DevOps",
            "Documentación",
            "Otros"
        );
        // Añadir categorías existentes de las tareas en BD que no estén en la lista
        dataManager.getTareas().stream()
            .map(Tarea::getProyectoCategoria)
            .filter(cat -> cat != null && !cat.isEmpty())
            .distinct()
            .filter(cat -> !comboCategoria.getItems().contains(cat))
            .forEach(cat -> comboCategoria.getItems().add(cat));

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
            // Establecer categoría en el ComboBox (si no existe, se añade)
            String categoria = tareaEditar.getProyectoCategoria();
            if (categoria != null && !categoria.isEmpty()) {
                if (!comboCategoria.getItems().contains(categoria)) {
                    comboCategoria.getItems().add(categoria);
                }
                comboCategoria.setValue(categoria);
            }
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
        ValidadorFormulario validador = validarCampos();

        if (validador.tieneErrores()) {
            // Mostrar validación visual (campos en rojo) después de renderizar
            Platform.runLater(validador::validarVisualmente);

            // Mostrar alerta con errores
            AlertHelper.mostrarAdvertencia(Constants.TITULO_VALIDACION,
                validador.obtenerErroresFormateados());
            return;
        }

        try {
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
        } catch (SQLException e) {
            // Extraer mensaje de error de la BD y mostrar de forma amigable
            String mensajeError = e.getMessage();
            if (mensajeError.contains("debe ser posterior")) {
                mensajeError = "La fecha límite debe ser posterior a la fecha de creación";
            } else if (mensajeError.contains("violates")) {
                mensajeError = "Error: Datos inválidos o falta información requerida";
            }
            AlertHelper.mostrarAdvertencia(Constants.TITULO_VALIDACION, mensajeError);
        }
    }

    private Tarea crearTareaDesdeFormulario() {
        String titulo = txtTitulo.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        // Obtener categoría del ComboBox (puede ser seleccionada o escrita manualmente)
        String categoria = comboCategoria.getValue() != null ? comboCategoria.getValue().trim() :
                          (comboCategoria.getEditor().getText() != null ? comboCategoria.getEditor().getText().trim() : "");
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
        // Obtener categoría del ComboBox (puede ser seleccionada o escrita manualmente)
        String categoria = comboCategoria.getValue() != null ? comboCategoria.getValue().trim() :
                          (comboCategoria.getEditor().getText() != null ? comboCategoria.getEditor().getText().trim() : "");
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

    private ValidadorFormulario validarCampos() {
        ValidadorFormulario validador = new ValidadorFormulario();

        // Registrar controles para validación visual
        validador.registrarControl("Título", txtTitulo);
        validador.registrarControl("Descripción", txtDescripcion);
        validador.registrarControl("Categoría", comboCategoria);
        validador.registrarControl("Prioridad", comboPrioridad);
        validador.registrarControl("Estado", comboEstado);
        validador.registrarControl("Fecha límite", dateFechaLimite);
        validador.registrarControl("Tiempo estimado", txtTiempoEstimado);

        // Validar título (obligatorio)
        validador.validarNoVacio("Título", txtTitulo.getText(), "Es obligatorio");

        // Validar descripción (obligatorio)
        validador.validarNoVacio("Descripción", txtDescripcion.getText(), "Es obligatoria");

        // Validar categoría (obligatorio) - obtener del ComboBox o del editor si es editable
        String categoriaValor = comboCategoria.getValue() != null ? comboCategoria.getValue() :
                               (comboCategoria.getEditor().getText() != null ? comboCategoria.getEditor().getText() : "");
        validador.validarNoVacio("Categoría", categoriaValor, "Es obligatoria");

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

        // Validar tiempo estimado (OBLIGATORIO - debe estar entre 15 y 999 minutos)
        String tiempoTexto = txtTiempoEstimado.getText();
        if (Validaciones.esTextoVacio(tiempoTexto)) {
            validador.agregarError("Tiempo estimado", "Es obligatorio (15-999 minutos)");
        } else if (!Validaciones.numeroEnRango(tiempoTexto, 15, 999)) {
            validador.agregarError("Tiempo estimado", "Debe estar entre 15 y 999 minutos");
        } else {
            validador.removerError("Tiempo estimado");
        }

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
