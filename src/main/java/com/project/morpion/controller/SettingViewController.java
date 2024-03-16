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
    public Label errorLabel;
    public RadioButton easyRadioButton;
    public RadioButton mediumRadioButton;
    public RadioButton hardRadioButton;
    private boolean save = false;
    private ToggleGroup difficulty;


    @FXML
    public void initialize() {
        difficulty = new ToggleGroup();
        easyRadioButton.setToggleGroup(difficulty);
        mediumRadioButton.setToggleGroup(difficulty);
        hardRadioButton.setToggleGroup(difficulty);
        easyRadioButton.setSelected(true);
        String[][] settings = getSettings();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                TextField textField;
                if(j == 2){
                    textField = createDecimalTextField(settings[i][j+1]);
                }
                else{
                    textField = createTextField(settings[i][j+1]);
                }

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
        boolean error = false;
        String[] set = {"F:", "M:", "D:"};
        for(Node n : settingsPane.getChildren()){
            if(n instanceof TextField){
                TextField tmp = (TextField) n;
                int rowIndex = GridPane.getRowIndex(tmp);
                int colIndex = GridPane.getColumnIndex(tmp);
                if(tmp.getText().isEmpty()){
                    errorLabel.setVisible(true);
                    errorLabel.setText("Champs Vides");
                    tmp.setStyle("-fx-text-box-border: red; -fx-focus-color: red;");
                    error = true;
                }
                else if(Double.parseDouble(tmp.getText()) > 150){
                    errorLabel.setVisible(true);
                    errorLabel.setText("Maximum : 150");
                    tmp.setStyle("-fx-text-box-border: red; -fx-focus-color: red;");
                    error = true;
                }
                else{
                    tmp.setStyle("-fx-text-box-border: transparent;");
                }
                if(colIndex == 3){
                    set[rowIndex - 1] += tmp.getText();
                }
                else{
                    set[rowIndex - 1] += tmp.getText() + ":";
                }
            }
        }
        RadioButton tmp = (RadioButton) difficulty.getSelectedToggle();
        String diff = (String) tmp.getUserData();
        if(!error){
            errorLabel.setVisible(false);
            setSettings(set, diff);
            save = true;
        }

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

    private TextField createDecimalTextField(String setText){
        TextField textField = new TextField(setText);

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getControlNewText();
            if (Pattern.matches("\\d*(\\.\\d*)?", text)) {
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
                if(line.charAt(0) != 'Z'){
                    setting[i] = line.split(":");
                    i++;
                }
                else{
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
            return setting;
        } catch (IOException ignored) {
        }

        return null;
    }

    public void setSettings(String[] settings, String diff){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/com/project/morpion/ai/config.txt"))) {
            for(int i =0; i < 3 ; i++){
                writer.write(settings[i]);
                writer.newLine();
            }
            writer.write("Z:"+diff);
        } catch (IOException ignored) { }
    }

    public void selectDifficulty(ActionEvent actionEvent) {
    }
}
