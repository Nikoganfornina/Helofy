package org.example.helofy.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
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

        // Cargar descripción (solo si no es superplaylist)
        String descripcion = "";
        if (playlist.getIsSuperPlaylist() != null && playlist.getIsSuperPlaylist()) {
            descripcion = playlist.getDescription();
        } else {
            File carpetaPlaylist = new File(playlist.getCoverPath()).getParentFile();
            descripcion = cargarDescripcionDesdeArchivo(carpetaPlaylist);
            playlist.setDescription(descripcion);
        }
        lblDescripcion.setText(descripcion);

        // Cargar imagen portada
        try {
            String coverPath = playlist.getCoverPath();
            Image img;
            if (coverPath != null && !coverPath.isBlank()) {
                if (coverPath.startsWith("/")) {
                    img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(coverPath)));
                } else {
                    File coverFile = new File(coverPath);
                    if (coverFile.exists()) {
                        img = new Image(coverFile.toURI().toString());
                    } else {
                        System.out.println("Archivo de portada no encontrado. Usando imagen por defecto.");
                        img = getDefaultCoverImage();
                    }
                }
            } else {
                System.out.println("Ruta de portada vacía. Usando imagen por defecto.");
                img = getDefaultCoverImage();
            }
            imgPortada.setImage(img);
        } catch (Exception e) {
            System.out.println("Error cargando la imagen de portada: " + e.getMessage());
            imgPortada.setImage(getDefaultCoverImage());
        }

        Rounded.applyRoundedClip(imgPortada, 10.0);

        listaCanciones.getItems().setAll(playlist.getSongs());

        // Contar canciones y sumar duración total
        int numCanciones = playlist.getSongs().size();
        double duracionTotalSegundos = 0;
        for (Song song : playlist.getSongs()) {
            duracionTotalSegundos += song.getDuration();
        }

        String duracionFormateada = formatearDuracion(duracionTotalSegundos);

        lblNumeroCanciones.setText(
                String.format("Esta Playlist contiene %d canción%s, duración total %s",
                        numCanciones,
                        (numCanciones == 1) ? "" : "es",
                        duracionFormateada
                )
        );

        // Configurar celda para mostrar título y destacar canción actual
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

                    if (cancion.equals(player.getCurrentSong())) {
                        setStyle("-fx-text-fill: #00aeef; -fx-font-weight: bold;");
                    } else {
                        setStyle("");
                    }

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

    private String cargarDescripcionDesdeArchivo(File carpetaPlaylist) {
        if (carpetaPlaylist == null) {
            return "Playlist sin descripción";
        }
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

    private Image getDefaultCoverImage() {
        final String DEFAULT_COVER_PATH = "/org/example/helofy/styles/default_cover.png";
        try {
            return new Image(Objects.requireNonNull(getClass().getResourceAsStream(DEFAULT_COVER_PATH)));
        } catch (Exception e) {
            System.out.println("Error cargando imagen por defecto: " + e.getMessage());
            return null;
        }
    }

    // Formatear duración de segundos a formato hh:mm:ss o mm:ss
    private String formatearDuracion(double totalSegundos) {
        int totalSegsInt = (int) Math.round(totalSegundos);
        int horas = totalSegsInt / 3600;
        int minutos = (totalSegsInt % 3600) / 60;
        int segundos = totalSegsInt % 60;

        if (horas > 0) {
            return String.format("%d:%02d:%02d", horas, minutos, segundos);
        } else {
            return String.format("%d:%02d", minutos, segundos);
        }
    }
}
