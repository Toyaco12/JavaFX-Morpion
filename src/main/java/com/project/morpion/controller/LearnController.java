package com.project.morpion.controller;

import com.project.morpion.model.ai.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.HashMap;


public class LearnController {
    @FXML
    public TextField completionField;
    @FXML
    public TextField errorfield;
    @FXML
    public ProgressBar progbar;
    @FXML
    public Button startbutton;
    @FXML
    public Button closeButton;
    @FXML
    public Label progressLabel;

    @FXML
    private void closeWindow(ActionEvent event) {
        // Récupère la scène actuelle et la ferme
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void processStart() {
        startbutton.setVisible(false);
        ConfigFileLoader cfl = new ConfigFileLoader();
        cfl.loadConfigFile("src/main/resources/com/project/morpion/ai/config.txt");
        Config config = cfl.get("F");
        System.out.println("Test.main() : "+config);
        HashMap<Integer, Coup> mapTrain = Test.setup();
        double epochs = 10000;
        int size =9;
        int h = config.hiddenLayerSize;
        double lr = config.learningRate;
        int l = config.numberOfhiddenLayers;
        boolean verbose = true;

        Task<Void> learningTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    if ( verbose ) {
                        System.out.println();
                        System.out.println("START TRAINING ...");
                        System.out.println();
                    }
                    //
                    //			int[] layers = new int[]{ size, 128, 128, size };
                    int[] layers = new int[l+2];
                    layers[0] = size ;
                    for (int i = 0; i < l; i++) {
                        layers[i+1] = h ;
                    }
                    layers[layers.length-1] = size ;
                    //
                    double error = 0.0 ;
                    MultiLayerPerceptron net = new MultiLayerPerceptron(layers, lr, new SigmoidalTransferFunction());

                    if ( verbose ) {
                        System.out.println("---");
                        System.out.println("Load data ...");
                        System.out.println("---");
                    }
                    //TRAINING ...
                    for(int i = 0; i < epochs; i++){

                        Coup c = null ;
                        while ( c == null )
                            c = mapTrain.get((int)(Math.round(Math.random() * mapTrain.size())));

                        error += net.backPropagate(c.in, c.out);

                        if ( i % 1000 == 0 && verbose) updateMessage("Error at step "+i+" is "+ (error/(double)i));
                        updateProgress(i, epochs);
                    }
                    if ( verbose )
                        updateMessage("Final error is "+ (error/epochs));

                    MultiLayerPerceptron.saveModel(net,"F");
                }
                catch (Exception e) {
                    System.out.println("Test.learn()");
                    e.printStackTrace();
                    System.exit(-1);
                }

                return null;
            }
        };

        progbar.progressProperty().bind(learningTask.progressProperty());
        errorfield.textProperty().bind(learningTask.messageProperty());
        progressLabel.textProperty().bind(
                Bindings.createStringBinding(
                        () -> String.format("%.0f%%", progbar.getProgress() * 100),
                        progbar.progressProperty()
                )
        );
        Thread learningThread = new Thread(learningTask);
        learningThread.setDaemon(true);
        learningThread.start();

        learningTask.setOnSucceeded(event -> {
            closeButton.setVisible(true);
            closeButton.setManaged(true);

            completionField.setVisible(true);
            completionField.setManaged(true);
            completionField.setText("Learning complete");
        });
    }
}
