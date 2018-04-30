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
    private VBox task_container;

    @FXML
    private Label label_taskblock;

    @FXML
    private Label label_question;

    @FXML
    private TextArea textarea_question;

    @FXML
    private Label label_answer;

    @FXML
    private VBox radio_container;

    @FXML
    private ToggleGroup toggleGroup_answers;

    @FXML
    private Button button_next;


    @FXML
    void onSubmitClick(ActionEvent event) {

        if(getNextTask()) {
            displayTask();

        } else {
            closeTaskStage();
        }

    }

    private String taskBlockName;
    private ArrayList<String> questions;
    private String currentQuestion;
    private ArrayList<String> answersForCurrentQuestion;
    private UserSession userInstance;
    private boolean answerSelected;


    //wird vom MainUIController aufgerufen
    public void initData(String taskBlockName, UserSession userInstance) {

        this.taskBlockName = taskBlockName;
        this.userInstance = userInstance;


        try {
            userInstance.startBlock(taskBlockName);
        } catch (SQLException e) {
            e.printStackTrace();
        }


        this.answerSelected = false;

        toggleGroup_answers = new ToggleGroup();

        label_taskblock.setText(taskBlockName);

        //fetch initial data from database
        questions = loadQuestions(taskBlockName);
        currentQuestion = questions.remove(0);
        answersForCurrentQuestion = loadAnswersForCurrentQuestion();

        displayTask();

    }

    public void displayTask() {

        textarea_question.setText(currentQuestion);

        toggleGroup_answers = new ToggleGroup();

        radio_container.getChildren().clear();

        for(String answer : answersForCurrentQuestion) {
            System.out.println(answer);
            RadioButton rb = new RadioButton(answer);
            rb.setToggleGroup(toggleGroup_answers);
            radio_container.getChildren().add(rb);
        }
    }

    public ArrayList<String> loadQuestions(String taskBlockName) {

        ArrayList<String> questions = null;
        try {
            questions = userInstance.loadQuestions(taskBlockName );
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return questions;
    }

    public ArrayList<String> loadAnswersForCurrentQuestion() {

        ArrayList<String> answers = null;
        try {
            answers = userInstance.loadAnswers(taskBlockName, currentQuestion );
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
                userInstance.checkAnswer(taskBlockName, currentQuestion, answer);
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
        Stage stage = (Stage) task_container.getScene().getWindow();
        stage.close();
    }

}
