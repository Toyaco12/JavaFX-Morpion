package com.project.morpion.controller;

import com.project.morpion.model.ItemSuppr;
import com.project.morpion.model.SupressCell;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.File;

public class ModelViewController {
    public Button closeButton;
    @FXML
    private ListView<ItemSuppr> easyListView;
    @FXML
    private ListView<ItemSuppr> mediumListView;
    @FXML
    private ListView<ItemSuppr> hardListView;

    public void initialize(){
        easyListView.setCellFactory(param -> new SupressCell());
        mediumListView.setCellFactory(param -> new SupressCell());
        hardListView.setCellFactory(param -> new SupressCell());
        loadModels("src/main/resources/com/project/morpion/ai/models/F", easyListView);
        loadModels("src/main/resources/com/project/morpion/ai/models/M", mediumListView);
        loadModels("src/main/resources/com/project/morpion/ai/models/D", hardListView);
    }
    private void loadModels(String directoryPath, ListView<ItemSuppr> listView) {
        File dir = new File(directoryPath);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".srl"));
        if (files != null) {
            for (File file : files) {
                ItemSuppr item = new ItemSuppr(file.getAbsolutePath());
                listView.getItems().add(item);
            }
        }
    }

    public void closeModal(ActionEvent actionEvent) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
