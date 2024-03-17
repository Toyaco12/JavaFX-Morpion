package com.project.morpion.controller;

import com.project.morpion.App;
import com.project.morpion.model.ModelUpdate;
import com.project.morpion.model.ai.Config;
import com.project.morpion.model.ai.ConfigFileLoader;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.*;
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

    @FXML
    private Stage stage;
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        if(isFrench())
            setToFrench();
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
        Stage stageSettings = new Stage();
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
        Stage stageModel = new Stage();
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
            bufferedWriter.flush();
            bufferedWriter.close();
            setLuminosity(lum);
        }
        catch (IOException ignored){}
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

    public void selectFrench(ActionEvent actionEvent) {
        language = "French";
        languageButton.setText(language);
    }
}