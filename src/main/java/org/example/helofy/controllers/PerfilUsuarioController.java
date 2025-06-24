package org.example.helofy.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import org.example.helofy.model.Usuario;
import org.example.helofy.utils.Rounded;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class PerfilUsuarioController {
    @FXML private StackPane fotoStack;
    @FXML private ImageView fotoUsuario;
    @FXML private Pane overlayFoto;
    @FXML private Label nombreUsuario, numPlaylists, tiempoMusica, playlistFavorita;

    private final Path jsonPath = Path.of("D:/REPOSITORIO/musicapp/HeloPlayList/usuario.json");
    private final Path imagenDestino = Path.of("D:/REPOSITORIO/musicapp/HeloPlayList/imagenusuario.png");
    private final String defaultImage = "/org/example/helofy/styles/defaultImage.png";

    private Usuario usuario;

    @FXML
    public void initialize() {
        overlayFoto.setVisible(false);

        // Mostrar texto al pasar ratón
        fotoStack.setOnMouseEntered(e -> overlayFoto.setVisible(true));
        fotoStack.setOnMouseExited(e -> overlayFoto.setVisible(false));

        cargarUsuario();
        cargarFoto();
    }

    private void cargarUsuario() {
        try {
            if (!Files.exists(jsonPath)) {
                usuario = new Usuario();
                usuario.setNombre("No se ha encontrado información");
                usuario.setImagenPath("");
                Alert alert = new Alert(Alert.AlertType.WARNING, "No hay usuario creado.");
                alert.show();
            } else {
                ObjectMapper mapper = new ObjectMapper();
                usuario = mapper.readValue(jsonPath.toFile(), Usuario.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        nombreUsuario.setText(usuario.getNombre());
        numPlaylists.setText(usuario.getNumPlaylists() + " Playlists");
        tiempoMusica.setText("Tiempo escuchado: " + usuario.getTiempoEscuchado() + " h");
        playlistFavorita.setText("Favorita: " + (usuario.getPlaylistFavorita() == null ? "Ninguna" : usuario.getPlaylistFavorita()));
    }

    private void cargarFoto() {
        Image image;
        if (usuario.getImagenPath() != null && !usuario.getImagenPath().isBlank() && Files.exists(Path.of(usuario.getImagenPath()))) {
            image = new Image("file:" + usuario.getImagenPath());
        } else {
            image = new Image(getClass().getResource(defaultImage).toExternalForm());
        }
        fotoUsuario.setImage(image);
        Rounded.applyRoundedClip(fotoUsuario, 100.0);
    }

    @FXML
    private void cambiarFoto() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Selecciona una nueva imagen");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );

        File selected = chooser.showOpenDialog(fotoStack.getScene().getWindow());
        if (selected != null) {
            try {
                Files.copy(selected.toPath(), imagenDestino, StandardCopyOption.REPLACE_EXISTING);
                usuario.setImagenPath(imagenDestino.toString());

                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(jsonPath.toFile(), usuario);

                cargarFoto();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
