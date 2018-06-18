package main.Controller;

import com.itextpdf.text.DocumentException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.Session.EditSession;
import main.Session.UserSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

public class MainUIController {

    @FXML
    private VBox container;

    @FXML
    private Button btn_logoff;

    @FXML
    private TextField tf_username;

    @FXML
    private ToolBar breadcrumbBar;

    @FXML
    private VBox element_container;


    @FXML
    void onSubmitClick(ActionEvent event) { loadSubjects(); }

    @FXML
    void onLogoffClick(ActionEvent event) {

        Stage mainUIStage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));

        try {
            Node source = (Node) event.getSource();
            mainUIStage = (Stage) source.getScene().getWindow();
            mainUIStage.setScene(new Scene(loader.load()));
            mainUIStage.centerOnScreen();
            mainUIStage.setMaximized(false);

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

        initLogoutButton();

    }

    private void initLogoutButton() {

        Image cameraIcon = new Image(getClass().getResourceAsStream("/logout20.png"));
        ImageView cameraIconView = new ImageView(cameraIcon);
        cameraIconView.setFitHeight(15);
        cameraIconView.setFitWidth(15);

        btn_logoff.setGraphic(cameraIconView);

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
                String subject = subjectButton.getText();
                loadCategories(subject);

                Button breadCrumBtn = new Button("Fächer");
                breadCrumBtn.getStyleClass().add("btn");
                breadCrumBtn.setOnAction((ev) -> {
                    loadSubjects();
                    breadcrumbBar.getItems().clear();
                });

                breadcrumbBar.getItems().add(breadCrumBtn);
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
                Button breadCrumBtn = new Button(subject);
                breadCrumBtn.getStyleClass().add("btn");
                Text splitter = new Text("/");

                breadCrumBtn.setOnAction((ev) -> {
                    loadCategories(subject);
                    breadcrumbBar.getItems().removeAll(breadCrumBtn, splitter);
                });

                breadcrumbBar.getItems().add(splitter);
                breadcrumbBar.getItems().add(breadCrumBtn);
            });

        });

        if (userInstance.isEditAllowed()) {
            final Button categoryAdder = new Button("+++ Neue Kategorie hinzufügen +++");
            categoryAdder.getStyleClass().add("btnNewElement");

            categoryAdder.setPrefWidth(2000);

            element_container.getChildren().add(categoryAdder);

            /* opens Dialog  */
            categoryAdder.setOnAction((event) -> {
                try {

                    TextInputDialog dialog = createInputDialog();
                    Optional<String> result = dialog.showAndWait();

                    if (result.isPresent()){
                        //only users with rights to edit can add a category -> explicit cast since we know they are privileged
                        ((EditSession)userInstance).addCategory(result.get(), subject );
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

        /*set listener*/
        element_container.getChildren().forEach((el) -> {

            final Button blockButton = (Button) el;
            blockButton.getStyleClass().add("btnQuiz");
            blockButton.setOnAction((event) -> {

                loadTaskBlock(blockButton.getText(), category);
                loadTaskBlocks(category);

            });

            if(userInstance.isEditAllowed()) {
                blockButton.setOnMousePressed((event) -> {
                    if (event.isSecondaryButtonDown()) {
                        showWindowWithStudents(blockButton.getText());
                    }
                });
            }
        });

        if (userInstance.isEditAllowed()) {
            addBlockAdderButton(category);
        }
    }


    /**
     * Displays all students who already solved the given block
     * Upon being clicked, a PDF-File is created showing the student's results.
     *
     * @param blockName
     */

   private void showWindowWithStudents(String blockName) {

       ArrayList<String> students = new ArrayList<>();

       try {
          students = userInstance.loadStudentsWhoSolvedTaskBlock(blockName);
       } catch (SQLException e) {
           e.printStackTrace();
       }

       ChoiceDialog<String> dialog = new ChoiceDialog<>(students.get(0), students);
       dialog.setTitle("Schülerauswahl zur PDF-Erzeugung");
       dialog.setHeaderText("OK klicken nach Auswahl zur Erzeugung einer Ergebnisübersicht");
       dialog.setContentText("Wählen Sie einen Schüler aus:");

       Optional<String> result = dialog.showAndWait();
       result.ifPresent(student -> {
           EditSession eSession = (EditSession) userInstance;
           try {
               eSession.saveResultsAsPDF(student, blockName);
           } catch (DocumentException e) {
               e.printStackTrace();
           } catch (SQLException e) {
               e.printStackTrace();
           } catch (IOException e) {
               e.printStackTrace();
           }
       });

    }


   private void addBlockAdderButton(String category) {

       final Button blockAdder = new Button("+++ Neuen Testblock hinzufügen +++");
       blockAdder.getStyleClass().add("btnNewElement");

       blockAdder.setPrefWidth(2000);
       element_container.getChildren().add(blockAdder);

       blockAdder.setOnAction((event) -> {
           openTaskBlockWindow("/Taskblock_new.fxml", category, true);
           loadTaskBlocks(category);
       });
    }

    private void openTaskBlockWindow(String fxmlPath, String category, boolean isNewTask) {

       FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        loader.setController(new TaskBlockNewController());

        Stage stage = new Stage();
        try {
            stage.setScene(
                    new Scene(loader.load())
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        stage.initModality(Modality.WINDOW_MODAL);

        Stage primaryStage = (Stage)container.getScene().getWindow();

        stage.initOwner(primaryStage);

        stage.setAlwaysOnTop(true);
        stage.setTitle("Neuen Aufgabenblock erstellen");

            TaskBlockNewController controller = loader.getController();
            //UserSession can be savely cast to EditSession since we know it is not a StudentSession
            controller.initData((EditSession) userInstance, category);

        stage.centerOnScreen();
        stage.setMaximized(true);
        stage.show();

        //reload all items after closing the stage
        stage.setOnCloseRequest((event) -> {
            loadTaskBlocks(category);
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

    private void loadTaskBlock(String blockName, String category) {

        Stage mainStage = (Stage)container.getScene().getWindow();

        try {
            userInstance.openTaskBlock(blockName, category, mainStage);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private TextInputDialog createInputDialog() {

        TextInputDialog dialog = new TextInputDialog();
       // dialog.setTitle("Text Input Dialog");
        //dialog.setHeaderText("Look, a Text Input Dialog");
        dialog.setContentText("Name: ");

        return dialog;

    }

}
