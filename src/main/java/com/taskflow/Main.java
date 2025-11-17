package com.taskflow;

import com.taskflow.view.ViewManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import static com.taskflow.util.Constants.*;

/**
 * Clase principal de la aplicación TaskFlow
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Inicializar ViewManager
            ViewManager viewManager = ViewManager.getInstance();
            viewManager.setPrimaryStage(primaryStage);

            // Cargar la vista principal desde FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
            BorderPane root = loader.load();

            // Configurar la ventana principal
            primaryStage.setTitle("TaskFlow - Gestión Inteligente de Tareas");

            // Crear y mostrar la escena
            Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            System.err.println("Error al iniciar la aplicación");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
