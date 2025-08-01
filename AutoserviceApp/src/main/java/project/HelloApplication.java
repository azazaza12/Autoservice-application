package project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static project.Config.initializeConfig;


public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        initializeConfig();
        Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
        Scene scene = new Scene(root, 500, 400);



        stage.setTitle("Авторизация");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}