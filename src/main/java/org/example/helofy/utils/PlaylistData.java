package org.example.helofy.utils;

import org.example.helofy.model.Playlist;

import java.util.ArrayList;
import java.util.List;

public class PlaylistData {
    private List<Playlist> playlists;

    public PlaylistData() {
        playlists = new ArrayList<>();  // Cambiado de colecciones inmutables a ArrayList
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(List<Playlist> playlists) {
        this.playlists = playlists;
    }
}
