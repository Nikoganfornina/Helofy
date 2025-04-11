package org.example.helofy.utils;

import org.example.helofy.model.Playlist;
import org.example.helofy.model.Song;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PlaylistLoader {
    public static List<Playlist> loadPlaylists(String baseDirectory) {
        List<Playlist> playlists = new ArrayList<>();
        File root = new File(baseDirectory);

        if (root.exists() && root.isDirectory()) {
            for (File playlistDir : root.listFiles(File::isDirectory)) {
                String name = playlistDir.getName();
                String coverPath = new File(playlistDir, "portada.jpg").getAbsolutePath();
                List<Song> songs = loadSongs(playlistDir);

                playlists.add(new Playlist(
                        name,
                        false,
                        "Playlist automática",
                        coverPath, // Pasamos la ruta de la portada
                        songs
                ));
            }
        }
        return playlists;
    }

    private static List<Song> loadSongs(File playlistDir) {
        List<Song> songs = new ArrayList<>();
        File songsDir = new File(playlistDir, "songs");

        if (songsDir.exists() && songsDir.isDirectory()) {
            for (File songFile : songsDir.listFiles((dir, name) -> name.endsWith(".mp3"))) {
                String title = cleanTitle(songFile.getName());
                songs.add(new Song(
                        title,
                        songFile.getAbsolutePath(),
                        new File(playlistDir, "portada.jpg").getAbsolutePath(),
                        0 // Duración puede calcularse después
                ));
            }
        }
        return songs;
    }

    private static String cleanTitle(String filename) {
        return filename.replace(".mp3", "")
                .replaceAll("^\\d+[\\.\\-]\\s*", "")
                .replace("_", " ")
                .trim();
    }
}