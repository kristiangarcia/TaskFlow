package com.taskflow.view;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Gestor de vistas y navegación de la aplicación usando FXML
 */
public class ViewManager {

    private static ViewManager instance;
    private Stage primaryStage;
    private Pane currentContentPane;

    private ViewManager() {}

    /**
     * Obtiene la instancia única del ViewManager (Singleton)
     */
    public static ViewManager getInstance() {
        if (instance == null) {
            instance = new ViewManager();
        }
        return instance;
    }

    /**
     * Establece el Stage principal de la aplicación
     */
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    /**
     * Obtiene el Stage principal
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Establece el contenedor de contenido actual
     */
    public void setContentPane(Pane pane) {
        this.currentContentPane = pane;
    }

    /**
     * Obtiene el contenedor de contenido actual
     */
    public Pane getContentPane() {
        return currentContentPane;
    }

    /**
     * Cambia el contenido de la vista principal con animación
     */
    public void setContent(Pane newContent) {
        if (currentContentPane != null) {
            currentContentPane.getChildren().clear();
            currentContentPane.getChildren().add(newContent);
            animarEntrada(newContent);
        }
    }

    /**
     * Carga una vista FXML y la muestra en el área de contenido principal
     *
     * @param fxmlPath Ruta al archivo FXML (relativa a resources)
     */
    public void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Pane view = loader.load();
            setContent(view);
        } catch (IOException e) {
            System.err.println("Error cargando vista FXML: " + fxmlPath);
            e.printStackTrace();
        }
    }

    /**
     * Aplica el icono de la aplicación a un Stage
     */
    private void aplicarIcono(Stage stage) {
        try {
            InputStream iconStream = getClass().getResourceAsStream("/images/icon.png");
            if (iconStream != null) {
                Image icon = new Image(iconStream);
                stage.getIcons().add(icon);
            }
        } catch (Exception e) {
            // Icono no disponible, continuar sin él
        }
    }

    /**
     * Carga CSS en una escena
     */
    private void cargarCSS(Scene scene) {
        try {
            URL cssResource = getClass().getResource("/styles.css");
            if (cssResource != null) {
                scene.getStylesheets().add(cssResource.toExternalForm());
            }
        } catch (Exception e) {
            // CSS no disponible, continuar sin estilos
        }
    }

    /**
     * Anima un Pane con una animación de fade in y escala suave
     */
    private void animarEntrada(Pane pane) {
        // Inicializar valores
        pane.setOpacity(0);
        pane.setScaleX(0.95);
        pane.setScaleY(0.95);

        // Animación de fade in
        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), pane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        // Animación de escala
        ScaleTransition scale = new ScaleTransition(Duration.millis(400), pane);
        scale.setFromX(0.95);
        scale.setFromY(0.95);
        scale.setToX(1);
        scale.setToY(1);

        // Ejecutar animaciones en paralelo
        fadeIn.play();
        scale.play();
    }

    /**
     * Anima un Stage (modal) con entrada suave
     */
    private void animarModalEntrada(Stage stage) {
        stage.setOpacity(0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), stage.getScene().getRoot());
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        ScaleTransition scale = new ScaleTransition(Duration.millis(300), stage.getScene().getRoot());
        scale.setFromX(0.9);
        scale.setFromY(0.9);
        scale.setToX(1);
        scale.setToY(1);
        scale.play();

        // Hacer visible después de una pequeña pausa
        stage.setOpacity(1);
    }

    /**
     * Abre una ventana modal cargando un archivo FXML
     *
     * @param fxmlPath Ruta al archivo FXML
     * @param title Título de la ventana
     * @param width Ancho de la ventana
     * @param height Alto de la ventana
     * @return El Stage creado
     */
    public Stage openModalFxml(String fxmlPath, String title, int width, int height) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Pane content = loader.load();

            Stage modalStage = new Stage();
            modalStage.setTitle(title);
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(primaryStage);
            aplicarIcono(modalStage);

            Scene scene = new Scene(content, width, height);
            cargarCSS(scene);
            modalStage.setScene(scene);

            // Centrar respecto a la ventana principal
            if (primaryStage != null) {
                modalStage.setX(primaryStage.getX() + (primaryStage.getWidth() - width) / 2);
                modalStage.setY(primaryStage.getY() + (primaryStage.getHeight() - height) / 2);
            }

            modalStage.show();
            animarModalEntrada(modalStage);
            return modalStage;
        } catch (IOException e) {
            System.err.println("Error cargando modal FXML: " + fxmlPath);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Abre una ventana modal con dimensiones estándar (600x500)
     */
    public Stage openModalFxml(String fxmlPath, String title) {
        return openModalFxml(fxmlPath, title, 600, 500);
    }

    /**
     * Abre una ventana modal programática (sin FXML)
     *
     * @param title Título de la ventana
     * @param content Contenido de la ventana
     * @param width Ancho de la ventana
     * @param height Alto de la ventana
     * @return El Stage creado
     */
    public Stage openModal(String title, Pane content, int width, int height) {
        Stage modalStage = new Stage();
        modalStage.setTitle(title);
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.initOwner(primaryStage);
        aplicarIcono(modalStage);

        Scene scene = new Scene(content, width, height);
        cargarCSS(scene);
        modalStage.setScene(scene);

        // Centrar respecto a la ventana principal
        if (primaryStage != null) {
            modalStage.setX(primaryStage.getX() + (primaryStage.getWidth() - width) / 2);
            modalStage.setY(primaryStage.getY() + (primaryStage.getHeight() - height) / 2);
        }

        modalStage.show();
        animarModalEntrada(modalStage);
        return modalStage;
    }

    /**
     * Abre una ventana modal con dimensiones estándar
     */
    public Stage openModal(String title, Pane content) {
        return openModal(title, content, 600, 500);
    }

    /**
     * Cambia la vista principal completa con tamaño específico
     */
    public void cambiarVista(String fxmlPath, String titulo, int width, int height) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Pane root = loader.load();

        Scene scene = new Scene(root, width, height);
        cargarCSS(scene);
        primaryStage.setScene(scene);
        primaryStage.setTitle(titulo);
        primaryStage.setResizable(true);
        primaryStage.centerOnScreen();

        // Animar la entrada de la nueva vista
        animarEntrada(root);
    }

    /**
     * Cambia la vista principal completa con tamaño por defecto
     */
    public void cambiarVista(String fxmlPath, String titulo) throws IOException {
        cambiarVista(fxmlPath, titulo, 1200, 800);
    }

    /**
     * Cierra una ventana
     */
    public void closeWindow(Window window) {
        if (window != null) {
            window.hide();
        }
    }
}
