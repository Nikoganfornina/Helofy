package org.example.helofy.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.stage.Stage;
import org.example.helofy.model.Perfil;
import org.example.helofy.model.RutasPerfil;

import java.nio.file.Path;

public class UsuarioStorage {

    private static final Path RUTA_USUARIO = RutasPerfil.JSON_PERFIL;

    public static boolean existeUsuario() {
        return RUTA_USUARIO.toFile().exists();
    }

    public static Perfil cargarUsuario(Stage stageOwner) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(RUTA_USUARIO.toFile(), Perfil.class);
        } catch (Exception e) {
            CustomAlert.show(stageOwner, "No se pudo cargar el perfil", CustomAlert.AlertType.WARNING);
            System.out.println("Error cargando perfil: " + e.getMessage());
            return null;
        }
    }

    public static void guardarUsuario(Perfil usuario, Stage stageOwner) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(RUTA_USUARIO.toFile(), usuario);
        } catch (Exception e) {
            CustomAlert.show(stageOwner, "Error al guardar el perfil", CustomAlert.AlertType.ERROR);
            System.out.println("Error guardando perfil: " + e.getMessage());
        }
    }

    public static void sumarTiempoEscuchado(long segundos) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            Perfil perfil = mapper.readValue(RUTA_USUARIO.toFile(), Perfil.class);

            long tiempoActual = perfil.getTiempoMusicaSegundos();
            perfil.setTiempoMusicaSegundos(tiempoActual + segundos);

            mapper.writeValue(RUTA_USUARIO.toFile(), perfil);

        } catch (Exception e) {
            System.err.println("Error sumando tiempo escuchado: " + e.getMessage());
            // Opcional: aquí podrías mostrar alertas si tienes el Stage o contexto
        }
    }


}
