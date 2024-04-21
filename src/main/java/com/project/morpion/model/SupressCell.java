package com.project.morpion.model;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

// Classe personnalisée pour un élément de liste qui gère l'affichage et la suppression de modèles IA dans une interface utilisateur.

public class SupressCell extends ListCell<ItemModel> {
    HBox hbox = new HBox();
    Label label = new Label("(vide)");
    Pane pane = new Pane();
    Button deleteButton = new Button("X");

    // Constructeur qui prépare l'interface de la cellule, incluant un bouton de suppression qui demande confirmation avant de supprimer le fichier associé au modèle.

    public SupressCell() {
        super();
        deleteButton.setFocusTraversable(false);
        hbox.getChildren().addAll(label, pane, deleteButton);
        HBox.setHgrow(pane, Priority.ALWAYS);

        deleteButton.setOnAction(event -> {
            ItemModel item = getItem();
            String text = "Do you really want to delete ";
            if(isFrench())
                text = "Voulez-vous vraiment supprimer ";
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, text +item.getName()+" ?", ButtonType.YES,ButtonType.NO);
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

    // Met à jour l'affichage de la cellule. Si l'élément n'est pas vide et existe, affiche le nom du modèle et le bouton de suppression. Sinon, cache l'affichage.

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

    // Vérifie si la langue configurée est le français en lisant les paramètres du fichier de configuration.

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
