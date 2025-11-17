package com.taskflow.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import com.taskflow.model.*;
import com.taskflow.util.DataManager;
import com.taskflow.view.ViewManager;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import static com.taskflow.util.Constants.*;

public class MainController implements Initializable {

    // ===========================
    // Botones de barra de herramientas
    // ===========================
    @FXML
    private Button btnNuevoUsuario;

    @FXML
    private Button btnNuevaTarea;

    @FXML
    private Button btnAsignaciones;

    // ===========================
    // Pestaña 1: Panel de administrador
    // ===========================
    @FXML
    private Label lblTotalTareas;

    @FXML
    private Label lblTareasPendientes;

    @FXML
    private Label lblTareasEnProgreso;

    @FXML
    private Label lblTotalUsuarios;

    @FXML
    private TableView<Tarea> tableDeadlines;

    @FXML
    private TableColumn<Tarea, String> colTituloDeadlines;

    @FXML
    private TableColumn<Tarea, String> colCategoriaDeadlines;

    @FXML
    private TableColumn<Tarea, String> colFechaLimiteDeadlines;

    // ===========================
    // Pestaña 2: Gestión de usuarios
    // ===========================
    @FXML
    private Button btnNuevoUsuarioTab;

    @FXML
    private ComboBox<String> comboFiltroRol;

    @FXML
    private CheckBox checkSoloActivos;

    @FXML
    private TableView<Usuario> tableUsuarios;

    @FXML
    private TableColumn<Usuario, Integer> colIdUsuarios;

    @FXML
    private TableColumn<Usuario, String> colNombreUsuarios;

    @FXML
    private TableColumn<Usuario, String> colEmailUsuarios;

    @FXML
    private TableColumn<Usuario, String> colTelefonoUsuarios;

    @FXML
    private TableColumn<Usuario, Rol> colRolUsuarios;

    @FXML
    private TableColumn<Usuario, Boolean> colActivoUsuarios;

    @FXML
    private TableColumn<Usuario, Void> colAccionesUsuarios;

    @FXML
    private Label lblPaginacion;

    // ===========================
    // Pestaña 3: Gestión de tareas
    // ===========================
    @FXML
    private Button btnNuevaTareaTab;

    @FXML
    private ComboBox<String> comboEstado;

    @FXML
    private ComboBox<String> comboPrioridad;

    @FXML
    private ComboBox<String> comboCategoria;

    @FXML
    private TableView<Tarea> tableTareas;

    @FXML
    private TableColumn<Tarea, Integer> colIdTareas;

    @FXML
    private TableColumn<Tarea, String> colTituloTareas;

    @FXML
    private TableColumn<Tarea, String> colCategoriaTareas;

    @FXML
    private TableColumn<Tarea, Prioridad> colPrioridadTareas;

    @FXML
    private TableColumn<Tarea, EstadoTarea> colEstadoTareas;

    @FXML
    private TableColumn<Tarea, Void> colAsignadosTareas;

    @FXML
    private TableColumn<Tarea, Void> colAccionesTareas;

    // ===========================
    // Pestaña 4: Panel de empleado
    // ===========================
    @FXML
    private Label lblMisTareas;

    @FXML
    private Label lblEnProgreso;

    @FXML
    private Label lblCompletadasHoy;

    @FXML
    private Label lblTiempoTotal;

    @FXML
    private Label lblTareaFoco;

    @FXML
    private Label lblPrediccionIA;

    @FXML
    private Button btnIniciarFoco;

    @FXML
    private TableView<Tarea> tableMisTareas;

    @FXML
    private TableColumn<Tarea, String> colTituloMisTareas;

    @FXML
    private TableColumn<Tarea, Prioridad> colPrioridadMisTareas;

    @FXML
    private TableColumn<Tarea, String> colFechaLimiteMisTareas;

    @FXML
    private TableColumn<Tarea, Void> colAccionMisTareas;

    // ===========================
    // Gestor de datos
    // ===========================
    private DataManager dataManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Obtener instancia de DataManager
        dataManager = DataManager.getInstance();

