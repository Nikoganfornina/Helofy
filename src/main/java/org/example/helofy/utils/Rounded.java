package org.example.helofy.utils;

import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

public class Rounded {
    private ImageView songImage;

    public Rounded(ImageView songImage) {
        this.songImage = songImage;
    }

    public void applyRoundedClip(double borderRadius) {
        if (songImage.getImage() != null) {
            // Verificar si la imagen tiene dimensiones válidas
            double width = songImage.getFitWidth();
            double height = songImage.getFitHeight();
            if (width > 0 && height > 0) {
                Rectangle clip = new Rectangle(width, height);
                clip.setArcWidth(borderRadius);
                clip.setArcHeight(borderRadius);
                songImage.setClip(clip);
            }
        }
    }
    public static void applyHeaderImageRoundness(ImageView headerImageView) {
        double borderRadius = 15.0; // Ajusta este valor según necesites

        headerImageView.imageProperty().addListener((obs, oldImg, newImg) -> {
            if (newImg != null) {
                // Usar las dimensiones del ImageView o de la imagen
                double width = headerImageView.getFitWidth() > 0 ?
                        headerImageView.getFitWidth() : newImg.getWidth();
                double height = headerImageView.getFitHeight() > 0 ?
                        headerImageView.getFitHeight() : newImg.getHeight();

                Rectangle clip = new Rectangle(width, height);
                clip.setArcWidth(borderRadius);
                clip.setArcHeight(borderRadius);
                headerImageView.setClip(clip);
            }
        });
    }
}
