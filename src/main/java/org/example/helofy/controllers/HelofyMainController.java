package org.example.helofy.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.helofy.model.Playlist;
import org.example.helofy.model.Song;
import org.example.helofy.utils.MusicPlayer;
import org.example.helofy.utils.PlaylistLoader;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class HelofyMainController {

    @FXML public StackPane contentArea;
    @FXML public Slider volumeSlider;
    @FXML public Button playButton;
    @FXML public Slider progressSlider;
    @FXML public VBox playlistsContainer;
    @FXML public Label songName;
    @FXML public ImageView songImage;

    private MusicPlayer musicPlayer = new MusicPlayer();
    private boolean isPlaying = false;

    @FXML
    public void initialize() {
        configureMusicPlayer();
        loadPlaylists();
        setupControls();
    }

    private void configureMusicPlayer() {
        musicPlayer.setOnSongChanged(this::updateSongInfo);
    }

    private void loadPlaylists() {
        List<Playlist> playlists = PlaylistLoader.loadPlaylists(System.getProperty("user.home") + "/Desktop/HelofySongs");
        playlistsContainer.getChildren().clear();

        for (Playlist playlist : playlists) {
            Button btn = new Button(playlist.getName());
            btn.getStyleClass().add("playlist-button");
            btn.setOnAction(e -> loadPlaylistView(playlist));
            playlistsContainer.getChildren().add(btn);
        }
    }

    private void setupControls() {
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) ->
                musicPlayer.setVolume(newVal.doubleValue() / 100));
    }

    private void loadPlaylistView(Playlist playlist) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/helofy/views/PlaylistView.fxml"));
            Parent view = loader.load();

            PlaylistViewController controller = loader.getController();
            controller.initialize(playlist, musicPlayer);

            contentArea.getChildren().setAll(view);
        } catch (IOException ex) {
            showError("Error loading playlist: " + ex.getMessage());
        }
    }

    private void updateSongInfo() {
        Song current = musicPlayer.getCurrentSong();
        if (current != null) {
            songName.setText(current.getTitle());
            try {
                songImage.setImage(new Image(new File(current.getCoverPath()).toURI().toString()));
            } catch (Exception e) {
                songImage.setImage(new Image(Objects.requireNonNull(
                        getClass().getResource("/org/example/helofy/styles/default_cover.png")).toString()));
            }
        }
    }

    @FXML
    private void togglePlayback() {
        if (isPlaying) {
            musicPlayer.pause();
            playButton.setText("⏯");
        } else {
            musicPlayer.resume();
            playButton.setText("⏸");
        }
        isPlaying = !isPlaying;
    }

    @FXML
    private void previousTrack() {
        musicPlayer.previousSong();
    }

    @FXML
    private void nextTrack() {
        musicPlayer.nextSong();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }
}