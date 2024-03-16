package com.project.morpion.model;

import com.project.morpion.model.ai.Config;
import com.project.morpion.model.ai.ConfigFileLoader;
import com.project.morpion.model.ai.MultiLayerPerceptron;

public class Morpion {
    private MultiLayerPerceptron model;
    private int[] board;
    private boolean isGameStarted = false;

    public Morpion(){
        this.model = null;
        this.board = new int[9];
    }
    public Morpion(MultiLayerPerceptron model){

        this.model = model;
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
