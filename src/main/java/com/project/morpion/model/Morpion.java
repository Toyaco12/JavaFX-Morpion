package com.project.morpion.model;

import com.project.morpion.model.ai.Config;
import com.project.morpion.model.ai.ConfigFileLoader;
import com.project.morpion.model.ai.Coup;
import com.project.morpion.model.ai.MultiLayerPerceptron;

public class Morpion {
    private MultiLayerPerceptron model;
    private Coup board;
    private boolean isGameStarted = false;

    public Morpion(){
        board = new Coup(9,"Morpion");
        this.model = null;

    }
    public Morpion(MultiLayerPerceptron model){
        board = new Coup(9,"Morpion");
        this.model = model;

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
