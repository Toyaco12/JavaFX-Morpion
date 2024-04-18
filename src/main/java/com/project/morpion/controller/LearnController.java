package com.project.morpion.controller;

import com.project.morpion.App;
import com.project.morpion.model.AudioPlayer;
import com.project.morpion.model.ModelUpdate;
import com.project.morpion.model.ai.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;


public class LearnController {
    public Label title;
    public Button sound;
    public ImageView soundImage;
    private ModelUpdate updateModel;
    private ModelUpdate notUpdateModel;
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
    public Button cancelButton;
    public Label diff;
    @FXML
    private Task<Void> learningTask;
    private String language = "English";
    AudioPlayer audioPlayer;
    double volume;
    private boolean isSound = true;

    private Stage PreviousStage;

    public void setUpdateListener(ModelUpdate updateModel) {
        this.updateModel = updateModel;
    }
    public void setNotUpdateListener(ModelUpdate notUpdateModel) {
        this.notUpdateModel = notUpdateModel;
    }


    public void getPreviousStage(Stage stage) {
        this.PreviousStage = stage;
    }


    public void trainingCompleted() {
        if (updateModel != null) {
            updateModel.onModelUpdated();
        }
    }
    public void trainingNotCompleted() {
        if (updateModel != null) {
            notUpdateModel.onModelNotUpdated();
        }
    }


