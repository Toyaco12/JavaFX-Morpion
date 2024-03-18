package com.project.morpion.controller;

import com.project.morpion.model.Morpion;
import com.project.morpion.model.ai.Coup;
import com.project.morpion.model.ai.MultiLayerPerceptron;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.Objects;
import java.util.Random;

public class PlaySinglePlayerController {
    public GridPane morpionGrille;
    public Label chooseLabel;
    public Label startLabel;
    public Button startPlayer1;
    public Button startPlayer2;
    public Button startRandom;
    public Label partyState;
    public Label whosTurn;
    public TextField player1Name;
    public ImageView player1Object;
    public Label victoryPlayer1;
    public Button homeButton;
    public TextField player2Name;
    public ImageView player2Object;
    public Label victoryPlayer2;
    public Button restartButton;
    public HBox hboxStart;
    public VBox vboxChoice;
    public VBox vBoxVictory;
    public Label victoryLabel;
    public Label revengeLabel;
    public HBox hboxTop;
    public VBox vboxLeft;
    public VBox vboxRight;
    private String modelName;
    private String difficulty;
    public Morpion game;

    private Image player1Image;
    private Image player2Image;
    MultiLayerPerceptron model;

    PauseTransition pauseTransition = new PauseTransition(Duration.seconds(0.5));
    public void setModelName(String modelName){
        this.modelName = modelName;
    }
    public void setDifficulty(String difficulty){
        this.difficulty = difficulty;
    }
    public void initModel() {
        System.out.println(this.modelName);
        System.out.println(this.difficulty);
        model = MultiLayerPerceptron.loadModel(difficulty, modelName);
        //game = new Morpion(model, Coup.X);
        //game.startGame();
    }

    private void setClickListener(){
        morpionGrille.getChildren().forEach(node -> {
            StackPane stackPane = (StackPane) node;
            ImageView imageView = (ImageView) stackPane.getChildren().getFirst();
            imageView.setImage(null);
            imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public  void  handle(MouseEvent mouseEvent){
                    int position = GridPane.getRowIndex(stackPane) * morpionGrille.getColumnCount() + GridPane.getColumnIndex(stackPane);
                    if(game.isAvailable(position)){
                        game.playGUI(position);
                        updateGridPane();
                        if(game.isWin()){
                            showVictory(1);
                            System.out.println("huma  win");
                        }
                        if(game.isDraw()){
                            showVictory(0);
                            System.out.println("huma  draw");
                        }
                        game.playIAGUI();
                        //updateGridPane();
                        pauseTransition.play();

                    }
                    else{

                    }
                }
            });
        });
    }

    private void lanchGame(){

    }

    private void showVictory(int player){
        if(player == 1){
                victoryLabel.setText("And the Winner Is " + player1Name.getText() + "!!!!");
        }
        else if (player == -1){
                victoryLabel.setText("And the Winner Is " + player2Name.getText() + "!!!!");
        }
        else{
                victoryLabel.setText("And It's A Draw .....");
        }

        blur();
        fadeOutGridPane();
        vBoxVictory.setVisible(true);
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(1), vBoxVictory);
        scaleTransition.setFromX(0.0); // Taille initiale en x
        scaleTransition.setFromY(0.0); // Taille initiale en y
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

    private void updateGridPane(){
        double[] board = game.getBoard();
        for( int i = 0; i < board.length; i++){
            if(board[i] == 1){
                StackPane s = (StackPane) morpionGrille.getChildren().get((int)i);
                ImageView imageView = (ImageView) s.getChildren().getFirst();
                imageView.setImage(player1Image);
            }
            if(board[i] == -1){
                StackPane s = (StackPane) morpionGrille.getChildren().get((int)i);
                ImageView imageView = (ImageView) s.getChildren().getFirst();
                imageView.setImage(player2Image);
            }
        }
    }


    @FXML
    public void initialize() {
        setClickListener();
        pauseTransition.setOnFinished(e->{
            updateGridPane();
            if(game.isWin()){
                showVictory(-1);
                System.out.println("pas huma  win");
            }
            if(game.isDraw()){
                showVictory(0);
                System.out.println("pas human  draw");
            }
        });
    }

    public void handleKeyPressed(KeyEvent keyEvent) {
    }

    public void returnHome(ActionEvent actionEvent) {
    }

    public void restartGame(ActionEvent actionEvent) {
    }

    public void chooseCross(MouseEvent mouseEvent) {
        player1Image = new Image("file:src/main/resources/com/project/morpion/ai/images/TicTacToe/cross.png");
        player2Image = new Image("file:src/main/resources/com/project/morpion/ai/images/TicTacToe/circle.png");
        player1Object.setImage(player1Image);
        player2Object.setImage(player2Image);
        if(!hboxStart.isVisible()) {
            fadeInNode(startLabel);
            fadeInNode(hboxStart);
        }
        fadeInNode(player2Object);
        fadeInNode(player1Object);
    }

    public void chooseCircle(MouseEvent mouseEvent) {
        player2Image = new Image("file:src/main/resources/com/project/morpion/ai/images/TicTacToe/cross.png");
        player1Image = new Image("file:src/main/resources/com/project/morpion/ai/images/TicTacToe/circle.png");

        player1Object.setImage(player1Image);
        player2Object.setImage(player2Image);
        if(!hboxStart.isVisible()) {
            fadeInNode(hboxStart);
            fadeInNode(startLabel);
        }
        fadeInNode(player2Object);
        fadeInNode(player1Object);
    }

    public void startPlayer1(ActionEvent actionEvent) {
        hideToStartGame();
        game = new Morpion(model, Coup.O);
        whosTurn.setVisible(true);
        fadeInGridPane();
    }

    public void startPlayer2(ActionEvent actionEvent) {
        hideToStartGame();
        game = new Morpion(model, Coup.X);
        whosTurn.setVisible(true);
        fadeInGridPane();
        game.playIAGUI();
    }

    public void startRandom(ActionEvent actionEvent) {
        hideToStartGame();
        Random random = new Random();
        int randomNumber = random.nextInt(2) + 1;
        if(randomNumber == 1){
            game = new Morpion(model, Coup.O);
        }
        else{
            game = new Morpion(model, Coup.X);
            game.playIAGUI();
        }
        whosTurn.setVisible(true);
        fadeInGridPane();
    }

    private void fadeInNode(Node node){
        node.setVisible(true);
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), node);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.play();
    }

    private void fadeInGridPane(){
        morpionGrille.setVisible(true);
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), morpionGrille);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.play();
        fadeTransition.setOnFinished(e -> {
            updateGridPane();
        });
    }

    public void hideToStartGame(){
        checkEmptyField();
        startLabel.setVisible(false);
        hboxStart.setVisible(false);
        vboxChoice.setVisible(false);
        player1Name.setEditable(false);
        player2Name.setEditable(false);
        player1Name.setDisable(true);
        player2Name.setDisable(true);
    }

    private void checkEmptyField(){
        if(player1Name.getText().isEmpty()){
            player1Name.setText("Player 1");
        }
    }
}
