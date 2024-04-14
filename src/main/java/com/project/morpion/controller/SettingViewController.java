package com.project.morpion.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.*;

import java.io.*;
import java.util.ArrayList;
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
    public VBox mainVbox;
    public TextField createTextField;
    public Button createButton;
    public HBox createHbox;
    private boolean save = false;
    private ToggleGroup difficulty;
    private Scene scene;
    private Cursor cursor;
    private String[] optionnalLevel = null;
    private String[][] initialSettings;
    private String[] levelName;


    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void initialization() {
        getOptionnalLevel();
        getCursor();
        getTheme();
        difficulty = new ToggleGroup();
        easyRadioButton.setToggleGroup(difficulty);
        mediumRadioButton.setToggleGroup(difficulty);
        hardRadioButton.setToggleGroup(difficulty);
        //easyRadioButton.setSelected(true);
        initialSettings = getSettings();

        for(int i = 0; i < initialSettings.length; i++){
            if(i >= 3){
                Label l = new Label(optionnalLevel[i%3]);
                l.setAlignment(Pos.CENTER);
                l.setPrefHeight(69.0);
                l.setPrefWidth(200);
                if(i%2 != 1) l.setStyle("-fx-text-fill: fc6c00;");
                else l.setStyle("-fx-text-fill: rgb(1, 191, 200);");
                settingsPane.add(l, 0, i+1);
                RadioButton radioButton = new RadioButton();
                radioButton.setUserData("C" + (i%3+1));
                System.out.println("C"+(i%3+1));
                radioButton.setToggleGroup(difficulty);
                settingsPane.add(radioButton, 4, i+1);

            }
            for(int j = 0; j < 3; j++){
                TextField textField;
                if(j == 2){
                    textField = createDecimalTextField(initialSettings[i][j]);
                }
                else{
                    textField = createTextField(initialSettings[i][j]);
                }

                settingsPane.add(textField, j+1, i+1);
            }
        }
    }
    public void closeModal(ActionEvent actionEvent) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        String[][] settings = getSettings();
        String[] set = {"F:", "M:", "D:"};
        String[][] current = new String[settings.length][3];
        int i = 0, j = 0;
        for(Node n : settingsPane.getChildren()){
            if(n instanceof TextField){
                TextField tmp = (TextField) n;
//                int rowIndex = GridPane.getRowIndex(tmp);
//                int colIndex = GridPane.getColumnIndex(tmp);
//                if(colIndex == 3){
//                    set[rowIndex - 1] += tmp.getText();
//                }
//                else{
//                    set[rowIndex - 1] += tmp.getText() + ":";
//                }
                if(j != 2){
                    current[i][j] = tmp.getText();
                    j++;
                }
                else{
                    current[i][j] = tmp.getText();
                    i++;
                    j = 0;
                }
            }
        }
        //String[] prev = {"F:" + settings[0][1] + ":" + settings[0][2] + ":" + settings[0][3], "M:" + settings[1][1] + ":" + settings[1][2] + ":" + settings[1][3], "D:" + settings[2][1] + ":" + settings[2][2] + ":" + settings[2][3]};
