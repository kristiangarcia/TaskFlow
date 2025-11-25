package com.taskflow.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

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
     * Cambia el contenido de la vista principal
     */
    public void setContent(Pane newContent) {
        if (currentContentPane != null) {
            currentContentPane.getChildren().clear();
            currentContentPane.getChildren().add(newContent);
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

            Scene scene = new Scene(content, width, height);
            modalStage.setScene(scene);

            // Centrar respecto a la ventana principal
            if (primaryStage != null) {
                modalStage.setX(primaryStage.getX() + (primaryStage.getWidth() - width) / 2);
                modalStage.setY(primaryStage.getY() + (primaryStage.getHeight() - height) / 2);
            }

            modalStage.show();
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

        Scene scene = new Scene(content, width, height);
        modalStage.setScene(scene);

        // Centrar respecto a la ventana principal
        if (primaryStage != null) {
            modalStage.setX(primaryStage.getX() + (primaryStage.getWidth() - width) / 2);
            modalStage.setY(primaryStage.getY() + (primaryStage.getHeight() - height) / 2);
        }

        modalStage.show();
        return modalStage;
    }

    /**
     * Abre una ventana modal con dimensiones estándar
     */
    public Stage openModal(String title, Pane content) {
        return openModal(title, content, 600, 500);
    }

    /**
     * Cambia la vista principal completa
     */
    public void cambiarVista(String fxmlPath, String titulo) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Pane root = loader.load();

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle(titulo);
        primaryStage.centerOnScreen();
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
