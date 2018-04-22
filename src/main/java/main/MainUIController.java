package main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.ArrayList;

public class MainUIController {

    @FXML
    public void initialize() {

        tf_username.setText(GUIManager.getUsername());

        loadSubjects();

    }

    private void loadSubjects() {


        ArrayList<String> subjectNames = null;
        try {
            subjectNames = GUIManager.loadSubjects();
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

    private void loadCategories(String subjectName) {

        ArrayList<String> categoryNames = null;
        try {
            categoryNames = GUIManager.loadCategories(subjectName);
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
    }

    private void loadTaskBlocks(String categoryName) {


        ArrayList<String> blockNames = null;
        try {
            blockNames = GUIManager.loadTaskBlocks(categoryName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        addElementsToContainer(blockNames);

        element_container.getChildren().forEach((el) -> {

            final Button blockButton = (Button) el;
            blockButton.setOnAction((event) -> {
                //loadCategories(blockButton.getText());
            });
        });

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


    private void loadTaskBlock() {

    }

    @FXML
    private TextField tf_username;

    @FXML
    private Button btn_logoff;

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

}
