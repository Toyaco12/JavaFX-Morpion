package com.project.morpion.controller;

import com.project.morpion.App;
import com.project.morpion.model.ModelUpdate;
import com.project.morpion.model.ai.Config;
import com.project.morpion.model.ai.ConfigFileLoader;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;

public class MainController implements ModelUpdate {
    @FXML
    public Button play;
    @FXML
    public VBox chooseGameMode;
    @FXML
    public VBox playGame;
    @FXML
    public VBox chooseDifficulty;
    @FXML
    public Button submitBtn;
    public Label errorLabel;
    public Button homeButton;
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

    @FXML
    private Stage stage;
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        playGame.setVisible(true);
        playGame.setManaged(true);
        difficultyGroup = new ToggleGroup();
        easyRadioButton.setToggleGroup(difficultyGroup);
        mediumRadioButton.setToggleGroup(difficultyGroup);
        hardRadioButton.setToggleGroup(difficultyGroup);
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
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("view/PlaySinglePlayerController.fxml"));
        Parent root = fxmlLoader.load();
        PlaySinglePlayerController controller = fxmlLoader.getController();
        controller.setModelName(this.modelName);
        controller.setDifficulty(letterDifficulty);
        controller.initModel();

        Scene scene = new Scene(root);
        Stage s  = (Stage) ((Node) easyRadioButton).getScene().getWindow();
        s.setScene(scene);

        s.show();
    }


    public void openSettings(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("view/setting-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stageSettings = new Stage();
        stageSettings.setResizable(false);
        stageSettings.setTitle("Model Settings");
        stageSettings.setScene(scene);
        stageSettings.show();
    }

    public void openLearning(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("view/learn.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stageLearn = new Stage();
        stageLearn.setScene(scene);
        LearnController controller = fxmlLoader.getController();
        controller.setDifficulty(letterDifficulty);
        controller.setUpdateListener(this);
        stageLearn.show();
    }

    public void openModels(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("view/model-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stageModel = new Stage();
        stageModel.setScene(scene);
        stageModel.show();
    }

    @FXML
    public void play(ActionEvent actionEvent) {
        playGame.setVisible(false);
        playGame.setManaged(false);

        chooseGameMode.setManaged(true);
        chooseGameMode.setVisible(true);
    }

    @FXML
    public void chooseDiff(ActionEvent actionEvent) {
        chooseGameMode.setManaged(false);
        chooseGameMode.setVisible(false);

        chooseDifficulty.setManaged(true);
        chooseDifficulty.setVisible(true);
        try {
            FileReader settingFile = new FileReader("src/main/resources/com/project/morpion/ai/config.txt");
            BufferedReader reader = new BufferedReader(settingFile);
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.charAt(0) == 'Z') {
                    String d = line.substring(line.lastIndexOf(":") + 1);
                    if(d.matches("E")){
                        easyRadioButton.setSelected(true);
                        mediumRadioButton.setSelected(false);
                        hardRadioButton.setSelected(false);
                    }
                    else if(d.matches("M")){
                        easyRadioButton.setSelected(false);
                        mediumRadioButton.setSelected(true);
                        hardRadioButton.setSelected(false);
                    }
                    else{
                        easyRadioButton.setSelected(false);
                        mediumRadioButton.setSelected(false);
                        hardRadioButton.setSelected(true);
                    }
                }
            }
        }catch(IOException ignored){}
    }

    @FXML
    public void selectDifficulty(ActionEvent actionEvent) {
        RadioButton selectedRadioButton = (RadioButton) actionEvent.getSource();
        selectDifficulty = selectedRadioButton.getText();
    }

    @FXML
    private void handleSubmit(ActionEvent event) throws IOException {
        if (selectDifficulty != null && !selectDifficulty.isEmpty()) {
            errorLabel.setVisible(false);
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
            errorLabel.setVisible(true);
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
        if(chooseDifficulty.isVisible()){
            chooseDifficulty.setVisible(false);
            chooseGameMode.setVisible(true);
        }
        else if(chooseGameMode.isVisible()){
            chooseGameMode.setVisible(false);
            playGame.setVisible(true);
        }
    }
/*    private void loadModels(MenuButton menuButton, String path){
        File dir = new File(path);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".srl"));

        if (files != null){
            menuButton.getItems().clear();
            for (File file : files){
                ItemModel item = new ItemModel(file.getAbsolutePath());
                MenuItem menuItem = new MenuItem(item.getName());
                menuItem.setOnAction(event -> {
                    //gérer ce qui se passe quand on clique sur un modèle
                    System.out.println("Modèle sélectionné : " + item.getFullPath());
                });
                menuButton.getItems().add(menuItem);
            }
            String level = menuButton.getId();
            switch (level){
                case "easyMenu":
                    menuButton.getItems().add(F);
                    break;
                case "mediumMenu":
                    menuButton.getItems().add(M);
                    break;
                case "hardMenu":
                    menuButton.getItems().add(D);
                    break;
            }
        }
<<<<<<< HEAD
    }

    public void startGame(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("view/game-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stageGame = new Stage();
        stageGame.setScene(scene);
        stageGame.show();
    }
=======
    }*/

}