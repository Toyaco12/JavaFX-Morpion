package com.project.morpion.controller;

import com.project.morpion.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class MainController {
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }


    public Pane panel;
    public Button toNext;
    @FXML
    private VBox vbox1;

    @FXML
    private Label welcomeText;

    @FXML
    public void initialize() {
    }


//    public void openSettings(ActionEvent actionEvent) {
//        AtomicBoolean save = new AtomicBoolean(false);
//        Stage modalStage = new Stage();
//        modalStage.initModality(Modality.APPLICATION_MODAL);
//        modalStage.setTitle("Settings Window");
//
//        GridPane gridPane = new GridPane();
//        gridPane.setPadding(new Insets(10));
//        gridPane.setHgap(5);
//        gridPane.setVgap(5);
//
//        // Labels pour les lignes
//        Label labelRow1 = new Label("FACILE");
//        Label labelRow2 = new Label("MOYEN");
//        Label labelRow3 = new Label("DIFFICILE");
//
//        // Ajout des labels pour les lignes
//        gridPane.add(labelRow1, 0, 1);
//        gridPane.add(labelRow2, 0, 2);
//        gridPane.add(labelRow3, 0, 3);
//
//        // Labels pour les colonnes
//        Label labelColumn1 = new Label("Hidden Layer Size");
//        Label labelColumn2 = new Label("Number of Hidden Layers");
//        Label labelColumn3 = new Label("Learning Rate");
//
//        // Ajout des labels pour les colonnes
//        gridPane.add(labelColumn1, 1, 0);
//        gridPane.add(labelColumn2, 2, 0);
//        gridPane.add(labelColumn3, 3, 0);
//
//        String[][] settings = getSettings();
//
//        for (int i = 0; i < 3; i++) {
//            for (int j = 0; j < 3; j++) {
//                TextField textField = createTextField(settings[i][j+1]);
//                gridPane.add(textField, j + 1, i + 1);
//            }
//        }
//
//        // Centrage du GridPane
//        StackPane modalLayout = new StackPane();
//        Button saveButton = new Button("Save Settings");
//        saveButton.setOnAction(e -> {
//            String[] set = {"F:", "M:", "D:"};
//            for(Node n : gridPane.getChildren()){
//                if(n instanceof TextField){
//                    TextField tmp = (TextField) n;
//                    int rowIndex = GridPane.getRowIndex(tmp);
//                    int colIndex = GridPane.getColumnIndex(tmp);
//                    if(colIndex == 3){
//                        set[rowIndex - 1] += tmp.getText();
//                    }
//                    else{
//                        set[rowIndex - 1] += tmp.getText() + ":";
//                    }
//                }
//            }
//            setSettings(set);
//            save.set(true);
//        });
//
//        Button closeButton = new Button("Close Settings");
//        closeButton.setOnAction(e -> {
//            String[] set = {"F:", "M:", "D:"};
//            for(Node n : gridPane.getChildren()){
//                if(n instanceof TextField){
//                    TextField tmp = (TextField) n;
//                    int rowIndex = GridPane.getRowIndex(tmp);
//                    int colIndex = GridPane.getColumnIndex(tmp);
//                    if(colIndex == 3){
//                        set[rowIndex - 1] += tmp.getText();
//                    }
//                    else{
//                        set[rowIndex - 1] += tmp.getText() + ":";
//                    }
//                }
//            }
//            String[] prev = {"F:" + settings[0][1] + ":" + settings[0][2] + ":" + settings[0][3], "M:" + settings[1][1] + ":" + settings[1][2] + ":" + settings[1][3], "D:" + settings[2][1] + ":" + settings[2][2] + ":" + settings[2][3]};
//            if(Arrays.equals(set, prev)){
//                modalStage.close();
//            }
//            else if (!save.get()){
//                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//                alert.setTitle("Quitter sans Sauvegarder");
//                alert.setHeaderText("Confirmation nécessaire");
//                alert.setContentText("Êtes-vous sûr de vouloir quiter sans sauvegarder ?");
//
//                alert.showAndWait().ifPresent(response -> {
//                    if(response == ButtonType.OK){
//                        modalStage.close();
//                    }
//                });
//            }
//            else{
//                modalStage.close();
//            }
//        });
//
//        modalLayout.getChildren().addAll(gridPane);
//        StackPane.setAlignment(gridPane, javafx.geometry.Pos.CENTER);
//        VBox vBox = new VBox();
//        vBox.getChildren().addAll(modalLayout, closeButton, saveButton); // Ajout du GridPane et du bouton "Close Modal"
//        vBox.setPadding(new Insets(10));
//        vBox.setSpacing(10);
//        vBox.setAlignment(javafx.geometry.Pos.BOTTOM_CENTER);
//
//        Scene modalScene = new Scene(vBox, 600, 200);
//        modalStage.setScene(modalScene);
//        modalStage.setResizable(false);
//        modalStage.showAndWait();
//    }


    public String[][] getSettings() {
        try {
            FileReader settingFile = new FileReader("src/main/resources/com/project/morpion/ai/config.txt");
            BufferedReader reader = new BufferedReader(settingFile);
            String line;
            String[][] setting = new String[4][4];
            int i =0;
            while ((line = reader.readLine()) != null) {
                setting[i] = line.split(":");
                i++;
            }
            return setting;
        } catch (IOException ignored) {
        }

        return null;
    }

    public void setSettings(String[] settings){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/com/project/morpion/ai/config.txt"))) {
            for(int i =0; i < 3 ; i++){
                writer.write(settings[i]);
                writer.newLine();
            }
        } catch (IOException ignored) { }
    }

    private TextField createTextField(String setText){
        TextField textField = new TextField(setText);

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getText();
            if (Pattern.matches("[0-9]*", text)) {
                return change;
            }
            return null;
        };

        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        textField.setTextFormatter(textFormatter);

        return textField;
    }

    public void openLearning(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("view/learn.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stageLearn = new Stage();
        stageLearn.setScene(scene);
        stageLearn.show();
    }

    public void openModels(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("view/model-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stageModel = new Stage();
        stageModel.setScene(scene);
        stageModel.show();
    }

    public void openSettings(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("view/setting-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stageSettings = new Stage();
        stageSettings.setResizable(false);
        stageSettings.setScene(scene);
        stageSettings.show();
    }
}