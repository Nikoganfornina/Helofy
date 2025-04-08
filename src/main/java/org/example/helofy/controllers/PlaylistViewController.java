package org.example.helofy.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.helofy.model.Playlist;
import org.example.helofy.model.Song;
import org.example.helofy.utils.MusicPlayer;

public class PlaylistViewController {

    @FXML private ImageView coverImage;
    @FXML private Label playlistName;
    @FXML private Label playlistDescription;
    @FXML private ListView<Song> songsListView;

    private MusicPlayer musicPlayer;

    public void initialize(Playlist playlist, MusicPlayer player) {
        this.musicPlayer = player;
        this.musicPlayer.playPlaylist(playlist.getSongs()); // Carga la playlist al abrir

        playlistName.setText(playlist.getName());
        playlistDescription.setText(playlist.getDescription());
        coverImage.setImage(new Image("file:" + playlist.getCoverPath()));

        songsListView.getItems().setAll(playlist.getSongs());
        songsListView.setCellFactory(lv -> new ListCell<Song>() {
            @Override
            protected void updateItem(Song song, boolean empty) {
                super.updateItem(song, empty);
                if (empty || song == null) {
                    setText(null);
                } else {
                    setText(song.getTitle());
                    setOnMouseClicked(e -> musicPlayer.playSong(getIndex()));
                }
            }
        });
    }
}