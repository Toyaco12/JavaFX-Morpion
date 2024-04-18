package com.project.morpion.model;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class AudioPlayer {
    private MediaPlayer mediaPlayer;

    public void playVictoryMusic() {
        String musicFile = "/com/project/morpion/music/victoryMusic.mp3"; // Chemin relatif
        try {
            Media sound = new Media(getClass().getResource(musicFile).toURI().toString());
            mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Resource not found: " + musicFile);
        }
    }
    public void playDefeatMusic() {
        String musicFile = "/com/project/morpion/music/victoryMusic.mp3"; // Chemin relatif
        try {
            Media sound = new Media(getClass().getResource(musicFile).toURI().toString());
            mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Resource not found: " + musicFile);
        }
    }
    public void playDrawMusic() {
        String musicFile = "/com/project/morpion/music/yoasobi-racing-into-the-night-iphone-ringtone-remix.mp3"; // Chemin relatif
        try {
            Media sound = new Media(getClass().getResource(musicFile).toURI().toString());
            mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Resource not found: " + musicFile);
        }
    }

    public void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }
    public void changeVolume(double volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume);
        }
    }
}

