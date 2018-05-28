package main.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.Session.UserSession;
import main.models.Answer;
import main.models.Block;

import java.sql.SQLException;

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
    public void initData(Block taskBlock, UserSession userInstance) {

        this.taskBlockName = taskBlockName;
        this.userSession = userInstance;

        block = taskBlock;

        try {
            userInstance.startBlock(taskBlock.getName());
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
            RadioButton rb = new RadioButton(answer.getAnswerText());
            rb.setToggleGroup(toggleGroup_answers);
            radioContainer.getChildren().add(rb);
        }
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
        if(toggleGroup_answers.getSelectedToggle() == null) { return true; }

        checkAnswer();

        return block.switchToNextTask();

    }

    void closeTaskStage() {
        Stage stage = (Stage) taskContainer.getScene().getWindow();
        stage.close();
    }

}
