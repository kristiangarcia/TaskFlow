package com.taskflow;

import com.taskflow.view.ViewManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
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

            // Cargar la vista de login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Pane root = loader.load();

            // Configurar la ventana de login
            primaryStage.setTitle(APP_TITLE + " - Login");

            // Crear y mostrar la escena
            Scene scene = new Scene(root, 400, 600);
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();
            primaryStage.show();

        } catch (Exception e) {
            System.err.println("Error al iniciar la aplicacion");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
