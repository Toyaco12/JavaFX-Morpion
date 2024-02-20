package com.project.morpion.model;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

import java.io.File;

public class SupressCell extends ListCell<ItemModel> {
    HBox hbox = new HBox();
    Label label = new Label("(vide)");
    Pane pane = new Pane();
    Button deleteButton = new Button("X");

    public SupressCell() {
        super();
        deleteButton.setFocusTraversable(false);
        hbox.getChildren().addAll(label, pane, deleteButton);
        HBox.setHgrow(pane, Priority.ALWAYS);

        deleteButton.setOnAction(event -> {
            ItemModel item = getItem();
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Do you really want to delete "+item.getName()+" ?", ButtonType.YES,ButtonType.NO);
            confirm.showAndWait().ifPresent(response -> {
                if(response == ButtonType.YES){
                    File file = new File(item.getFullPath());
                    if(file.delete()) {
                        System.out.println(item.getName() + " supprimé.");
                        Platform.runLater(() -> getListView().getItems().remove(item));
                    } else {
                        System.out.println("Échec de la suppression de " + item.getName());
                    }
                }
            });

        });
    }

    @Override
    protected void updateItem(ItemModel item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        if (empty || item == null) {
            setGraphic(null);
        } else {
            label.setText(item.getName());
            setGraphic(hbox);
        }
    }
}
