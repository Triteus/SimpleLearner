package main.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.Session.UserSession;

import java.sql.SQLException;
import java.util.ArrayList;

public class TaskBlockController {

    @FXML
    private VBox taskContainer;

    @FXML
    private Label label_taskblock;

    @FXML
    private Label questionLabel;

    @FXML
    private TextArea questionTextarea;

    @FXML
    private Label answerLabel;

    @FXML
    private VBox radioContainer;

    @FXML
    private ToggleGroup toggleGroup_answers;

    @FXML
    private Button nextTaskButton;


    @FXML
    void onSubmitClick(ActionEvent event) {

        if(getNextTask()) {

            displayTask();

        } else {

            closeTaskStage();

        }
    }

    String taskBlockName;
    ArrayList<String> questions;
    String currentQuestion;
    ArrayList<String> answersForCurrentQuestion;
     UserSession userSession;

    //wird vom MainUIController aufgerufen
    public void initData(String taskBlockName, UserSession userInstance) {

        this.taskBlockName = taskBlockName;
        this.userSession = userInstance;

        try {
            userInstance.startBlock(taskBlockName);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        toggleGroup_answers = new ToggleGroup();

        label_taskblock.setText(taskBlockName);

        //fetch initial data from database
        questions = loadQuestions(taskBlockName);
        currentQuestion = questions.remove(0);
        answersForCurrentQuestion = loadAnswersForCurrentQuestion();

        displayTask();

    }

    public void displayTask() {

        questionTextarea.setText(currentQuestion);

        toggleGroup_answers = new ToggleGroup();

        radioContainer.getChildren().clear();

        for(String answer : answersForCurrentQuestion) {
            System.out.println(answer);
            RadioButton rb = new RadioButton(answer);
            rb.setToggleGroup(toggleGroup_answers);
            radioContainer.getChildren().add(rb);
        }
    }

    public ArrayList<String> loadQuestions(String taskBlockName) {

        ArrayList<String> questions = null;
        try {
            questions = userSession.loadQuestions(taskBlockName );
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return questions;
    }

    public ArrayList<String> loadAnswersForCurrentQuestion() {

        ArrayList<String> answers = null;
        try {
            answers = userSession.loadAnswers(taskBlockName, currentQuestion );
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return answers;
    }

    public boolean getNextTask() {

        //no answer selected -> keep current task
        if(toggleGroup_answers.getSelectedToggle() == null) {
            return true;
        }

        if(!questions.isEmpty()) {

                String answer = ((RadioButton)toggleGroup_answers.getSelectedToggle()).getText();

            try {
                userSession.checkAnswer(taskBlockName, currentQuestion, answer);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            currentQuestion = getNextQuestion();
            answersForCurrentQuestion = loadAnswersForCurrentQuestion();

            return true;
        }

        return false;

    }

     private String getNextQuestion() {

        return questions.remove(0);
    }

    public void closeTaskStage() {
        Stage stage = (Stage) taskContainer.getScene().getWindow();
        stage.close();
    }

}
