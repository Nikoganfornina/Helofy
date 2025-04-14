package org.example.helofy.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.example.helofy.model.Playlist;
import org.example.helofy.model.Song;
import org.example.helofy.utils.MusicPlayer;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import org.example.helofy.utils.Rounded;
import java.io.File;


public class PlaylistViewController {

    @FXML private ListView<Song> listaCanciones;
    @FXML private ImageView imgPortada;
    @FXML private Label lblTitulo;

    public void configurarPlaylist(Playlist playlist, MusicPlayer player) {
        final String DEFAULT_COVER_PATH = "/org/example/helofy/styles/default_cover.png";
        lblTitulo.setText(playlist.getName());

        try {
            File coverFile = new File(playlist.getCoverPath());
            if (!coverFile.exists()) throw new Exception("Portada no encontrada");
            imgPortada.setImage(new Image("file:" + playlist.getCoverPath()));
        } catch (Exception e) {
            imgPortada.setImage(new Image(getClass().getResource(DEFAULT_COVER_PATH).toString()));
        }

        new Rounded(imgPortada).applyRoundedClip(10);
        listaCanciones.getItems().setAll(playlist.getSongs());

        listaCanciones.setCellFactory(lv -> new javafx.scene.control.ListCell<Song>() {
            @Override


            protected void updateItem(Song cancion, boolean vacio) {
                super.updateItem(cancion, vacio);

                if (vacio || cancion == null) {
                    setText(null);
                    setStyle("");
                    setOnMouseClicked(null);
                } else {
                    setText(cancion.getTitle());

                    // Aseguramos que la canción que se reproduce se resalte
                    if (cancion.equals(player.getCurrentSong())) {
                        setStyle("-fx-text-fill: #00aeef; -fx-font-weight: bold;");
                    } else {
                        setStyle("");  // Las canciones no actuales se resetean al estilo normal
                    }

                    // Al hacer clic en una canción, la seleccionamos y reproducimos
                    setOnMouseClicked(e -> {
                        if (e.getClickCount() == 1) {
                            player.playSong(cancion);
                            // Seleccionamos la canción visualmente
                            listaCanciones.getSelectionModel().select(cancion);
                            // Esto es importante para refrescar la selección visualmente en la lista
                            listaCanciones.scrollTo(cancion);
                        }
                    });
                }
            }


        });
    }
}