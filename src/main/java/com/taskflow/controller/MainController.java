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
import com.taskflow.service.AuthService;

public class MainController implements Initializable {

    // ===========================
    // TabPane y Tabs
    // ===========================
    @FXML
    private TabPane tabPane;

    @FXML
    private Tab tabDashboardAdmin;

    @FXML
    private Tab tabGestionUsuarios;

    @FXML
    private Tab tabGestionTareas;

    @FXML
    private Tab tabDashboardEmpleado;

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

        // Configurar visibilidad de pestañas según rol del usuario
        configurarVistaPorRol();

        // Inicializar las 4 pestañas
        initializeDashboardAdmin();
        initializeGestionUsuarios();
        initializeGestionTareas();
        initializeDashboardEmpleado();
    }

    // ===========================
    // Configuración de visibilidad por rol
    // ===========================
    private void configurarVistaPorRol() {
        AuthService authService = AuthService.getInstance();
        Usuario usuarioActual = authService.getUsuarioActual();

        if (usuarioActual != null) {
            if (usuarioActual.getRol() == Rol.empleado) {
                // Si es empleado, solo mostrar Dashboard Empleado
                tabPane.getTabs().remove(tabDashboardAdmin);
                tabPane.getTabs().remove(tabGestionUsuarios);
                tabPane.getTabs().remove(tabGestionTareas);
            } else if (usuarioActual.getRol() == Rol.admin) {
                // Si es admin, solo mostrar pestañas de administración
                tabPane.getTabs().remove(tabDashboardEmpleado);
            }
        }
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
                    handleEditarUsuario(usuario);
                });

                btnEliminar.setOnAction(event -> {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmacion.setTitle("Confirmar eliminacion");
                    confirmacion.setHeaderText("Eliminar usuario");
                    confirmacion.setContentText("¿Estas seguro de eliminar a " + usuario.getNombreCompleto() + "?");

                    confirmacion.showAndWait().ifPresent(response -> {
                        if (response == javafx.scene.control.ButtonType.OK) {
                            if (dataManager.eliminarUsuario(usuario.getIdUsuario())) {
                                Alert exito = new Alert(Alert.AlertType.INFORMATION);
                                exito.setTitle("Exito");
                                exito.setHeaderText(null);
                                exito.setContentText("Usuario eliminado correctamente");
                                exito.showAndWait();
                            } else {
                                Alert error = new Alert(Alert.AlertType.ERROR);
                                error.setTitle("Error");
                                error.setHeaderText(null);
                                error.setContentText("No se pudo eliminar el usuario");
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

        // Fábrica de celdas personalizada para colAsignadosTareas (contador de asignaciones reales)
        colAsignadosTareas.setCellFactory(column -> new TableCell<Tarea, Void>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    // Obtener la tarea actual
                    Tarea tarea = getTableView().getItems().get(getIndex());

                    // Contar asignaciones reales de esta tarea
                    int count = (int) dataManager.getAsignaciones().stream()
                        .filter(a -> a.getTareaId() == tarea.getIdTarea())
                        .count();

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
                    handleEditarTarea(tarea);
                });

                btnEliminar.setOnAction(event -> {
                    Tarea tarea = getTableView().getItems().get(getIndex());
                    Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmacion.setTitle("Confirmar eliminacion");
                    confirmacion.setHeaderText("Eliminar tarea");
                    confirmacion.setContentText("¿Estas seguro de eliminar la tarea \"" + tarea.getTitulo() + "\"?");

                    confirmacion.showAndWait().ifPresent(response -> {
                        if (response == javafx.scene.control.ButtonType.OK) {
                            if (dataManager.eliminarTarea(tarea.getIdTarea())) {
                                Alert exito = new Alert(Alert.AlertType.INFORMATION);
                                exito.setTitle("Exito");
                                exito.setHeaderText(null);
                                exito.setContentText("Tarea eliminada correctamente");
                                exito.showAndWait();
                            } else {
                                Alert error = new Alert(Alert.AlertType.ERROR);
                                error.setTitle("Error");
                                error.setHeaderText(null);
                                error.setContentText("No se pudo eliminar la tarea");
                                error.showAndWait();
                            }
                        }
                    });
                });

                btnAsignaciones.setOnAction(event -> {
                    Tarea tarea = getTableView().getItems().get(getIndex());
                    handleAsignaciones(tarea);
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
        colTituloMisTareas.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colPrioridadMisTareas.setCellValueFactory(new PropertyValueFactory<>("prioridad"));
        colFechaLimiteMisTareas.setCellValueFactory(new PropertyValueFactory<>("fechaLimite"));

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
        ObservableList<Tarea> tareasLimitadas = FXCollections.observableArrayList(
            allTareas.subList(0, Math.min(5, allTareas.size()))
        );

        // Usar Platform.runLater para que la tabla renderice correctamente en la pestaña
        Platform.runLater(() -> tableMisTareas.setItems(tareasLimitadas));

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

    void handleAsignaciones(Tarea tarea) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/fxml/ModalAsignaciones.fxml")
            );
            javafx.scene.layout.Pane content = loader.load();

            // Obtener el controlador y configurarlo con la tarea seleccionada
            ModalAsignacionesController controller = loader.getController();
            controller.setTarea(tarea);

            // Abrir modal
            ViewManager.getInstance().openModal("Asignaciones", content, 800, 600);
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("No se pudo abrir el formulario de asignaciones");
            alert.showAndWait();
        }
    }

    void handleEditarUsuario(Usuario usuario) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/fxml/ModalNuevoUsuario.fxml")
            );
            javafx.scene.layout.Pane content = loader.load();

            // Obtener el controlador y configurarlo para edición
            ModalNuevoUsuarioController controller = loader.getController();
            controller.setUsuarioEditar(usuario);

            // Abrir modal
            ViewManager.getInstance().openModal("Editar Usuario", content, 600, 500);
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("No se pudo abrir el formulario de edición");
            alert.showAndWait();
        }
    }

    void handleEditarTarea(Tarea tarea) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/fxml/ModalNuevaTarea.fxml")
            );
            javafx.scene.layout.Pane content = loader.load();

            // Obtener el controlador y configurarlo para edición
            ModalNuevaTareaController controller = loader.getController();
            controller.setTareaEditar(tarea);

            // Abrir modal
            ViewManager.getInstance().openModal("Editar Tarea", content, 700, 600);
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("No se pudo abrir el formulario de edición");
            alert.showAndWait();
        }
    }
}
