package com.project.morpion.model;

import com.project.morpion.model.ai.Config;
import com.project.morpion.model.ai.ConfigFileLoader;

import java.io.File;

public class ItemModel {
    private String name;
    private String fullPath;

    public ItemModel(String fullPath) {
        this.fullPath = fullPath;
        File Filename = new File(fullPath);
        this.name = Filename.getName();
        ConfigFileLoader cfl = new ConfigFileLoader();
        cfl.loadConfigFile("src/main/resources/com/project/morpion/ai/config.txt");
        Config configF = cfl.get("F");
        Config configM = cfl.get("M");
        Config configD = cfl.get("D");
        Config configC1 = cfl.get("C1");
        Config configC2 = cfl.get("C2");
        Config configC3 = cfl.get("C3");
//        String actualDifficultyF = new File("src/main/resources/com/project/morpion/ai/models/"+"F"+"/model_"+configF.hiddenLayerSize+"_"+configF.learningRate+"_"+configF.numberOfhiddenLayers+".srl").getName();
//        String actualDifficultyD = new File("src/main/resources/com/project/morpion/ai/models/"+"D"+"/model_"+configD.hiddenLayerSize+"_"+configD.learningRate+"_"+configD.numberOfhiddenLayers+".srl").getName();
//        String actualDifficultyM = new File("src/main/resources/com/project/morpion/ai/models/"+"M"+"/model_"+configM.hiddenLayerSize+"_"+configM.learningRate+"_"+configM.numberOfhiddenLayers+".srl").getName();
        String actualDifficultyF = new File("src/main/resources/com/project/morpion/ai/models/model_"+configF.hiddenLayerSize+"_"+configF.learningRate+"_"+configF.numberOfhiddenLayers+".srl").getName();
        String actualDifficultyD = new File("src/main/resources/com/project/morpion/ai/models/model_"+configD.hiddenLayerSize+"_"+configD.learningRate+"_"+configD.numberOfhiddenLayers+".srl").getName();
        String actualDifficultyM = new File("src/main/resources/com/project/morpion/ai/models/model_"+configM.hiddenLayerSize+"_"+configM.learningRate+"_"+configM.numberOfhiddenLayers+".srl").getName();
        try{
            String actualDifficultyC3 = new File("src/main/resources/com/project/morpion/ai/models/model_"+configC3.hiddenLayerSize+"_"+configC3.learningRate+"_"+configC3.numberOfhiddenLayers+".srl").getName();
            if (Filename.getName().equals(actualDifficultyC3)){
                this.name +=" [C3]";
            }

        }catch(Exception e){

        }
        try{
            String actualDifficultyC2 = new File("src/main/resources/com/project/morpion/ai/models/model_"+configC2.hiddenLayerSize+"_"+configC2.learningRate+"_"+configC2.numberOfhiddenLayers+".srl").getName();
            if (Filename.getName().equals(actualDifficultyC2)){
                this.name +=" [C2]";
            }

        }catch(Exception e){

        }
        try{
            String actualDifficultyC1 = new File("src/main/resources/com/project/morpion/ai/models/model_"+configC1.hiddenLayerSize+"_"+configC1.learningRate+"_"+configC1.numberOfhiddenLayers+".srl").getName();
            String actualDifficultyC2 = new File("src/main/resources/com/project/morpion/ai/models/model_"+configC1.hiddenLayerSize+"_"+configC1.learningRate+"_"+configC1.numberOfhiddenLayers+".srl").getName();
            if (Filename.getName().equals(actualDifficultyC1)){
                this.name +=" [C1]";
            }

        }catch(Exception e){

        }
        if (Filename.getName().equals(actualDifficultyF)){
            this.name +=" [F]";
        }
        if (Filename.getName().equals(actualDifficultyM)){
            this.name +=" [M]";
        }
        if (Filename.getName().equals(actualDifficultyD)){
            this.name +=" [D]";
        }




    }

    public String getName() {
        return name;
    }

    public String getFullPath() {
        return fullPath;
    }

    @Override
    public String toString() {
        return getName();
    }
}

