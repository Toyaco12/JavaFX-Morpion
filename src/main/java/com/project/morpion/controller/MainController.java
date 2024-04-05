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
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.util.Objects;

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
    public Slider sliderLuminosity;
    public Label luminosityLabel;
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
    @FXML
    private RadioButton easyRadioButton;
    @FXML
    private RadioButton mediumRadioButton;
    @FXML
    private RadioButton hardRadioButton;
    @FXML
    private ToggleGroup difficultyGroup;
    private String selectDifficulty;
    private String letterDifficulty ="F";
    private String modelName;
    int seconds = 3;

    private String language = "English";
    private String theme = "Black";
    private Cursor cursor;
    private String cursorSett;

    @FXML
    private Stage stage = null;
    private Scene scene = null;
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    public void setScene(Scene scene) {
        this.scene = scene;
    }


    public void initialization() {
        getCursor();
        if(isFrench())
            setToFrench();
        getTheme();
        int lum = getLuminosity();
        playGame.setVisible(true);
        playGame.setManaged(true);
        difficultyGroup = new ToggleGroup();
        easyRadioButton.setToggleGroup(difficultyGroup);
        mediumRadioButton.setToggleGroup(difficultyGroup);
        hardRadioButton.setToggleGroup(difficultyGroup);
        sliderVolume.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Mettre à jour le texte du Label avec la nouvelle valeur du curseur
            volumeLabel.setText(String.valueOf(newValue.intValue()));
        });
        sliderLuminosity.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Mettre à jour le texte du Label avec la nouvelle valeur du curseur
            luminosityLabel.setText(String.valueOf(newValue.intValue()));
        });
        sliderLuminosity.setValue(lum);
        luminosityLabel.setText(String.valueOf(lum));
    }
    @Override
    public void onModelUpdated() {
        Platform.runLater(() -> {
            try {
                loadPlay1v1View(modelName);
            } catch (IOException e) {
                e.printStackTrace(); // Gérer l'exception comme vous le souhaitez
            }
        });
    }
    private void loadPlay1v1View(String modelName) throws IOException {
        timer.setVisible(true);
        timer.setManaged(true);
        timerMessage.setVisible(true);
        timerMessage.setManaged(true);
        timer.setText(String.valueOf(seconds));
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            seconds--;
            timer.setText(String.valueOf(seconds));
            if (seconds == 0) {
                FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("view/PlaySinglePlayerController.fxml"));
                Parent root = null;
                try {
                    root = fxmlLoader.load();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                PlaySinglePlayerController controller = fxmlLoader.getController();
                controller.setModelName(this.modelName);
                controller.setDifficulty(letterDifficulty);
                controller.initModel();

                Scene scene = new Scene(root);
                Stage s  = (Stage) ((Node) easyRadioButton).getScene().getWindow();
                s.setScene(scene);

                s.show();
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
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
        stageSettings.showAndWait();
    }

    public void openLearning(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("view/learn.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stageLearn = new Stage();
        stageLearn.setScene(scene);
        LearnController controller = fxmlLoader.getController();
        controller.setDifficulty(letterDifficulty);
        controller.processStart();
        controller.setUpdateListener(this);
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

    public void setLuminosity(int lum){
        double l = -0.9 + ((double) lum /100);
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(l);
        borderPane.setEffect(colorAdjust);
    }

    public void save(){
        try{
            FileWriter fileWriter = new FileWriter(new File("src/main/resources/com/project/morpion/settings.txt"));
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            int lum = (int) sliderLuminosity.getValue();
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
            bufferedWriter.flush();
            bufferedWriter.close();
            setLuminosity(lum);
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
        RadioButton selectedRadioButton = (RadioButton) actionEvent.getSource();
        selectDifficulty = selectedRadioButton.getText();
    }

    @FXML
    private void handleSubmit(ActionEvent event) throws IOException {
        if (selectDifficulty != null && !selectDifficulty.isEmpty()) {
            System.out.println("Difficulté sélectionnée: " + selectDifficulty);
            switch (selectDifficulty){
                case "Easy":
                    letterDifficulty = "F";
                    break;
                case "Medium":
                    letterDifficulty = "M";
                    break;
                case "Hard":
                    letterDifficulty ="D";
                    break;
            }
            ConfigFileLoader cfl = new ConfigFileLoader();
            cfl.loadConfigFile("src/main/resources/com/project/morpion/ai/config.txt");
            Config config = cfl.get(letterDifficulty);
            File model = new File("src/main/resources/com/project/morpion/ai/models/"+letterDifficulty+"/model_"+config.hiddenLayerSize+"_"+config.learningRate+"_"+config.numberOfhiddenLayers+".srl");
            this.modelName = model.getName();
            if(!model.exists()){
                openLearning(event);
            }
            else{
                loadPlay1v1View(model.getName());
            }

        } else {
            System.out.println("Aucune difficulté sélectionnée.");
        }
    }

    public void startGame(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("view/game-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        GameController controller = fxmlLoader.getController();
        controller.setScene(scene);
        controller.initialization();
        Stage stageGame = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stageGame.setScene(scene);
        stageGame.show();
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
        btnSinglePlayer.setText("1 Joueur");
    }

    public void setToEnglish(){
        playButton.setText("Play");
        settingButton.setText("Settings");
        exitButton.setText("Exit");
        saveButton.setText("Save");
        title.setText("Game Lauch");
        chooseMode.setText("Choose Game Mode");
        btnSinglePlayer.setText("SinglePlayer");
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
                switch (d) {
                    case "E" -> easyRadioButton.setSelected(true);
                    case "M" -> mediumRadioButton.setSelected(true);
                    case "H" -> hardRadioButton.setSelected(true);
                }
            }
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
        transition.setToX(-helpText.getLayoutBounds().getWidth());
        transition.setOnFinished(event -> {
        helpTitle.setText("Rules !");
        helpTitle.setStyle("-fx-text-fill: rgb(1, 191, 200); -fx-font-size: 22px;");
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
            helpText.setStyle("-fx-fill: fc6c00; -fx-font-size: 14px;");
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
        transition.setToX(-helpText.getLayoutBounds().getWidth());
        transition.setOnFinished(event -> {
            helpTitle.setText("IA Training !");
            helpTitle.setStyle("-fx-text-fill: fc6c00; -fx-font-size: 22px;");
            helpText.setText("Pour jouer contre notre IA au Morpion, commencez par entraîner notre modèle. Lorsque vous lancez une partie contre l'IA, l'entraînement vous sera automatiquement proposé. \n Cliquez simplement sur 'Entraîner' pour débuter.\n" +
                    "\n" +
                    "L'entraînement vous permettra de mieux comprendre le fonctionnement de notre IA et de vous familiariser avec ses stratégies de jeu.\n Vous pourrez ainsi affiner vos propres compétences et anticiper les mouvements de votre adversaire.\n" +
                    "\n" +
                    "Une fois que vous vous sentirez prêt, lancez-vous dans une partie officielle contre l'IA. Testez vos compétences et voyez si vous pouvez remporter la victoire !\n" +
                    "\n" +
                    "N'oubliez pas que vous pouvez également personnaliser l'IA en ajustant les paramètres dans 'Fichier' puis 'Paramètres'. \n Cela vous permettra de personnaliser l'expérience de jeu selon vos préférences, que ce soit en modifiant le niveau de difficulté ou en choisissant un style visuel qui vous convient.\n" +
                    "\n" +
                    "Bonne chance et amusez-vous bien ! Nous espérons que vous apprécierez cette expérience de jeu contre notre IA au Morpion.");
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
}