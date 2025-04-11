package org.example.helofy.utils;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.example.helofy.model.Playlist;
import org.example.helofy.model.Song;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class MusicPlayer {

    private Playlist currentPlaylist;  // Nuevo campo
    private MediaPlayer mediaPlayer;
    private List<Song> playlist = new ArrayList<>();
    private int currentIndex = -1;
    private final BooleanProperty shuffle = new SimpleBooleanProperty(false);
    private final BooleanProperty playing = new SimpleBooleanProperty(false);
    private Timeline progressUpdater;
    private Consumer<Double> onProgressChanged;
    private Consumer<Song> onSongChanged;
    private Runnable onSongFinished;
    private double lastVolume = 1.0;

    public void playSong(Song song) {
        int index = playlist.indexOf(song);
        if (index != -1) {
            currentIndex = index;
            internalPlaySong();
        }
    }

    private void internalPlaySong() {
        stop();

        try {
            Song song = playlist.get(currentIndex);
            File file = new File(song.getFilePath());

            Media media = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(media);

            mediaPlayer.setOnReady(() -> {
                mediaPlayer.play();
                playing.set(true);
                startProgressTracking();
                setVolume(lastVolume);
                if (onSongChanged != null) onSongChanged.accept(song);
            });

            mediaPlayer.setOnEndOfMedia(this::handleSongEnd);
        } catch (Exception e) {
            System.err.println("Error cargando canción: " + e.getMessage());
        }
    }

    public void playSong(int index) {
        if (index >= 0 && index < playlist.size()) {
            currentIndex = index;
            internalPlaySong();
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            playing.set(false);
            stopProgressTracking();
        }
    }

    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            playing.set(false);
        }
    }

    public void resume() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
            playing.set(true);
        }
    }

    public void previousSong() {
        if (shuffle.get()) {
            playRandomSong();
        } else if (currentIndex > 0) {
            playSong(currentIndex - 1);
        }
    }

    public void nextSong() {
        if (shuffle.get()) {
            playRandomSong();
        } else if (currentIndex < playlist.size() - 1) {
            playSong(currentIndex + 1);
        } else {
            playSong(0);
        }
    }

    private void playRandomSong() {
        if (!playlist.isEmpty()) {
            List<Integer> availableIndices = new ArrayList<>();
            for (int i = 0; i < playlist.size(); i++) {
                if (i != currentIndex) availableIndices.add(i);
            }
            if (!availableIndices.isEmpty()) {
                int newIndex = availableIndices.get((int) (Math.random() * availableIndices.size()));
                playSong(newIndex);
            }
        }
    }

    public void toggleShuffle() {
        shuffle.set(!shuffle.get());
        if (shuffle.get()) Collections.shuffle(playlist);
    }

    public Song getCurrentSong() {
        return (currentIndex >= 0 && currentIndex < playlist.size()) ?
                playlist.get(currentIndex) : null;
    }

    public boolean isPlaying() {
        return playing.get();
    }

    public void seek(double position) {
        if (mediaPlayer != null && position >= 0 && position <= 1) {
            mediaPlayer.seek(Duration.seconds(position * mediaPlayer.getMedia().getDuration().toSeconds()));
        }
    }

    public double getVolume() {
        return mediaPlayer != null ? mediaPlayer.getVolume() : lastVolume;
    }

    public void setVolume(double volume) {
        lastVolume = volume;
        if (mediaPlayer != null) mediaPlayer.setVolume(volume);
    }

    private void startProgressTracking() {
        stopProgressTracking();
        progressUpdater = new Timeline(
                new KeyFrame(Duration.seconds(0.1), e -> updateProgress())
        );
        progressUpdater.setCycleCount(Timeline.INDEFINITE);
        progressUpdater.play();
    }

    private void stopProgressTracking() {
        if (progressUpdater != null) progressUpdater.stop();
    }

    private void updateProgress() {
        if (mediaPlayer != null && onProgressChanged != null) {
            double progress = mediaPlayer.getCurrentTime().toSeconds() /
                    mediaPlayer.getMedia().getDuration().toSeconds();
            onProgressChanged.accept(progress);
        }
    }

    private void handleSongEnd() {
        if (onSongFinished != null) onSongFinished.run();
        nextSong();
    }

    // Setters para listeners
    public void setOnProgressChanged(Consumer<Double> listener) {
        this.onProgressChanged = listener;
    }

    public void setOnSongChanged(Consumer<Song> listener) {
        this.onSongChanged = listener;
    }

    public void setOnSongFinished(Runnable listener) {
        this.onSongFinished = listener;
    }

    public void setOnPlayingStatusChanged(Consumer<Boolean> listener) {
        playing.addListener((obs, oldVal, newVal) -> listener.accept(newVal));
    }

    public void setPlaylist(Playlist playlist) {
        this.currentPlaylist = playlist;
        this.playlist = playlist.getSongs();
        this.currentIndex = -1;
    }

    public String getCurrentPlaylistName() {
        return currentPlaylist != null ? currentPlaylist.getName() : "Desconocido";
    }

    public boolean isShuffle() {
        return shuffle.get();
    }

    public BooleanProperty shuffleProperty() {
        return shuffle;
    }

    public BooleanProperty playingProperty() {
        return playing;
    }
}