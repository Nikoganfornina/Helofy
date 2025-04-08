package org.example.helofy.utils;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.example.helofy.model.Song;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MusicPlayer {
    private MediaPlayer mediaPlayer;
    private List<Song> playlist;
    private int currentIndex = -1;
    private boolean isShuffle = false;
    private boolean isRepeat = false;

    // Métodos nuevos añadidos
    public void setVolume(double volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume);
        }
    }

    public Song getCurrentSong() {
        if (currentIndex >= 0 && currentIndex < playlist.size()) {
            return playlist.get(currentIndex);
        }
        return null;
    }

    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public void resume() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }

    public void previousSong() {
        if (currentIndex > 0) {
            playSong(currentIndex - 1);
        }
    }

    public void nextSong() {
        if (currentIndex < playlist.size() - 1) {
            playSong(currentIndex + 1);
        } else if (isRepeat) {
            playSong(0);
        }
    }

    // Métodos existentes con mejoras
    public void playPlaylist(List<Song> playlist) {
        this.playlist = new ArrayList<>(playlist);
        if (isShuffle) {
            Collections.shuffle(this.playlist);
        }
        playSong(0);
    }

    public void playSong(int index) {
        if (playlist == null || index < 0 || index >= playlist.size()) return;

        try {
            stop();
            currentIndex = index;
            Song song = playlist.get(index);

            Media media = new Media(new File(song.getFilePath()).toURI().toString());
            mediaPlayer = new MediaPlayer(media);

            mediaPlayer.setOnReady(() -> {
                mediaPlayer.play();
                if (onSongChanged != null) onSongChanged.run();
            });

            mediaPlayer.setOnEndOfMedia(this::handleSongEnd);

        } catch (Exception e) {
            System.err.println("Error loading song: " + e.getMessage());
        }
    }

    private void handleSongEnd() {
        if (isRepeat) {
            playSong(currentIndex);
        } else {
            nextSong();
        }
    }

    public void toggleShuffle() {
        isShuffle = !isShuffle;
        if (isShuffle && playlist != null) {
            Collections.shuffle(playlist);
        }
    }

    public void toggleRepeat() {
        isRepeat = !isRepeat;
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
    }

    private Runnable onSongChanged;
    public void setOnSongChanged(Runnable callback) {
        this.onSongChanged = callback;
    }
}