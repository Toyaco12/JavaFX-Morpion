package com.project.morpion.controller;

import com.project.morpion.App;
import com.project.morpion.model.AudioPlayer;
import com.project.morpion.model.Morpion;
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
import javafx.scene.control.TextFormatter;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;

public class GameController {

    public GridPane morpionGrille;
    public ImageView player1Object;
    public ImageView player2Object;
    public VBox vboxChoice;
    public Button startPlayer1;
    public Button startPlayer2;
    public Button startRandom;
    public HBox hboxStart;
    public Label startLabel;
    public Label whosTurn;
    public StackPane mainGame;
    public VBox vboxLeft;
    public VBox vboxRight;
    public HBox hboxTop;
    public VBox vBoxVictory;
    public Label victoryLabel;
    public Button homeButton;
    public Button restartButton;

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
    private Cursor cursor;
    private double volume;
    AudioPlayer audioPlayer;

    private Scene scene;


    public void setScene(Scene scene) {
        this.scene = scene;
    }

    // Initialise le contrôleur en configurant l'audio, les préférences de l'utilisateur, et en préparant l'interface utilisateur pour le démarrage du jeu.

    public void initialization() {
        audioPlayer = new AudioPlayer();
        getCursor();
        if(isFrench()){
            language = "French";
            setToFrench();
        }
        getTheme();
        getLuminosity();
        getVolume();
        setClickListener();
        fadeInNode(vboxChoice);
        fadeInNode(partyState);
        fadeInNode(player1Name);
        fadeInNode(player2Name);
        fadeInNode(restartButton);
        restartButton.setDisable(true);
        restartButton.setVisible(false);
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

    // Ajuste la luminosité de l'interface utilisateur selon le paramètre spécifié.

    public void setLuminosity(int lum){
        double l = -0.9 + ((double) lum /100);
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(l);
        borderPane.setEffect(colorAdjust);
    }

    // Récupère et applique la luminosité configurée à partir d'un fichier de paramètres.

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

    // Configure les écouteurs d'événements sur les images de la grille pour gérer les clics et placer les pions.

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

    // Configure les écouteurs d'événements sur les images de la grille pour gérer les clics et placer les pions.

    public void changeWhosTurn(TextField textField){
        if (Objects.equals(language, "French"))
            whosTurn.setText("Au Tour De " + textField.getText());
        else
            whosTurn.setText(textField.getText() + "'s Turn");
    }

    // Gère les actions à effectuer après un clic sur une case du jeu, y compris le placement des pions et la vérification des conditions de victoire.

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

    // Gère le retour à l'écran d'accueil lorsqu'on clique sur le bouton correspondant.

    @FXML
    public void returnHome(ActionEvent actionEvent) throws IOException {
        audioPlayer.stopMusic();
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("view/main-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            MainController controller = fxmlLoader.getController();
            controller.setScene(scene);
            controller.initialization();
            Stage stageGame = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stageGame.setScene(scene);
            stageGame.show();
    }

    // Vérifie les conditions de victoire sur la grille et retourne le résultat (vainqueur, égalité, ou jeu en cours).
    public int victory() {
        // Vérifier chaque ligne
        for (int i = 0; i <= 6; i += 3) {
            if (placement[i] != 0 && placement[i] == placement[i + 1] && placement[i] == placement[i + 2]) {
                return placement[i];
            }
        }

        // Vérifier chaque colonne
        for (int i = 0; i < 3; i++) {
            if (placement[i] != 0 && placement[i] == placement[i + 3] && placement[i] == placement[i + 6]) {
                return placement[i];
            }
        }

        // Vérifier les diagonales
        if (placement[0] != 0 && placement[0] == placement[4] && placement[0] == placement[8]) {
            return placement[0];
        }
        if (placement[2] != 0 && placement[2] == placement[4] && placement[2] == placement[6]) {
            return placement[2];
        }

        // Vérifier l'état de l'égalité
        boolean full = true;
        for (int i : placement) {
            if (i == 0) {
                full = false;
                break;
            }
        }
        if (full) {
            return -2; // Code pour l'égalité
        }

        return 0; // Pas de vainqueur encore
    }


    // Renvoie les indices des cases formant une ligne gagnante pour mettre en évidence les pions victorieux.

    private int[] getVictory() {
        // Vérifie chaque ligne
        for (int i = 0; i <= 6; i += 3) {
            if (placement[i] != 0 && placement[i] == placement[i + 1] && placement[i] == placement[i + 2]) {
                return new int[]{i, i + 1, i + 2};
            }
        }

        // Vérifie chaque colonne
        for (int i = 0; i < 3; i++) {
            if (placement[i] != 0 && placement[i] == placement[i + 3] && placement[i] == placement[i + 6]) {
                return new int[]{i, i + 3, i + 6};
            }
        }

        // Vérifie les diagonales
        if (placement[0] != 0 && placement[0] == placement[4] && placement[0] == placement[8]) {
            return new int[]{0, 4, 8};
        }
        if (placement[2] != 0 && placement[2] == placement[4] && placement[2] == placement[6]) {
            return new int[]{2, 4, 6};
        }

        // Si aucune ligne gagnante n'est trouvée, retourne un tableau vide ou null
        return new int[0];
    }

    // Crée et joue une animation de rotation pour l'image spécifiée, utilisée pour souligner un pion dans une ligne gagnante.

    private RotateTransition rotateImage(ImageView i){
        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(2), i);
        rotateTransition.setByAngle(360);
        rotateTransition.setCycleCount(2);
        rotateTransition.setAutoReverse(false);
        rotateTransition.play();
        return rotateTransition;
    }

    // Permet au joueur de sélectionner le symbole avec lequel il souhaite jouer (cercle ou croix).

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
    }

