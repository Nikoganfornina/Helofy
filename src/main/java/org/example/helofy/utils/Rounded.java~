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
}
