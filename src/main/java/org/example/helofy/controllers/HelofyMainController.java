package org.example.helofy.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import org.example.helofy.model.Song;
import org.example.helofy.utils.ImageLoader;
import org.example.helofy.utils.MusicPlayer;
import org.example.helofy.model.Playlist;
import org.example.helofy.utils.Rounded;
import java.io.IOException;
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
    @FXML private ImageView imagenBienvenida ;
    @FXML private Button createPlaylistButton;


    private final MusicPlayer musicPlayer = new MusicPlayer();
    private boolean isDraggingProgress = false;

    @FXML
    public void initialize() {
        configureMusicPlayer();
        setupControls();
        setupVolumePersistente();


        headerImage.setImage(ImageLoader.loadAppLogo2());
        Rounded.applyRoundedClip(headerImage, 15.0); // ✅ Forma correcta
        ImagenBienvenida();

    }

    // Metodo para redondear las 2 Imagenes de la bienvenida

    private void ImagenBienvenida() {
        imagenBienvenida.setImage(new Image(getClass().getResource("/org/example/helofy/styles/welcome.png").toExternalForm()));

        // Ajustar ancho fijo y mantener proporción (alto se ajusta solo)

        imagenBienvenida.setPreserveRatio(true);

        // Como el alto varía según la proporción, obtenemos el alto real mostrado:
        imagenBienvenida.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
            double width = newVal.getWidth();
            double height = newVal.getHeight();


        });
    }


    private void configureMusicPlayer() {
        musicPlayer.setOnSongChanged(song -> {
            Platform.runLater(() -> {
                if (song != null) {
                    songName.setText(song.getTitle());
                    songArtist.setText(musicPlayer.getCurrentPlaylistName());
                    loadSongCover(song);
                    selectCurrentSongInListView(song);
                } else {
                    resetSongInfo();
                }
            });
        });

        musicPlayer.setOnProgressChanged(progress -> {
            Platform.runLater(() -> {
                if (!isDraggingProgress) {
                    progressSlider.setValue(progress * 100);
                }
            });
        });

        musicPlayer.setOnSongFinished(() -> {
            Platform.runLater(() -> musicPlayer.nextSong());
        });

        musicPlayer.setOnPlayingStatusChanged(isPlaying -> {
            Platform.runLater(() -> playPauseButton.setText(isPlaying ? "⏸" : "▶"));
        });
    }

    private void setupControls() {
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) ->
                musicPlayer.setVolume(newVal.doubleValue())
        );

        progressSlider.setOnMousePressed(e -> isDraggingProgress = true);
        progressSlider.setOnMouseDragged(e -> {
            if (musicPlayer.getCurrentSong() != null) {
                musicPlayer.seek(progressSlider.getValue() / 100);
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
        Rounded.applyRoundedClip(songImage, 10.0); // ✅
    }

    public void setCenterContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = loader.load();

            if (fxmlPath.contains("LibraryView")) {
                LibraryViewController controller = loader.getController();
                controller.setMainController(this);
            }

            contentArea.getChildren().setAll(content);
        } catch (IOException e) {
            showError("Error al cargar vista", e.getMessage());
        }
    }

    public void loadPlaylistView(Playlist playlist) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/helofy/views/PlaylistView.fxml"));
            Parent view = loader.load();

            PlaylistViewController controller = loader.getController();
            controller.configurarPlaylist(playlist, musicPlayer);

            contentArea.getChildren().setAll(view);
            musicPlayer.setPlaylist(playlist);
        } catch (IOException e) {
            showError("Error al cargar playlist", e.getMessage());
        }
    }

    @FXML
    private void togglePlayback() {
        if (musicPlayer.getCurrentSong() == null) return;
        if (musicPlayer.isPlaying()) musicPlayer.pause();
        else musicPlayer.resume();
    }

    @FXML
    private void previousTrack() {
        musicPlayer.previousSong();
    }

    @FXML
    private void nextTrack() {
        musicPlayer.nextSong();
    }

    private void loadSongCover(Song song) {
        try {
            Image cover = new Image("file:" + song.getCoverPath());
            songImage.setImage(cover);
        } catch (Exception e) {
            loadDefaultCover();
        }
    }

    private void resetSongInfo() {
        songName.setText("Sin canción");
        songArtist.setText("Artista desconocido");
        loadDefaultCover();
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
            // Busca por filePath en lugar de por índice
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

    private void updateProgress(double progress) {
        Platform.runLater(() -> {
            if (!isDraggingProgress) progressSlider.setValue(progress * 100);
        });
    }

    @FXML
    private void handleShuffle() {
        musicPlayer.toggleShuffle();
        updateShuffleButton();
    }

    private void updateShuffleButton() {
        shuffleButton.setText(musicPlayer.isShuffle() ? "🔀" : "🔁");
        shuffleButton.setStyle(musicPlayer.isShuffle() ?
                "-fx-text-fill: #1DB954; -fx-font-weight: bold;" :
                "-fx-text-fill: white;"
        );
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

    @FXML
    private void handleLibraryClick() {
        setCenterContent("/org/example/helofy/views/LibraryView.fxml");
    }

    @FXML
    private void handleCreatePlaylistClick() {
        setCenterContent("/org/example/helofy/views/CreatePlaylistView.fxml");
    }
    @FXML
    private void handleEditPlaylistClick(){
        setCenterContent("/org/example/helofy/views/EditPlaylistView.fxml");

    }
    @FXML
    private void handleSuperPlayListlick() {

        setCenterContent("/org/example/helofy/views/SuperPlayListView.fxml");
    }
}