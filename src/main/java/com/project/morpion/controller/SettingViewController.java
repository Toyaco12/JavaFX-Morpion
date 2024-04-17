package com.project.morpion.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class SettingViewController {
    public GridPane settingsPane;
    public Button saveButton;
    public Button closeButton;
    public Label errorLabel;
    public VBox mainVbox;
    public TextField createTextField;
    public Button createButton;
    public HBox createHbox;
    public Label createLabel;
    private boolean save = false;
    private ToggleGroup difficulty;
    private Scene scene;
    private Cursor cursor;
    private String[] optionnalLevel = null;
    private String[][] initialSettings;
    private String radio;
    private String language;


    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void initialization() {
        if(isFrench()){
            language = "Français";
            createLabel.setText("Créer un niveau");
            createButton.setText("Créer");
            saveButton.setText("Sauvegarder");
            closeButton.setText("Fermer");
        }
        else language = "English";
        getOptionnalLevel();
        getCursor();
        getTheme();
        difficulty = new ToggleGroup();
        setLevel();
        getSelectedLevel();

        TextFormatter<String> textFormatter = new TextFormatter<>(change -> {
            if(change.getControlNewText().length() <=10){
                return change;
            }
            else{
                return null;
            }
        });
        createTextField.setTextFormatter(textFormatter);
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

        if(areMatricesEquals(settings, current)){
            stage.close();
        }
        else if (!save){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            if(Objects.equals(language, "Français")) {
                alert.setTitle("Quitter sans Sauvegarder");
                alert.setHeaderText("Confirmation nécessaire");
                alert.setContentText("Êtes-vous sûr de vouloir quiter sans sauvegarder ?");
            }
            else{
                alert.setTitle("Quit Without Saving");
                alert.setHeaderText("Confirmation Required");
                alert.setContentText("Are you sure you want to exit without saving ?");
            }
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
                    if(Objects.equals(language, "Français"))
                        errorLabel.setText("Champs Vides");
                    else
                        errorLabel.setText("Empty Fields");
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
                if(difficulty.getSelectedToggle() == null){
                    difficulty.getToggles().getFirst().setSelected(true);
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
        RadioButton selected = (RadioButton) difficulty.getSelectedToggle();
        String diff = (String) selected.getUserData();
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
            reader.close();
            reader = new BufferedReader(new FileReader(settingFile));
            String line;
            String[][] setting = new String[nbRow-1][3];
            int i =0;
            while ((line = reader.readLine()) != null) {
                if(line.charAt(0) != 'Z'){
                    String[] parts = line.split(":");
                    String[] data = new String[parts.length-1];
                    System.arraycopy(parts, 1, data, 0, data.length);
                    setting[i] = data;
                    i++;
                }
                else{
                    radio = line.substring(line.lastIndexOf(":") + 1);
                }
            }
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
            if(optionnalLevel == null || optionnalLevel.length != 3){
                createHbox.setVisible(true);
            }
            else {
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
                writeOptionnalLevel("S:" + newLvl, 1);
            }
            else{
                int l = optionnalLevel.length;
                String lvl = "S:";
                for (String i : optionnalLevel){
                    lvl += i + ":";
                }
                writeOptionnalLevel(lvl + newLvl, l+1);
            }
            getOptionnalLevel();
            setLevel();
            createTextField.clear();
        }
    }

    private void writeOptionnalLevel(String lvl, int index){
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


            File config = new File("src/main/resources/com/project/morpion/ai/config.txt");
            BufferedReader r = new BufferedReader(new FileReader(config));
            String l;
            String[] temp = new String[7];
            int j = 0;
            while ((l = r.readLine()) != null){
                if(l.startsWith("Z")){
                    temp[j] = "C" + index + ":1:1:1";
                    j++;
                    temp[j] = l;
                }
                else{
                    temp[j] = l;
                }
                j++;
            }
            r.close();

            BufferedWriter w = new BufferedWriter(new FileWriter(config));
            for(String a : temp){
                if(a != null){
                    w.write(a);
                    w.newLine();
                }
            }
            w.close();
        }
        catch (IOException ignored){}
    }

    private void deletelevel(int lvl){
        String deleteLvl = optionnalLevel[lvl];
        String option = "S";
        for(String i : optionnalLevel){
            if(!Objects.equals(i, deleteLvl)){
                option += ":" + i;
            }
        }
        try{
            File file = new File("src/main/resources/com/project/morpion/settings.txt");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String[] tmp = new String[5];
            String line;
            int i = 0;
            while ((line = reader.readLine()) != null) {
                // Vérifier si la ligne commence par "S"
                if (line.startsWith("S")) {
                    tmp[i] = option;
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


            File config = new File("src/main/resources/com/project/morpion/ai/config.txt");
            BufferedReader r = new BufferedReader(new FileReader(config));
            String l;
            String[] temp = new String[7];
            int j = 0;
            while ((l = r.readLine()) != null){
                if(l.startsWith("C")){
                    if(Character.getNumericValue(l.charAt(1)) < lvl + 1){
                        temp[j] = l;
                    }
                    else if(Character.getNumericValue(l.charAt(1)) > lvl + 1){
                        int a = Character.getNumericValue(l.charAt(1)) - 1;
                        String first = l.substring(0,1);
                        String reste = l.substring(2);
                        temp[j] = first + a + reste;
                    }
                }
                else{
                    temp[j] = l;
                }
                j++;
            }
            r.close();
            BufferedWriter w = new BufferedWriter(new FileWriter(config));
            for(String a : temp){
                if(a != null){
                    w.write(a);
                    w.newLine();
                }
            }
            w.close();
            if(optionnalLevel.length == 1){
                optionnalLevel = null;
            }
            getOptionnalLevel();
            setLevel();
        }
        catch (IOException ignored){}

    }

    private void setLevel(){
        settingsPane.getChildren().clear();

        initialSettings = getSettings();
        for(int i = 0; i < 4; i++){
            Label label = new Label();
            if(Objects.equals(language, "Français")) {
                if (i == 0) label.setText("Taille de la couche cachée");
                else if (i == 1) label.setText("Nombre de couche cachée");
                else if (i == 2) label.setText("Taux d'apprentissage");
                else label.setText("Défaut");
            }
            else{
                if (i == 0) label.setText("Hidden Layer Size");
                else if (i == 1) label.setText("Number of Hidden Layers");
                else if (i == 2) label.setText("Learning Rate");
                else label.setText("Default");
            }
            label.setAlignment(Pos.CENTER);
            if(i != 3){
                label.setPrefHeight(69.0);
                label.setPrefWidth(200);
            }
            if(i%2 != 1) label.setStyle("-fx-text-fill: fc6c00;");
            else label.setStyle("-fx-text-fill: rgb(1, 191, 200);");
            settingsPane.add(label, i+1, 0);
            if(i > 0){
                RadioButton r = new RadioButton();
                r.setToggleGroup(difficulty);
                //settingsPane.add(r, 4, i);
                Label label1 = new Label();
                if(i == 1){
                    if(Objects.equals(language, "Français"))
                        label1.setText("FACILE");
                    else
                        label1.setText("EASY");
                    r.setUserData("F");
                }
                else if(i == 2){
                    if(Objects.equals(language, "Français"))
                        label1.setText("MOYEN");
                    else
                        label1.setText("MEDIUM");
                    r.setUserData("M");
                }
                else{
                    if(Objects.equals(language, "Français"))
                        label1.setText("DIFICILE");
                    else
                        label1.setText("HARD");
                    r.setUserData("D");
                }
                label1.setAlignment(Pos.CENTER);
                label1.setPrefHeight(69.0);
                label1.setPrefWidth(200);
                if(i%2 != 1) label1.setStyle("-fx-text-fill: fc6c00;");
                else label1.setStyle("-fx-text-fill: rgb(1, 191, 200);");
                settingsPane.add(r, 4, i);
                settingsPane.add(label1, 0, i);
            }
        }

        for(int i = 0; i < initialSettings.length; i++){
            if(i >= 3){
                final int index = i;
                Label l = new Label(optionnalLevel[i%3]);
                l.setAlignment(Pos.CENTER);
                l.setPrefHeight(69.0);
                l.setPrefWidth(200);
                if(i%2 != 1) l.setStyle("-fx-text-fill: rgb(1, 191, 200);");
                else l.setStyle("-fx-text-fill: fc6c00;");
                settingsPane.add(l, 0, i+1);
                RadioButton radioButton = new RadioButton();
                radioButton.setUserData("C" + (i%3+1));
                //System.out.println(radioButton.getUserData());
                radioButton.setToggleGroup(difficulty);
                settingsPane.add(radioButton, 4, i+1);
                Button delete = new Button("X");
                delete.setFocusTraversable(true);
                delete.setOnAction(e -> deletelevel(index%3));
                settingsPane.add(delete, 5, i+1);
            }
            for(int j = 0; j < 4; j++){
                TextField textField = null;

                if(j == 2){
                    textField = createDecimalTextField(initialSettings[i][j]);
                }
                else if(j != 3){
                    textField = createTextField(initialSettings[i][j]);
                }
                if(textField!=null)
                    settingsPane.add(textField, j+1, i+1);
            }
        }
        if(optionnalLevel == null || optionnalLevel.length != 3){
            createHbox.setVisible(true);
        }
        else {
            createHbox.setVisible(false);
        }
    }

    private void getSelectedLevel(){
        for(Toggle toggle : difficulty.getToggles()){
            RadioButton radioButton = (RadioButton) toggle;
            if(radioButton.getUserData().equals(radio)){
                difficulty.selectToggle(radioButton);
            }
        }
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
}
