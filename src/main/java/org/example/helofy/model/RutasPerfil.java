package org.example.helofy.model;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RutasPerfil {

    public static final Path JSON_PERFIL = Paths.get("usuarioJson", "perfil.json");
    public static final Path JSON_PLAYLIST = Paths.get("usuarioJson", "playlists.json");
    public static final Path JSON_HISTORIAL = Paths.get("usuarioJson", "historial.json");

    private static final ObjectMapper mapper = new ObjectMapper();

    public static Perfil cargarPerfil() {
        try {
            return mapper.readValue(JSON_PERFIL.toFile(), Perfil.class);
        } catch (IOException e) {
            e.printStackTrace();
            return new Perfil(); // Devuelve uno vacío si hay error
        }
    }

    public static void guardarPerfil(Perfil perfil) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(JSON_PERFIL.toFile(), perfil);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