    @FXML
    private void closeWindow(ActionEvent event) {
        // Récupère la scène actuelle et la ferme
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
    @FXML
    public void cancelTask(ActionEvent actionEvent) {
        if (learningTask != null){
            learningTask.cancel();
        }
    }
    private String difficulty;

    public void setDifficulty(String difficulty){
        this.difficulty = difficulty;
        String textDiff = "";
        switch (this.difficulty){
            case "F":
                if(Objects.equals(language, "English"))
                    textDiff = " Easy";
                else
                    textDiff = " Facile";
                break;
            case "M":
                if(Objects.equals(language, "English"))
                    textDiff = " Medium";
                else
                    textDiff = " Moyen";
                break;
            case "D":
                if(Objects.equals(language, "English"))
                    textDiff = " Hard";
                else
                    textDiff = " Difficile";
                break;
            case "C1":
                if(Objects.equals(language, "English"))
                    textDiff = " Custom 1";
                else
                    textDiff = " Personnalisé 1";
                break;
            case "C2":
                if(Objects.equals(language, "English"))
                    textDiff = " Custom 2";
                else
                    textDiff = " Personnalisé 2";
                break;
            case "C3":
                if(Objects.equals(language, "English"))
                    textDiff = " Custom 3";
                else
                    textDiff = " Personnalisé 3";
                break;
        }

        this.diff.setText(diff.getText() + textDiff);
    }
    public void initialize(){
        audioPlayer = new AudioPlayer();
        getVolume();
    }

    @FXML
    public void processStart() {

        startbutton.setVisible(false);
        startbutton.setManaged(false);
        audioPlayer.playWaitingMusic();
        audioPlayer.changeVolume(volume);

        //Initialisation des variables pour la task
        ConfigFileLoader cfl = new ConfigFileLoader();
        cfl.loadConfigFile("src/main/resources/com/project/morpion/ai/config.txt");
        Config config = cfl.get(difficulty);
        System.out.println("Test.main() : "+config);
        HashMap<Integer, Coup> mapTrain = Test.setup();
        double epochs = 10000;
        int size =9;
        int h = config.hiddenLayerSize;
        double lr = config.learningRate;
        int l = config.numberOfhiddenLayers;
        boolean verbose = true;

        //TASK
        learningTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    if ( verbose ) {
                        System.out.println();
                        System.out.println("START TRAINING ...");
                        System.out.println();
                    }

                    int[] layers = new int[l+2];
                    layers[0] = size ;
                    for (int i = 0; i < l; i++) {
                        layers[i+1] = h ;
                    }
                    layers[layers.length-1] = size ;
                    //
                    double error = 0.0 ;
                    double actualTrainError = error;
                    double pastTrainError = Double.MAX_VALUE;
                    MultiLayerPerceptron net = new MultiLayerPerceptron(layers, lr, new SigmoidalTransferFunction());

                    if ( verbose ) {
                        System.out.println("---");
                        System.out.println("Load data ...");
                        System.out.println("---");
                    }
                    //TRAINING ...
                    for(int i = 0; i < epochs; i++){

                        if (isCancelled()) {
                            if(Objects.equals(language, "English")){
                                updateMessage("Last error was : "+error/(double)i);
                            }
                            else{
                                updateMessage("La dernière erreur était : "+error/(double)i);
                            }
                            return null;
                        }

                        Coup c = null ;
                        while ( c == null )
                            c = mapTrain.get((int)(Math.round(Math.random() * mapTrain.size())));

                        error += net.backPropagate(c.in, c.out);

                        if ( i % 1000 == 0 && verbose) {
                            String part1 = "Error at step ";
                            if(!Objects.equals(language, "English")){
                                part1 = "Erreur à l'étape ";
                            }
                            actualTrainError = (error/(double)i);
                            if(i==0) actualTrainError = Double.MAX_VALUE;
                            System.out.println(actualTrainError);
                            if(actualTrainError < pastTrainError+0.001 && actualTrainError > pastTrainError-0.001){
                                if(Objects.equals(language, "English")){
                                    updateMessage("Error at step "+i+" is constant");;
                                }
                                else{
                                    updateMessage("L'erreur à l'étape "+i+" est constante");
                                }
                            }
                            else if(actualTrainError > pastTrainError){
                                if(Objects.equals(language, "English")){
                                    updateMessage("Error at step "+i+" is increasing");;
                                }
                                else{
                                    updateMessage("L'erreur à l'étape "+i+" augmente");
                                }                            }
                            else if (actualTrainError < pastTrainError) {
                                if(Objects.equals(language, "English")){
                                    updateMessage("Error at step "+i+" is decreasing");;
                                }
                                else{
                                    updateMessage("L'erreur à l'étape "+i+" diminue");
                                }
                            }
                            else {

                            }
                            pastTrainError = actualTrainError;
                        }
                        updateProgress(i, epochs);
                    }
                    if ( verbose ){
                        updateMessage("Final error is "+ (error/epochs));
                        if(Objects.equals(language, "English")){
                            updateMessage("Final error is"+(error/epochs));;
                        }
                        else{
                            updateMessage("L'erreur final est "+(error/epochs));
                        }
                        System.out.println(error/epochs);
                    }

                    MultiLayerPerceptron.saveModel(net,difficulty);
                }
                catch (Exception e) {
                    System.out.println("Test.learn()");
                    e.printStackTrace();
                    System.exit(-1);
                }

                return null;
            }
        };
        cancelButton.visibleProperty().bind(learningTask.runningProperty());
        cancelButton.managedProperty().bind(learningTask.runningProperty());

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
            trainingCompleted();
            audioPlayer.stopMusic();
            closeButton.setVisible(true);
            closeButton.setManaged(true);

            completionField.setVisible(true);
            completionField.setManaged(true);
            completionField.setText("Learning completed");
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.close();
        });
        learningTask.setOnCancelled(event -> {
            trainingNotCompleted();
            audioPlayer.stopMusic();
            closeButton.setVisible(true);
            closeButton.setManaged(true);
            completionField.setVisible(true);
            completionField.setManaged(true);
            completionField.setText("Learning cancelled");

        });

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

    public void setLanguage(){
        if(isFrench()){
            language = "French";
            title.setText("Entrainer un Modèle");
            diff.setText("Difficulté : ");
            closeButton.setText("Fermer");
        }

    }
    public void getVolume(){
        try{
            FileReader fileReader = new FileReader("src/main/resources/com/project/morpion/settings.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            while(line != null) {
                if (line.charAt(0) == 'V') {
                    String d = line.substring(line.lastIndexOf(":") + 1);
                    volume = Double.parseDouble(d);
                    return;
                }
                line = bufferedReader.readLine();
            }
        }catch (IOException e){}
    }
    public void changeMusic(){
        if(isSound){
            soundImage.setImage(new Image("file:src/main/resources/com/project/morpion/image/noSound.png"));
            audioPlayer.pauseMusic();
            isSound = false;
        }
        else{
            soundImage.setImage(new Image("file:src/main/resources/com/project/morpion/image/sound.png"));
            audioPlayer.playMusic();
            isSound = true;
        }
    }


    }
