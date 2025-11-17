package com.taskflow.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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

        // Configurar columna Acciones con botón Eliminar
        colAcciones.setCellFactory(column -> new TableCell<Asignacion, Void>() {
            private final Button btnEliminar = new Button("Eliminar");

            {
                btnEliminar.setOnAction(event -> {
                    // Funcionalidad en desarrollo
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Eliminar Asignación");
                    alert.setHeaderText(null);
                    alert.setContentText("Funcionalidad para eliminar asignación aún no está programada");
                    alert.showAndWait();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnEliminar);
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
        // Marcador de posición para añadir asignación
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Asignacion");
        alert.setHeaderText(null);
        alert.setContentText("Funcionalidad para añadir asignación en desarrollo");
        alert.showAndWait();
    }

    @FXML
    void handleCerrar() {
        // Cerrar ventana
        ((Stage) btnCerrar.getScene().getWindow()).close();
    }
}
