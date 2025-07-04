package org.example.helofy.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class EditPlaylistController {

    @FXML
    private ComboBox<String> playlistSelector;
    @FXML
    private TextField playlistNameField;
    @FXML
    private TextField playlistDescriptionField;
    @FXML
    private ImageView playlistImage;
    @FXML
    private ListView<String> songListView;
    @FXML
    private Button addSongsButton, removeSongButton, changeImageButton, saveChangesButton, deletePlaylistButton;
    @FXML
    private GridPane mainLayout;

    // ✅ Ruta corregida según tu estructura real
    private final Path basePath = Paths.get("HeloPlayList");

    private final FileChooser fileChooser = new FileChooser();
    private Path currentPlaylistPath;
    private File selectedImage;

    @FXML
    public void initialize() {
        loadPlaylists();
        applyRoundedImageClip();

        playlistSelector.setOnAction(e -> loadSelectedPlaylist());


        playlistImage.setImage(new Image(getClass().getResource("/org/example/helofy/styles/defaultImage.png").toExternalForm()));


        addSongsButton.setOnAction(e -> addSongs());
        removeSongButton.setOnAction(e -> removeSelectedSong());
        changeImageButton.setOnAction(e -> changeImage());
        saveChangesButton.setOnAction(e -> savePlaylistChanges());
        deletePlaylistButton.setOnAction(e -> deleteSelectedPlaylist());
    }

    private void loadPlaylists() {
        try {
            playlistSelector.getItems().clear();
            if (!Files.exists(basePath)) {
                Files.createDirectories(basePath);
            }
            Files.list(basePath)
                    .filter(Files::isDirectory)
                    .forEach(p -> playlistSelector.getItems().add(p.getFileName().toString()));
        } catch (IOException e) {
            showError("Error al cargar playlists: " + e.getMessage());
        }
    }

    private void loadSelectedPlaylist() {
        String name = playlistSelector.getValue();
        if (name == null) return;

        currentPlaylistPath = basePath.resolve(name);
        playlistNameField.setText(name);

        // Descripción
        Path descPath = currentPlaylistPath.resolve("descripcion.txt");
        try {
            playlistDescriptionField.setText(Files.exists(descPath) ? Files.readString(descPath) : "");
        } catch (IOException e) {
            playlistDescriptionField.setText("");
        }

        // Imagen
        Path imagePath = currentPlaylistPath.resolve("portada.jpg");
        if (Files.exists(imagePath)) {
            playlistImage.setImage(new Image(imagePath.toUri().toString()));
        } else {
            playlistImage.setImage(new Image(getClass().getResource("/org/example/helofy/styles/defaultImage.png").toExternalForm()));
        }

        // Canciones
        Path songsFolder = currentPlaylistPath.resolve("songs");
        songListView.getItems().clear();
        if (Files.exists(songsFolder)) {
            try {
                Files.list(songsFolder)
                        .filter(f -> f.toString().toLowerCase().endsWith(".mp3"))
                        .forEach(f -> songListView.getItems().add(f.getFileName().toString()));
            } catch (IOException e) {
                showError("Error al cargar canciones.");
            }
        }
    }

    private void applyRoundedImageClip() {
        Rectangle clip = new Rectangle(150, 150);
        clip.setArcWidth(30);
        clip.setArcHeight(30);
        playlistImage.setClip(clip);
    }

    private void addSongs() {
        fileChooser.getExtensionFilters().setAll(new FileChooser.ExtensionFilter("Archivos MP3", "*.mp3"));
        List<File> songs = fileChooser.showOpenMultipleDialog(new Stage());
        if (songs == null || currentPlaylistPath == null) return;

        Path songsFolder = currentPlaylistPath.resolve("songs");
        try {
            Files.createDirectories(songsFolder);
            for (File song : songs) {
                Files.copy(song.toPath(), songsFolder.resolve(song.getName()), StandardCopyOption.REPLACE_EXISTING);
            }
            loadSelectedPlaylist(); // Recargar vista
        } catch (IOException e) {
            showError("Error al añadir canciones: " + e.getMessage());
        }
    }

    private void removeSelectedSong() {
        String selected = songListView.getSelectionModel().getSelectedItem();
        if (selected == null || currentPlaylistPath == null) return;

        try {
            Files.deleteIfExists(currentPlaylistPath.resolve("songs").resolve(selected));
            loadSelectedPlaylist();
        } catch (IOException e) {
            showError("No se pudo eliminar la canción.");
        }
    }

    private void changeImage() {
        fileChooser.getExtensionFilters().setAll(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
        selectedImage = fileChooser.showOpenDialog(new Stage());
        if (selectedImage != null) {
            playlistImage.setImage(new Image(selectedImage.toURI().toString()));
        }
    }

    private void savePlaylistChanges() {
        if (currentPlaylistPath == null) return;

        String newName = playlistNameField.getText().trim();
        String newDesc = playlistDescriptionField.getText().trim();

        // Guardar descripción
        try {
            Files.writeString(currentPlaylistPath.resolve("descripcion.txt"), newDesc, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            showError("No se pudo guardar la descripción.");
        }

        // Guardar imagen
        if (selectedImage != null) {
            try {
                Files.copy(selectedImage.toPath(), currentPlaylistPath.resolve("portada.jpg"), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                showError("No se pudo guardar la imagen.");
            }
        }

        // Cambiar nombre de carpeta si cambió
        if (!newName.equals(currentPlaylistPath.getFileName().toString())) {
            try {
                Path newPath = basePath.resolve(newName);
                Files.move(currentPlaylistPath, newPath, StandardCopyOption.REPLACE_EXISTING);
                currentPlaylistPath = newPath;
                loadPlaylists();
                playlistSelector.setValue(newName);
            } catch (IOException e) {
                showError("No se pudo renombrar la playlist.");
            }
        }

        new Alert(Alert.AlertType.INFORMATION, "Cambios guardados correctamente.").showAndWait();
    }

    private void deleteSelectedPlaylist() {
        if (currentPlaylistPath == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Seguro que deseas eliminar esta playlist?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    Files.walk(currentPlaylistPath)
                            .sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(File::delete);
                    loadPlaylists();
                    playlistNameField.clear();
                    playlistDescriptionField.clear();
                    songListView.getItems().clear();
                    playlistImage.setImage(null);
                    currentPlaylistPath = null;
                } catch (IOException e) {
                    showError("Error al eliminar la playlist.");
                }
            }
        });
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }
}
