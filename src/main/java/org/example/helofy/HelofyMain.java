package org.example.helofy;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.example.helofy.model.RutaUsuario;
import org.example.helofy.utils.ImageLoader;

import java.io.File;

public class HelofyMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        File userFile = RutaUsuario.JSON_USUARIO.toFile();
        FXMLLoader loader;
        if (userFile.exists()) {
            loader = new FXMLLoader(getClass().getResource("/org/example/helofy/HelofyMain.fxml"));
        } else {
            loader = new FXMLLoader(getClass().getResource("/org/example/helofy/views/usuario/RegistroUsuario.fxml"));
        }

        Parent root = loader.load();

        primaryStage.initStyle(javafx.stage.StageStyle.UNDECORATED);

        primaryStage.getIcons().add(ImageLoader.loadAppLogo());
        primaryStage.setTitle("Helofy Music Player");
        primaryStage.setResizable(false);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
