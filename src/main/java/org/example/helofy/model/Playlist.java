package org.example.helofy.model;

import java.util.List;

public class Playlist {
    private String name;
    private String description;
    private String coverPath;
    private List<Song> songs;

    // Constructor corregido (4 parámetros)
    public Playlist(String name, String description, String coverPath, List<Song> songs) {
        this.name = name;
        this.description = description;
        this.coverPath = coverPath;
        this.songs = songs;
    }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCoverPath() { return coverPath; }
    public List<Song> getSongs() { return songs; }
}