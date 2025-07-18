package org.example.helofy.model;

import java.util.Objects;

public class Song {
    private String title;
    private String filePath;
    private String coverPath;
    private double duration;

    public Song(String title, String filePath, String coverPath, double duration) {
        this.title = title;
        this.filePath = filePath;
        this.coverPath = coverPath;
        this.duration = duration;
    }

    // Getter añadido
    public String getCoverPath() {
        return coverPath;
    }

    // Resto de getters...
    public String getTitle() { return title; }
    public String getFilePath() { return filePath; }
    public double getDuration() { return duration; }
    public double setDuration(double duration) { return this.duration = duration; }
    @Override
    public String toString() {
        return title;  // Retorna el título de la canción
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return Objects.equals(filePath, song.filePath); // Compara por ruta del archivo
    }

    @Override
    public int hashCode() {
        return Objects.hash(filePath);
    }


}