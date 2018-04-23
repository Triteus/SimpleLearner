package main.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.Session.UserSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class MainUIController {

    @FXML
    private Button btn_logoff;

    @FXML
    private TextField tf_username;

    @FXML
    private ToolBar breadcrumbBar;

    @FXML
    private Button btn_subjectRef;

    @FXML
    private VBox element_container;

    @FXML
    void onSubmitClick(ActionEvent event) {

        loadSubjects();

    }

    @FXML
    void onLogoffClick(ActionEvent event) {

        Stage mainUIStage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));

        try {
            Node source = (Node) event.getSource();
            mainUIStage = (Stage) source.getScene().getWindow();
            mainUIStage.setScene(new Scene(loader.load()));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private UserSession userInstance;


    //gets called from the LoginController
    void initData(UserSession instance) {

        this.userInstance = instance;

        tf_username.setText(userInstance.getUsername());

        loadSubjects();
    }

    private void loadSubjects() {

        ArrayList<String> subjectNames = null;

        try {
            subjectNames = userInstance.loadSubjects("");
        } catch (Exception e) {
            e.printStackTrace();
        }

        addElementsToContainer(subjectNames);

        element_container.getChildren().forEach((el) -> {

            final Button subjectButton = (Button) el;

            subjectButton.setOnAction((event) -> {
                loadCategories(subjectButton.getText());
            });
        });

    }

    private void loadCategories(String subject) {

        ArrayList<String> categoryNames = null;

        try {
            categoryNames = userInstance.loadCategories(subject, "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        addElementsToContainer(categoryNames);

        element_container.getChildren().forEach((el) -> {

            final Button catButton = (Button) el;

            catButton.setOnAction((event) -> {
                loadTaskBlocks(catButton.getText());
            });

        });


        if (userInstance.isEditAllowed()) {
            final Button categoryAdder = new Button("+++ Neue Kategorie hinzufügen +++");
            categoryAdder.setPrefWidth(2000);

            element_container.getChildren().add(categoryAdder);


            categoryAdder.setOnAction((event) -> {
                try {
                    userInstance.addCategory("testCategoryCreation", subject );
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

        }
    }

    private void loadTaskBlocks(String category) {

        ArrayList<String> blockNames = null;

        try {
            blockNames = userInstance.loadTaskBlocks(category, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        addElementsToContainer(blockNames);

        element_container.getChildren().forEach((el) -> {
            final Button blockButton = (Button) el;
            blockButton.setOnAction((event) -> {
                loadTaskBlock(blockButton.getText());
            });
        });

        if (userInstance.isEditAllowed()) {
            final Button blockAdder = new Button("+++ Neuen Testblock hinzufügen +++");
            blockAdder.setPrefWidth(2000);
            element_container.getChildren().add(blockAdder);

            blockAdder.setOnAction((event) -> {

                    userInstance.openTaskBlockCreator(category);

            });
        }
    }

    private void addElementsToContainer(ArrayList<String> elNames) {

        element_container.getChildren().setAll();

        for(String elName : elNames) {

            final Button elButton = new Button();
            elButton.setText(elName);
            elButton.setPrefWidth(1000);
            element_container.getChildren().add(elButton);

        }
    }


    private void loadTaskBlock(String blockName) {

        userInstance.loadTaskBlock(blockName);

    }

}
