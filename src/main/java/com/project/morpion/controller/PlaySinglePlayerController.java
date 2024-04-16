package com.project.morpion.controller;

import com.project.morpion.App;
import com.project.morpion.model.Morpion;
import com.project.morpion.model.ai.Coup;
import com.project.morpion.model.ai.MultiLayerPerceptron;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
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
    public BorderPane borderPane;
    public Button revengeButton;
    public HBox changeHbox;
    private String modelName;
    private String difficulty;
    public Morpion game = null;
    private Image player1Image;
    private Image player2Image;
    MultiLayerPerceptron model;
    private boolean readyToPlay;
    ScaleTransition scaleTransition = null;
    private Cursor cursor;
    private Scene scene;
    PauseTransition pauseTransition = new PauseTransition(Duration.seconds(0.5));
    public void setModelName(String modelName){
        this.modelName = modelName;
    }
    public void setDifficulty(String difficulty){
        this.difficulty = difficulty;
    }

    private String language = "English";
    private Stage stage;
    public void setStage(Stage s){
        this.stage = stage;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
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
                    if(readyToPlay) {
                        int position = GridPane.getRowIndex(stackPane) * morpionGrille.getColumnCount() + GridPane.getColumnIndex(stackPane);
                        if (game.isAvailable(position)) {
                            game.playGUI(position);
                            readyToPlay = false;
                            updateGridPane();
                            if (game.isWin()) {
                                showVictory(1);
                            }
                            else if (game.isDraw()) {
                                showVictory(0);
                            }
                            else {
                                game.playIAGUI();
                                //updateGridPane();
                                pauseTransition.play();
                            }
                        }
                    }
                }
            });
        });
    }

    private void lanchGame(){

    }

    private void showVictory(int player){
        restartButton.setDisable(true);
        scaleTransition = new ScaleTransition(Duration.seconds(1), vBoxVictory);
        if(player != 0){
            int[] row = game.getVictory();
            RotateTransition rotateTransition = null;
            for (int a : row) {
                StackPane s = (StackPane) morpionGrille.getChildren().get(a);
                ImageView imageView1 = (ImageView) s.getChildren().getFirst();
                rotateTransition = rotateImage(imageView1);
            }
            rotateTransition.setOnFinished(e -> {
                restartButton.setDisable(false);
                if(player == 1){
                    if(Objects.equals(language, "french"))
                        victoryLabel.setText("Et Le Gagnant Est  " + player1Name.getText() + "!!!!");
                    else
                        victoryLabel.setText("And the Winner Is " + player1Name.getText() + "!!!!");
                    game.addSuccessWinPlayer();
                }
                else if (player == -1){
                    if(Objects.equals(language, "french"))
                        victoryLabel.setText("Et Le Gagnant Est  " + player2Name.getText() + "!!!!");
                    else
                        victoryLabel.setText("And the Winner Is " + player2Name.getText() + "!!!!");
                    game.addSuccessWinBot();
                }
                else{
                    if(Objects.equals(language, "french"))
                        victoryLabel.setText("Et C'est Une Égalité ...");
                    else
                        victoryLabel.setText("And It's A Draw .....");

                }
                String[] level = getLevel();
                int i = 0;
                for(String lvl : level){
                    Button button = new Button(lvl);
                    if(i%2 == 0)
                        button.getStyleClass().add("orange-button");
                    else
                        button.getStyleClass().add("blue-button");
                    button.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
                    button.prefWidth(80);
                    button.prefHeight(80);
                    int finalI = i;
                    button.setOnAction(event->{
                        changeLevel(finalI);
                    });
                    changeHbox.getChildren().add(button);
                    i++;
                }

                blur();
                fadeOutGridPane();
                vBoxVictory.setVisible(true);

                scaleTransition.setFromX(0.0); // Taille initiale en x
                scaleTransition.setFromY(0.0); // Taille initiale en y
                scaleTransition.setToX(1.0);   // Taille finale en x
                scaleTransition.setToY(1.0);   // Taille finale en y
                scaleTransition.play();
            });
        }
        else{
            restartButton.setDisable(false);
            if(Objects.equals(language, "french"))
                victoryLabel.setText("Et C'est Une Égalité ...");
            else
                victoryLabel.setText("And It's A Draw .....");
            blur();
            fadeOutGridPane();
            vBoxVictory.setVisible(true);

            scaleTransition.setFromX(0.0); // Taille initiale en x
            scaleTransition.setFromY(0.0); // Taille initiale en y
            scaleTransition.setToX(1.0);   // Taille finale en x
            scaleTransition.setToY(1.0);   // Taille finale en y
            scaleTransition.play();
        }




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


    public void initialization() {
        //game = new Morpion(model, Coup.O);

        getCursor();
        if(isFrench()){
            setToFrench();
            language = "french";
        }

        getTheme();
        getLuminosity();
        setClickListener();
        pauseTransition.setOnFinished(e->{
            updateGridPane();
            if(game.isWin()){
                showVictory(-1);
                System.out.println("pas huma  win");
            }
            else if(game.isDraw()){
                showVictory(0);
                System.out.println("pas human  draw");
            }
            readyToPlay = true;
        });
    }

    public void handleKeyPressed(KeyEvent keyEvent) {
    }

    public void returnHome(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("view/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        MainController controller = fxmlLoader.getController();
        controller.setScene(scene);
        controller.initialization();
        Stage stageGame = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stageGame.setScene(scene);
        stageGame.show();
    }

    public void restartGame(ActionEvent actionEvent) {
        setClickListener();
        game.restart();
        morpionGrille.setVisible(false);
        hideVictory();
        victoryPlayer1.setVisible(false);
        victoryPlayer2.setVisible(false);
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
        if(game == null)
            game = new Morpion(model, Coup.O);
        else
            game.setCurrentPlayer(1);
        whosTurn.setVisible(true);
        fadeInGridPane();
        readyToPlay = false;
        readyToPlay = true;
    }

    public void startPlayer2(ActionEvent actionEvent) {
        hideToStartGame();
        if(game == null)
            game = new Morpion(model, Coup.X);
        else
            game.setCurrentPlayer(-1);
        //whosTurn.setVisible(true);
        fadeInGridPane();
        game.playIAGUI();
        pauseTransition.play();
    }

    public void startRandom(ActionEvent actionEvent) {
        hideToStartGame();
        Random random = new Random();
        int randomNumber = random.nextInt(2) + 1;
        if(randomNumber == 1){
            if(game == null)
                game = new Morpion(model, Coup.O);
            else
                game.setCurrentPlayer(1);
            readyToPlay = true;
        }
        else{
            if(game == null)
                game = new Morpion(model, Coup.X);
            else
                game.setCurrentPlayer(-1);
            game.playIAGUI();
            pauseTransition.play();
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
            if(Objects.equals(language, "french"))
                player1Name.setText("Joueur 1");
            else
                player1Name.setText("Player 1");
        }
    }

    public void revenge(ActionEvent actionEvent) {
        game.restart();
        restartGame(null);
        if(Objects.equals(language, "french")) {
            victoryPlayer2.setText("Nombre de Victoire : " + game.getSuccessWinBot());
            victoryPlayer1.setText("Nombre de Victoire : " + game.getSuccessWinPlayer());
        }
        else {
            victoryPlayer2.setText("Number of Victory : " + game.getSuccessWinBot());
            victoryPlayer1.setText("Number of Victory : " + game.getSuccessWinPlayer());
        }
        victoryPlayer2.setVisible(true);
        victoryPlayer1.setVisible(true);
    }

    private RotateTransition rotateImage(ImageView i){
        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(2), i);
        rotateTransition.setByAngle(360);
        rotateTransition.setCycleCount(2);
        rotateTransition.setAutoReverse(false);
        rotateTransition.play();
        return rotateTransition;
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

    public void setLuminosity(int lum){
        double l = -0.9 + ((double) lum /100);
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(l);
        borderPane.setEffect(colorAdjust);
    }

    public void setToFrench(){
        partyState.setText("Partie en Cours");
        chooseLabel.setText("Joueur 1 Choisissez Votre Forme");
        startLabel.setText("Qui Commence ?");
        startPlayer1.setText("Joueur 1");
        startPlayer2.setText("Robot");
        startRandom.setText("Hasard");
        revengeLabel.setText("N'Hesitez Pas a Prendre Votre Revanche !!");
        revengeButton.setText("Revanche");
        player2Name.setText("Robot");
        player1Name.setText("Joueur 1");
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

    private void getTheme(){
        try{
            FileReader fileReader = new FileReader("src/main/resources/com/project/morpion/settings.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            while(line.charAt(0) != 'T')
                line = bufferedReader.readLine();
            if(line.charAt(0) == 'T'){
                String d = line.substring(line.lastIndexOf(":") + 1);
                if(d.equals("W")){
                    borderPane.setStyle("-fx-background-color: white;");
                }
                else{
                    borderPane.setStyle("-fx-background-color: rgb(20,20,20);");
                }
            }
        }catch (IOException ignored){}
    }

    private void getCursor(){
        try{
            FileReader fileReader = new FileReader("src/main/resources/com/project/morpion/settings.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            while(line.charAt(0) != 'C')
                line = bufferedReader.readLine();
            if(line.charAt(0) == 'C'){
                String d = line.substring(line.lastIndexOf(":") + 1);
                if(d.equals("D")){
                    cursor = new ImageCursor(new Image("file:src/main/resources/com/project/morpion/image/cursor.png"));
                }
                else if(d.equals("C")){
                    cursor = new ImageCursor(new Image("file:src/main/resources/com/project/morpion/image/catcursor.png"));
                }
                else{
                    cursor = new ImageCursor(new Image("file:src/main/resources/com/project/morpion/image/pattes.png"));
                }
                scene.setCursor(cursor);
            }
        }catch (IOException ignored){}
    }

    private String[] getLevel(){
        try {
            String[] optionnalLevel = new String[0];
            String[] base = {"Easy", "Medium", "Hard"};
            FileReader fileReader = new FileReader("src/main/resources/com/project/morpion/settings.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            while (line.charAt(0) != 'S')
                line = bufferedReader.readLine();
            if (line.charAt(0) == 'S') {
                String[] lvl = line.split(":");
                if (lvl.length > 1) {
                    optionnalLevel = new String[lvl.length - 1];
                    System.arraycopy(lvl, 1, optionnalLevel, 0, lvl.length - 1);
                }
                else {
                    optionnalLevel = new String[0];
                }
            }
            String[] level = Arrays.copyOf(base, base.length + optionnalLevel.length);
            System.arraycopy(optionnalLevel, 0, level, base.length, optionnalLevel.length);

            return level;
        }
        catch (IOException ignored){}
        return null;
    }

    private void changeLevel(int i){
        System.out.println(i);
    }


}
