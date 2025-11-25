package com.taskflow.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import com.taskflow.model.*;
import com.taskflow.util.DataManager;
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
    private TextField txtRol;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dataManager = DataManager.getInstance();

        // Cargar usuarios y poblar comboUsuario
        for (Usuario usuario : dataManager.getUsuarios()) {
            comboUsuario.getItems().add(usuario.getNombreCompleto());
        }

        // Configurar columnas de TableView
        setupTableColumns();

        // Cargar asignaciones de ejemplo (filtrar aquellas con tareaId = 1)
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
        // Cargar asignaciones desde la base de datos
        asignaciones = FXCollections.observableArrayList(dataManager.getAsignaciones());
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
        String usuarioSeleccionado = comboUsuario.getValue();
        String rol = txtRol.getText().trim();
        String horasTexto = txtHoras.getText().trim();

        // Validar campos
        if (usuarioSeleccionado == null || usuarioSeleccionado.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validación");
            alert.setContentText("Debes seleccionar un usuario");
            alert.showAndWait();
            return;
        }

        if (rol.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validación");
            alert.setContentText("Debes especificar un rol");
            alert.showAndWait();
            return;
        }

        double horas;
        try {
            horas = Double.parseDouble(horasTexto);
            if (horas <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validación");
            alert.setContentText("Las horas deben ser un número positivo");
            alert.showAndWait();
            return;
        }

        // Encontrar el usuario por nombre
        Usuario usuario = dataManager.getUsuarios().stream()
            .filter(u -> u.getNombreCompleto().equals(usuarioSeleccionado))
            .findFirst()
            .orElse(null);

        if (usuario == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Usuario no encontrado");
            alert.showAndWait();
            return;
        }

        // Crear nueva asignación (tareaId hardcoded como 1 por ahora - esto se debería pasar dinámicamente)
        Asignacion nuevaAsignacion = new Asignacion(0, 1, usuario.getIdUsuario(), rol, horas, false);

        if (dataManager.insertarAsignacion(nuevaAsignacion)) {
            loadAsignaciones();
            updateSummaryLabels();
            // Limpiar campos
            comboUsuario.setValue(null);
            txtRol.clear();
            txtHoras.clear();
            Alert exito = new Alert(Alert.AlertType.INFORMATION);
            exito.setTitle("Éxito");
            exito.setContentText("Asignación creada correctamente");
            exito.showAndWait();
        } else {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error");
            error.setContentText("No se pudo crear la asignación");
            error.showAndWait();
        }
    }

    void handleEditarAsignacion(Asignacion asignacion) {
        // Prellenar campos con datos de la asignación
        Usuario usuario = dataManager.getUsuarios().stream()
            .filter(u -> u.getIdUsuario() == asignacion.getUsuarioId())
            .findFirst()
            .orElse(null);

        if (usuario != null) {
            comboUsuario.setValue(usuario.getNombreCompleto());
        }
        txtRol.setText(asignacion.getRolAsignacion());
        txtHoras.setText(String.valueOf(asignacion.getHorasAsignadas()));

        // Cambiar el botón para modo edición
        btnAnadirAsignacion.setText("Actualizar");
        btnAnadirAsignacion.setOnAction(event -> {
            String rol = txtRol.getText().trim();
            String horasTexto = txtHoras.getText().trim();

            if (rol.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Validación");
                alert.setContentText("Debes especificar un rol");
                alert.showAndWait();
                return;
            }

            double horas;
            try {
                horas = Double.parseDouble(horasTexto);
                if (horas <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Validación");
                alert.setContentText("Las horas deben ser un número positivo");
                alert.showAndWait();
                return;
            }

            asignacion.setRolAsignacion(rol);
            asignacion.setHorasAsignadas(horas);

            if (dataManager.actualizarAsignacion(asignacion)) {
                loadAsignaciones();
                updateSummaryLabels();
                resetFormulario();
                Alert exito = new Alert(Alert.AlertType.INFORMATION);
                exito.setTitle("Éxito");
                exito.setContentText("Asignación actualizada correctamente");
                exito.showAndWait();
            } else {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Error");
                error.setContentText("No se pudo actualizar la asignación");
                error.showAndWait();
            }
        });
    }

    private void resetFormulario() {
        comboUsuario.setValue(null);
        txtRol.clear();
        txtHoras.clear();
        btnAnadirAsignacion.setText("Añadir");
        btnAnadirAsignacion.setOnAction(event -> handleAnadirAsignacion());
    }

    @FXML
    void handleCerrar() {
        // Cerrar ventana
        ((Stage) btnCerrar.getScene().getWindow()).close();
    }
}
