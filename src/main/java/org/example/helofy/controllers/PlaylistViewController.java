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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Objects;


public class PlaylistViewController {

    @FXML private ListView<Song> listaCanciones;
    @FXML private ImageView imgPortada;
    @FXML private Label lblTitulo;
    @FXML private Label lblDescripcion;


    public void configurarPlaylist(Playlist playlist, MusicPlayer player) {
        final String DEFAULT_COVER_PATH = "/org/example/helofy/styles/default_cover.png";
        lblTitulo.setText(playlist.getName());

        File carpetaPlaylist = new File(playlist.getCoverPath()).getParentFile();
        String descripcion = cargarDescripcionDesdeArchivo(carpetaPlaylist);
        playlist.setDescription(descripcion);
        lblDescripcion.setText(descripcion);

        try {
            File coverFile = new File(playlist.getCoverPath());
            if (!coverFile.exists()) throw new Exception("Portada no encontrada");
            imgPortada.setImage(new Image("file:" + playlist.getCoverPath()));
        } catch (Exception e) {
            imgPortada.setImage(new Image(Objects.requireNonNull(getClass().getResource(DEFAULT_COVER_PATH)).toString()));
        }

        Rounded.applyRoundedClip(imgPortada, 10.0); // ✅


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
                            listaCanciones.refresh(); // Fuerza actualización visual
                        }
                    });

                }
            }


        });
    }

    private String cargarDescripcionDesdeArchivo(File carpetaPlaylist) {
        File archivoDescripcion = new File(carpetaPlaylist, "descripcion.txt");
        System.out.println("Buscando descripcion.txt en: " + archivoDescripcion.getAbsolutePath());


        try {
            if (!archivoDescripcion.exists()) {
                System.out.println("descripcion.txt NO EXISTE. Lo creamos vacío.");
                boolean creado = archivoDescripcion.createNewFile();
                if (!creado) {
                    System.out.println("No se pudo crear descripcion.txt");
                }
                return "Playlist sin descripción";
            }

            System.out.println("descripcion.txt encontrado. Leyendo...");
            BufferedReader reader = new BufferedReader(new FileReader(archivoDescripcion));
            StringBuilder contenido = new StringBuilder();
            String linea;

            while ((linea = reader.readLine()) != null) {
                contenido.append(linea).append("\n");
            }
            reader.close();

            String resultado = contenido.toString().trim();
            System.out.println("Contenido leído: '" + resultado + "'");

            return resultado.isBlank() ? "Playlist sin descripción" : resultado;

        } catch (Exception e) {
            System.out.println("Error al leer descripcion.txt: " + e.getMessage());
            return "Playlist sin descripción";
        }
    }

}