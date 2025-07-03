package org.example.helofy.controllers;

import javafx.application.Platform;
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
import org.example.helofy.utils.ImageLoader;
import org.example.helofy.utils.MusicPlayer;
import org.example.helofy.utils.Rounded;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class PlaylistViewController {

    @FXML private ListView<Song> listaCanciones;
    @FXML private ImageView imgPortada;
    @FXML private Label lblTitulo;
    @FXML private Label lblDescripcion;
    @FXML private Label lblNumeroCanciones;
    @FXML private Button playButton;
    @FXML private Button orderButton;

    @FXML
    private Button botonCorazon;

    private boolean esFavorita = false;

    private Playlist playlistActual;
    private MusicPlayer musicPlayer;
    private boolean ordenAscendente = true;

    private HelofyMainController mainController;

    public void setMainController(HelofyMainController mainController) {
        this.mainController = mainController;
    }
    public void configurarPlaylist(Playlist playlist, MusicPlayer player) {
        this.playlistActual = playlist;
        this.musicPlayer = player;

        lblTitulo.setText(playlist.getName());
        lblDescripcion.setText(cargarDescripcionDesdeArchivo(new File(playlist.getCoverPath()).getParentFile()));

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

        precargarDuracionCanciones(() -> Platform.runLater(() -> {
            ordenarCancionesPorNombre();
            actualizarTextoBotonPlay();
            actualizarEstadoOrden();
        }));
    }

    // Precarga duración para todas las canciones y luego ejecuta el callback
    private void precargarDuracionCanciones(Runnable callback) {
        List<Song> canciones = playlistActual.getSongs();
        if (canciones == null || canciones.isEmpty()) {
            callback.run();
            return;
        }

        AtomicInteger pendientes = new AtomicInteger(canciones.size());

        for (Song song : canciones) {
            // Solo precargar si duración <= 0
            if (song.getDuration() <= 0) {
                try {
                    Media media = new Media(new File(song.getFilePath()).toURI().toString());
                    MediaPlayer mp = new MediaPlayer(media);
                    mp.setOnReady(() -> {
                        double duracion = media.getDuration().toSeconds();
                        song.setDuration(duracion);
                        mp.dispose();
                        if (pendientes.decrementAndGet() == 0) callback.run();
                    });
                    mp.setOnError(() -> {
                        song.setDuration(0);
                        mp.dispose();
                        if (pendientes.decrementAndGet() == 0) callback.run();
                    });
                } catch (Exception e) {
                    song.setDuration(0);
                    if (pendientes.decrementAndGet() == 0) callback.run();
                }
            } else {
                // Ya tiene duración
                if (pendientes.decrementAndGet() == 0) callback.run();
            }
        }
    }

    private void ordenarCancionesPorNombre() {
        if (playlistActual == null) return;

        List<Song> cancionesOrdenadas = playlistActual.getSongs().stream()
                .sorted((s1, s2) -> ordenAscendente
                        ? s1.getTitle().compareToIgnoreCase(s2.getTitle())
                        : s2.getTitle().compareToIgnoreCase(s1.getTitle()))
                .collect(Collectors.toList());

        listaCanciones.getItems().setAll(cancionesOrdenadas);

        int numCanciones = cancionesOrdenadas.size();
        double duracionTotal = cancionesOrdenadas.stream().mapToDouble(Song::getDuration).sum();
        actualizarDescripcionCanciones(numCanciones, duracionTotal);

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
                            ? String.format("%d:%02d", (int) (duracion / 60), (int) (duracion % 60))
                            : "--:--";

                    Label tiempo = new Label(duracionStr);
                    tiempo.setStyle("-fx-text-fill: #aaaaaa;");

                    Region espaciador = new Region();
                    HBox.setHgrow(espaciador, Priority.ALWAYS);

                    HBox hbox = new HBox(nombreCancion, espaciador, tiempo);
                    hbox.setSpacing(10);
                    hbox.setStyle("-fx-padding: 4 15 4 15;");

                    if (cancion.equals(musicPlayer.getCurrentSong())) {
                        nombreCancion.setStyle("-fx-text-fill: #00aeef; -fx-font-weight: bold;");
                    }

                    setGraphic(hbox);

                    setOnMouseClicked(e -> {
                        if (e.getClickCount() == 1) {
                            musicPlayer.playSong(cancion);
                            listaCanciones.refresh();
                            actualizarTextoBotonPlay();
                        }
                    });
                }
            }
        });

        listaCanciones.refresh();
    }

    private void actualizarDescripcionCanciones(int numCanciones, double duracionTotalSegundos) {
        int horas = (int) duracionTotalSegundos / 3600;
        int minutos = (int) (duracionTotalSegundos % 3600) / 60;

        String texto = numCanciones + " Cancione" + (numCanciones == 1 ? "" : "s") + ", "
                + horas + " h " + minutos + " min";

        lblNumeroCanciones.setText(texto);
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

    @FXML
    private void handleToggleFavorito() {
        esFavorita = !esFavorita;
        actualizarIconoFavorito();
    }

    private void actualizarIconoFavorito() {
        if (esFavorita) {
            botonCorazon.setText("♥");
            botonCorazon.setStyle("-fx-text-fill: #00aeef; -fx-font-size: 18px;");
        } else {
            botonCorazon.setText("♡");
            botonCorazon.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
        }
    }
    private Image getDefaultCoverImage() {
        try {
            return new Image(Objects.requireNonNull(getClass().getResourceAsStream(String.valueOf(ImageLoader.loadDefaultFrontPage()))));
        } catch (Exception e) {
            return null;
        }
    }

    @FXML
    private void reproducirPrimeraCancion() {
        if (musicPlayer == null || playlistActual == null) return;

        Song cancionActual = musicPlayer.getCurrentSong();
        if (cancionActual == null) {
            Song primera = playlistActual.getSongs().stream().findFirst().orElse(null);
            if (primera != null) {
                musicPlayer.playSong(primera);
            }
        } else {
            if (musicPlayer.isPlaying()) {
                musicPlayer.pause();
            } else {
                musicPlayer.resume();
            }
        }
        actualizarTextoBotonPlay();
    }

    private void actualizarTextoBotonPlay() {
        if (musicPlayer != null && musicPlayer.isPlaying()) {
            playButton.setText("⏸");
        } else {
            playButton.setText("▶");
        }
    }

    @FXML
    private void alternarOrden() {
        ordenAscendente = !ordenAscendente;
        actualizarEstadoOrden();
        ordenarCancionesPorNombre();
    }

    private void actualizarEstadoOrden() {
        if (ordenAscendente) {
            orderButton.getStyleClass().setAll("round-button", "orden-ascendente");
            orderButton.setText("⇅");
        } else {
            orderButton.getStyleClass().setAll("round-button", "orden-descendente");
            orderButton.setText("⇵");
        }
    }
}
