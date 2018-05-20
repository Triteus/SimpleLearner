package main.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.Session.UserSession;
import main.models.Answer;
import main.models.Block;
import main.models.Task;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class TaskBlockEditController extends TaskBlockController {

    @FXML
    private VBox task_container;

    @FXML
    private TextField taskBlockTextField;

    @FXML
    private Label questionLabel;

    @FXML
    private TextArea questionTextarea;

    @FXML
    private Label answerLabel;

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
    void onAddAnswerBtnClicked(ActionEvent event) {

        String answer = answerTextfield.getText();

        if(!answer.isEmpty()) {
            RadioButton answerButton = new RadioButton(answer);
            answerButton.setToggleGroup(toggleAnswer);
            radioContainer.getChildren().add(answerButton);
        }
    }


    /**
     * ALL changes are submitted only after saving them
     * @param event
     */
    @FXML
    void onBlockSaveBtnClicked(ActionEvent event) {


    }

    private void saveUpdatedTask() {

        if(formsFilled() && toggleAnswer.getSelectedToggle() != null) {

                String question = questionTextarea.getText();
                ArrayList<Answer> answers = new ArrayList<>();

                radioContainer.getChildren()
                        .forEach((radioButton) -> {
                            RadioButton btn = (RadioButton) radioButton;
                            String answerText = btn.getText();
                            boolean isRight = btn.isSelected();

                            answers.add(new Answer(answerText, isRight));
                        });

                Task updatedTask = new Task(question, answers);

                try {
                    userSession.updateTask(block.getCurrTask(), updatedTask, userSession.getUsername(), block.getName());
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                block.getTasks().remove(block.getCurrTask());
                block.getTasks().add(updatedTask);
                block.setCurrTask(block.getTasks().size() - 1);
            }

    }

    private void updateBlockName() {

        String updatedBlockName = taskBlockTextField.getText();

        try {
            userSession.updateBlock(block.getName(), updatedBlockName);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        block.setName(updatedBlockName);

    }

    @FXML
    void onSubmitButtonClicked(ActionEvent event) {


        saveUpdatedTask();

        updateBlockName();

    }

    @FXML
    void onNextTaskClick(ActionEvent event) {
        loadNextTask();
    }

    @FXML
    void onPrevTaskClick(ActionEvent event) {
        loadPrevTask();
    }


    private ToggleGroup toggleAnswer;

    private String category;
    private Block block;
    private Block oldBlock;

    private boolean isDirty;

    //wird von MainUIController aufgerufen
    public void initData(UserSession userSession, String category, String taskBlockName) {

        this.userSession = userSession;

        block = createBlock(taskBlockName);

        isDirty = false;

        try {
            userSession.startBlock(taskBlockName);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        toggleAnswer = new ToggleGroup();

        //make buttons for switching tasks visible
        prevTaskButton.setOpacity(0);
        prevTaskButton.setDisable(true);

        if(block.getTasks().size() > 1) {
            nextTaskButton.setOpacity(1);
            nextTaskButton.setDisable(false);
        }

        displayTask();

    }


    void loadNextTask() {

        block.switchToNextTask();

        displayTask();

        updateTaskSwitchButtons();

    }

    void loadPrevTask() {


        block.switchToPrevTask();

        displayTask();

        updateTaskSwitchButtons();

    }


    void updateTaskSwitchButtons() {

        if (block.isFirstTask()) {
            prevTaskButton.setOpacity(0);
            prevTaskButton.setDisable(true);

        } else {
            prevTaskButton.setOpacity(1);
            prevTaskButton.setDisable(false);

        }

        if(block.isLastTask()) {
            nextTaskButton.setOpacity(0);
            nextTaskButton.setDisable(true);
        } else {
            nextTaskButton.setOpacity(1);
            nextTaskButton.setDisable(false);
        }

    }

    @Override
    void displayTask() {

        block.printBlock();

        taskBlockTextField.setText(block.getName());

        questionTextarea.setText(block.getCurrTask().getQuestion());

        toggleAnswer = new ToggleGroup();

        this.radioContainer.getChildren().clear();

        for(Answer answer : block.getCurrTask().getAnswers()) {

            RadioButton rb = new RadioButton(answer.getAnswerText());
            rb.setToggleGroup(toggleAnswer);

                if(answer.isRight()) {
                    rb.setSelected(true);
                }

            this.radioContainer.getChildren().add(rb);
        }
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

        return !questionTextarea.getText().isEmpty() && !radioContainer.getChildren().isEmpty();

    }

}
