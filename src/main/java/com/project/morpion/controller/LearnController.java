package com.project.morpion.controller;

import com.project.morpion.model.ai.Test;
import javafx.beans.property.DoubleProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

public class LearnController {

    @FXML
    public TextField errorfield;
    @FXML
    public ProgressBar progbar;
    @FXML
    public Button startbutton;

    @FXML
    public void processStart(ActionEvent actionEvent) {
        Task<Void> learning = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                String difficulty = "F";
                Test.setupAndLearn(difficulty, (DoubleProperty) progressProperty());
                return null;
            }
        };
        progbar.progressProperty().bind(learning.progressProperty());

        Thread learningThread = new Thread(learning);
        learningThread.setDaemon(true);
        learningThread.start();
    }
}
