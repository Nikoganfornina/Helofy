module org.example.helofy {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires com.fasterxml.jackson.databind;

    opens org.example.helofy to javafx.fxml;
    opens org.example.helofy.controllers to javafx.fxml;
    opens org.example.helofy.utils to com.fasterxml.jackson.databind;

    exports org.example.helofy;
}