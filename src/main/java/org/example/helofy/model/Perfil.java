package org.example.helofy.model;

import java.util.List;

public class Perfil {
        private String nombreUsuario;
        private int totalPlaylists;
        private int totalCanciones;
        private long tiempoMusicaSegundos;  // Acumulado total escuchado
        private int reproduccionesTotales;
        private List<String> playlistsFavoritas;
        private String ultimaPlaylistEscuchada;
        private long fechaCreacion;  // timestamp milis
        private double volumenPreferido;

        public Perfil() {
            this.fechaCreacion = System.currentTimeMillis();
            this.volumenPreferido = 1.0; // volumen por defecto máximo
        }

        // Getters y setters
        public String getNombreUsuario() { return nombreUsuario; }
        public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

        public int getTotalPlaylists() { return totalPlaylists; }
        public void setTotalPlaylists(int totalPlaylists) { this.totalPlaylists = totalPlaylists; }

        public int getTotalCanciones() { return totalCanciones; }
        public void setTotalCanciones(int totalCanciones) { this.totalCanciones = totalCanciones; }

        public long getTiempoMusicaSegundos() { return tiempoMusicaSegundos; }
        public void setTiempoMusicaSegundos(long tiempoMusicaSegundos) { this.tiempoMusicaSegundos = tiempoMusicaSegundos; }

        public int getReproduccionesTotales() { return reproduccionesTotales; }
        public void setReproduccionesTotales(int reproduccionesTotales) { this.reproduccionesTotales = reproduccionesTotales; }

        public List<String> getPlaylistsFavoritas() { return playlistsFavoritas; }
        public void setPlaylistsFavoritas(List<String> playlistsFavoritas) { this.playlistsFavoritas = playlistsFavoritas; }

        public String getUltimaPlaylistEscuchada() { return ultimaPlaylistEscuchada; }
        public void setUltimaPlaylistEscuchada(String ultimaPlaylistEscuchada) { this.ultimaPlaylistEscuchada = ultimaPlaylistEscuchada; }

        public long getFechaCreacion() { return fechaCreacion; }
        public void setFechaCreacion(long fechaCreacion) { this.fechaCreacion = fechaCreacion; }

        public double getVolumenPreferido() { return volumenPreferido; }
        public void setVolumenPreferido(double volumenPreferido) { this.volumenPreferido = volumenPreferido; }
    }


