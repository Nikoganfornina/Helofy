package org.example.helofy.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.example.helofy.model.Playlist;
import org.example.helofy.model.Song;
import org.example.helofy.utils.MusicPlayer;
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
    @FXML private Label lblNumeroCanciones;

    public void configurarPlaylist(Playlist playlist, MusicPlayer player) {
        lblTitulo.setText(playlist.getName());

        // Descripción
        String descripcion = "";
        if (playlist.getIsSuperPlaylist() != null && playlist.getIsSuperPlaylist()) {
            descripcion = playlist.getDescription();
        } else {
            File carpetaPlaylist = new File(playlist.getCoverPath()).getParentFile();
            descripcion = cargarDescripcionDesdeArchivo(carpetaPlaylist);
            playlist.setDescription(descripcion);
        }
        lblDescripcion.setText(descripcion);

        // Imagen portada
        try {
            String coverPath = playlist.getCoverPath();
            Image img;
            if (coverPath != null && !coverPath.isBlank()) {
                if (coverPath.startsWith("/")) {
                    img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(coverPath)));
                } else {
                    File coverFile = new File(coverPath);
                    img = coverFile.exists()
                            ? new Image(coverFile.toURI().toString())
                            : getDefaultCoverImage();
                }
            } else {
                img = getDefaultCoverImage();
            }
            imgPortada.setImage(img);
        } catch (Exception e) {
            imgPortada.setImage(getDefaultCoverImage());
        }

        Rounded.applyRoundedClip(imgPortada, 10.0);
        listaCanciones.getItems().setAll(playlist.getSongs());

        // Formatear descripción con cantidad y duración
        int numCanciones = playlist.getSongs().size();
        actualizarDescripcionCanciones(numCanciones, calcularDuracionTotal(playlist));

        // Obtener duración real si alguna es 0
        for (Song song : playlist.getSongs()) {
            if (song.getDuration() <= 0) {
                try {
                    Media media = new Media(new File(song.getFilePath()).toURI().toString());
                    MediaPlayer mediaPlayer = new MediaPlayer(media);
                    mediaPlayer.setOnReady(() -> {
                        double duracionReal = media.getDuration().toSeconds();
                        song.setDuration(duracionReal);
                        actualizarDescripcionCanciones(numCanciones, calcularDuracionTotal(playlist));
                        listaCanciones.refresh();
                    });
                } catch (Exception e) {
                    System.out.println("Error leyendo duración de: " + song.getTitle() + " → " + e.getMessage());
                }
            }
        }

        // Celdas personalizadas con duración a la derecha
        listaCanciones.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Song cancion, boolean vacio) {
                super.updateItem(cancion, vacio);
                if (vacio || cancion == null) {
                    setText(null);
                    setGraphic(null);
                    setOnMouseClicked(null);
                } else {
                    Label nombreCancion = new Label(cancion.getTitle());
                    nombreCancion.setStyle("-fx-text-fill: white;");

                    double duracion = cancion.getDuration();
                    String duracionStr = duracion > 0
                            ? String.format("%d:%02d", (int)(duracion / 60), (int)(duracion % 60))
                            : "--:--";

                    Label tiempo = new Label(duracionStr);
                    tiempo.setStyle("-fx-text-fill: #aaaaaa;");

                    Region espaciador = new Region();
                    HBox.setHgrow(espaciador, Priority.ALWAYS);

                    HBox hbox = new HBox(nombreCancion, espaciador, tiempo);
                    hbox.setSpacing(10);
                    hbox.setStyle("-fx-padding: 4 15 4 15;");

                    if (cancion.equals(player.getCurrentSong())) {
                        nombreCancion.setStyle("-fx-text-fill: #00aeef; -fx-font-weight: bold;");
                    }

                    setGraphic(hbox);

                    setOnMouseClicked(e -> {
                        if (e.getClickCount() == 1) {
                            player.playSong(cancion);
                            listaCanciones.refresh();
                        }
                    });
                }
            }
        });
    }

    private void actualizarDescripcionCanciones(int numCanciones, double duracionTotalSegundos) {
        int horas = (int) duracionTotalSegundos / 3600;
        int minutos = (int) (duracionTotalSegundos % 3600) / 60;

        String texto = numCanciones + " Cancione" + (numCanciones == 1 ? "" : "s") + ", "
                + horas + " h " + minutos + " min";

        lblNumeroCanciones.setText(texto);
    }

    private double calcularDuracionTotal(Playlist playlist) {
        return playlist.getSongs().stream()
                .mapToDouble(Song::getDuration)
                .sum();
    }

    private String cargarDescripcionDesdeArchivo(File carpetaPlaylist) {
        if (carpetaPlaylist == null) return "Playlist sin descripción";

        File archivoDescripcion = new File(carpetaPlaylist, "descripcion.txt");
        try {
            if (!archivoDescripcion.exists()) {
                archivoDescripcion.createNewFile();
                return "Playlist sin descripción";
            }

            BufferedReader reader = new BufferedReader(new FileReader(archivoDescripcion));
            StringBuilder contenido = new StringBuilder();
            String linea;
            while ((linea = reader.readLine()) != null) {
                contenido.append(linea).append("\n");
            }
            reader.close();

            String resultado = contenido.toString().trim();
            return resultado.isBlank() ? "Playlist sin descripción" : resultado;
        } catch (Exception e) {
            return "Playlist sin descripción";
        }
    }

    private Image getDefaultCoverImage() {
        final String DEFAULT_COVER_PATH = "/org/example/helofy/styles/default_cover.png";
        try {
            return new Image(Objects.requireNonNull(getClass().getResourceAsStream(DEFAULT_COVER_PATH)));
        } catch (Exception e) {
            return null;
        }
    }
}
