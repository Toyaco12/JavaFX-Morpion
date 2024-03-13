package com.project.morpion.controller;

import com.project.morpion.App;
import com.project.morpion.model.ItemModel;
import com.project.morpion.model.ModelUpdate;
import com.project.morpion.model.ai.Config;
import com.project.morpion.model.ai.ConfigFileLoader;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

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
                loadPlay1v1View();
            } catch (IOException e) {
                e.printStackTrace(); // Gérer l'exception comme vous le souhaitez
            }
        });
    }
    private void loadPlay1v1View() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("view/play1v1-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);  // Utilisez 'stage' si vous souhaitez réutiliser la fenêtre actuelle
        stage.show();
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
            System.out.println(letterDifficulty);
            Config config = cfl.get(letterDifficulty);
            File model = new File("src/main/resources/com/project/morpion/ai/models/"+letterDifficulty+"/model_"+config.hiddenLayerSize+"_"+config.learningRate+"_"+config.numberOfhiddenLayers+".srl");
            if(!model.exists()){
                openLearning(event);
            }
            else{
                loadPlay1v1View();
            }

        } else {
            System.out.println("Aucune difficulté sélectionnée.");
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
    }*/
}