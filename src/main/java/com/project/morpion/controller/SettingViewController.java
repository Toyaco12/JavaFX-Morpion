package com.project.morpion.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.*;
import java.util.Arrays;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class SettingViewController {
    public GridPane settingsPane;
    public Button saveButton;
    public Button closeButton;

    private boolean save = false;


    @FXML
    public void initialize() {
        String[][] settings = getSettings();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                TextField textField = createTextField(settings[i][j+1]);
                settingsPane.add(textField, j + 1, i + 1);
            }
        }
    }
    public void closeModal(ActionEvent actionEvent) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        String[][] settings = getSettings();
        String[] set = {"F:", "M:", "D:"};
        for(Node n : settingsPane.getChildren()){
            if(n instanceof TextField){
                TextField tmp = (TextField) n;
                int rowIndex = GridPane.getRowIndex(tmp);
                int colIndex = GridPane.getColumnIndex(tmp);
                if(colIndex == 3){
                    set[rowIndex - 1] += tmp.getText();
                }
                else{
                    set[rowIndex - 1] += tmp.getText() + ":";
                }
            }
        }
        String[] prev = {"F:" + settings[0][1] + ":" + settings[0][2] + ":" + settings[0][3], "M:" + settings[1][1] + ":" + settings[1][2] + ":" + settings[1][3], "D:" + settings[2][1] + ":" + settings[2][2] + ":" + settings[2][3]};
        if(Arrays.equals(set, prev)){
            stage.close();
        }
        else if (!save){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Quitter sans Sauvegarder");
            alert.setHeaderText("Confirmation nécessaire");
            alert.setContentText("Êtes-vous sûr de vouloir quiter sans sauvegarder ?");

            alert.showAndWait().ifPresent(response -> {
                if(response == ButtonType.OK){
                    stage.close();
                }
            });
        }
        else{
            stage.close();
        }
    }

    public void saveSettings(ActionEvent actionEvent) {
        String[] set = {"F:", "M:", "D:"};
        for(Node n : settingsPane.getChildren()){
            if(n instanceof TextField){
                TextField tmp = (TextField) n;
                int rowIndex = GridPane.getRowIndex(tmp);
                int colIndex = GridPane.getColumnIndex(tmp);
                if(colIndex == 3){
                    set[rowIndex - 1] += tmp.getText();
                }
                else{
                    set[rowIndex - 1] += tmp.getText() + ":";
                }
            }
        }
        setSettings(set);
        save = true;
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
}
