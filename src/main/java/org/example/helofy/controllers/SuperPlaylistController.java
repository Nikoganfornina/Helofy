package org.example.helofy.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import org.example.helofy.model.Playlist;
import org.example.helofy.utils.PlaylistLoader;

import java.io.File;
import java.io.IOException;

public class SuperPlaylistController {

    @FXML private AnchorPane superPlaylistRoot;
    @FXML private HBox playlistCardContainer;
    @FXML private Label slot1, slot2, slot3;

    @FXML
    public void initialize() {
        cargarPlaylists();
        configurarSlotDragDrop(slot1);
        configurarSlotDragDrop(slot2);
        configurarSlotDragDrop(slot3);
    }

    private void cargarPlaylists() {
        File raizProyecto = new File(System.getProperty("user.dir")).getParentFile();
        File directorio = new File(raizProyecto, "musicapp/HeloPlayList");

        if (!directorio.exists() || !directorio.isDirectory()) {
            System.err.println("No se encontró el directorio de playlists en: " + directorio.getAbsolutePath());
            return;
        }

        for (Playlist playlist : PlaylistLoader.loadPlaylists(directorio.getAbsolutePath())) {
            try {
                Node tarjeta = crearTarjetaPlaylist(playlist);
                aplicarDragEvents(tarjeta);
                playlistCardContainer.getChildren().add(tarjeta);
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
            System.out.println("Playlist seleccionada: " + playlist.getName());
        });

        return tarjeta;
    }

    private void aplicarDragEvents(Node tarjeta) {
        tarjeta.setOnDragDetected(event -> {
            Dragboard db = tarjeta.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString("playlist");
            db.setContent(content);
            tarjeta.setOpacity(0.5);
            event.consume();
        });

        tarjeta.setOnDragDone(event -> {
            tarjeta.setOpacity(1.0);
            event.consume();
        });
    }

    private void configurarSlotDragDrop(Label slot) {
        slot.setOnDragOver(event -> {
            if (event.getGestureSource() != slot && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        slot.setOnDragEntered(event -> {
            if (event.getGestureSource() != slot && event.getDragboard().hasString()) {
                slot.getStyleClass().add("playlist-slot-hover");
            }
        });

        slot.setOnDragExited(event -> {
            slot.getStyleClass().remove("playlist-slot-hover");
        });

        slot.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasString()) {
                Node tarjeta = (Node) event.getGestureSource();
                if (tarjeta.getParent() != null) {
                    ((HBox) tarjeta.getParent()).getChildren().remove(tarjeta);
                }
                slot.setGraphic(tarjeta);
                success = true;
            }

            event.setDropCompleted(success);
            event.consume();
        });
    }
}
