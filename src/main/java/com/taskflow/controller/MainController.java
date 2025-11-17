package com.taskflow.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.time.LocalDate;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
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
    private Label lblUsuariosActivos;

    @FXML
    private Label lblTareasActivas;

    @FXML
    private Label lblTareasCompletadas;

    @FXML
    private BarChart<String, Number> chartTareasPorEstado;

    @FXML
    private TableView<Tarea> tableDeadlines;

    @FXML
    private TableColumn<Tarea, String> colTituloDeadlines;

    @FXML
    private TableColumn<Tarea, String> colFechaLimiteDeadlines;

    @FXML
    private TableColumn<Tarea, Prioridad> colPrioridadDeadlines;

    @FXML
    private TableColumn<Tarea, EstadoTarea> colEstadoDeadlines;

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
    private Label lblSaludo;

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
    private Label lblCategoriaFoco;

    @FXML
    private Label lblPrioridadFoco;

    @FXML
    private Label lblTiempoEstimadoFoco;

    @FXML
    private Label lblFechaLimiteFoco;

    @FXML
    private Label lblPrediccionIA;

    @FXML
    private Button btnIniciarFoco;

    @FXML
    private BarChart<String, Number> chartProgresoSemanal;

    @FXML
    private TableView<Tarea> tableMisTareas;

    @FXML
    private TableColumn<Tarea, String> colTituloMisTareas;

    @FXML
    private TableColumn<Tarea, Prioridad> colPrioridadMisTareas;

    @FXML
    private TableColumn<Tarea, LocalDate> colFechaLimiteMisTareas;

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

        // Contar usuarios activos
        long usuariosActivos = usuarios.stream().filter(Usuario::isActivo).count();

        // Contar tareas activas (abiertas + en progreso)
        long tareasActivas = dataManager.countTareasByEstado(EstadoTarea.abierta) +
                            dataManager.countTareasByEstado(EstadoTarea.en_progreso);

        // Contar tareas completadas
        long tareasCompletadas = dataManager.countTareasByEstado(EstadoTarea.completada);

        // Establecer etiquetas de métricas
        lblUsuariosActivos.setText(String.valueOf(usuariosActivos));
        lblTareasActivas.setText(String.valueOf(tareasActivas));
        lblTareasCompletadas.setText(String.valueOf(tareasCompletadas));

        // Configurar gráfico de barras
        configurarGraficoBarras();

        // Configurar columnas de tableDeadlines
        colTituloDeadlines.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colFechaLimiteDeadlines.setCellValueFactory(new PropertyValueFactory<>("fechaLimite"));
        colPrioridadDeadlines.setCellValueFactory(new PropertyValueFactory<>("prioridad"));
        colEstadoDeadlines.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Cargar tareas ordenadas por fecha límite (próximas primero)
        ObservableList<Tarea> tareasProximas = tareas.stream()
            .filter(t -> t.getFechaLimite() != null)
            .sorted((t1, t2) -> t1.getFechaLimite().compareTo(t2.getFechaLimite()))
            .limit(10)
            .collect(Collectors.toCollection(FXCollections::observableArrayList));
        tableDeadlines.setItems(tareasProximas);
    }

    private void configurarGraficoBarras() {
        // Crear serie de datos
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        // Obtener contadores de tareas por estado
        long enProgreso = dataManager.countTareasByEstado(EstadoTarea.en_progreso);
        long abiertas = dataManager.countTareasByEstado(EstadoTarea.abierta);
        long completadas = dataManager.countTareasByEstado(EstadoTarea.completada);
        long retrasadas = dataManager.countTareasByEstado(EstadoTarea.retrasada);

        // Añadir datos al gráfico
        series.getData().add(new XYChart.Data<>("En Progreso", enProgreso));
        series.getData().add(new XYChart.Data<>("Abiertas", abiertas));
        series.getData().add(new XYChart.Data<>("Completadas", completadas));
        series.getData().add(new XYChart.Data<>("Retrasadas", retrasadas));

        // Añadir serie al gráfico
        chartTareasPorEstado.getData().add(series);
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
        // Saludo personalizado (hardcoded por ahora)
        lblSaludo.setText("Hola, Usuario");

        // Establecer métricas personales codificadas
        lblMisTareas.setText("8");
        lblEnProgreso.setText("3");
        lblCompletadasHoy.setText("2");
        lblTiempoTotal.setText("4.5h");

        // Configurar Modo Focus con la primera tarea
        ObservableList<Tarea> allTareas = dataManager.getTareas();
        if (!allTareas.isEmpty()) {
            Tarea tareaFoco = allTareas.get(0);
            lblTareaFoco.setText(tareaFoco.getTitulo());
            lblCategoriaFoco.setText(tareaFoco.getProyectoCategoria() != null ? tareaFoco.getProyectoCategoria() : "--");
            lblPrioridadFoco.setText(tareaFoco.getPrioridad() != null ? tareaFoco.getPrioridad().toString() : "--");
            lblTiempoEstimadoFoco.setText(tareaFoco.getTiempoEstimadoMins() > 0 ? tareaFoco.getTiempoEstimadoMins() + " mins" : "--");
            lblFechaLimiteFoco.setText(tareaFoco.getFechaLimite() != null ? tareaFoco.getFechaLimite().toString() : "--");
        }

        // Configurar columnas de tableMisTareas
        System.out.println("=== DEBUG tableMisTareas ===");
        System.out.println("tableMisTareas is null? " + (tableMisTareas == null));
        System.out.println("colTituloMisTareas is null? " + (colTituloMisTareas == null));
        System.out.println("colPrioridadMisTareas is null? " + (colPrioridadMisTareas == null));
        System.out.println("colFechaLimiteMisTareas is null? " + (colFechaLimiteMisTareas == null));
        System.out.println("colAccionMisTareas is null? " + (colAccionMisTareas == null));

        // Set fixed row height
        tableMisTareas.setFixedCellSize(40);

        colTituloMisTareas.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colPrioridadMisTareas.setCellValueFactory(new PropertyValueFactory<>("prioridad"));
        colFechaLimiteMisTareas.setCellValueFactory(new PropertyValueFactory<>("fechaLimite"));

        // Test con cell factory personalizada para título
        colTituloMisTareas.setCellFactory(column -> new TableCell<Tarea, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    System.out.println("Rendering cell: " + item);
                }
            }
        });

        // Fábrica de celdas personalizada para colAccionMisTareas (botón)
        colAccionMisTareas.setCellFactory(column -> new TableCell<Tarea, Void>() {
            private final Button btnIniciar = new Button("Iniciar");

            {
                btnIniciar.setOnAction(event -> {
                    Tarea tarea = getTableView().getItems().get(getIndex());
                    // Actualizar sección de Modo Focus
                    lblTareaFoco.setText(tarea.getTitulo());
                    lblCategoriaFoco.setText(tarea.getProyectoCategoria() != null ? tarea.getProyectoCategoria() : "--");
                    lblPrioridadFoco.setText(tarea.getPrioridad() != null ? tarea.getPrioridad().toString() : "--");
                    lblTiempoEstimadoFoco.setText(tarea.getTiempoEstimadoMins() > 0 ? tarea.getTiempoEstimadoMins() + " mins" : "--");
                    lblFechaLimiteFoco.setText(tarea.getFechaLimite() != null ? tarea.getFechaLimite().toString() : "--");
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

        // Cargar las primeras 5 tareas
        System.out.println("Total tareas disponibles: " + allTareas.size());
        if (!allTareas.isEmpty()) {
            System.out.println("Primera tarea: " + allTareas.get(0).getTitulo());
        }

        final ObservableList<Tarea> limitedTareas = FXCollections.observableArrayList(
            allTareas.subList(0, Math.min(5, allTareas.size()))
        );
        System.out.println("Tareas limitadas: " + limitedTareas.size());

        // Usar Platform.runLater para asegurar que la UI esté lista
        Platform.runLater(() -> {
            System.out.println("Platform.runLater: Cargando datos en tabla");
            tableMisTareas.setItems(limitedTareas);
            System.out.println("Platform.runLater: Items en tabla: " + tableMisTareas.getItems().size());
            tableMisTareas.refresh();
        });

        // Configurar gráfico de progreso semanal
        configurarGraficoSemanal();

        // Añadir manejador de evento para btnIniciarFoco
        btnIniciarFoco.setOnAction(event -> {
            System.out.println("Iniciando modo foco");
        });
    }

    private void configurarGraficoSemanal() {
        // Crear serie de datos para el progreso semanal
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        // Datos de ejemplo para cada día de la semana
        series.getData().add(new XYChart.Data<>("Lun", 4));
        series.getData().add(new XYChart.Data<>("Mar", 5));
        series.getData().add(new XYChart.Data<>("Mié", 3));
        series.getData().add(new XYChart.Data<>("Jue", 6));
        series.getData().add(new XYChart.Data<>("Vie", 2));
        series.getData().add(new XYChart.Data<>("Sáb", 0));
        series.getData().add(new XYChart.Data<>("Dom", 0));

        // Añadir serie al gráfico
        chartProgresoSemanal.getData().add(series);
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
