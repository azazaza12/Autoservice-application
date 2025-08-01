module com.example.archfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    requires java.sql;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;


    opens project to javafx.fxml;
    exports project;
}