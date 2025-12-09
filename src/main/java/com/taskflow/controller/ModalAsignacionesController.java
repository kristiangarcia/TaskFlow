package com.taskflow.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.application.Platform;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import com.taskflow.model.*;
import com.taskflow.util.DataManager;
import com.taskflow.util.ValidadorFormulario;
import com.taskflow.util.AlertHelper;
import com.taskflow.util.Constants;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ModalAsignacionesController implements Initializable {

    @FXML
    private Label lblTareaTitulo;

    @FXML
    private Label lblTareaInfo;

    @FXML
    private ComboBox<String> comboUsuario;

    @FXML
    private ComboBox<String> comboRol;

    @FXML
    private TextField txtHoras;

    @FXML
    private Button btnAnadirAsignacion;

    @FXML
    private TableView<Asignacion> tableAsignaciones;

    @FXML
    private Label lblTotalUsuarios;

    @FXML
    private Label lblHorasAsignadas;

    @FXML
    private Label lblTiempoEstimado;

    @FXML
    private Button btnCerrar;

    @FXML
    private TableColumn<Asignacion, Integer> colUsuario;

    @FXML
    private TableColumn<Asignacion, String> colRol;

    @FXML
    private TableColumn<Asignacion, Double> colHoras;

    @FXML
    private TableColumn<Asignacion, Boolean> colCompletado;

    @FXML
    private TableColumn<Asignacion, Void> colAcciones;

    private DataManager dataManager;
    private ObservableList<Asignacion> asignaciones;
    private Asignacion asignacionEditar; // null si es nueva, contiene la asignación si es edición
    private Tarea tareaActual; // Almacena la tarea seleccionada

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dataManager = DataManager.getInstance();

        // Cargar usuarios y poblar comboUsuario
        for (Usuario usuario : dataManager.getUsuarios()) {
            comboUsuario.getItems().add(usuario.getNombreCompleto());
        }

        // Poblar comboRol con roles válidos (deben coincidir con el constraint de la BD)
        comboRol.getItems().addAll(
            "desarrollador",
            "diseñador",
            "gerente",
            "revisor",
            "tester",
            "documentador"
        );

        // Configurar columnas de TableView
        setupTableColumns();
    }

    /**
     * Establece la tarea actual y carga sus asignaciones
     */
    public void setTarea(Tarea tarea) {
        this.tareaActual = tarea;

        // Mostrar información de la tarea en los labels
        lblTareaTitulo.setText(tarea.getTitulo());
        lblTareaInfo.setText("ID: " + tarea.getIdTarea() + " | Categoría: " + tarea.getProyectoCategoria() +
                           " | Prioridad: " + tarea.getPrioridad());

        // Cargar asignaciones de la tarea actual
        loadAsignaciones();

        // Actualizar etiquetas de resumen
        updateSummaryLabels();
    }

    private void setupTableColumns() {
        // Configurar columna Usuario con fábrica de celdas para buscar nombre de usuario
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuarioId"));
        colUsuario.setCellFactory(column -> new TableCell<Asignacion, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    Usuario usuario = dataManager.getUsuarios().stream()
                        .filter(u -> u.getIdUsuario() == item)
                        .findFirst()
                        .orElse(null);
                    setText(usuario != null ? usuario.getNombreCompleto() : "Desconocido");
                }
            }
        });

        // Configurar columna Rol
        colRol.setCellValueFactory(new PropertyValueFactory<>("rolAsignacion"));

        // Configurar columna Horas
        colHoras.setCellValueFactory(new PropertyValueFactory<>("horasAsignadas"));

        // Configurar columna Completado con CheckBox
        colCompletado.setCellValueFactory(new PropertyValueFactory<>("completado"));
        colCompletado.setCellFactory(column -> new TableCell<Asignacion, Boolean>() {
            private final CheckBox checkBox = new CheckBox();

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    checkBox.setSelected(item != null && item);
                    setGraphic(checkBox);
                }
            }
        });

        // Configurar columna Acciones con botones Editar y Eliminar
        colAcciones.setCellFactory(column -> new TableCell<Asignacion, Void>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");
            private final HBox pane = new HBox(5, btnEditar, btnEliminar);

            {
                btnEditar.setOnAction(event -> {
                    Asignacion asignacion = getTableView().getItems().get(getIndex());
                    handleEditarAsignacion(asignacion);
                });

                btnEliminar.setOnAction(event -> {
                    Asignacion asignacion = getTableView().getItems().get(getIndex());
                    Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmacion.setTitle("Confirmar eliminación");
                    confirmacion.setHeaderText("Eliminar asignación");
                    confirmacion.setContentText("¿Estás seguro de eliminar esta asignación?");

                    confirmacion.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            if (dataManager.eliminarAsignacion(asignacion.getIdAsignacion())) {
                                loadAsignaciones();
                                updateSummaryLabels();
                                Alert exito = new Alert(Alert.AlertType.INFORMATION);
                                exito.setTitle("Éxito");
                                exito.setContentText("Asignación eliminada correctamente");
                                exito.showAndWait();
                            } else {
                                Alert error = new Alert(Alert.AlertType.ERROR);
                                error.setTitle("Error");
                                error.setContentText("No se pudo eliminar la asignación");
                                error.showAndWait();
                            }
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        });
    }

    private void loadAsignaciones() {
        if (tareaActual == null) {
            return;
        }

        // Cargar asignaciones de la tarea actual filtrando por tareaId
        java.util.List<Asignacion> todasAsignaciones = dataManager.getAsignaciones();
        java.util.List<Asignacion> asignacionesFiltradas = todasAsignaciones.stream()
            .filter(a -> a.getTareaId() == tareaActual.getIdTarea())
            .collect(java.util.stream.Collectors.toList());

        asignaciones = FXCollections.observableArrayList(asignacionesFiltradas);
        tableAsignaciones.setItems(asignaciones);
    }

    private void updateSummaryLabels() {
        int totalUsuarios = asignaciones.size();
        double horasAsignadas = asignaciones.stream()
            .mapToDouble(Asignacion::getHorasAsignadas)
            .sum();

        lblTotalUsuarios.setText("Total usuarios: " + totalUsuarios);
        lblHorasAsignadas.setText("Horas asignadas: " + horasAsignadas + "h");
        lblTiempoEstimado.setText("Tiempo estimado: --");
    }

    @FXML
    void handleAnadirAsignacion() {
        ValidadorFormulario validador = validarAsignacion();

        if (validador.tieneErrores()) {
            // Mostrar validación visual (campos en rojo) después de renderizar
            Platform.runLater(validador::validarVisualmente);

            // Mostrar alerta con errores
            AlertHelper.mostrarAdvertencia(Constants.TITULO_VALIDACION,
                validador.obtenerErroresFormateados());
            return;
        }

        String usuarioSeleccionado = comboUsuario.getValue();
        String rol = comboRol.getValue();
        String horasTexto = txtHoras.getText().trim();
        double horas = Double.parseDouble(horasTexto);

        // Encontrar el usuario por nombre
        Usuario usuario = dataManager.getUsuarios().stream()
            .filter(u -> u.getNombreCompleto().equals(usuarioSeleccionado))
            .findFirst()
            .orElse(null);

        if (usuario == null) {
            AlertHelper.mostrarError("Error", "Usuario no encontrado");
            return;
        }

        // Crear nueva asignación con la tarea actual
        Asignacion nuevaAsignacion = new Asignacion(0, tareaActual.getIdTarea(), usuario.getIdUsuario(), rol, horas, false);

        if (dataManager.insertarAsignacion(nuevaAsignacion)) {
            loadAsignaciones();
            updateSummaryLabels();
            resetFormulario();
            AlertHelper.mostrarExito("Éxito", "Asignación creada correctamente");
        } else {
            AlertHelper.mostrarError("Error", "No se pudo crear la asignación");
        }
    }

    void handleEditarAsignacion(Asignacion asignacion) {
        // Guardar la asignación en modo edición
        asignacionEditar = asignacion;

        // Prellenar campos con datos de la asignación
        Usuario usuario = dataManager.getUsuarios().stream()
            .filter(u -> u.getIdUsuario() == asignacion.getUsuarioId())
            .findFirst()
            .orElse(null);

        if (usuario != null) {
            comboUsuario.setValue(usuario.getNombreCompleto());
        }
        comboRol.setValue(asignacion.getRolAsignacion());
        txtHoras.setText(String.valueOf(asignacion.getHorasAsignadas()));

        // Cambiar el botón para modo edición
        btnAnadirAsignacion.setText("Actualizar");
        btnAnadirAsignacion.setOnAction(event -> handleActualizarAsignacion());
    }

    private void handleActualizarAsignacion() {
        if (asignacionEditar == null) {
            return;
        }

        ValidadorFormulario validador = validarAsignacion();

        if (validador.tieneErrores()) {
            // Mostrar validación visual (campos en rojo) después de renderizar
            Platform.runLater(validador::validarVisualmente);

            // Mostrar alerta con errores
            AlertHelper.mostrarAdvertencia(Constants.TITULO_VALIDACION,
                validador.obtenerErroresFormateados());
            return;
        }

        String rol = comboRol.getValue();
        String horasTexto = txtHoras.getText().trim();
        double horas = Double.parseDouble(horasTexto);

        asignacionEditar.setRolAsignacion(rol);
        asignacionEditar.setHorasAsignadas(horas);

        if (dataManager.actualizarAsignacion(asignacionEditar)) {
            loadAsignaciones();
            updateSummaryLabels();
            resetFormulario();
            AlertHelper.mostrarExito("Éxito", "Asignación actualizada correctamente");
        } else {
            AlertHelper.mostrarError("Error", "No se pudo actualizar la asignación");
        }
    }

    private void resetFormulario() {
        comboUsuario.setValue(null);
        comboRol.setValue(null);
        txtHoras.clear();
        btnAnadirAsignacion.setText("Añadir");
        btnAnadirAsignacion.setOnAction(event -> handleAnadirAsignacion());
        asignacionEditar = null; // Limpiar modo edición
    }

    private ValidadorFormulario validarAsignacion() {
        ValidadorFormulario validador = new ValidadorFormulario();

        // Registrar controles para validación visual
        validador.registrarControl("Usuario", comboUsuario);
        validador.registrarControl("Rol", comboRol);
        validador.registrarControl("Horas", txtHoras);

        // Validar usuario (obligatorio)
        String usuarioSeleccionado = comboUsuario.getValue();
        if (usuarioSeleccionado == null || usuarioSeleccionado.isEmpty()) {
            validador.agregarError("Usuario", "Debes seleccionar un usuario");
        } else {
            validador.removerError("Usuario");
        }

        // Validar rol (obligatorio)
        String rol = comboRol.getValue();
        if (rol == null || rol.isEmpty()) {
            validador.agregarError("Rol", "Debes seleccionar un rol");
        } else {
            validador.removerError("Rol");
        }

        // Validar horas (obligatorio, debe ser número positivo)
        String horasTexto = txtHoras.getText().trim();
        if (horasTexto.isEmpty()) {
            validador.agregarError("Horas", "Las horas son obligatorias");
        } else {
            try {
                double horas = Double.parseDouble(horasTexto);
                if (horas <= 0) {
                    validador.agregarError("Horas", "Las horas deben ser un número positivo");
                } else {
                    validador.removerError("Horas");
                }
            } catch (NumberFormatException e) {
                validador.agregarError("Horas", "Las horas deben ser un número válido");
            }
        }

        return validador;
    }

    @FXML
    void handleCerrar() {
        // Cerrar ventana
        ((Stage) btnCerrar.getScene().getWindow()).close();
    }
}
