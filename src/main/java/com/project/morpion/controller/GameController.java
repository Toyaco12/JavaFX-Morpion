package com.project.morpion.controller;

import javafx.animation.RotateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class GameController {

    public GridPane morpionGrille;
    public Label currentPlayer;

    private int[] placement = new int[9];
    private boolean turn = true;


    @FXML
    public void initialize() {
        setClickListener();
    }


    private void setClickListener(){
        morpionGrille.getChildren().forEach(node -> {
            StackPane stackPane = (StackPane) node;
            ImageView imageView = (ImageView) stackPane.getChildren().getFirst();

            imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    int position = GridPane.getRowIndex(stackPane) * morpionGrille.getColumnCount() + GridPane.getColumnIndex(stackPane);
                    placement[position] = turn ? 1 : -1;
                    Image i;
                    if(turn){
                        currentPlayer.setText("Current Player : Player 1");
                        currentPlayer.setVisible(true);
                        i = new Image("file:src/main/resources/com/project/morpion/ai/images/TicTacToe/cross.png");
                    }
                    else{
                        currentPlayer.setText("Current Player : Player 2");
                        currentPlayer.setVisible(true);
                        i = new Image("file:src/main/resources/com/project/morpion/ai/images/TicTacToe/circle.png");
                    }
                    imageView.setImage(i);
                    imageView.setOnMouseClicked(null);
                    turn = !turn;
                    int victory = victory();
                    if(victory != 0){
                        int []row = getVictory();
                        for(int a : row){
                            StackPane s = (StackPane) morpionGrille.getChildren().get(a);
                            ImageView imageView = (ImageView) s.getChildren().getFirst();
                            rotateImage(imageView);
                        }
                        if(victory == 1)
                            currentPlayer.setText("The Winner Is Player 1 !!!");
                        else currentPlayer.setText("The Winner Is Player 2 !!!");
                    }
                }
            });
        });
    }

    public void returnHome(ActionEvent actionEvent) {
    }

    public int victory(){
        for(int i = 0; i < 7; i+=3){
            if(placement[i] == placement[i+1] && placement[i] == placement[i+2]){
                return placement[i];
            }
        }
        for(int i = 0; i < 3; i++){
            if(placement[i] == placement[i+3] && placement[i] == placement[i+6]){
                return placement[i];
            }
        }
        if(placement[0] == placement[4] && placement[0] == placement[8]){
            return placement[0];
        }
        if(placement[2] == placement[4] && placement[2] == placement[6]){
            return placement[2];
        }
        return 0;
    }

    private int[] getVictory(){
        int [] rowVictory = new int [3];
        for(int i = 0; i < 7; i+=3){
            if(placement[i] == placement[i+1] && placement[i] == placement[i+2]){
                rowVictory = new int[]{i, i + 1, i + 2};
            }
        }
        for(int i = 0; i < 3; i++){
            if(placement[i] == placement[i+3] && placement[i] == placement[i+6]){
                rowVictory = new int[]{i, i + 3, i + 6};
            }
        }
        if(placement[0] == placement[4] && placement[0] == placement[8]){
            rowVictory = new int[]{0, 4, 8};
        }
        if(placement[2] == placement[4] && placement[2] == placement[6]){
            rowVictory = new int[]{2, 4, 6};
        }
        return rowVictory;
    }

    private void rotateImage(ImageView i){
        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(2), i);
        rotateTransition.setByAngle(360);
        rotateTransition.setCycleCount(RotateTransition.INDEFINITE);
        rotateTransition.setAutoReverse(false);
        rotateTransition.play();
    }


}
