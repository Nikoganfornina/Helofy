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

import java.io.IOException;
import java.util.function.Consumer;
import java.io.File;  // Import necesario para verificar archivos

public class PlaylistViewController {

    @FXML private ListView<Song> listaCanciones;
    @FXML private ImageView imgPortada;
    @FXML private Label lblTitulo;
    private Song currentSong = null;

    public void configurarPlaylist(Playlist playlist, MusicPlayer player) {
        final String DEFAULT_COVER_PATH = "/org/example/helofy/styles/defaultImage.png"; // Usa tu ruta aquí

        lblTitulo.setText(playlist.getName());

        try {
            // Verificar si el archivo existe primero
            File coverFile = new File(playlist.getCoverPath());
            if (!coverFile.exists()) {
                throw new IOException("Archivo de portada no encontrado");
            }

            // Cargar imagen personalizada
            Image portada = new Image("file:" + playlist.getCoverPath());
            imgPortada.setImage(portada);

        } catch (Exception e) {
            // Cargar imagen por defecto desde recursos internos
            Image defaultCover = new Image(
                    getClass().getResource(DEFAULT_COVER_PATH).toString()  // Usamos la variable
            );
            imgPortada.setImage(defaultCover);
        }

        // Aplicar redondeado una sola vez (evita duplicación)
        new Rounded(imgPortada).applyRoundedClip(10);

        // Configurar lista de canciones
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

                    // Resaltar canción actual
                    setStyle(cancion.equals(currentSong) ?
                            "-fx-text-fill: #00aeef; -fx-font-weight: bold;" : "");

                    // Manejar clic
                    setOnMouseClicked(e -> {
                        if (e.getClickCount() == 1) {
                            player.playSong(cancion);
                        }
                    });
                }
            }
        });

        // Listeners del reproductor
        player.setOnSongChanged(song -> {
            currentSong = song;
            listaCanciones.refresh();
        });

        player.setOnSongFinished(() -> {
            if (currentSong != null) {
                int index = listaCanciones.getItems().indexOf(currentSong);
                if (index != -1) {
                    boolean esUltima = index == listaCanciones.getItems().size() - 1;
                    player.playSong(esUltima ?
                            listaCanciones.getItems().get(0) :
                            listaCanciones.getItems().get(index + 1)
                    );
                }
            }
        });
    }
}