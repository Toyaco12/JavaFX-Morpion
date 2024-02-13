module com.project.morpion {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.project.morpion to javafx.fxml;
    exports com.project.morpion;
}