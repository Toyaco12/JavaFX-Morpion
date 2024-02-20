package com.project.morpion.model;

import java.io.File;

public class ItemModel {
    private String name;
    private String fullPath;

    public ItemModel(String fullPath) {
        this.fullPath = fullPath;
        this.name = new File(fullPath).getName();
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

