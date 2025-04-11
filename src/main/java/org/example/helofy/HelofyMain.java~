package org.example.helofy;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class HelofyMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Cargar el FXML para la interfaz
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/helofy/HelofyMain.fxml"));
        Parent root = loader.load();

        // Mostrar la ventana sin especificar el tamaño, ya que lo toma del FXML
        primaryStage.setTitle("Helofy Music Player");
        primaryStage.setScene(new javafx.scene.Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
