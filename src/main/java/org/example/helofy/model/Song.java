package org.example.helofy.model;

public class Song {
    private String title;
    private String filePath;
    private String coverPath; // Campo corregido
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
}