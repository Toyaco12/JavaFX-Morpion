package com.project.morpion.controller;

import com.project.morpion.App;
import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
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
    public Button homeButton;
    public Button restartButton;
    public Label numberTry;

    public TextField player1Name;
    public TextField player2Name;
    public Label victoryPlayer1;
    public Label victoryPlayer2;
    public Label partyState;
    public BorderPane borderPane;
    public Label chooseLabel;
    public Label revengeLabel;
    public Button revengeButton;
    private Image player1Image;
    private Image player2Image;
    private boolean player1win;
    private int[] placement = new int[9];
    private boolean turn;
    private boolean finish = false;
    private int[] cptVictory = new int[2];
    private String language = "English";

    boolean playKeyBoard = false;

    @FXML
    public void initialize() {
        if(isFrench()){
            language = "French";
            setToFrench();
        }
        getLuminosity();
        setClickListener();
        fadeInNode(vboxChoice);
        fadeInNode(partyState);
        fadeInNode(player1Name);
        fadeInNode(player2Name);
        fadeInNode(restartButton);
        fadeInNode(homeButton);
        TextFormatter<String> textFormatter1 = new TextFormatter<>(change -> {
            if (change.isContentChange() && change.getControlNewText().length() > 12) {
                return null; // Rejeter le changement si le texte est trop long
            }
            return change;
        });
        TextFormatter<String> textFormatter2 = new TextFormatter<>(change -> {
            if (change.isContentChange() && change.getControlNewText().length() > 12) {
                return null; // Rejeter le changement si le texte est trop long
            }
            return change;
        });
        player1Name.setTextFormatter(textFormatter1);
        player2Name.setTextFormatter(textFormatter2);
    }

    public void setLuminosity(int lum){
        double l = -0.9 + ((double) lum /100);
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(l);
        borderPane.setEffect(colorAdjust);
    }

    public int getLuminosity(){
        try{
            FileReader fileReader = new FileReader("src/main/resources/com/project/morpion/settings.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            if(line.charAt(0) == 'L'){
                String d = line.substring(line.lastIndexOf(":") + 1);
                int lum = Integer.parseInt(d);
                setLuminosity(lum);
                return lum;
            }
        }catch (IOException e){}
        return 0;
    }


    private void setClickListener(){
        morpionGrille.getChildren().forEach(node -> {
            StackPane stackPane = (StackPane) node;
            ImageView imageView = (ImageView) stackPane.getChildren().getFirst();
            imageView.setImage(null);
            imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public  void  handle(MouseEvent mouseEvent){
                    imageClicked(imageView, stackPane);
                }
            });
        });
    }

    public void changeWhosTurn(TextField textField){
        if (Objects.equals(language, "French"))
            whosTurn.setText("Au Tour De " + textField.getText());
        else
            whosTurn.setText(textField.getText() + "'s Turn");
    }

    public void imageClicked(ImageView imageView, StackPane stackPane){
        if(!finish) {
            int position = GridPane.getRowIndex(stackPane) * morpionGrille.getColumnCount() + GridPane.getColumnIndex(stackPane);
            placement[position] = turn ? 1 : -1;
            Image i;
            if (turn) {
                changeWhosTurn(player2Name);
                imageView.setImage(player1Image);
            } else {
                changeWhosTurn(player1Name);
                imageView.setImage(player2Image);
            }
            imageView.setOnMouseClicked(null);
            turn = !turn;
            int victory = victory();

            if (victory != 0) {
                whosTurn.setVisible(false);
                finish = true;
                if(victory == -2){
                    showVictory(victory);
                }
                else {
                    if(victory == 1){
                        cptVictory[0]++;
                    }
                    else{
                        cptVictory[1]++;
                    }
                    player1win = !turn;
                    for (javafx.scene.Node node : morpionGrille.getChildren()) {
                        if (node instanceof StackPane s) {
                            ImageView imageView1 = (ImageView) s.getChildren().getFirst();
                            imageView1.setOnMouseClicked(null);
                        }
                    }
                    int[] row = getVictory();
                    RotateTransition rotateTransition = null;
                    for (int a : row) {
                        StackPane s = (StackPane) morpionGrille.getChildren().get(a);
                        ImageView imageView1 = (ImageView) s.getChildren().getFirst();
                        rotateTransition = rotateImage(imageView1);
                    }
                    rotateTransition.setOnFinished(e -> showVictory(victory));
                }
            }
        }
    }

    @FXML
    public void returnHome(ActionEvent actionEvent) {
        try{
            Parent mainView = FXMLLoader.load(Objects.requireNonNull(App.class.getResource("view/main-view.fxml")));
            Scene scene = homeButton.getScene();
            scene.setRoot(mainView);
        }catch (IOException ignored){};
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
        int cpt = 0;
        for(int i : placement){
          if(i != 0) cpt++;
        }
        if(cpt == 9)
            return -2;
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
        if(!hboxStart.isVisible()) {
            fadeInNode(hboxStart);
            fadeInNode(startLabel);
        }
        fadeInNode(player2Object);
        fadeInNode(player1Object);
//        startLabel.setVisible(true);
//        hboxStart.setVisible(true);
    }

    public void chooseCross(MouseEvent actionEvent) {
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
//        startLabel.setVisible(true);
//        hboxStart.setVisible(true);
    }

    public void startRandom(ActionEvent actionEvent) {
        hideToStartGame();
        Random random = new Random();
        int randomNumber = random.nextInt(2) + 1;
        if(randomNumber == 1){
            turn = true;
            changeWhosTurn(player1Name);
        }
        else{
            turn = false;
            changeWhosTurn(player2Name);
        }
        whosTurn.setVisible(true);
        fadeInGridPane();
    }

    public void startPlayer2(ActionEvent actionEvent) {
        hideToStartGame();
        turn = false;
        changeWhosTurn(player2Name);
        whosTurn.setVisible(true);
        fadeInGridPane();
    }

    public void startPlayer1(ActionEvent actionEvent) {
        hideToStartGame();
        turn = true;
        changeWhosTurn(player1Name);
        whosTurn.setVisible(true);
        fadeInGridPane();
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
        if(player2Name.getText().isEmpty()){
            player2Name.setText("Player 2");
        }
    }

    private void showVictory(int player){
        if(player == 1){
            if(Objects.equals(language, "French"))
                victoryLabel.setText("Et le Vainqueur Est " + player1Name.getText() + "!!!!");
            else
                victoryLabel.setText("And the Winner Is " + player1Name.getText() + "!!!!");
        }
        else if (player == -1){
            if(Objects.equals(language, "French"))
                victoryLabel.setText("Et le Vainqueur Est " + player2Name.getText() + "!!!!");
            else
                victoryLabel.setText("And the Winner Is " + player2Name.getText() + "!!!!");
        }
        else{
            if(Objects.equals(language, "French"))
                victoryLabel.setText("Et C'est Une Égalité ..... ");
            else
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

    private void hideVictory(){
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(1), vBoxVictory);
        scaleTransition.setFromX(1.0); // Taille initiale en x
        scaleTransition.setFromY(1.0); // Taille initiale en y
        scaleTransition.setToX(0.0);   // Taille finale en x
        scaleTransition.setToY(0.0);   // Taille finale en y
        scaleTransition.play();
        scaleTransition.setOnFinished(event ->{
            vBoxVictory.setVisible(false);
            vboxLeft.setEffect(null);
            vboxRight.setEffect(null);
            hboxTop.setEffect(null);
            //
            //fadeInGridPane();
            //hideVictory();
            fadeInNode(hboxStart);
            fadeInNode(startLabel);
            fadeInNode(vboxChoice);
        });
    }

    private void blur(){
        BoxBlur boxBlur = new BoxBlur(10, 10, 3);
        vboxLeft.setEffect(boxBlur);
        vboxRight.setEffect(boxBlur);
        hboxTop.setEffect(boxBlur);
    }

    private void fadeOutGridPane() {
        playKeyBoard = false;
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), morpionGrille);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.play();
    }

    private void fadeInGridPane(){
        morpionGrille.setVisible(true);
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), morpionGrille);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.play();
        fadeTransition.setOnFinished(e -> playKeyBoard = true);
    }

    private void fadeInNode(Node node){
        node.setVisible(true);
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), node);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.play();
    }

    @FXML
    public void restartGame(ActionEvent actionEvent) {
        try{
            Parent mainView = FXMLLoader.load(Objects.requireNonNull(App.class.getResource("view/game-view.fxml")));
            Scene scene = homeButton.getScene();
            scene.setRoot(mainView);
        }catch (IOException ignored){};
    }

    public void handleKeyPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode().isKeypadKey() && !Objects.equals(keyEvent.getText(), "0") && playKeyBoard){
            int index = Integer.parseInt(keyEvent.getText());
            if(placement[index-1] != 0) return;
            StackPane stackPane = (StackPane) morpionGrille.getChildren().get(index-1);
            ImageView imageView = (ImageView) stackPane.getChildren().getFirst();
            imageClicked(imageView, stackPane);
        }
    }

    public void revenge(ActionEvent actionEvent) {
        hideVictory();
//        fadeInNode(hboxStart);
//        fadeInNode(startLabel);
//        fadeInNode(vboxChoice);

        for(int i = 0; i < placement.length ; i++){
            placement[i] = 0;
        }
        finish = false;
        setClickListener();
        if(Objects.equals(language, "French")) {
            victoryPlayer1.setText("Nombre de Victoire : " + cptVictory[0]);
            victoryPlayer2.setText("Nombre de Victoire : " + cptVictory[1]);
        }
        else{
            victoryPlayer1.setText("Number of Victory : " + cptVictory[0]);
            victoryPlayer2.setText("Number of Victory : " + cptVictory[1]);
        }
        victoryPlayer1.setVisible(true);
        victoryPlayer2.setVisible(true);
    }

    public void setToFrench(){
        partyState.setText("Partie en Cours");
        chooseLabel.setText("Joueur 1 Choisissez Votre Forme");
        startLabel.setText("Qui Commence ?");
        startPlayer1.setText("Joueur 1");
        startPlayer2.setText("Joueur 2");
        startRandom.setText("Hasard");
        revengeLabel.setText("N'Hesitez Pas a Prendre Votre Revanche !!");
        revengeButton.setText("Revanche");
    }

    private boolean isFrench(){
        try{
            FileReader fileReader = new FileReader("src/main/resources/com/project/morpion/settings.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            while(line.charAt(0) != 'A')
                line = bufferedReader.readLine();
            if(line.charAt(0) == 'A'){
                String d = line.substring(line.lastIndexOf(":") + 1);
                return !d.equals("E");
            }
        }catch (IOException ignored){}
        return false;
    }
}
