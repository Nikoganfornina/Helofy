package org.example.helofy.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.OverrunStyle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.helofy.model.Perfil;
import org.example.helofy.model.Playlist;
import org.example.helofy.model.RutasPerfil;
import org.example.helofy.model.Song;
import org.example.helofy.utils.MusicPlayer;
import org.example.helofy.utils.Rounded;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HelofyMainController {

    @FXML private StackPane contentArea;
    @FXML private Slider volumeSlider;
    @FXML private Button playPauseButton;
    @FXML private Button previousButton;
    @FXML private Button nextButton;
    @FXML private Slider progressSlider;
    @FXML private Label songName;
    @FXML private Label songArtist;
    @FXML private ImageView songImage;
    @FXML private Button shuffleButton;
    @FXML private ImageView headerImage;
    @FXML private ImageView imagenBienvenida;
    @FXML private Button createPlaylistButton;
    @FXML private Label lblTiempoTranscurrido;
    @FXML private Label lblTiempoRestante;
    @FXML private StackPane songNameContainer;
    @FXML private ImageView userImage;
    @FXML private Button userButton;
    @FXML private Button ExitButton;
    @FXML private HBox header;

    @FXML private ListView<String> playlistListViewFavorites;

    private final MusicPlayer musicPlayer = new MusicPlayer();
    private boolean isDraggingProgress = false;
    private double duracionTotalSegundos = 0;
    private TranslateTransition marqueeAnimation;

    private Perfil perfil ;
    private double xOffset = 0;
    private double yOffset = 0;

    // Aquí asigna tu lista global real de playlists (carga externa)
    private List<Playlist> listasGlobales = new ArrayList<>();

    @FXML
    public void initialize() {
        // TODO: Carga tus playlists globales reales aquí y asigna a listasGlobales

        inicializarListaFavoritas();

        header.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        header.setOnMouseDragged(event -> {
            Stage stage = (Stage) header.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        configureMusicPlayer();
        setupControls();
        setupVolumePersistente();

        headerImage.setImage(new Image(getClass().getResource("/org/example/helofy/styles/logoapp2.png").toExternalForm()));
        Rounded.applyRoundedClip(headerImage, 15.0);

        imagenBienvenida.setImage(new Image(getClass().getResource("/org/example/helofy/styles/welcome.png").toExternalForm()));
        imagenBienvenida.setPreserveRatio(true);

        System.out.println("Inicializando controlador...");
    }

    // Inicializa la lista de favoritas en la UI
    private void inicializarListaFavoritas() {
        playlistListViewFavorites.getItems().clear();

        if (perfil != null && perfil.getPlaylistsFavoritas() != null && !perfil.getPlaylistsFavoritas().isEmpty()) {
            List<String> favoritasValidas = new ArrayList<>();
            for (String nombreFav : perfil.getPlaylistsFavoritas()) {
                // Solo añadir si existe la playlist real en listasGlobales
                if (buscarPlaylistPorNombre(nombreFav) != null) {
                    favoritasValidas.add(nombreFav);
                } else {
                    System.out.println("Favorita no encontrada en listasGlobales: " + nombreFav);
                }
            }
            if (favoritasValidas.isEmpty()) {
                favoritasValidas.add("No hay favoritos");
            }
            playlistListViewFavorites.getItems().addAll(favoritasValidas);
        } else {
            playlistListViewFavorites.getItems().add("No hay favoritos");
        }

        playlistListViewFavorites.setCellFactory(list -> new ListCell<>() {
            private final Button button = new Button();

            {
                button.getStyleClass().add("menu-button");
                button.setMaxWidth(Double.MAX_VALUE);
                button.setAlignment(Pos.CENTER);
                button.setOnAction(e -> {
                    String playlistName = button.getText();
                    if (playlistName.equals("No hay favoritos")) {
                        return;
                    }
                    System.out.println("Botón playlist favorito pulsado: " + playlistName);
                    cargarPlaylistPorNombre(playlistName);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    button.setText(item);
                    setGraphic(button);
                    setAlignment(Pos.CENTER);
                }
            }
        });
    }

    // Carga la playlist seleccionada por nombre
    private void cargarPlaylistPorNombre(String nombre) {
        Playlist playlist = buscarPlaylistPorNombre(nombre);
        if (playlist != null) {
            System.out.println("Cargando playlist: " + nombre);
            loadPlaylistView(playlist);
        } else {
            System.out.println("Playlist no encontrada: " + nombre);
            showError("Error", "No se encontró la playlist: " + nombre);
        }
    }

    // Busca playlist en listasGlobales por nombre
    private Playlist buscarPlaylistPorNombre(String nombre) {
        if (listasGlobales == null) return null;
        for (Playlist p : listasGlobales) {
            if (p.getName().equalsIgnoreCase(nombre)) { // ignore case para evitar fallos
                return p;
            }
        }
        return null;
    }

    // Refresca la lista de favoritas en la UI (llamar tras añadir/quitar favoritos)
    public void refrescarListaFavoritas() {
        System.out.println("Refrescando lista de favoritas...");
        inicializarListaFavoritas();
    }

    private void configureMusicPlayer() {
        musicPlayer.setOnSongChanged(song -> {
            Platform.runLater(() -> {
                if (song != null) {
                    updateSongName(song.getTitle());
                    songArtist.setText(musicPlayer.getCurrentPlaylistName());
                    loadSongCover(song);
                    lblTiempoTranscurrido.setText("0:00");
                    lblTiempoRestante.setText("-0:00");
                    selectCurrentSongInListView(song);
                } else {
                    updateSongName("No hay canción en reproducción");
                    songArtist.setText("Artista desconocido");
                    loadDefaultCover();
                    duracionTotalSegundos = 0;
                    progressSlider.setMax(1);
                    lblTiempoTranscurrido.setText("0:00");
                    lblTiempoRestante.setText("-0:00");
                }
            });
        });

        musicPlayer.setOnDurationChanged(duracion -> {
            Platform.runLater(() -> {
                duracionTotalSegundos = duracion;
                progressSlider.setMax(duracionTotalSegundos);
                lblTiempoRestante.setText("-" + formatearTiempo(duracionTotalSegundos));
            });
        });

        musicPlayer.setOnProgressChanged(progress -> {
            Platform.runLater(() -> {
                if (!isDraggingProgress && duracionTotalSegundos > 0) {
                    double tiempoActual = progress * duracionTotalSegundos;
                    if (tiempoActual > duracionTotalSegundos) tiempoActual = duracionTotalSegundos;
                    progressSlider.setValue(tiempoActual);
                    lblTiempoTranscurrido.setText(formatearTiempo(tiempoActual));
                    lblTiempoRestante.setText("-" + formatearTiempo(duracionTotalSegundos - tiempoActual));
                }
            });
        });

        musicPlayer.setOnSongFinished(() -> Platform.runLater(musicPlayer::nextSong));
        musicPlayer.setOnPlayingStatusChanged(isPlaying ->
                Platform.runLater(() -> playPauseButton.setText(isPlaying ? "⏸" : "▶"))
        );
    }

    private void updateSongName(String text) {
        songName.setText(text);
        setupSongNameMarquee();
    }

    private void setupSongNameMarquee() {
        Platform.runLater(() -> {
            songName.setEllipsisString("");
            songName.setTextOverrun(OverrunStyle.CLIP);
            songName.setWrapText(false);

            if (marqueeAnimation != null) {
                marqueeAnimation.stop();
                marqueeAnimation = null;
                songName.setTranslateX(0);
            }

            songName.applyCss();
            songName.layout();
            songNameContainer.applyCss();
            songNameContainer.layout();

            double labelWidth = songName.getWidth();
            double containerWidth = songNameContainer.getWidth();

            Rectangle clip = new Rectangle(containerWidth, songNameContainer.getHeight());
            songNameContainer.setClip(clip);

            if (labelWidth > containerWidth) {
                double distance = labelWidth - containerWidth;

                marqueeAnimation = new TranslateTransition(Duration.seconds(8), songName);
                marqueeAnimation.setFromX(0);
                marqueeAnimation.setToX(-distance);
                marqueeAnimation.setCycleCount(Animation.INDEFINITE);
                marqueeAnimation.setAutoReverse(true);
                marqueeAnimation.play();
            }
        });
    }

    private void setupControls() {
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> musicPlayer.setVolume(newVal.doubleValue()));

        progressSlider.setOnMousePressed(e -> isDraggingProgress = true);

        progressSlider.setOnMouseDragged(e -> {
            if (musicPlayer.getCurrentSong() != null) {
                double valor = progressSlider.getValue();
                lblTiempoTranscurrido.setText(formatearTiempo(valor));
                lblTiempoRestante.setText("-" + formatearTiempo(duracionTotalSegundos - valor));
                musicPlayer.seek(valor / duracionTotalSegundos);
            }
        });

        progressSlider.setOnMouseReleased(e -> isDraggingProgress = false);

        playPauseButton.setOnAction(e -> togglePlayback());
        previousButton.setOnAction(e -> previousTrack());
        nextButton.setOnAction(e -> nextTrack());
        shuffleButton.setOnAction(e -> handleShuffle());
    }

    private void setupVolumePersistente() {
        volumeSlider.setValue(musicPlayer.getVolume());
        Rounded.applyRoundedClip(songImage, 10.0);
    }

    public void setCenterContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = loader.load();

            Object controller = loader.getController();
            if (controller instanceof LibraryViewController) {
                ((LibraryViewController) controller).setMainController(this);
            } else if (controller instanceof SuperPlaylistController) {
                ((SuperPlaylistController) controller).setMainController(this);
            }

            contentArea.getChildren().setAll(content);
        } catch (Exception e) {
            showError("Error al cargar vista", e.getMessage());
        }
    }

    public void loadPlaylistView(Playlist playlist) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/helofy/views/PlaylistView.fxml"));
            Parent view = loader.load();

            PlaylistViewController controller = loader.getController();
            controller.setMainController(this); // Asignamos controlador principal
            controller.configurarPlaylist(playlist, musicPlayer);

            contentArea.getChildren().setAll(view);
            musicPlayer.setPlaylist(playlist);
        } catch (Exception e) {
            showError("Error al cargar playlist", e.getMessage());
        }
    }

    @FXML private void togglePlayback() {
        if (musicPlayer.isPlaying()) musicPlayer.pause();
        else musicPlayer.resume();
    }

    @FXML
    private void nextTrack() {
        musicPlayer.nextSong();
    }

    @FXML
    private void previousTrack() {
        musicPlayer.previousSong();
    }

    private void loadSongCover(Song song) {
        try {
            Image cover = new Image("file:" + song.getCoverPath());
            songImage.setImage(cover);
        } catch (Exception e) {
            loadDefaultCover();
        }
    }



    private void loadDefaultCover() {
        try {
            Image defaultCover = new Image(
                    Objects.requireNonNull(getClass().getResource("/org/example/helofy/styles/default_cover.png")).toExternalForm()
            );
            songImage.setImage(defaultCover);
        } catch (Exception e) {
            System.err.println("Error cargando cover predeterminado");
        }
    }

    private void selectCurrentSongInListView(Song currentSong) {
        if (contentArea.getChildren().isEmpty()) return;

        Parent currentContent = (Parent) contentArea.getChildren().get(0);
        ListView<Song> listaCanciones = (ListView<Song>) currentContent.lookup("#listaCanciones");

        if (listaCanciones != null && currentSong != null) {
            for (int i = 0; i < listaCanciones.getItems().size(); i++) {
                Song song = listaCanciones.getItems().get(i);
                if (song.getFilePath().equals(currentSong.getFilePath())) {
                    int finalIndex = i;
                    Platform.runLater(() -> {
                        listaCanciones.getSelectionModel().select(finalIndex);
                        listaCanciones.scrollTo(finalIndex);
                    });
                    break;
                }
            }
        }
    }

    private String formatearTiempo(double segundos) {
        int mins = (int) segundos / 60;
        int segs = (int) segundos % 60;
        return String.format("%d:%02d", mins, segs);
    }

    @FXML private void handleShuffle() {
        musicPlayer.toggleShuffle();
        updateShuffleButton();
    }

    private void updateShuffleButton() {
        shuffleButton.setText(musicPlayer.isShuffle() ? "🔀" : "🔁");
        shuffleButton.setStyle(musicPlayer.isShuffle()
                ? "-fx-text-fill: #1DB954; -fx-font-weight: bold;"
                : "-fx-text-fill: white;");
    }

    public void onSongSelected(Song song) {
        musicPlayer.playSong(song);
    }

    private void showError(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    @FXML private void handleLibraryClick() {
        setCenterContent("/org/example/helofy/views/LibraryView.fxml");
    }

    @FXML private void handleCreatePlaylistClick() {
        setCenterContent("/org/example/helofy/views/CreatePlaylistView.fxml");
    }

    @FXML private void handleEditPlaylistClick() {
        setCenterContent("/org/example/helofy/views/EditPlaylistView.fxml");
    }

    @FXML private void handleUserClick() {
        setCenterContent("/org/example/helofy/views/usuario/PerfilUsuario.fxml");
    }

    @FXML private void handleExitClick() {
        Platform.exit();
    }

    @FXML
    private void handleMinimizeClick() {
        Stage stage = (Stage) header.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void handleSuperPlayListClick() {
        setCenterContent("/org/example/helofy/views/SuperPlayListView.fxml");
    }

}
