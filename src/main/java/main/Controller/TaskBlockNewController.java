package main.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import main.Session.EditSession;
import main.Session.UserSession;
import main.models.Answer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class TaskBlockNewController {

    @FXML
    private VBox task_container;

    @FXML
    private TextField taskBlockTextField;

    @FXML
    private Label label_question;

    @FXML
    private TextArea questionTextarea;

    @FXML
    private Label label_answer;

    @FXML
    private TextField answerTextfield;

    @FXML
    private Button addAnswerButton;

    @FXML
    private VBox radioContainer;

    @FXML
    private Button saveTaskButton;

    @FXML
    private Button finalSaveButton;

    @FXML
    private Button nextTaskButton;

    @FXML
    private Button prevTaskButton;

    @FXML
    void onNextTaskClick(ActionEvent event) { }

    @FXML
    void onPrevTaskClick(ActionEvent event) { }


    private ToggleGroup toggleAnswer;
    private HashMap<String, ArrayList<Answer>> tasks;

    private EditSession userSession;
    private String category;
    private MainUIController mainController;
    private Stage stage;

    //wird von MainUIController aufgerufen
   public void initData(EditSession userSession, String category, MainUIController controller) {


       stage = (Stage)task_container.getScene().getWindow();

       stage.setOnCloseRequest(ev -> {
           // dialog öffnen falls Änderungen noch nicht gespeichert wurden
               Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
               alert.setTitle("");
               alert.setHeaderText("Bearbeitete Aufgabe noch nicht gespeichert!");
               alert.setContentText("Fenster wirklich schließen?");
               alert.initOwner(stage);

           ButtonType save = new ButtonType("Speichern");
           ButtonType noSave = new ButtonType("Nicht Speichern");
           ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

           alert.getButtonTypes().setAll(save, noSave,cancel);

           Optional<ButtonType> result = alert.showAndWait();
           if (result.get() == save){
               onBlockSaveBtnClicked(new ActionEvent());
           } else if (result.get() == cancel) {
               ev.consume();
           }
       });


       this.mainController = controller;

       toggleAnswer = new ToggleGroup();
       tasks = new HashMap<>();
       this.userSession = userSession;
       this.category = category;
   }

    @FXML
    void onBlockNameChanged(KeyEvent event) { }

    @FXML
    void onQuestionChanged(KeyEvent event) { }

    @FXML
    void onAddAnswerBtnClicked(ActionEvent event) {

        String answer = answerTextfield.getText();

        if(answer.isEmpty()) return;

        HBox answerBox = new HBox();
        answerBox.setAlignment(Pos.CENTER);

        RadioButton answerButton = new RadioButton(answer);
        answerButton.setToggleGroup(toggleAnswer);

        Button answerDeleteButton = new Button("-");

        answerBox.getChildren().addAll(answerButton, answerDeleteButton );
        radioContainer.getChildren().add(answerBox);

        answerDeleteButton.setOnAction((actionEvent) -> {
            radioContainer.getChildren().remove(answerBox);
        });

    }

    @FXML
    void onBlockSaveBtnClicked(ActionEvent event) {

        //create only if there is at least one task included
        if(!tasks.isEmpty()) {

            String block = taskBlockTextField.getText();

            try {
                userSession.createBlock(block, category, tasks);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        Stage stage = (Stage)task_container.getScene().getWindow();

        /*
        stage.fireEvent(
                new WindowEvent(
                        stage,
                        WindowEvent.WINDOW_CLOSE_REQUEST
                )
        );

        */

        /* update MainUI */
        mainController.loadTaskBlocks(category);

        stage.close();

    }

    @FXML
    void onSubmitButtonClicked(ActionEvent event) {

       if(formsFilled()) {

           String question = questionTextarea.getText();
           ArrayList<Answer> answers = new ArrayList<>();

           radioContainer.getChildren()
                   .forEach((radioBox) -> {

                        //get button out of hbox
                       RadioButton btn = (RadioButton)((HBox) radioBox).getChildren().get(0);

                       String answerText = btn.getText();
                       boolean isRight = btn.isSelected();

                       answers.add(new Answer(answerText, isRight));
                   });

           tasks.put(question, answers);

           resetForms();
       }

    }

    @FXML
    private Button deleteTaskButton;

    @FXML
    void onTaskDeleteButtonClicked(ActionEvent event) {

    }

    /*
    Clear questionText and all answers
     */

    private void resetForms() {

       questionTextarea.setText("");
       radioContainer.getChildren().clear();
       answerTextfield.setText("");
       toggleAnswer = new ToggleGroup();

    }

    private boolean formsFilled() {

        return !questionTextarea.getText().isEmpty() && !radioContainer.getChildren().isEmpty() &&
                toggleAnswer.getSelectedToggle() != null;

    }

}
