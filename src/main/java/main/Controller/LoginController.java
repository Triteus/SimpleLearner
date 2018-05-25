package main.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.Authentication.Authenticator;
import main.Session.UserSession;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField tf_name;

    @FXML
    private PasswordField tf_password;

    @FXML
    private Button btn_login;

    @FXML
    private VBox container;


    //Öffnet das Main User Interface

    @FXML
    void onLoginClick(ActionEvent event) {

        String username = tf_name.getCharacters().toString();
        String password = tf_password.getCharacters().toString();
        UserSession instance = null;

        try {
            instance = Authenticator.login(username, password);
        } catch (Exception e) {
            System.out.println("Fehler bei der Anmeldung: ");
            e.printStackTrace();
        }

        if(instance != null) {

            openMainUIWindow(instance);

        } else {
            System.out.println("Ungültige Eingabe");
        }
    }

    private void openMainUIWindow(UserSession userSession) {

        Stage mainUIStage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));

        try {

            mainUIStage = (Stage) container.getScene().getWindow();
            mainUIStage.setScene(new Scene(loader.load()));

        } catch (IOException e) {
            e.printStackTrace();
        }

        MainUIController controller = loader.getController();
        controller.initData(userSession);


    }

}
