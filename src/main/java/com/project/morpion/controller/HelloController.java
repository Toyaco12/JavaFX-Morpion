package com.project.morpion.controller;

import com.project.morpion.HelloApplication;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }


    public Pane panel;
    public Button toNext;
    @FXML
    private VBox vbox1;

    @FXML
    private Label welcomeText;

    @FXML
    public void initialize() {
        // Création de la balle
        Circle ball = new Circle(10, Color.BLUE);
        ball.relocate(100, 100);

        panel.getChildren().add(ball);

        // Animation
        AnimationTimer timer = new AnimationTimer() {
            double dx = 1;
            double dy = 1;

            @Override
            public void handle(long now) {
                if (ball.getLayoutX() <= 0 || ball.getLayoutX() >= panel.getWidth() - ball.getRadius() * 2) {
                    dx *= -1;
                }
                if (ball.getLayoutY() <= 0 || ball.getLayoutY() >= panel.getHeight() - ball.getRadius() * 2) {
                    dy *= -1;
                }

                ball.setLayoutX(ball.getLayoutX() + dx);
                ball.setLayoutY(ball.getLayoutY() + dy);
            }
        };
        timer.start();
    }

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    public void goToNextStage(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("view/crazy-size.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        CrazySize controller = fxmlLoader.getController();
        controller.setStage(stage);
        stage.setScene(scene);
    }
}