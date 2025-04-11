package org.example.helofy.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.helofy.model.Playlist;
import org.example.helofy.utils.Rounded;
import java.io.File;

public class PlaylistCardController {
    @FXML private ImageView imgPortada;
    @FXML private Label lblNombre;

    private Runnable alHacerClick;
    private final String DEFAULT_COVER_PATH = "/org/example/helofy/styles/defaultImage.png"; // Ruta personalizable

    public void configurar(Playlist playlist, Runnable accionClick) {
        lblNombre.setText(playlist.getName());

        try {
            // Verificar si existe el archivo de portada
            File coverFile = new File(playlist.getCoverPath());
            if (!coverFile.exists()) {
                throw new Exception("Archivo de portada no encontrado");
            }

            // Cargar imagen personalizada
            imgPortada.setImage(new Image("file:" + playlist.getCoverPath()));
        } catch (Exception e) {
            // Cargar imagen por defecto desde recursos
            imgPortada.setImage(new Image(
                    getClass().getResource(DEFAULT_COVER_PATH).toString()
            ));
        }

        // Aplicar redondeado a cualquier imagen (custom o default)
        new Rounded(imgPortada).applyRoundedClip(10);

        this.alHacerClick = accionClick;
    }

    @FXML
    private void handleClick() {
        if (alHacerClick != null) alHacerClick.run();
    }
}