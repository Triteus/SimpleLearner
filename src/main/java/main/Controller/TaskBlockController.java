package main.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.Session.UserSession;
import main.models.Answer;
import main.models.Block;
import main.models.Task;

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
    UserSession userSession;
    Block block;

    //wird vom MainUIController aufgerufen
    public void initData(String taskBlockName, UserSession userInstance) {

        this.taskBlockName = taskBlockName;
        this.userSession = userInstance;

        ArrayList<Task> tasks = loadTasks(taskBlockName);
        block = new Block(taskBlockName, tasks);
        block.setCurrTask(0);

        try {
            userInstance.startBlock(taskBlockName);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        toggleGroup_answers = new ToggleGroup();

        label_taskblock.setText(taskBlockName);

        displayTask();

    }

    void displayTask() {

        questionTextarea.setText(block.getCurrTask().getQuestion());

        toggleGroup_answers = new ToggleGroup();

        radioContainer.getChildren().clear();

        for(Answer answer : block.getCurrTask().getAnswers()) {
            System.out.println(answer);
            RadioButton rb = new RadioButton(answer.getAnswerText());
            rb.setToggleGroup(toggleGroup_answers);
            radioContainer.getChildren().add(rb);
        }
    }

    ArrayList<Task> loadTasks(String taskBlockName) {

        ArrayList<String> questions = new ArrayList<>();
        ArrayList<Task> tasks = new ArrayList<>();

        try {
            questions = userSession.loadQuestions(taskBlockName );

            for(String question : questions) {
                ArrayList<String> answersText = loadAnswersForCurrentQuestion(taskBlockName, question);
                ArrayList<Answer> answers = new ArrayList<>();


                /*
                    Answers are created manually since method in SqlLogik only returns names of answers.
                 */

                for(String answer: answersText) {
                    answers.add(new Answer(answer, false));
                }

                tasks.add(new Task(question, answers));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tasks;
    }

     private ArrayList<String> loadAnswersForCurrentQuestion(String blockName, String currQuestion) {

        ArrayList<String> answers = null;
        try {
            answers = userSession.loadAnswers(blockName, currQuestion );
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return answers;
    }


    void checkAnswer() {

        String answer = ((RadioButton)toggleGroup_answers.getSelectedToggle()).getText();

        try {
            userSession.checkAnswer(block.getName(), block.getCurrTask().getQuestion(), answer);
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    boolean getNextTask() {

        //no answer selected -> keep current task
        if(toggleGroup_answers.getSelectedToggle() == null) {
            return true;
        }

        checkAnswer();

        return block.switchToNextTask();

    }


    void closeTaskStage() {
        Stage stage = (Stage) taskContainer.getScene().getWindow();
        stage.close();
    }

}
