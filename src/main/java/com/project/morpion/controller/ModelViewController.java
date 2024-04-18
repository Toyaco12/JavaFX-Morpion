package com.project.morpion.controller;

import com.project.morpion.model.ItemModel;
import com.project.morpion.model.SupressCell;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ModelViewController {
    public Button closeButton;
    public BorderPane mainPane;
    public Label title;
    public Label subtitle;
    @FXML
    private ListView<ItemModel> mediumListView;
    private String[] settings = new String[4];
    private Cursor cursor;
    private Scene scene;
    private String language = "English";
    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void initialization(){
        if(isFrench()){
            title.setText("Gestion des modèles");
            subtitle.setText("Modèles Crées");
            closeButton.setText("Fermer");
            deleteAllButton.setText("Tout Supprimer");
            language = "French";
        }
        getCursor();
        getTheme();
        mediumListView.setCellFactory(param -> new SupressCell());
        loadModels("src/main/resources/com/project/morpion/ai/models", mediumListView);

    }

    private void loadModels(String directoryPath, ListView<ItemModel> listView) {
        File dir = new File(directoryPath);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".srl"));
        if (files != null) {
            for (File file : files) {
                ItemModel item = new ItemModel(file.getAbsolutePath());
                listView.getItems().add(item);
            }
        }
    }

    @FXML
    private Button deleteAllButton;

    public void deleteAllModels(ActionEvent actionEvent) {
        // Créer une alerte
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        if(Objects.equals(language, "French")) {
            confirmAlert.setTitle("Confirmation de suppression");
            confirmAlert.setHeaderText("Suppression de tous les modèles");
            confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer tous les modèles ? Cette action est irréversible.");
        }
        else{
            confirmAlert.setTitle("Deletion Confirmation");
            confirmAlert.setHeaderText("Deletion of all templates");
            confirmAlert.setContentText("Are you sure you want to delete all templates? This action is irreversible.");
            confirmAlert.getButtonTypes().stream()
                    .filter(buttonType -> buttonType.getButtonData().isCancelButton())
                    .findFirst()
                    .ifPresent(buttonType -> ((Button) confirmAlert.getDialogPane().lookupButton(buttonType)).setText("Cancel"));
        }
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteModelsInListView(mediumListView);
        }
    }

    private void deleteModelsInListView(ListView<ItemModel> listView) {
        List<ItemModel> models = new ArrayList<>(listView.getItems());
        for (ItemModel model : models) {
            File file = new File(model.getFullPath());
            if (file.delete()) {
                listView.getItems().remove(model);
            } else {
                System.err.println("Failed to delete the model: " + model.getFullPath());
            }
        }
    }


    public void closeModal(ActionEvent actionEvent) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
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
                    mainPane.setStyle("-fx-background-color: white;");
                }
                else{
                    mainPane.setStyle("-fx-background-color: rgb(20,20,20);");
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
