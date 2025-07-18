package org.example.helofy.utils;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.example.helofy.model.Playlist;
import org.example.helofy.model.Song;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;

public class MusicPlayer {
    private Playlist currentPlaylist;
    private MediaPlayer mediaPlayer;
    private List<Song> playlist = new ArrayList<>();
    private int currentIndex = -1;
    private final BooleanProperty shuffle = new SimpleBooleanProperty(false);
    private final BooleanProperty playing = new SimpleBooleanProperty(false);
    private Timeline progressUpdater;
    private Consumer<Double> onProgressChanged;
    private Consumer<Song> onSongChanged;
    private Runnable onSongFinished;
    private Consumer<Double> onDurationChanged;
    private double lastVolume = 1.0;
    private long starPlayback = 0;

    public void playSong(Song song) {
        int index = playlist.indexOf(song);
        if (index != -1) playSong(index);
    }

    public void playSong(int index) {
        registrarTiempoEscuchado();

        if (index < 0 || index >= playlist.size()) return;
        stop();
        currentIndex = index;
        internalPlaySong();

        Platform.runLater(() -> {
            if (onSongChanged != null) onSongChanged.accept(getCurrentSong());
        });
    }

    private void registrarTiempoEscuchado() {
        if (starPlayback > 0) {
            long ahora = System.currentTimeMillis();
            long tiempoEscuchado = (ahora - starPlayback) / 1000;
            UsuarioStorage.sumarTiempoEscuchado(tiempoEscuchado);
            starPlayback = 0;
        }
    }

    private void internalPlaySong() {
        try {
            Song song = playlist.get(currentIndex);
            File file = new File(song.getFilePath());
            if (!file.exists()) throw new Exception("Archivo no existe: " + song.getFilePath());

            Media media = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setVolume(lastVolume);

            mediaPlayer.setOnReady(() -> {
                Platform.runLater(() -> {
                    starPlayback = System.currentTimeMillis();
                    double duracion = mediaPlayer.getMedia().getDuration().toSeconds();
                    if (onDurationChanged != null) onDurationChanged.accept(duracion);

                    mediaPlayer.play();
                    playing.set(true);
                    startProgressTracking();
                    if (onSongChanged != null) onSongChanged.accept(song);
                });
            });

            mediaPlayer.setOnEndOfMedia(() -> {
                if (onSongFinished != null) Platform.runLater(onSongFinished);
                Platform.runLater(this::nextSong);
            });

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            if (onSongChanged != null) Platform.runLater(() -> onSongChanged.accept(null));
        }
    }

    public List<Song> getPlaylistSongs() {
        return Collections.unmodifiableList(playlist);
    }

    public void stop() {
        registrarTiempoEscuchado();

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            playing.set(false);
            stopProgressTracking();
        }
    }

    public void pause() {
        registrarTiempoEscuchado();

        if (mediaPlayer != null) {
            mediaPlayer.pause();
            playing.set(false);
        }
    }

    public void resume() {
        registrarTiempoEscuchado();

        if (mediaPlayer != null) {
            mediaPlayer.play();
            playing.set(true);
        }
    }

    public void nextSong() {
        if (shuffle.get()) playRandomSong();
        else playSong((currentIndex < playlist.size() - 1) ? currentIndex + 1 : 0);
    }

    public void previousSong() {
        if (shuffle.get()) playRandomSong();
        else if (currentIndex > 0) playSong(currentIndex - 1);
    }

    private void playRandomSong() {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < playlist.size(); i++)
            if (i != currentIndex) indices.add(i);

        if (!indices.isEmpty())
            playSong(indices.get(new Random().nextInt(indices.size())));
    }

    public void toggleShuffle() {
        shuffle.set(!shuffle.get());
        if (shuffle.get()) {
            Song current = getCurrentSong();
            Collections.shuffle(playlist);
            currentIndex = playlist.indexOf(current);
        }
    }

    private void startProgressTracking() {
        stopProgressTracking();
        progressUpdater = new Timeline(new KeyFrame(Duration.seconds(0.1), e -> updateProgress()));
        progressUpdater.setCycleCount(Timeline.INDEFINITE);
        progressUpdater.play();
    }

    private void updateProgress() {
        if (mediaPlayer != null && onProgressChanged != null) {
            double durationSeconds = mediaPlayer.getMedia().getDuration().toSeconds();
            if (durationSeconds <= 0) return;
            double progress = mediaPlayer.getCurrentTime().toSeconds() / durationSeconds;
            Platform.runLater(() -> onProgressChanged.accept(progress));
        }
    }

    private void stopProgressTracking() {
        if (progressUpdater != null) progressUpdater.stop();
    }

    public Song getCurrentSong() {
        return (currentIndex >= 0 && currentIndex < playlist.size()) ? playlist.get(currentIndex) : null;
    }

    public String getCurrentPlaylistName() {
        return currentPlaylist != null ? currentPlaylist.getName() : "Desconocido";
    }

    public void setPlaylist(Playlist playlist) {
        this.currentPlaylist = playlist;
        this.playlist = new ArrayList<>(playlist.getSongs());
        this.currentIndex = -1;
        if (shuffle.get()) Collections.shuffle(this.playlist);
    }

    // Setters para listeners
    public void setOnProgressChanged(Consumer<Double> listener) { this.onProgressChanged = listener; }
    public void setOnSongChanged(Consumer<Song> listener) { this.onSongChanged = listener; }
    public void setOnSongFinished(Runnable listener) { this.onSongFinished = listener; }
    public void setOnPlayingStatusChanged(Consumer<Boolean> listener) {
        playing.addListener((obs, oldVal, newVal) -> listener.accept(newVal));
    }
    public void setOnDurationChanged(Consumer<Double> listener) { this.onDurationChanged = listener; }

    // ======================== SEEK ========================
    public void seek(double position) {
        if (mediaPlayer != null && position >= 0 && position <= 1) {
            mediaPlayer.seek(javafx.util.Duration.seconds(
                    position * mediaPlayer.getMedia().getDuration().toSeconds()
            ));
        }
    }

    // Getters y setters
    public double getVolume() { return mediaPlayer != null ? mediaPlayer.getVolume() : lastVolume; }
    public void setVolume(double volume) {
        lastVolume = volume;
        if (mediaPlayer != null) mediaPlayer.setVolume(volume);
    }
    public boolean isPlaying() { return playing.get(); }
    public boolean isShuffle() { return shuffle.get(); }
}