    // Démarre une nouvelle partie en déterminant aléatoirement ou manuellement quel joueur commence.

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

    // Cache les éléments de configuration pour commencer le jeu, désactive les modifications du nom des joueurs.

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

    // Affiche l'écran de victoire en indiquant le gagnant ou une égalité et joue la musique appropriée.

    private void showVictory(int player){
        if(player == 1){
            audioPlayer.playVictoryMusic();
            audioPlayer.changeVolume(volume);
            if(Objects.equals(language, "French"))
                victoryLabel.setText("Et le Vainqueur Est " + player1Name.getText() + "!!!!");
            else
                victoryLabel.setText("And the Winner Is " + player1Name.getText() + "!!!!");
        }
        else if (player == -1){
            audioPlayer.playVictoryMusic();
            audioPlayer.changeVolume(volume);
            if(Objects.equals(language, "French"))
                victoryLabel.setText("Et le Vainqueur Est " + player2Name.getText() + "!!!!");
            else
                victoryLabel.setText("And the Winner Is " + player2Name.getText() + "!!!!");
        }
        else{
            audioPlayer.playDrawMusic();
            audioPlayer.changeVolume(volume);
            if(Objects.equals(language, "French")) {
                victoryLabel.setText("Et C'est Une Égalité ..... ");
                revengeLabel.setText("N'hesitez pas à vous départager !!");
            }
            else {
                victoryLabel.setText("And It's A Draw .....");
                revengeLabel.setText("Don't hesitate to settle the matter !!");
            }
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

    // Cache l'écran de victoire et réinitialise les éléments visuels pour un nouveau jeu.

    private void hideVictory(){

        audioPlayer.stopMusic();
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(1), vBoxVictory);
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(0.0);
        scaleTransition.setToY(0.0);
        scaleTransition.play();
        scaleTransition.setOnFinished(event ->{
            vBoxVictory.setVisible(false);
            vboxLeft.setEffect(null);
            vboxRight.setEffect(null);
            hboxTop.setEffect(null);
            fadeInNode(hboxStart);
            fadeInNode(startLabel);
            fadeInNode(vboxChoice);
            revengeButton.setDisable(false);
        });
    }

    // Applique un effet de flou aux éléments de l'interface pour mettre en évidence l'écran de victoire.

    private void blur(){
        BoxBlur boxBlur = new BoxBlur(10, 10, 3);
        vboxLeft.setEffect(boxBlur);
        vboxRight.setEffect(boxBlur);
        hboxTop.setEffect(boxBlur);
    }

    // Gère les transitions de visibilité pour la grille de jeu en utilisant des animations de fondu.

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
        //audioPlayer.stopMusic();
        try{
            Parent mainView = FXMLLoader.load(Objects.requireNonNull(App.class.getResource("view/game-view.fxml")));
            Scene scene = homeButton.getScene();
            scene.setRoot(mainView);
        }catch (IOException ignored){};
    }

    public void handleKeyPressed(KeyEvent keyEvent) {
//        if(keyEvent.getCode().isKeypadKey() && !Objects.equals(keyEvent.getText(), "0") && playKeyBoard){
//            int index = Integer.parseInt(keyEvent.getText());
//            switch (index){
//                case 1 : index = 7;
//                break;
//                case 2 : index = 8;
//                break;
//                case 3 : index = 9;
//                break;
//                case 7 : index = 1;
//                break;
//                case 8 : index = 2;
//                break;
//                case 9 : index = 3;
//                break;
//            }
//            if(placement[index-1] != 0) return;
//            StackPane stackPane = (StackPane) morpionGrille.getChildren().get(index-1);
//            ImageView imageView = (ImageView) stackPane.getChildren().getFirst();
//            imageClicked(imageView, stackPane);
//        }
    }

    // Permet de démarrer une nouvelle partie après une égalité ou une victoire pour revanche.

    public void revenge(ActionEvent actionEvent) {
        revengeButton.setDisable(true);
        hideVictory();

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

    // Configure l'interface et les textes en français.

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

    // Détermine si la langue de l'application doit être configurée en français en lisant les paramètres.

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

    public void getVolume(){
        try{
            FileReader fileReader = new FileReader("src/main/resources/com/project/morpion/settings.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            while(line != null) {
                if (line.charAt(0) == 'V') {
                    String d = line.substring(line.lastIndexOf(":") + 1);
                    volume = Double.parseDouble(d);
                    return;
                }
                line = bufferedReader.readLine();
            }
        }catch (IOException e){}
    }


}
