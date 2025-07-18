package org.example.helofy.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
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
    @FXML private Button createButton;

    private HelofyMainController mainController;

    @FXML
    public void initialize() {
        detectarMainController();
        cargarPlaylists();
        configurarSlotDragDrop(slot1);
        configurarSlotDragDrop(slot2);
        configurarSlotDragDrop(slot3);
        createButton.setVisible(false);
    }

    /** Detecta automáticamente el mainController desde el árbol de nodos */
    private void detectarMainController() {
        Node parent = superPlaylistRoot;
        while (parent != null) {
            parent = parent.getParent();
            if (parent instanceof Parent) {
                Object controller = ((Parent) parent).getProperties().get("controller");
                if (controller instanceof HelofyMainController) {
                    this.mainController = (HelofyMainController) controller;
                    System.out.println("✅ mainController detectado desde vista.");
                    break;
                }
            }
        }
    }

    public void setMainController(HelofyMainController controller) {
        this.mainController = controller;
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

        tarjeta.setUserData(playlist); // Esto es clave para poder extraer luego la playlist
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

            verificarSlots();
        });
    }

    private void verificarSlots() {
        boolean completos = slot1.getGraphic() != null &&
                slot2.getGraphic() != null &&
                slot3.getGraphic() != null;

        System.out.println("📦 Verificando slots: " + (completos ? "✅ COMPLETOS" : "⛔ INCOMPLETOS"));

        createButton.setVisible(completos);
    }

    @FXML
    private void crearSuperPlaylist() {
        Playlist p1 = extraerPlaylistDeSlot(slot1);
        Playlist p2 = extraerPlaylistDeSlot(slot2);
        Playlist p3 = extraerPlaylistDeSlot(slot3);

        if (p1 == null || p2 == null || p3 == null) {
            System.err.println("❌ Alguno de los slots no tiene playlist.");
            return;
        }

        String descripcion = crearDescripcionSuperPlaylist(p1, p2, p3);

        Playlist superPlaylist = new Playlist(
                "🎧 Super Playlist",
                true,
                descripcion,  // descripción antes de coverPath
                "/org/example/helofy/styles/superplaylist.png", // imagen del recurso
                p1.getSongs()
        );
        superPlaylist.getSongs().addAll(p2.getSongs());
        superPlaylist.getSongs().addAll(p3.getSongs());
        superPlaylist.setIsSuperPlaylist(true); // Marcamos que es superplaylist

        if (mainController != null) {
            mainController.loadPlaylistView(superPlaylist);
        } else {
            System.err.println("❌ mainController sigue sin estar inicializado.");
        }
    }

    private Playlist extraerPlaylistDeSlot(Label slot) {
        if (slot.getGraphic() != null && slot.getGraphic().getUserData() instanceof Playlist) {
            return (Playlist) slot.getGraphic().getUserData();
        }
        return null;
    }

    private String crearDescripcionSuperPlaylist(Playlist p1, Playlist p2, Playlist p3) {
        String nombre1 = p1.getName();
        String nombre2 = p2.getName();
        String nombre3 = p3.getName();

        return String.format(
                "Esta Super Playlist combina lo mejor de '%s', '%s' y '%s', fusionando estilos y emociones para una experiencia musical inigualable.",
                nombre1, nombre2, nombre3
        );
    }
}
