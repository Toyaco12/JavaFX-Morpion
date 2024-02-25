package com.project.morpion.controller;

import com.project.morpion.App;
import com.project.morpion.model.ItemModel;
import com.project.morpion.model.ModelUpdate;
import com.project.morpion.model.ai.Config;
import com.project.morpion.model.ai.ConfigFileLoader;
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
    public MenuButton difficultyMenu;
    @FXML
    public Button submitBtn;
    private String selectDifficulty;
    @FXML
    private Stage stage;
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        playGame.setVisible(true);
        playGame.setManaged(true);
    }
    @Override
    public void onModelUpdated() {
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
        MenuItem menuItem = (MenuItem) actionEvent.getSource();
        String difficulty = menuItem.getId();

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("view/learn.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stageLearn = new Stage();
        stageLearn.setScene(scene);
        LearnController controller = fxmlLoader.getController();
        controller.setDifficulty(difficulty);
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
        MenuItem selected = (MenuItem) actionEvent.getSource();
        selectDifficulty = selected.getText();
        difficultyMenu.setText("Difficulté: " + selectDifficulty);
    }
    @FXML
    private void handleSubmit(ActionEvent event) {
        if (selectDifficulty != null) {
            System.out.println("Difficulté sélectionnée: " + selectDifficulty);
            switch (selectDifficulty){
                case "easy":
                    break;
            }
            ConfigFileLoader cfl = new ConfigFileLoader();
            cfl.loadConfigFile("src/main/resources/com/project/morpion/ai/config.txt");
           // Config config = cfl.get(difficulty);
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