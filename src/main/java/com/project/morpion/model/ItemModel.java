package com.project.morpion.model;

import com.project.morpion.model.ai.Config;
import com.project.morpion.model.ai.ConfigFileLoader;

import java.io.File;

public class ItemModel {
    private String name;
    private String fullPath;

    public ItemModel(String fullPath) {
        this.fullPath = fullPath;
        this.name = new File(fullPath).getName();
        ConfigFileLoader cfl = new ConfigFileLoader();
        cfl.loadConfigFile("src/main/resources/com/project/morpion/ai/config.txt");
        Config configF = cfl.get("F");
        Config configM = cfl.get("M");
        Config configD = cfl.get("D");
        String actualDifficultyF = new File("src/main/resources/com/project/morpion/ai/models/"+"F"+"/model_"+configF.hiddenLayerSize+"_"+configF.learningRate+"_"+configF.numberOfhiddenLayers+".srl").getName();
        String actualDifficultyD = new File("src/main/resources/com/project/morpion/ai/models/"+"D"+"/model_"+configD.hiddenLayerSize+"_"+configD.learningRate+"_"+configD.numberOfhiddenLayers+".srl").getName();
        String actualDifficultyM = new File("src/main/resources/com/project/morpion/ai/models/"+"M"+"/model_"+configM.hiddenLayerSize+"_"+configM.learningRate+"_"+configM.numberOfhiddenLayers+".srl").getName();
        if (this.name.equals(actualDifficultyF)){
            this.name +=" [F]";
        }
        if (this.name.equals(actualDifficultyM)){
            this.name +=" [M]";
        }
        if (this.name.equals(actualDifficultyD)){
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

