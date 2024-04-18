package com.project.morpion;

import com.project.morpion.controller.MainController;
import com.project.morpion.model.AudioPlayer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {


    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("view/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        MainController controller = fxmlLoader.getController();
        controller.setScene(scene);
        controller.setStage(stage);
        controller.initialization();
        stage.setTitle("Morpion project");
        stage.getIcons().add(new Image("file:src/main/resources/com/project/morpion/image/morpionlogo.png"));
        stage.setScene(scene);
        stage.setResizable(false);
        //AudioPlayer audioPlayer = new AudioPlayer();
        //audioPlayer.playMusic();
        //stage.setOnCloseRequest(event -> audioPlayer.stopMusic());
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}