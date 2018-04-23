package main.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import main.Session.UserSession;

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
    private HashMap<String, ArrayList<String>> tasks;

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


    }

    @FXML
    void onSubmitButtonClicked(ActionEvent event) {

       String question = questionTextarea.getText();
       ArrayList<String> answers = new ArrayList<>();

       radioContainer.getChildren()
               .forEach(( radioButton) -> {
           RadioButton btn = (RadioButton) radioButton;
            answers.add(btn.getText());
       });

       tasks.put(question, answers);

    }

}
