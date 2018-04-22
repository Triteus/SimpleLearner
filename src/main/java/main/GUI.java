package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GUI extends Application {


    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent pane = FXMLLoader.load(getClass().getResource("/login.fxml"));

        Scene scene = new Scene( pane );
        primaryStage.setScene(scene);

        primaryStage.show();


    }
}