//        if(Arrays.equals(set, prev)){
//            stage.close();
//        }
        if(areMatricesEquals(settings, current)){
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
        ArrayList<String> set = new ArrayList<>();
        int i = 0;
        int j = 0;
        String ligne = "";
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
                if(j != 2){
                    ligne += tmp.getText() + ":";
                    j++;
                }
                else{
                    ligne += tmp.getText();
                    set.add(ligne);
                    ligne = "";
                    j = 0;
                }
            }
        }
        RadioButton tmp = (RadioButton) difficulty.getSelectedToggle();
        String diff = (String) tmp.getUserData();
        if(!error){
            errorLabel.setVisible(false);
            setSettings(set, diff);
            save = true;
            Stage stage = (Stage) saveButton.getScene().getWindow();
            stage.close();
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
            File settingFile = new File("src/main/resources/com/project/morpion/ai/config.txt");
            BufferedReader reader = new BufferedReader(new FileReader(settingFile));
            int nbRow = countLines(settingFile);
            //System.out.println(nbRow);
            reader.close();
            reader = new BufferedReader(new FileReader(settingFile));
            String line;
            String[][] setting = new String[nbRow-1][3];
            int i =0;
            while ((line = reader.readLine()) != null) {
                //System.out.println("a");
                if(line.charAt(0) != 'Z'){
                    String[] parts = line.split(":");
//                    for(String a : parts)
//                        System.out.println(a);
                    String[] data = new String[parts.length-1];
                    System.arraycopy(parts, 1, data, 0, data.length);
                    setting[i] = data;
                    i++;
                }
                else{
                    String d = line.substring(line.lastIndexOf(":") + 1);
                    System.out.println(d);
                    difficulty.getToggles().forEach(toggle -> {
                        RadioButton radioButton = (RadioButton) toggle;
                        System.out.println(radioButton.getUserData());
                        if(radioButton.getUserData().equals(d)){

                            difficulty.selectToggle(radioButton);
                        }
                    });
//                    if(d.matches("E")){
//                        easyRadioButton.setSelected(true);
//                        mediumRadioButton.setSelected(false);
//                        hardRadioButton.setSelected(false);
//                    }
//                    else if(d.matches("M")){
//                        easyRadioButton.setSelected(false);
//                        mediumRadioButton.setSelected(true);
//                        hardRadioButton.setSelected(false);
//                    }
//                    else{
//                        easyRadioButton.setSelected(false);
//                        mediumRadioButton.setSelected(false);
//                        hardRadioButton.setSelected(true);
//                    }
                }
            }
//            for(int ii = 0; ii < setting.length; ii++){
//                for(int jj = 0; jj < setting[ii].length; jj++){
//                    System.out.println(setting[ii][jj]);
//                }
//            }
            reader.close();
            return setting;
        } catch (IOException ignored) {}
        return null;
    }

    private int countLines(File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            int lines = 0;
            while (reader.readLine() != null) lines++;
            reader.close();
            return lines;
        } catch (IOException ignored) {}
        return 0;
    }

    public void setSettings(ArrayList<String> settings, String diff){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/com/project/morpion/ai/config.txt"))) {
            int i = 0;
            for(String tmp : settings){
                String lvl = "";
                if(i == 0) lvl = "F";
                else if(i == 1) lvl = "M";
                else if(i == 2) lvl = "D";
                else if(i == 3) lvl = "C1";
                else if(i == 4) lvl = "C2";
                else if(i == 5) lvl = "C3";
                writer.write(lvl + ":" +tmp);
                writer.newLine();
                i++;
            }
            writer.write("Z:"+diff);
            writer.close();
        } catch (IOException ignored) { }
    }

    public void selectDifficulty(ActionEvent actionEvent) {
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
                    mainVbox.setStyle("-fx-background-color: white;");
                }
                else{
                    mainVbox.setStyle("-fx-background-color: rgb(20,20,20);");
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

    private void getOptionnalLevel() {
        try {
            FileReader fileReader = new FileReader("src/main/resources/com/project/morpion/settings.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            while (line.charAt(0) != 'S')
                line = bufferedReader.readLine();
            if (line.charAt(0) == 'S') {
                String[] lvl = line.split(":");
                if (lvl.length > 1) {
                    optionnalLevel = new String[lvl.length - 1];
                    System.arraycopy(lvl, 1, optionnalLevel, 0, lvl.length - 1);
                }
            }
            if(optionnalLevel.length == 3){
                createHbox.setVisible(false);
            }
        }
        catch (IOException ignored){}
    }

    private boolean areMatricesEquals(String[][] a, String[][] b){
        if(a.length != b.length || a[0].length != b[0].length){
            return false;
        }
        for(int i = 0; i < a.length; i++){
            for(int j = 0; j < a[0].length; j++){
                if(!a[i][j].equals(b[i][j])){
                    return false;
                }
            }
        }
        return true;
    }

    public void addLevel(ActionEvent actionEvent) {
        if(!createTextField.getText().isEmpty()){
            String newLvl = createTextField.getText();
            if(optionnalLevel == null){
                Label la = new Label(newLvl);
                la.setAlignment(Pos.CENTER);
                la.setPrefHeight(69.0);
                la.setPrefWidth(200);
                la.setStyle("-fx-text-fill: rgb(1, 191, 200);");
                settingsPane.add(la, 0, 4);
                for(int i = 0; i < 3; i++){
                    if(i==2){
                        settingsPane.add(createDecimalTextField("1"), i+1, 4);
                    }
                    else{
                        settingsPane.add(createTextField("1"), i+1, 4);
                    }
                }
                writeOptionnalLevel("S:" + newLvl);
            }
            else{
                int l = optionnalLevel.length;
                Label la = new Label(newLvl);
                la.setAlignment(Pos.CENTER);
                la.setPrefHeight(69.0);
                la.setPrefWidth(200);
                if(l%2 != 1) la.setStyle("-fx-text-fill: fc6c00;");
                else la.setStyle("-fx-text-fill: rgb(1, 191, 200);");
                settingsPane.add(la, 0, l+4);
                for(int i = 0; i < 3; i++){
                    if(i==2){
                        settingsPane.add(createDecimalTextField("1"), i+1, l+4);
                    }
                    else{
                        settingsPane.add(createTextField("1"), i+1, l+4);
                    }
                }
                String lvl = "S:";
                for (String i : optionnalLevel){
                    lvl += i + ":";
                }
                writeOptionnalLevel(lvl + newLvl);
            }
            getOptionnalLevel();
        }
    }

    private void writeOptionnalLevel(String lvl){
        try{
            File file = new File("src/main/resources/com/project/morpion/settings.txt");
            //File tmp = new File("src/main/resources/com/project/morpion/tmp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(file));
            //BufferedWriter writer = new BufferedWriter(new FileWriter(tmp));
            String[] tmp = new String[5];
            String line;
            int i = 0;
            while ((line = reader.readLine()) != null) {
                // Vérifier si la ligne commence par "S"
                if (line.startsWith("S")) {
                    tmp[i] = lvl;
                } else {
                    tmp[i] = line;
                }
                i++;
            }
            reader.close();

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for(String j : tmp){
                writer.write(j);
                writer.newLine();
            }
            writer.close();
        }
        catch (IOException ignored){}
    }
}
