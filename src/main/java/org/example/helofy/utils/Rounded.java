package org.example.helofy.utils;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

public class Rounded {

    /**
     * Aplica un recorte redondeado al ImageView si ya tiene una imagen y dimensiones definidas.
     */
    public static void applyRoundedClip(ImageView imageView, double borderRadius) {
        Image img = imageView.getImage();
        if (img != null) {
            double width = imageView.getFitWidth() > 0 ? imageView.getFitWidth() : img.getWidth();
            double height = imageView.getFitHeight() > 0 ? imageView.getFitHeight() : img.getHeight();

            Rectangle clip = new Rectangle(width, height);
            clip.setArcWidth(borderRadius * 2);
            clip.setArcHeight(borderRadius * 2);
            imageView.setClip(clip);
        }
    }

    /**
     * Aplica el recorte redondeado cuando la imagen se cargue de forma asíncrona o diferida.
     * Útil para evitar problemas cuando el ImageView aún no tiene dimensiones o imagen.
     */
    public static void applyHeaderImageRoundness(ImageView imageView, double borderRadius) {
        imageView.imageProperty().addListener((obs, oldImg, newImg) -> {
            if (newImg != null) {
                double width = imageView.getFitWidth() > 0 ? imageView.getFitWidth() : newImg.getWidth();
                double height = imageView.getFitHeight() > 0 ? imageView.getFitHeight() : newImg.getHeight();

                Rectangle clip = new Rectangle(width, height);
                clip.setArcWidth(borderRadius * 2);
                clip.setArcHeight(borderRadius * 2);
                imageView.setClip(clip);
            }
        });
    }
}
