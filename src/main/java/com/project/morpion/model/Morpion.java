package com.project.morpion.model;

import com.project.morpion.model.ai.Config;
import com.project.morpion.model.ai.ConfigFileLoader;
import com.project.morpion.model.ai.MultiLayerPerceptron;

public class Morpion {
    private MultiLayerPerceptron model;
    private String difficulty;
    private int[] board;
    private boolean isGameStarted = false;

    public Morpion(){
        this.model = null;
        this.difficulty = null;
        this.board = new int[9];
    }
    public Morpion(String difficulty){
        ConfigFileLoader cfl = new ConfigFileLoader();
        cfl.loadConfigFile("src/main/resources/com/project/morpion/ai/config.txt");
        Config config = cfl.get(difficulty);
        this.model = MultiLayerPerceptron.loadModel(difficulty,"model_"+config.hiddenLayerSize+"_"+config.learningRate+"_"+config.numberOfhiddenLayers+".srl");
        this.difficulty = difficulty;
        this.board = new int[9];
    }

    public boolean startGame(){
        if(this.isGameStarted){
            return false;
        }
        else{
            this.isGameStarted = true;
            return true;
        }
    }
    public void endGame(){
        this.isGameStarted = false;
    }
}
