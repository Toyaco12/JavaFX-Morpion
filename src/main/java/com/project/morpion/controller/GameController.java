package com.project.morpion.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class GameController {

    public GridPane morpionGrille;


    @FXML
    public void initialize() {
        setClickListener();
        StackPane s = (StackPane) morpionGrille.getChildren().get(5);
        ImageView i = (ImageView) s.getChildren().getFirst();
        Image ii = new Image("file:src/main/resources/com/project/morpion/ai/images/TicTacToe/cross.png");
        i.setImage(ii);
    }


    private void setClickListener(){
        morpionGrille.getChildren().forEach(node -> {
            StackPane stackPane = (StackPane) node;
            ImageView imageView = (ImageView) stackPane.getChildren().getFirst();
            //int position = GridPane.getRowIndex(stackPane) * morpionGrille.getColumnCount() + GridPane.getColumnIndex(stackPane);

            imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    int position =
                            GridPane.getRowIndex(stackPane) * morpionGrille.getColumnCount() + GridPane.getColumnIndex(stackPane);
                    System.out.println(position);
                    Image i = new Image("file:src/main/resources/com/project/morpion/ai/images/TicTacToe/cross.png");
                    imageView.setImage(i);
                    imageView.setOnMouseClicked(null);
                }
            });
        });
    }

    public void returnHome(ActionEvent actionEvent) {
    }
}
