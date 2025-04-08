package org.example.helofy.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.helofy.model.Playlist;
import org.example.helofy.model.Song;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PlaylistLoader {

    public static List<Playlist> loadPlaylists(String basePath) {
        List<Playlist> playlists = new ArrayList<>();
        File playlistsDir = new File(basePath);

        if (playlistsDir.exists() && playlistsDir.isDirectory()) {
            for (File playlistDir : playlistsDir.listFiles(File::isDirectory)) {
                File configFile = new File(playlistDir, "config.json");
                if (configFile.exists()) {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        PlaylistConfig config = mapper.readValue(configFile, PlaylistConfig.class);

                        List<Song> songs = new ArrayList<>();
                        for (String songPath : config.playlist.songs) {
                            File songFile = Path.of(playlistDir.getAbsolutePath(), songPath).toFile();
                            songs.add(new Song(
                                    cleanTitle(songFile.getName()),
                                    songFile.getAbsolutePath(),
                                    Path.of(playlistDir.getAbsolutePath(), config.playlist.coverImage).toString(),
                                    0
                            ));
                        }

                        playlists.add(new Playlist(
                                config.playlist.name,
                                config.playlist.description,
                                Path.of(playlistDir.getAbsolutePath(), config.playlist.coverImage).toString(),
                                songs
                        ));

                    } catch (Exception e) {
                        System.err.println("Error cargando playlist: " + playlistDir.getName());
                    }
                }
            }
        }
        return playlists;
    }

    private static String cleanTitle(String filename) {
        return filename.replace(".mp3", "")
                .replaceAll("^\\d+\\s*-\\s*", "")  // Elimina números al inicio
                .replace("_", " ")
                .replaceAll("[^a-zA-Z0-9áéíóúÁÉÍÓÚüÜñÑ ]", "") // Caracteres especiales
                .trim();
    }

    private static class PlaylistConfig {
        public PlaylistData playlist;

        @JsonCreator
        public PlaylistConfig(@JsonProperty("playlist") PlaylistData playlist) {
            this.playlist = playlist;
        }
    }

    private static class PlaylistData {
        public String name;
        public String description;
        public String coverImage;
        public List<String> songs;

        @JsonCreator
        public PlaylistData(@JsonProperty("name") String name,
                            @JsonProperty("description") String description,
                            @JsonProperty("coverImage") String coverImage,
                            @JsonProperty("songs") List<String> songs) {
            this.name = name;
            this.description = description;
            this.coverImage = coverImage;
            this.songs = songs;
        }
    }
}