package main.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import main.Session.UserSession;
import sql.Answer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class TaskBlockEditController {

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


    private ToggleGroup toggleAnswer;
    private HashMap<String, ArrayList<Answer>> tasks;

    private UserSession userSession;
    private String category;


    //wird von MainUIController aufgerufen
   public void initData(UserSession userSession, String category) {
       toggleAnswer = new ToggleGroup();
       tasks = new HashMap<>();
       this.userSession = userSession;
       this.category = category;
   }


    @FXML
    void onAddAnswerBtnClicked(ActionEvent event) {

        String answer = answerTextfield.getText();

        RadioButton answerButton = new RadioButton(answer);
        answerButton.setToggleGroup(toggleAnswer);
        radioContainer.getChildren().add(answerButton);

    }

    @FXML
    void onBlockSaveBtnClicked(ActionEvent event) {

       String block = taskBlockTextField.getText();

        try {
            userSession.createBlock(block, category, tasks);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @FXML
    void onSubmitButtonClicked(ActionEvent event) {

       String question = questionTextarea.getText();
       ArrayList<Answer> answers = new ArrayList<>();

       radioContainer.getChildren()
               .forEach(( radioButton) -> {
           RadioButton btn = (RadioButton) radioButton;
           String answerText = btn.getText();
           boolean isRight = btn.isSelected();

            answers.add(new Answer(answerText, isRight ));
       });

       tasks.put(question, answers);

       resetForms();

    }

    /*
    Clear questionText and all answers
     */

    private void resetForms() {

       questionTextarea.setText("");
       radioContainer.getChildren().clear();

    }

}
