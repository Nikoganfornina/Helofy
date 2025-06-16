package org.example.helofy.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.List;

public class CreatePlaylistController {

    @FXML
    private ImageView playlistImage;

    @FXML
    private TextField playlistNameField;

    @FXML
    private TextField playlistDescriptionArea;

    @FXML
    private ListView<String> songListView;

    @FXML
    private Button changeImageButton;

    @FXML
    private Button createPlaylistButton;

    private FileChooser fileChooser = new FileChooser();
    private File selectedImage;

    @FXML
    public void initialize() {
        ImagenPredeterminada();

        // Para que el ListView muestre solo el nombre del archivo
        songListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty || item == null ? null : Paths.get(item).getFileName().toString());
                    }
                };
            }
        });
    }

    private void aplicarClipConBorde(ImageView imageView, double width, double height, double arc) {
        Rectangle clip = new Rectangle(width, height);
        clip.setArcWidth(arc);
        clip.setArcHeight(arc);
        imageView.setClip(clip);
    }

    public void ImagenPredeterminada() {
        playlistImage.setImage(new Image(getClass().getResource("/org/example/helofy/styles/defaultImage.png").toExternalForm()));
        playlistImage.setFitWidth(200);
        playlistImage.setFitHeight(200);
        playlistImage.setPreserveRatio(true);
        aplicarClipConBorde(playlistImage, 200, 200, 30);
    }

    @FXML
    public void cambiarImagen() {
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png"));
        selectedImage = fileChooser.showOpenDialog(new Stage());

        if (selectedImage != null) {
            String filePath = selectedImage.toURI().toString();
            playlistImage.setImage(new Image(filePath));
            playlistImage.setFitWidth(200);
            playlistImage.setFitHeight(200);
            playlistImage.setPreserveRatio(true);
            aplicarClipConBorde(playlistImage, 200, 200, 30);
        } else {
            ImagenPredeterminada();
        }
    }


    @FXML
    public void añadirCanciones() {
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3 Files", "*.mp3"));
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(new Stage());

        if (selectedFiles != null) {
            for (File file : selectedFiles) {
                songListView.getItems().add(file.getAbsolutePath());
            }
        }
    }

    @FXML
    public void crearPlaylist() {
        String playlistName = playlistNameField.getText().trim();
        String descripcion = playlistDescriptionArea.getText().trim();

        if (playlistName.isEmpty()) {
            showError("El nombre de la playlist no puede estar vacío.");
            return;
        }

        try {
            Path basePath = Paths.get("D:/musicapp/HeloPlayList");
            Path playlistFolder = basePath.resolve(playlistName);
            Path songsFolder = playlistFolder.resolve("songs");

            Files.createDirectories(songsFolder);

            // Guardar imagen
            Path imagePath = playlistFolder.resolve("portada.jpg");
            if (selectedImage != null) {
                Files.copy(selectedImage.toPath(), imagePath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                try (InputStream defaultImageStream = getClass().getResourceAsStream("/org/example/helofy/styles/defaultImage.png")) {
                    if (defaultImageStream != null) {
                        Files.copy(defaultImageStream, imagePath, StandardCopyOption.REPLACE_EXISTING);
                    } else {
                        System.out.println("No se encontró la imagen por defecto.");
                    }
                }
            }

            // Copiar canciones
            for (String songPath : songListView.getItems()) {
                File songFile = new File(songPath);
                if (songFile.exists()) {
                    Files.copy(songFile.toPath(), songsFolder.resolve(songFile.getName()), StandardCopyOption.REPLACE_EXISTING);
                }
            }

            // Reset campos
            playlistNameField.clear();
            playlistDescriptionArea.clear();
            songListView.getItems().clear();
            ImagenPredeterminada();
            selectedImage = null;

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Playlist creada");
            alert.setHeaderText(null);
            alert.setContentText("La playlist se ha creado correctamente.");
            alert.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Error al crear la playlist: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
