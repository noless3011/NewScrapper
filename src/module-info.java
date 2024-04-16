module FacebookPost {
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;

    opens main to javafx.fxml;
    exports main;
    opens controller to javafx.fxml;
}