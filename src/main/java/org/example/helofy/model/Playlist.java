package org.example.helofy.model;

import java.util.List;

public class Playlist {
    private String name;
    private boolean favorite;
    private String description;
    private String coverPath; // Nuevo campo
    private List<Song> songs;

    // Constructor actualizado
    public Playlist(String name, boolean favorite, String description, String coverPath, List<Song> songs) {
        this.name = name;
        this.favorite = favorite;
        this.description = description;
        this.coverPath = coverPath;
        this.songs = songs;
    }

    // Getters
    public String getName() { return name; }
    public boolean isFavorite() { return favorite; }
    public String getDescription() { return description; }
    public String getCoverPath() { return coverPath; } // Getter añadido
    public List<Song> getSongs() { return songs; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setFavorite(boolean favorite) { this.favorite = favorite; }
    public void setDescription(String description) { this.description = description; }
    public void setCoverPath(String coverPath) { this.coverPath = coverPath; }
    public void setSongs(List<Song> songs) { this.songs = songs; }
}