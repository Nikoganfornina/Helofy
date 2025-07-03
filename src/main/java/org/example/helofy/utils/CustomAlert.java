package org.example.helofy.utils;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

public class CustomAlert {

    public enum AlertType {
        SUCCESS, WARNING, ERROR
    }

    public static void show(Stage ownerStage, String message, AlertType type) {
        Platform.runLater(() -> {
            Popup popup = new Popup();

            Label label = new Label(message);
            label.setStyle("-fx-background-color: " + getColor(type) + ";" +
                    "-fx-text-fill: black;" +
                    "-fx-padding: 15px 25px;" +
                    "-fx-font-size: 14px;" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-radius: 10;" +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 3);");

            StackPane content = new StackPane(label);
            content.setOpacity(0);

            popup.getContent().add(content);
            popup.setAutoFix(true);
            popup.setAutoHide(true);
            popup.setHideOnEscape(true);

            popup.show(ownerStage);

            // Centrar en pantalla
            double centerX = ownerStage.getX() + ownerStage.getWidth() / 2 - 150;
            double centerY = ownerStage.getY() + ownerStage.getHeight() / 2;
            popup.setX(centerX);
            popup.setY(centerY);

            // Animación de entrada
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), content);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();

            // Animación de salida después de 3 segundos
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), content);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setDelay(Duration.seconds(3));
            fadeOut.setOnFinished(e -> popup.hide());
            fadeOut.play();
        });
    }

    private static String getColor(AlertType type) {
        return switch (type) {
            case SUCCESS -> "#4CAF50";   // Verde
            case WARNING -> "#FFC107";   // Amarillo
            case ERROR -> "#F44336";     // Rojo
        };
    }
}
