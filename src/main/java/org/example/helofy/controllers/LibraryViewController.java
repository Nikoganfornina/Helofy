package org.example.helofy.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import org.example.helofy.model.Playlist;
import org.example.helofy.utils.PlaylistLoader;
import java.io.File;
import java.io.IOException;

public class LibraryViewController {

    @FXML private TilePane tilePane;

    private HelofyMainController mainController;

    // Esta nueva función debe ser llamada cuando se cargue el controlador principal.
    public void setMainController(HelofyMainController controller) {
        this.mainController = controller;
    }

    @FXML
    public void initialize() {
        cargarPlaylists();
    }

    private void cargarPlaylists() {
        File directorio = new File(System.getProperty("user.dir")).getParentFile();
        directorio = new File(directorio, "HeloPlayList");

        for (Playlist playlist : PlaylistLoader.loadPlaylists(directorio.getAbsolutePath())) {
            try {
                Node tarjeta = crearTarjetaPlaylist(playlist);
                tilePane.getChildren().add(tarjeta);
            } catch (IOException e) {
                System.err.println("Error creando tarjeta: " + e.getMessage());
            }
        }
    }

    private Node crearTarjetaPlaylist(Playlist playlist) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/helofy/views/PlaylistCard.fxml"));
        Node tarjeta = loader.load();

        PlaylistCardController controller = loader.getController();
        controller.configurar(playlist, () -> {
            if (mainController != null) {
                // Cambié 'cargarPlaylist' por 'loadPlaylistView'
                mainController.loadPlaylistView(playlist);
            } else {
                System.err.println("Error: mainController no está inicializado.");
            }
        });

        return tarjeta;
    }
}
