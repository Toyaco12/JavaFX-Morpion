package com.project.morpion.model;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class AudioPlayer {
    private MediaPlayer mediaPlayer;

    // Joue la musique de victoire. Charge le fichier audio depuis les ressources et le joue via un MediaPlayer.

    public void playVictoryMusic() {
        String musicFile = "/com/project/morpion/music/victorySound.mp3"; // Chemin relatif
        try {
            Media sound = new Media(getClass().getResource(musicFile).toURI().toString());
            mediaPlayer = new MediaPlayer(sound);
            //mediaPlayer.setVolume(1);
            mediaPlayer.play();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Resource not found: " + musicFile);
        }
    }

    // Joue la musique de défaite. Charge le fichier audio approprié et le joue, similaire à la méthode pour la musique de victoire.

    public void playDefeatMusic() {
        String musicFile = "/com/project/morpion/music/defeatSound.mp3"; // Chemin relatif
        try {
            Media sound = new Media(getClass().getResource(musicFile).toURI().toString());
            mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Resource not found: " + musicFile);
        }
    }

    // Joue la musique pour un match nul. Charge et joue le fichier audio spécifié pour les situations où le jeu se termine par une égalité.

    public void playDrawMusic() {
        String musicFile = "/com/project/morpion/music/drawSound.mp3"; // Chemin relatif
        try {
            Media sound = new Media(getClass().getResource(musicFile).toURI().toString());
            mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Resource not found: " + musicFile);
        }
    }

    // Joue une musique d'attente, utilisée lors des apprentissage de modèle.

    public void playWaitingMusic() {
        String musicFile = "/com/project/morpion/music/Elevator.mp3"; // Chemin relatif
        try {
            Media sound = new Media(getClass().getResource(musicFile).toURI().toString());
            mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Resource not found: " + musicFile);
        }
    }

    // Arrête toute musique en cours de lecture.

    public void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    // Met en pause la musique actuellement jouée.

    public void pauseMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    // Reprend la lecture de la musique précédemment mise en pause ou arrêtée.

    public void playMusic(){
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }

    // Ajuste le volume de la musique en cours de lecture. Le volume est une valeur entre 0 (silencieux) et 1 (volume maximum), ajustée ici pour être toujours une fraction de 0.2.

    public void changeVolume(double volume) {
        if (mediaPlayer != null) {
            volume = (volume /100)*0.2;
            mediaPlayer.setVolume(volume);
        }
    }
}

