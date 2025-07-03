package org.example.helofy.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.example.helofy.model.Perfil;

public class PerfilUsuarioController {
    @FXML private StackPane fotoStack;
    @FXML private ImageView fotoUsuario;
    @FXML private Pane overlayFoto;
    @FXML private Label nombreUsuario, numPlaylists, tiempoMusica, playlistFavorita;


    private Perfil usuario;

    private HelofyMainController mainController;

    public void setMainController(HelofyMainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize() {

    }






}
