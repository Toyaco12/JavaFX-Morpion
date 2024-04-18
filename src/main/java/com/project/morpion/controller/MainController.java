package com.project.morpion.controller;

import com.project.morpion.App;
import com.project.morpion.model.ModelUpdate;
import com.project.morpion.model.ai.Config;
import com.project.morpion.model.ai.ConfigFileLoader;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.util.Objects;
import java.util.Optional;

public class MainController implements ModelUpdate {
    @FXML
    public Button playButton;
    @FXML
    public VBox chooseGameMode;
    @FXML
    public VBox playGame;
    @FXML
    public VBox chooseDifficulty;
    @FXML
    public Button submitBtn;
    @FXML
    public Label timer;
    @FXML
    public Label timerMessage;
    public Button homeButton;
    public VBox settingVbox;
    public Slider sliderVolume;
    public Label volumeLabel;
    public Slider sliderBrightness;
    public Label brightnessLabel;
    public BorderPane borderPane;
    public Button settingButton;
    public Button exitButton;
    public Button saveButton;
    public Label title;
    public Label chooseMode;
    public Button btnSinglePlayer;
    public SplitMenuButton languageButton;
    public SplitMenuButton themeButton;
    public VBox helpVbox;
    public Text helpText;
    public Label helpTitle;
    public Button rulesButton;
    public Button iaButton;
    public Label brightnessTitle;
    public Label cursorLabel;
    public Button helpButton;
    public ImageView difficultyImage;
    public VBox singleVbox;
    public MenuItem settingItem;
    public MenuItem modelItem;
    private ToggleGroup difficultyGroup;
    private String selectDifficulty;
    private String letterDifficulty ="F";
    private String modelName;

    private String language = "English";
    private String theme = "Black";
    private Cursor cursor;
    private String cursorSett;

