package main.Controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.Session.UserSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

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
            subjectButton.getStyleClass().add("subjectButton");

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
            catButton.getStyleClass().add("btnCategory");

            catButton.setOnAction((event) -> {
                loadTaskBlocks(catButton.getText());
            });

        });


        if (userInstance.isEditAllowed()) {
            final Button categoryAdder = new Button("+++ Neue Kategorie hinzufügen +++");
            categoryAdder.getStyleClass().add("btnNewElement");

            categoryAdder.setPrefWidth(2000);

            element_container.getChildren().add(categoryAdder);


            categoryAdder.setOnAction((event) -> {
                try {

                    TextInputDialog dialog = createInputDialog();

                    Optional<String> result = dialog.showAndWait();

                    if (result.isPresent()){
                        userInstance.addCategory(result.get(), subject );
                        loadCategories(subject);
                    }

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
            blockButton.getStyleClass().add("btnQuiz");
            blockButton.setOnAction((event) -> {

                loadTaskBlock(blockButton.getText(), category);
            });
        });

        if (userInstance.isEditAllowed()) {

            final Button blockAdder = new Button("+++ Neuen Testblock hinzufügen +++");
            blockAdder.getStyleClass().add("btnNewElement");

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

    private void loadTaskBlock(String blockName, String category) {

        userInstance.loadTaskBlock(blockName, category);

    }


    private TextInputDialog createInputDialog() {

        TextInputDialog dialog = new TextInputDialog("walter");
       // dialog.setTitle("Text Input Dialog");
        //dialog.setHeaderText("Look, a Text Input Dialog");
        dialog.setContentText("Name: ");

        return dialog;

    }

}
