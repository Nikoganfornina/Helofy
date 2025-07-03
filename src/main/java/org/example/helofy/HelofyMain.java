package org.example.helofy;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.helofy.utils.ImageLoader;

public class HelofyMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/helofy/HelofyMain.fxml"));


        Parent root = loader.load();

        // Quitar decoraciones y permitir transparencia
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        // Crear escena con fondo transparente
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);

        // Icono de la aplicación
        primaryStage.getIcons().add(ImageLoader.loadAppLogo());

        primaryStage.setTitle("Helofy Music Player");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