        // Inicializar las 4 pestañas
        initializeDashboardAdmin();
        initializeGestionUsuarios();
        initializeGestionTareas();
        initializeDashboardEmpleado();
    }

    // ===========================
    // Pestaña 1: Inicialización del panel de administrador
    // ===========================
    private void initializeDashboardAdmin() {
        // Cargar datos desde DataManager
        ObservableList<Tarea> tareas = dataManager.getTareas();
        ObservableList<Usuario> usuarios = dataManager.getUsuarios();

        // Establecer etiquetas de métricas con contadores
        lblTotalTareas.setText(String.valueOf(tareas.size()));
        lblTareasPendientes.setText(String.valueOf(dataManager.countTareasByEstado(EstadoTarea.abierta)));
        lblTareasEnProgreso.setText(String.valueOf(dataManager.countTareasByEstado(EstadoTarea.en_progreso)));
        lblTotalUsuarios.setText(String.valueOf(usuarios.size()));

        // Configurar columnas de tableDeadlines con PropertyValueFactory
        colTituloDeadlines.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colCategoriaDeadlines.setCellValueFactory(new PropertyValueFactory<>("proyectoCategoria"));
        colFechaLimiteDeadlines.setCellValueFactory(new PropertyValueFactory<>("fechaLimite"));

        // Cargar todas las tareas
        tableDeadlines.setItems(tareas);
    }

    // ===========================
    // Pestaña 2: Inicialización de gestión de usuarios
    // ===========================
    private void initializeGestionUsuarios() {
        // Poblar comboFiltroRol
        comboFiltroRol.getItems().addAll(FILTRO_TODOS, "admin", "empleado");
        comboFiltroRol.setValue(FILTRO_TODOS);

        // Configurar columnas de tableUsuarios
        colIdUsuarios.setCellValueFactory(new PropertyValueFactory<>("idUsuario"));
        colNombreUsuarios.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        colEmailUsuarios.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTelefonoUsuarios.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colRolUsuarios.setCellValueFactory(new PropertyValueFactory<>("rol"));
        colActivoUsuarios.setCellValueFactory(new PropertyValueFactory<>("activo"));

        // Fábrica de celdas personalizada para colActivoUsuarios mostrando "Sí"/"No"
        colActivoUsuarios.setCellFactory(column -> new TableCell<Usuario, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item ? "Sí" : "No");
                }
            }
        });

        // Fábrica de celdas personalizada para colAccionesUsuarios con botones Editar y Eliminar
        colAccionesUsuarios.setCellFactory(column -> new TableCell<Usuario, Void>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");
            private final HBox pane = new HBox(5, btnEditar, btnEliminar);

            {
                btnEditar.setOnAction(event -> {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    // Funcionalidad en desarrollo
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Editar Usuario");
                    alert.setHeaderText(null);
                    alert.setContentText("Funcionalidad para editar usuario aún no está programada");
                    alert.showAndWait();
                });

                btnEliminar.setOnAction(event -> {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    // Funcionalidad en desarrollo
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Eliminar Usuario");
                    alert.setHeaderText(null);
                    alert.setContentText("Funcionalidad para eliminar usuario aún no está programada");
                    alert.showAndWait();
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

        // Cargar todos los usuarios
        ObservableList<Usuario> usuarios = dataManager.getUsuarios();
        tableUsuarios.setItems(usuarios);

        // Establecer etiqueta de paginación
        lblPaginacion.setText(String.format("Mostrando %d de %d usuarios",
            usuarios.size(), usuarios.size()));

        // Conectar acción de btnNuevoUsuarioTab
        btnNuevoUsuarioTab.setOnAction(event -> handleNuevoUsuario());
    }

    // ===========================
    // Pestaña 3: Inicialización de gestión de tareas
    // ===========================
    private void initializeGestionTareas() {
        // Obtener tareas desde DataManager
        ObservableList<Tarea> tareas = dataManager.getTareas();

        // Poblar combos (estado, prioridad, categoría)
        comboEstado.getItems().addAll(FILTRO_TODOS, "abierta", "en_progreso", "completada", "retrasada");
        comboEstado.setValue(FILTRO_TODOS);

        comboPrioridad.getItems().addAll(FILTRO_TODOS, "alta", "media", "baja");
        comboPrioridad.setValue(FILTRO_TODOS);

        comboCategoria.getItems().add(FILTRO_TODOS);
        comboCategoria.getItems().addAll(
            tareas.stream()
                .map(Tarea::getProyectoCategoria)
                .distinct()
                .sorted()
                .collect(Collectors.toList())
        );
        comboCategoria.setValue(FILTRO_TODOS);

        // Configurar columnas de tableTareas
        colIdTareas.setCellValueFactory(new PropertyValueFactory<>("idTarea"));
        colTituloTareas.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colCategoriaTareas.setCellValueFactory(new PropertyValueFactory<>("proyectoCategoria"));
        colPrioridadTareas.setCellValueFactory(new PropertyValueFactory<>("prioridad"));
        colEstadoTareas.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Fábrica de celdas personalizada para colAsignadosTareas (contador)
        colAsignadosTareas.setCellFactory(column -> new TableCell<Tarea, Void>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    // Por ahora, mostrar contador aleatorio (0-3) como marcador de posición
                    // En implementación real, esto consultaría DataManager para asignaciones
                    int count = (int) (Math.random() * 4);
                    setText(String.valueOf(count));
                }
            }
        });

        // Fábrica de celdas personalizada para colAccionesTareas con botones Editar, Eliminar y Asignaciones
        colAccionesTareas.setCellFactory(column -> new TableCell<Tarea, Void>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");
            private final Button btnAsignaciones = new Button("Asignaciones");
            private final HBox pane = new HBox(5, btnEditar, btnEliminar, btnAsignaciones);

            {
                btnEditar.setOnAction(event -> {
                    Tarea tarea = getTableView().getItems().get(getIndex());
                    // Funcionalidad en desarrollo
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Editar Tarea");
                    alert.setHeaderText(null);
                    alert.setContentText("Funcionalidad para editar tarea aún no está programada");
                    alert.showAndWait();
                });

                btnEliminar.setOnAction(event -> {
                    Tarea tarea = getTableView().getItems().get(getIndex());
                    // Funcionalidad en desarrollo
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Eliminar Tarea");
                    alert.setHeaderText(null);
                    alert.setContentText("Funcionalidad para eliminar tarea aún no está programada");
                    alert.showAndWait();
                });

                btnAsignaciones.setOnAction(event -> {
                    Tarea tarea = getTableView().getItems().get(getIndex());
                    // Abrir diálogo de asignación para esta tarea
                    handleAsignaciones();
                    System.out.println("Asignaciones for tarea: " + tarea.getTitulo());
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

        // Cargar todas las tareas
        tableTareas.setItems(tareas);

        // Conectar acción de btnNuevaTareaTab
        btnNuevaTareaTab.setOnAction(event -> handleNuevaTarea());
    }

    // ===========================
    // Pestaña 4: Inicialización del panel de empleado
    // ===========================
    private void initializeDashboardEmpleado() {
        // Establecer métricas personales codificadas
        lblMisTareas.setText("8");
        lblEnProgreso.setText("3");
        lblCompletadasHoy.setText("2");
        lblTiempoTotal.setText("4.5h");

        // Establecer etiquetas del modo enfoque
        lblTareaFoco.setText("Ninguna tarea seleccionada");
        lblPrediccionIA.setText("Tiempo estimado: --");

        // Configurar columnas de tableMisTareas
        colTituloMisTareas.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colPrioridadMisTareas.setCellValueFactory(new PropertyValueFactory<>("prioridad"));
        colFechaLimiteMisTareas.setCellValueFactory(new PropertyValueFactory<>("fechaLimite"));

        // Fábrica de celdas personalizada para colAccionMisTareas (botón)
        colAccionMisTareas.setCellFactory(column -> new TableCell<Tarea, Void>() {
            private final Button btnIniciar = new Button("Iniciar");

            {
                btnIniciar.setOnAction(event -> {
                    Tarea tarea = getTableView().getItems().get(getIndex());
                    // Actualizar etiquetas del modo enfoque
                    lblTareaFoco.setText(tarea.getTitulo());
                    lblPrediccionIA.setText("Tiempo estimado: 2.5h");
                    System.out.println("Iniciando tarea: " + tarea.getTitulo());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnIniciar);
                }
            }
        });

        // Cargar las primeras 3 tareas
        ObservableList<Tarea> allTareas = dataManager.getTareas();
        ObservableList<Tarea> limitedTareas = FXCollections.observableArrayList(
            allTareas.subList(0, Math.min(3, allTareas.size()))
        );
        tableMisTareas.setItems(limitedTareas);

        // Añadir manejador de evento para btnIniciarFoco
        btnIniciarFoco.setOnAction(event -> {
            System.out.println("Iniciando modo foco");
        });
    }

    // ===========================
    // Métodos de acción @FXML
    // ===========================

    @FXML
    void handleNuevoUsuario() {
        ViewManager.getInstance().openModalFxml("/fxml/ModalNuevoUsuario.fxml", "Nuevo Usuario");
    }

    @FXML
    void handleNuevaTarea() {
        ViewManager.getInstance().openModalFxml("/fxml/ModalNuevaTarea.fxml", "Nueva Tarea", 700, 600);
    }

    @FXML
    void handleAsignaciones() {
        ViewManager.getInstance().openModalFxml("/fxml/ModalAsignaciones.fxml", "Asignaciones", 800, 600);
    }
}
