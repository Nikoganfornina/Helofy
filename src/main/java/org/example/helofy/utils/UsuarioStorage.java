package org.example.helofy.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.helofy.model.Usuario;

import java.io.File;

public class UsuarioStorage {
    private static final String FILE_PATH = "usuario.json";

    public static boolean existeUsuario() {
        return new File(FILE_PATH).exists();
    }

    public static Usuario cargarUsuario() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(FILE_PATH), Usuario.class);
    }

    public static void guardarUsuario(Usuario usuario) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File(FILE_PATH), usuario);
    }
}
