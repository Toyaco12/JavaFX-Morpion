package com.project.morpion;

import com.project.morpion.controller.CrazySize;
import com.project.morpion.controller.HelloController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {


    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("view/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        HelloController controller = fxmlLoader.getController();
        FXMLLoader fxmlLoader2 = new FXMLLoader(HelloApplication.class.getResource("view/crazy-size.fxml"));
        Scene scene2 = new Scene(fxmlLoader2.load());
        CrazySize controller2 = fxmlLoader2.getController();
        controller.setStage(stage);
        controller2.setStage(stage);
        stage.setTitle("Morpion project");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}