    @FXML
    private static Stage stage = null;
    private Scene scene = null;
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    public void setScene(Scene scene) {
        this.scene = scene;
    }

//testttt
    public void initialization() {
        difficultyGroup = new ToggleGroup();
        setOptionnalLevel();
        getCursor();
        if(isFrench())
            setToFrench();
        getTheme();
        int lum = getBrightness();
        playGame.setVisible(true);
        playGame.setManaged(true);

        sliderVolume.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Mettre à jour le texte du Label avec la nouvelle valeur du curseur
            volumeLabel.setText(String.valueOf(newValue.intValue()));
        });
        sliderBrightness.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Mettre à jour le texte du Label avec la nouvelle valeur du curseur
            brightnessLabel.setText(String.valueOf(newValue.intValue()));
        });
        sliderBrightness.setValue(lum);
        brightnessLabel.setText(String.valueOf(lum));
        try {
            String path = "src/main/resources/com/project/morpion/image/victoryMusic.mp3";
            //Media media = new Media(new File(path).toURI().toString());
            //MediaPlayer mediaPlayer = new MediaPlayer(media);

            // Lecture du son
            //mediaPlayer.play();
        }
        catch(Exception ignored){}
    }
    @Override
    public void onModelUpdated() {
        btnSinglePlayer.setDisable(false);
        Platform.runLater(() -> {
            // Création de l'alert
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            if(Objects.equals(language, "French")) {
                alert.setTitle("Modèle Prêt");
                alert.setHeaderText("Votre IA est prête !");
                alert.setContentText("Voulez-vous commencer la partie contre l'IA maintenant ? \nLe nouveau modèle sera enregistré dans les deux cas.");
            }
            else{
                alert.setTitle("Template Ready");
                alert.setHeaderText("Your AI is ready !");
                alert.setContentText("Would you like to start the game against the AI now ? \nThe new template will be saved in both cases.");
            }
            // Définition des boutons
            ButtonType buttonTypeYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
            ButtonType buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.NO);

            alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

            // Affichage de l'alerte et attente de la réponse
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == buttonTypeYes) {
                try {
                    loadPlay1v1View(modelName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void onModelNotUpdated(){
        btnSinglePlayer.setDisable(false);
    }
    public void enablePlayingButton(){
        this.btnSinglePlayer.setDisable(false);
    }
    public void disablePlayingButton(){
        this.btnSinglePlayer.setDisable(true);
    }
    private void loadPlay1v1View(String modelName) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("view/PlaySinglePlayerController.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        PlaySinglePlayerController controller = fxmlLoader.getController();
        controller.setScene(scene);
        controller.initialization();
        controller.setModelName(this.modelName);
        controller.setDifficulty(letterDifficulty);
        controller.setStage(stage);
        controller.initModel();

        stage.setScene(scene);

        stage.show();
    }

    public void startGame(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("view/game-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        GameController controller = fxmlLoader.getController();
        controller.setScene(scene);
        controller.initialization();
        //controller.setStage(stage);
        Stage stageGame = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stageGame.setScene(scene);
        stageGame.show();
    }


    public void openSettings(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("view/setting-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        SettingViewController controller = fxmlLoader.getController();
        controller.setScene(scene);
        controller.initialization();
        Stage stageSettings = new Stage();
        stageSettings.setTitle("Settings");
        stageSettings.getIcons().add(new Image("file:src/main/resources/com/project/morpion/image/morpionlogo.png"));
        stageSettings.initModality(Modality.APPLICATION_MODAL);
        stageSettings.setResizable(false);
        stageSettings.setTitle("Model Settings");
        stageSettings.setScene(scene);
        stageSettings.setOnHidden(event -> {
            setOptionnalLevel();
            getDefaultDifficulty();
        });
        stageSettings.showAndWait();
    }

    public void openLearning(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("view/learn.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stageLearn = new Stage();
        stageLearn.setScene(scene);
        LearnController controller = fxmlLoader.getController();
        controller.setLanguage();
        controller.setDifficulty(letterDifficulty);
        controller.processStart();
        controller.setUpdateListener(this);
        stageLearn.setResizable(false);
        controller.setNotUpdateListener(this);
        controller.getPreviousStage(stage);
        stageLearn.show();
    }

    public void openModels(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("view/model-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        ModelViewController controller = fxmlLoader.getController();
        controller.setScene(scene);
        controller.initialization();
        Stage stageModel = new Stage();
        stageModel.setTitle("Models");
        stageModel.getIcons().add(new Image("file:src/main/resources/com/project/morpion/image/morpionlogo.png"));
        stageModel.initModality(Modality.APPLICATION_MODAL);
        stageModel.setScene(scene);
        stageModel.showAndWait();
    }

    public void setBrightness(int lum){
        double l = -0.9 + ((double) lum /100);
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(l);
        borderPane.setEffect(colorAdjust);
    }

    public void save(){
        try{
            FileReader fileReader = new FileReader(new File("src/main/resources/com/project/morpion/settings.txt"));
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String l = bufferedReader.readLine();
            while (l.charAt(0) != 'S')
                l = bufferedReader.readLine();


            FileWriter fileWriter = new FileWriter(new File("src/main/resources/com/project/morpion/settings.txt"));
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            int lum = (int) sliderBrightness.getValue();
            bufferedWriter.write("L:"+lum);
            bufferedWriter.newLine();
            if(Objects.equals(language, "English")){
                bufferedWriter.write("A:E");
                setToEnglish();
            }
            else{
                bufferedWriter.write("A:F");
                setToFrench();
            }
            bufferedWriter.newLine();
            if(Objects.equals(theme, "black")){
                bufferedWriter.write("T:B");
                setTheme();
            }
            else{
                bufferedWriter.write("T:W");
                setTheme();
            }
            bufferedWriter.newLine();
            if(Objects.equals(cursorSett, "P")){
                bufferedWriter.write("C:P");
            }
            else if(Objects.equals(cursorSett, "C")){
                bufferedWriter.write("C:C");
            }
            else{
                bufferedWriter.write("C:D");
            }
            bufferedWriter.newLine();
            bufferedWriter.write(l);
            bufferedWriter.flush();
            bufferedWriter.close();
            setBrightness(lum);
            back(null);
        }
        catch (IOException ignored){
        }
    }

    private void setTheme() {
        if(Objects.equals(theme, "white")){
            borderPane.setStyle("-fx-background-color: white;");
        }
        else{
            borderPane.setStyle("-fx-background-color: rgb(20,20,20);");
        }

    }


    public int getBrightness(){
        try{
            FileReader fileReader = new FileReader("src/main/resources/com/project/morpion/settings.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            if(line.charAt(0) == 'L'){
                String d = line.substring(line.lastIndexOf(":") + 1);
                int lum = Integer.parseInt(d);
                setBrightness(lum);
                return lum;
            }
        }catch (IOException e){}
        return 0;
    }

    @FXML
    public void play(ActionEvent actionEvent) {
        playGame.setVisible(false);
        playGame.setManaged(false);
        homeButton.setVisible(true);
        chooseGameMode.setManaged(true);
        chooseGameMode.setVisible(true);
        getDefaultDifficulty();
    }

    @FXML
    public void chooseDiff(ActionEvent actionEvent) {
        chooseGameMode.setManaged(false);
        chooseGameMode.setVisible(false);

        chooseDifficulty.setManaged(true);
        chooseDifficulty.setVisible(true);
    }

    @FXML
    public void selectDifficulty(ActionEvent actionEvent) {
        RadioButton selectedRadioButton = (RadioButton) difficultyGroup.getSelectedToggle();
        String userData = (String) selectedRadioButton.getUserData();
        if(Objects.equals(userData, "F")){
            difficultyImage.setImage(new Image("file:src/main/resources/com/project/morpion/image/easy.png"));
        }
        else if(Objects.equals(userData, "M")){
            difficultyImage.setImage(new Image("file:src/main/resources/com/project/morpion/image/medium.png"));
        }
        else if(Objects.equals(userData, "D")){
            difficultyImage.setImage(new Image("file:src/main/resources/com/project/morpion/image/hard.png"));
        }
        else{
            difficultyImage.setImage(new Image("file:src/main/resources/com/project/morpion/image/custom.png"));
        }
    }

    @FXML
    private void handleSubmit(ActionEvent event) throws IOException {
        //if (selectDifficulty != null && !selectDifficulty.isEmpty()) {
        RadioButton radioButton = (RadioButton) difficultyGroup.getSelectedToggle();
        if (radioButton !=null) {
            String diff = (String) radioButton.getUserData();
            letterDifficulty = diff;
            System.out.println("Difficulté sélectionnée: " + selectDifficulty);
            System.out.println("dif :           " + diff);
            ConfigFileLoader cfl = new ConfigFileLoader();
            cfl.loadConfigFile("src/main/resources/com/project/morpion/ai/config.txt");
            Config config = cfl.get(diff);
            File model = new File("src/main/resources/com/project/morpion/ai/models/model_"+config.hiddenLayerSize+"_"+config.learningRate+"_"+config.numberOfhiddenLayers+".srl");
            //File model = new File("src/main/resources/com/project/morpion/ai/models/"+letterDifficulty+"/model_"+config.hiddenLayerSize+"_"+config.learningRate+"_"+config.numberOfhiddenLayers+".srl");
            this.modelName = model.getName();
            if(!model.exists()){
                openLearning(event);
                btnSinglePlayer.setDisable(true);
            }
            else{
                loadPlay1v1View(model.getName());
            }

        }
        else {
            System.out.println("Aucune difficulté sélectionnée.");
        }
    }



    public void back(ActionEvent actionEvent) {
        if(chooseGameMode.isVisible()){
            chooseGameMode.setVisible(false);
            playGame.setVisible(true);
            homeButton.setVisible(false);
        } else if (chooseDifficulty.isVisible()) {
            chooseDifficulty.setVisible(false);
            chooseGameMode.setVisible(true);
        } else if (settingVbox.isVisible()) {
            settingVbox.setVisible(false);
            playGame.setVisible(true);
            homeButton.setVisible(false);
        }
        else if (helpVbox.isVisible()){
            helpVbox.setVisible(false);
            playGame.setVisible(true);
            homeButton.setVisible(false);
        }
    }

    public void gameSettings(ActionEvent actionEvent) {
        playGame.setVisible(false);
        playGame.setManaged(false);
        homeButton.setVisible(true);
        settingVbox.setManaged(true);
        settingVbox.setVisible(true);
    }

    public void exit(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void setToFrench(){
        playButton.setText("Jouer");
        settingButton.setText("Réglages");
        exitButton.setText("Quitter");
        saveButton.setText("Sauver");
        title.setText("Lancement de la Partie");
        chooseMode.setText("Choisir un Mode de Jeu");
        brightnessTitle.setText("Luminosité : ");
        cursorLabel.setText("Curseur : ");
        rulesButton.setText("Règles");
        iaButton.setText("IA");
        helpButton.setText("Aide");
        modelItem.setText("Modèles");
        settingItem.setText("Paramètres");
    }

    public void setToEnglish(){
        playButton.setText("Play");
        settingButton.setText("Settings");
        exitButton.setText("Exit");
        saveButton.setText("Save");
        title.setText("Game Lauch");
        chooseMode.setText("Choose Game Mode");
        brightnessTitle.setText("Brightness : ");
        cursorLabel.setText("Cursor : ");
        rulesButton.setText("Rules");
        iaButton.setText("IA");
        helpButton.setText("Help");
        modelItem.setText("Models");
        settingItem.setText("Settings");
    }

    public void selectEnglish(ActionEvent actionEvent) {
        language = "English";
        languageButton.setText(language);
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
                    theme = "white";
                    setTheme();
                }
                else{
                    theme = "black";
                    setTheme();
                }
            }
        }catch (IOException ignored){}
    }

    public void selectFrench(ActionEvent actionEvent) {
        language = "French";
        languageButton.setText(language);
    }

    public void getDefaultDifficulty() {
        try {
            FileReader fileReader = new FileReader("src/main/resources/com/project/morpion/ai/config.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            while (line.charAt(0) != 'Z') {
                line = bufferedReader.readLine();
            }
            if (line.charAt(0) == 'Z') {
                String d = line.substring(line.lastIndexOf(":") + 1);
                for(Toggle toggle : difficultyGroup.getToggles()){
                    RadioButton radioButton = (RadioButton) toggle;
                    //System.out.println(radioButton.getUserData());
                    if(radioButton.getUserData().equals(d)){
                        difficultyGroup.selectToggle(radioButton);
                    }
                }
            }
            selectDifficulty(null);
        }catch (IOException ignored){}
    }

    public void selectWhite(ActionEvent actionEvent) {
        theme = "white";
        themeButton.setText("White");
    }

    public void selectBlack(ActionEvent actionEvent) {
        theme = "black";
        themeButton.setText("Black");
    }

    public void showHelp(ActionEvent actionEvent) {
        helpVbox.setVisible(true);
        helpVbox.setManaged(true);
        playGame.setVisible(false);
        playGame.setManaged(false);
        homeButton.setVisible(true);
        settingVbox.setManaged(false);
        settingVbox.setVisible(false);
        showRules(null);
    }

    public void showRules(ActionEvent actionEvent) {
        iaButton.setDisable(true);
        rulesButton.setDisable(true);
        TranslateTransition transition = new TranslateTransition(Duration.seconds(1), helpText);
        transition.setToX(-(helpText.getLayoutBounds().getWidth())*2);
        transition.setOnFinished(event -> {
            if(!Objects.equals(language, "English")){
                helpTitle.setText("Règles du Jeu !");
                helpText.setText("Matériel :\n" +
                        "\n" +
                        "Le jeu se joue sur une grille de 3x3 cases.\n" +
                        "Deux joueurs participent, habituellement représentés par deux symboles différents, souvent \"X\" et \"O\".\n" +
                        "But du jeu :\n" +
                        "\n" +
                        "Le but pour chaque joueur est d'aligner trois de ses symboles (X ou O) horizontalement, verticalement ou en diagonale.\n" +
                        "Déroulement du jeu :\n" +
                        "\n" +
                        "Le jeu commence avec une grille vide.\n" +
                        "Les joueurs jouent à tour de rôle en plaçant leur symbole dans une case vide.\n" +
                        "Le joueur qui commence est souvent déterminé par un tirage au sort.\n" +
                        "Déroulement des tours :\n" +
                        "\n" +
                        "À son tour, chaque joueur place son symbole dans une case vide de la grille.\n" +
                        "Une fois qu'un joueur a placé son symbole, c'est au tour de l'autre joueur de jouer.\n" +
                        "Victoire :\n" +
                        "\n" +
                        "Si un joueur parvient à aligner trois de ses symboles horizontalement, verticalement ou en diagonale, il remporte la partie.\n" +
                        "Si la grille est remplie sans qu'aucun joueur n'aligne trois symboles, la partie est déclarée nulle.\n");
            }
            else{
                helpTitle.setText("Rules of the Game !");
                helpText.setText("Materials :\n" +
                        "\n" +
                        "The game is played on a 3x3 grid. \n" +
                        "Two players participate, typically represented by two different symbols, often  \"X\" and \"O\".\n" +
                        "Objective :\n" +
                        "\n" +
                        "The goal for each player is to align three of their symbols (X or O) horizontally, vertically, or diagonally.\n" +
                        "Gameplay :\n" +
                        "\n" +
                        "The game begins with an empty grid.\n" +
                        "Players take turns placing their symbol in an empty square.\n" +
                        "The player who starts is often determined by a random draw.\n" +
                        "Turn Progression :\n" +
                        "\n" +
                        "On their turn, each player places their symbol in an empty square on the grid.\n" +
                        "Once a player has placed their symbol, it's the other player's turn to play. \n" +
                        "Victory :\n" +
                        "\n" +
                        "If a player manages to align three of their symbols horizontally, vertically, or diagonally, they win the game.\n" +
                        "If the grid is filled without any player aligning three symbols, the game is declared a draw.\n");
            }

            helpText.setStyle("-fx-fill: fc6c00; -fx-font-size: 14px;");
            helpTitle.setStyle("-fx-text-fill: rgb(1, 191, 200); -fx-font-size: 22px;");
            TranslateTransition reverse = new TranslateTransition(Duration.seconds(1), helpText);
            reverse.setFromX(helpText.getLayoutBounds().getWidth());
            reverse.setToX(0);
            reverse.setOnFinished(e -> {
                iaButton.setDisable(false);
                rulesButton.setDisable(false);
            });
            reverse.play();

        });
        transition.play();

    }

    public void showIA(ActionEvent actionEvent) {
        iaButton.setDisable(true);
        rulesButton.setDisable(true);
        TranslateTransition transition = new TranslateTransition(Duration.seconds(1), helpText);
        transition.setToX(-(helpText.getLayoutBounds().getWidth())*2);
        transition.setOnFinished(event -> {
            if(!Objects.equals(language, "English")){
                helpTitle.setText("Entrainement de l'IA !");
                helpText.setText("Pour jouer contre notre IA au Morpion, commencez par entraîner notre modèle. Lorsque vous lancez une partie contre l'IA, l'entraînement vous sera automatiquement proposé. \n Cliquez simplement sur 'Entraîner' pour débuter.\n" +
                        "\n" +
                        "L'entraînement vous permettra de mieux comprendre le fonctionnement de notre IA et de vous familiariser avec ses stratégies de jeu.\n Vous pourrez ainsi affiner vos propres compétences et anticiper les mouvements de votre adversaire.\n" +
                        "\n" +
                        "Une fois que vous vous sentirez prêt, lancez-vous dans une partie officielle contre l'IA. Testez vos compétences et voyez si vous pouvez remporter la victoire !\n" +
                        "\n" +
                        "N'oubliez pas que vous pouvez également personnaliser l'IA en ajustant les paramètres dans 'Fichier' puis 'Paramètres'. \n Cela vous permettra de personnaliser l'expérience de jeu selon vos préférences, que ce soit en modifiant le niveau de difficulté ou en choisissant un style visuel qui vous convient.\n" +
                        "\n" +
                        "Bonne chance et amusez-vous bien ! Nous espérons que vous apprécierez cette expérience de jeu contre notre IA au Morpion.");
            }
            else{
                helpTitle.setText("IA Training !");
                helpText.setText("To play against our Tic-Tac-Toe AI, start by training our model. When you start a game against the AI, training will be automatically offered to you. \n Simply click on 'Train' to begin. \n" +
                        "\n" +
                        "Training will help you better understand how our AI works and familiarize yourself with its gameplay strategies. \n You can refine your own skills and anticipate your opponent's moves. \n" +
                        "\n" +
                        "Once you feel ready, dive into an official game against the AI. Test your skills and see if you can emerge victorious! \n" +
                        "\n" +
                        "Remember that you can also customize the AI by adjusting settings in 'File' then 'Settings'. \n This will allow you to tailor the gaming experience to your preferences, whether by changing the difficulty level or selecting a visual style that suits you. \n" +
                        "\n" +
                        "Good luck and have fun! We hope you enjoy this Tic-Tac-Toe AI gaming experience.");
            }
            helpTitle.setStyle("-fx-text-fill: fc6c00; -fx-font-size: 22px;");
            helpText.setStyle("-fx-fill: rgb(1, 191, 200); -fx-font-size: 14px;");
            TranslateTransition reverse = new TranslateTransition(Duration.seconds(1), helpText);
            reverse.setFromX(helpText.getLayoutBounds().getWidth());
            reverse.setToX(0);
            reverse.setOnFinished(e -> {
                iaButton.setDisable(false);
                rulesButton.setDisable(false);
            });
            reverse.play();
        });

        transition.play();
    }

    public void setCursor(ActionEvent actionEvent) {
        cursor = new ImageCursor(new Image("file:src/main/resources/com/project/morpion/image/cursor.png"));
        scene.setCursor(cursor);
        cursorSett = "D";
    }

    public void setCatCursor(ActionEvent actionEvent) {
        cursor = new ImageCursor(new Image("file:src/main/resources/com/project/morpion/image/catcursor.png"));
        scene.setCursor(cursor);
        cursorSett = "C";
    }

    public void setPatteCursor(ActionEvent actionEvent) {
        cursor = new ImageCursor(new Image("file:src/main/resources/com/project/morpion/image/pattes.png"));
        scene.setCursor(cursor);
        cursorSett = "P";
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
                    setCursor(null);
                }
                else if(d.equals("C")){
                    setCatCursor(null);
                }
                else{
                    setPatteCursor(null);
                }
            }
        }catch (IOException ignored){}
    }

    private void setOptionnalLevel(){
        singleVbox.getChildren().removeIf(node -> node instanceof RadioButton);
        String[] basic = {"Easy", "Medium", "Hard"};
        String[] userData = {"F", "M", "D", "C1", "C2", "C3"};
        int i =0;
        for(; i < 3; i++) {
            RadioButton r = new RadioButton(basic[i]);
            r.setStyle("-fx-text-fill: rgb(1, 191, 200); -fx-font-weight: bold;");
            r.setOnAction(this::selectDifficulty);
            r.setPrefWidth(70.0);
            r.setUserData(userData[i]);
            r.setToggleGroup(difficultyGroup);
            singleVbox.getChildren().add(r);
        }
        try {
            FileReader fileReader = new FileReader("src/main/resources/com/project/morpion/settings.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            while (line.charAt(0) != 'S')
                line = bufferedReader.readLine();
            if (line.charAt(0) == 'S') {
                String[] lvl = line.split(":");
                String[] data = new String[lvl.length-1];
                System.arraycopy(lvl, 1, data, 0, data.length);
                if (lvl.length > 1) {
                    for(String a : data){
                        RadioButton radioButton = new RadioButton(a);
                        radioButton.setStyle("-fx-text-fill: rgb(1, 191, 200); -fx-font-weight: bold;");
                        radioButton.setOnAction(this::selectDifficulty);
                        radioButton.setPrefWidth(70.0);
                        radioButton.setUserData(userData[i]);
                        radioButton.setToggleGroup(difficultyGroup);
                        singleVbox.getChildren().add(radioButton);
                        i++;
                    }
                }
            }
        }
        catch (IOException ignored){}
    }
}