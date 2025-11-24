module com.example.quick_zee {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.quick_zee to javafx.fxml;
    exports com.example.quick_zee;
}