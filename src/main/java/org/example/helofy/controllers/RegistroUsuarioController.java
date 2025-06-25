package org.example.helofy.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.example.helofy.model.RutaUsuario;
import org.example.helofy.model.Usuario;
import org.example.helofy.utils.Rounded;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class RegistroUsuarioController {

    @FXML private TextField nombreField;
    @FXML private Button crearBtn;
    @FXML private ImageView logoImage;

    @FXML
    public void initialize() {
        Rounded.applyRoundedClip(logoImage, 30.0);

        crearBtn.setOnAction(e -> {
            String nombre = nombreField.getText().trim();

            if (nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ ]+")) {
                Usuario usuario = new Usuario();
                usuario.setNombre(nombre);
                usuario.setImagenPath(""); // puedes poner una por defecto si quieres
                usuario.setNumPlaylists(0);
                usuario.setTiempoEscuchado(0);
                usuario.setPlaylistFavorita("Ninguna");

                try {
                    Path carpeta = RutaUsuario.JSON_USUARIO.getParent();
                    if (carpeta != null && !Files.exists(carpeta)) {
                        Files.createDirectories(carpeta);
                    }

                    ObjectMapper mapper = new ObjectMapper();
                    mapper.writeValue(RutaUsuario.JSON_USUARIO.toFile(), usuario);

                    // Abrir vista principal
                    Stage stage = (Stage) crearBtn.getScene().getWindow();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/helofy/HelofyMain.fxml"));
                    Parent mainRoot = loader.load();
                    stage.setScene(new Scene(mainRoot));
                    stage.setTitle("Helofy Music Player");
                    stage.setResizable(false);

                } catch (IOException ex) {
                    mostrarError("Error al guardar el usuario: " + ex.getMessage());
                    ex.printStackTrace();
                }
            } else {
                mostrarError("El nombre solo puede contener letras.");
            }
        });
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(mensaje);
        alert.show();
    }
}
