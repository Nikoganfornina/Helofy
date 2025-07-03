package org.example.helofy.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.helofy.model.RutasPerfil;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class FavoritosStorage {

    private static final Path PATH_FAVORITOS = RutasPerfil.JSON_PLAYLIST ;
    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<String> cargarFavoritos() {
        try {
            if (!Files.exists(PATH_FAVORITOS)) {
                guardarFavoritos(new ArrayList<>());
                return new ArrayList<>();
            }
            return mapper.readValue(PATH_FAVORITOS.toFile(), mapper.getTypeFactory().constructCollectionType(List.class, String.class));
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void guardarFavoritos(List<String> favoritas) {
        try {
            if (!Files.exists(PATH_FAVORITOS.getParent())) {
                Files.createDirectories(PATH_FAVORITOS.getParent());
            }
            mapper.writerWithDefaultPrettyPrinter().writeValue(PATH_FAVORITOS.toFile(), favoritas);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
