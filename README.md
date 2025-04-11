# 🎧 HeloFy - Reproductor de Música Local en JavaFX

**HeloFy** es una aplicación de escritorio construida con JavaFX que funciona como reproductor de música local. Se ha diseñado desde cero con una interfaz visual atractiva, funcionalidades modernas y un sistema modular para facilitar la escalabilidad futura.

Actualmente se encuentra en fase de desarrollo, pero ya cuenta con un núcleo sólido de funcionalidades implementadas.

---

## 🚀 Características implementadas

### ✅ Reproductor funcional
- Reproducción de canciones locales: **Play, Pause, Stop, Next, Previous**.
- Control de volumen y mute.
- Gestión de errores: evita crasheos con listas vacías o canciones corruptas.
- Transición automática a la siguiente canción al finalizar la actual.
- Estilos visuales que indican la canción en reproducción.

### 🎨 Interfaz de usuario
- Vista de playlists en formato **card**, con redondeo y portadas.
- CSS personalizado completo: visual minimalista y profesional.
- Adaptación dinámica de las playlists según el contenido de la carpeta local.
- Lista de canciones con estilo que resalta la canción activa.

### 📂 Gestión de playlists
- Detección automática de playlists desde el sistema de archivos.
- Soporte visual para playlists sin portada (imagen por defecto).

---

## 🧩 Funcionalidades pendientes

Estas son las características en desarrollo o planificadas para versiones futuras:

- [ ] **Favoritos**: marcar canciones o playlists como favoritas.
- [ ] **CRUD de Playlists**: crear, editar, renombrar y eliminar playlists. Subir imagen personalizada y descripción.
- [ ] **Buscador inteligente**: filtrar canciones por nombre desde cualquier vista.
- [ ] **Superplaylist**: nueva vista para arrastrar y soltar múltiples playlists y reproducirlas como una sola.
- [ ] Mejoras en accesibilidad y responsividad visual.

---

## 🛠️ Tecnologías utilizadas

| Tecnología       | Uso                                      |
|------------------|-------------------------------------------|
| **Java 17+**      | Lógica principal y backend                |
| **JavaFX**        | Interfaz gráfica de usuario               |
| **FXML**          | Declaración visual de componentes         |
| **CSS**           | Estilizado de interfaz                    |
| **MediaPlayer**   | Reproducción de archivos de audio         |
| **Maven/Gradle**  | Gestión de dependencias (configurable)    |

---

## 🧠 Autor

Este proyecto está siendo desarrollado por [Niko](https://github.com/) con enfoque autodidacta, atención al detalle visual y pasión por los proyectos funcionales bien hechos.

---

## 📌 Notas

- Proyecto 100% local, no necesita conexión a Internet.
- Compatible con sistemas operativos que soporten JavaFX (Windows, Linux, macOS).
- En constante desarrollo, ¡cualquier mejora o sugerencia será bienvenida!
