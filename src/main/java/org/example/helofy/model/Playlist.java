package org.example.helofy.model;

import java.util.List;

public class Playlist {
    private String name;
    private boolean favorite;
    private String description ;
    private String coverPath;
    private List<Song> songs;
    private Boolean isSuperPlaylist ;

    // Constructor actualizado
    public Playlist(String name, boolean favorite, String description, String coverPath, List<Song> songs) {
        this.name = name;
        this.favorite = favorite;
        this.description = description;
        this.coverPath = coverPath;
        this.songs = songs;
        this.isSuperPlaylist = false;
    }

    public Playlist(String name, boolean favorite, String description, List<Song> songs) {
        this.name = name;
        this.favorite = favorite;
        this.description = description;
        this.songs = songs;
        this.isSuperPlaylist = false;
    }


    // Getters
    public String getName() {
        return name;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public String getDescription() {
        return description;
    }

    public String getCoverPath() {
        return coverPath;
    } // Getter añadido

    public List<Song> getSongs() {
        return songs;
    }

    public Boolean getIsSuperPlaylist() {
        return isSuperPlaylist;
    }

    public void setIsSuperPlaylist(Boolean isSuperPlaylist) {
        this.isSuperPlaylist = isSuperPlaylist;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }



}