package com.project.morpion.model;

import java.io.File;

public class ItemSuppr {
    private String name;
    private String fullPath;

    public ItemSuppr(String fullPath) {
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

