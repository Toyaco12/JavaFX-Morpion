package com.project.morpion.controller;

import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.Random;

public class GameController {

    public GridPane morpionGrille;
    public Label currentPlayer;
    public Button buttonChoice1;
    public Button buttonChoice2;
    public ImageView player1Object;
    public ImageView player2Object;
    public VBox vboxChoice;
    public Button startPlayer1;
    public Button startPlayer2;
    public Button startRandom;
    public HBox hboxStart;
    public Label startLabel;
    public Label whosTurn;
    public StackPane overlayPane;
    public StackPane mainGame;
    public VBox vboxLeft;
    public VBox vboxRight;
    public HBox hboxTop;
    public VBox vBoxVictory;
    public Label victoryLabel;
    private Image player1Image;
    private Image player2Image;
    private boolean player1win;
    private int[] placement = new int[9];
    private boolean turn;


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
                        whosTurn.setText("Player 2's Turn");
                        imageView.setImage(player1Image);
                    }
                    else{
                        whosTurn.setText("Player 1's Turn");
                        imageView.setImage(player2Image);
                    }
                    imageView.setOnMouseClicked(null);
                    turn = !turn;
                    int victory = victory();

                    if(victory != 0){
                        player1win = !turn;
                        currentPlayer.setVisible(false);
                        for (javafx.scene.Node node : morpionGrille.getChildren()) {
                            if (node instanceof StackPane stackPane) {
                                ImageView imageView = (ImageView) stackPane.getChildren().getFirst();
                                imageView.setOnMouseClicked(null);
                            }
                        }
                        int []row = getVictory();
                        RotateTransition rotateTransition = null;
                        for(int a : row){
                            StackPane s = (StackPane) morpionGrille.getChildren().get(a);
                            ImageView imageView = (ImageView) s.getChildren().getFirst();
                            rotateTransition = rotateImage(imageView);
                        }
                        rotateTransition.setOnFinished(e -> showVictory(victory));
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
                return rowVictory;
            }
        }
        for(int i = 0; i < 3; i++){
            if(placement[i] == placement[i+3] && placement[i] == placement[i+6]){
                rowVictory = new int[]{i, i + 3, i + 6};
                return rowVictory;
            }
        }
        if(placement[0] == placement[4] && placement[0] == placement[8]){
            rowVictory = new int[]{0, 4, 8};
            return rowVictory;
        }
        if(placement[2] == placement[4] && placement[2] == placement[6]){
            rowVictory = new int[]{2, 4, 6};
            return rowVictory;
        }
        return rowVictory;
    }

    private RotateTransition rotateImage(ImageView i){
        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(2), i);
        rotateTransition.setByAngle(360);
        rotateTransition.setCycleCount(2);
        rotateTransition.setAutoReverse(false);
        rotateTransition.play();
        return rotateTransition;
    }


    public void chooseCircle(MouseEvent actionEvent) {
        player2Image = new Image("file:src/main/resources/com/project/morpion/ai/images/TicTacToe/cross.png");
        player1Image = new Image("file:src/main/resources/com/project/morpion/ai/images/TicTacToe/circle.png");

        player1Object.setImage(player1Image);
        player2Object.setImage(player2Image);
        startLabel.setVisible(true);
        hboxStart.setVisible(true);
    }

    public void chooseCross(MouseEvent actionEvent) {
        player1Image = new Image("file:src/main/resources/com/project/morpion/ai/images/TicTacToe/cross.png");
        player2Image = new Image("file:src/main/resources/com/project/morpion/ai/images/TicTacToe/circle.png");
        player1Object.setImage(player1Image);
        player2Object.setImage(player2Image);
        startLabel.setVisible(true);
        hboxStart.setVisible(true);
    }

    public void startRandom(ActionEvent actionEvent) {
        startLabel.setVisible(false);
        hboxStart.setVisible(false);
        vboxChoice.setVisible(false);
        Random random = new Random();
        int randomNumber = random.nextInt(2) + 1;
        if(randomNumber == 1){
            turn = true;
            whosTurn.setText("Player 1's turn");
        }
        else{
            turn = false;
            whosTurn.setText("Player 2's turn");
        }
        morpionGrille.setVisible(true);
    }

    public void startPlayer2(ActionEvent actionEvent) {
        startLabel.setVisible(false);
        hboxStart.setVisible(false);
        vboxChoice.setVisible(false);
        turn = false;
        whosTurn.setText("Player 2's turn");
        morpionGrille.setVisible(true);
    }

    public void startPlayer1(ActionEvent actionEvent) {
        startLabel.setVisible(false);
        hboxStart.setVisible(false);
        vboxChoice.setVisible(false);
        turn = true;
        whosTurn.setText("Player 1's turn");
        morpionGrille.setVisible(true);
    }

    private void showVictory(int player){
        if(player1win){
            victoryLabel.setText("Player 1 Win");
        }
        else{
            victoryLabel.setText("Player 2 Win");
        }
        blur();
        fadeOutGridPane();
        vBoxVictory.setVisible(true);
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(1), vBoxVictory);
        scaleTransition.setFromX(0.1); // Taille initiale en x
        scaleTransition.setFromY(0.1); // Taille initiale en y
        scaleTransition.setToX(1.0);   // Taille finale en x
        scaleTransition.setToY(1.0);   // Taille finale en y
        scaleTransition.play();
    }

    private void blur(){
        BoxBlur boxBlur = new BoxBlur(10, 10, 3);
        vboxLeft.setEffect(boxBlur);
        vboxRight.setEffect(boxBlur);
        hboxTop.setEffect(boxBlur);
    }

    private void fadeOutGridPane() {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), morpionGrille);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.play();
    }
}