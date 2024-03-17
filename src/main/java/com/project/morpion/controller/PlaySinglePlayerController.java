package com.project.morpion.controller;

import com.project.morpion.model.Morpion;
import com.project.morpion.model.ai.Coup;
import com.project.morpion.model.ai.MultiLayerPerceptron;
import javafx.fxml.FXML;

public class PlaySinglePlayerController {
    private String modelName;
    private String difficulty;
    public void setModelName(String modelName){
        this.modelName = modelName;
    }
    public void setDifficulty(String difficulty){
        this.difficulty = difficulty;
    }
    public void initModel() {
        System.out.println(this.modelName);
        System.out.println(this.difficulty);
        MultiLayerPerceptron model = MultiLayerPerceptron.loadModel(difficulty, modelName);
        Morpion game = new Morpion(model, Coup.X);
        game.startGame();
    }
    @FXML
    public void initialize() {
    }
}
