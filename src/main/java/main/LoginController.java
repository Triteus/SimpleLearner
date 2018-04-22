package main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField tf_name;

    @FXML
    private PasswordField tf_password;

    @FXML
    private Button btn_login;


    //Öffnet das Main User Interface

    @FXML
    void onSubmitClick(ActionEvent event) {

        String username = tf_name.getCharacters().toString();
        String password = tf_password.getCharacters().toString();
        boolean loginSuccess = false;

        try {
            loginSuccess = GUIManager.initUser(username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }


        if(loginSuccess) {

            Stage mainUIStage;

            Parent pane = null;

            try {
                pane = FXMLLoader.load(getClass().getResource("/main.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            Scene scene = new Scene(pane);

            Node source = (Node) event.getSource();
            mainUIStage = (Stage) source.getScene().getWindow();

            mainUIStage.setScene(scene);


        } else {
            System.out.println("Ungültige Eingabe");
        }
    }

}
