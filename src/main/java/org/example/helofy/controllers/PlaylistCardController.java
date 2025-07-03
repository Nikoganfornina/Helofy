package org.example.helofy.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import org.example.helofy.model.Playlist;
import org.example.helofy.utils.ImageLoader;
import org.example.helofy.utils.Rounded;

import java.io.File;
import java.net.URL;

public class PlaylistCardController {

    @FXML
    private ImageView imgPortada;

    @FXML
    private Label lblNombre;

    private Runnable alHacerClick;

    // Ruta de imagen por defecto
    private static final String DEFAULT_COVER_PATH = "/org/example/helofy/styles/defaultImage.png";

    public void configurar(Playlist playlist, Runnable accionClick) {
        if (playlist == null) {
            System.out.println("Error: Playlist es null");
            return;
        }

        // Configurar nombre de la playlist
        lblNombre.setText(playlist.getName().toUpperCase());

        // Intentar cargar la portada personalizada
        try {
            String coverPath = playlist.getCoverPath();
            if (coverPath != null && !coverPath.isBlank()) {
                File coverFile = new File(coverPath);
                if (coverFile.exists()) {
                    imgPortada.setImage(new Image(coverFile.toURI().toString()));
                } else {
                    System.out.println("Archivo de portada no encontrado."+ lblNombre + " Usando imagen por defecto.");
                    cargarImagenPorDefecto();
                }
            } else {
                System.out.println("Ruta de portada vacía. Usando imagen por defecto.");
                cargarImagenPorDefecto();
            }
        } catch (Exception e) {
            System.out.println("Error cargando la imagen de portada: " + e.getMessage());
            cargarImagenPorDefecto();
        }

        // Redondear imagen
        Rounded.applyRoundedClip(imgPortada, 10.0); // ✅

        // Guardar la acción para cuando se haga click
        this.alHacerClick = accionClick;
    }

    private void cargarImagenPorDefecto() {
        try{
            imgPortada.setImage(ImageLoader.loadDefaultFrontPage());
        } catch (NullPointerException e){
            System.out.println("Error: No se encontró la imagen por defecto en recursos.");
        }
    }

    @FXML
    private void handleClick(MouseEvent event) {
        System.out.println("Tarjeta clicada");
        if (alHacerClick != null) {
            alHacerClick.run();
        } else {
            System.out.println("Advertencia: No se asignó ninguna acción al click.");
        }
    }
}
