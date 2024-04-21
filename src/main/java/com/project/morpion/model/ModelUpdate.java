package com.project.morpion.model;

// Interface utilisé pour créer des listener sur l'apprentissage des modèles afin de réaliser des actions sur d'autres fênetres.

public interface ModelUpdate {
    void onModelUpdated();
    void onModelNotUpdated();
}
