package org.example.helofy.utils;

import javafx.scene.image.Image;

public class ImageLoader {
    private static final String LOGO_PATH = "/org/example/helofy/styles/logoapp.png";
    private static final String LOGO_PATH2 = "/org/example/helofy/styles/logoapp2.png";


    public static Image loadAppLogo() {
        return new Image(ImageLoader.class.getResourceAsStream(LOGO_PATH));
    }
    public static Image loadAppLogo2() {
        return new Image(ImageLoader.class.getResourceAsStream(LOGO_PATH2));
    }

    public static String getLogoPath() {
        return LOGO_PATH;
    }
}