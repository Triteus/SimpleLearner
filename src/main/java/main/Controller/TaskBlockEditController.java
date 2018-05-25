package main.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import main.Session.EditSession;
import main.models.Answer;
import main.models.Block;
import main.models.Task;

import java.sql.SQLException;
import java.util.ArrayList;

public class TaskBlockEditController {

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


    private ToggleGroup toggleAnswer;

    private Block block;

    //true if blockname, question or answers were changed.
    private boolean isDirty;

    private EditSession userSession;


    @FXML
    void onBlockNameChanged(KeyEvent event) { isDirty = true; }

    @FXML
    void onQuestionChanged(KeyEvent event) { isDirty = true; }

    @FXML
    void onAddAnswerBtnClicked(ActionEvent event) {

        String answer = answerTextfield.getText();

        if(!answer.isEmpty()) {
            HBox answerBox = new HBox();
            answerBox.setAlignment(Pos.CENTER);
            RadioButton answerButton = new RadioButton(answer);

            //add Button to delete an answer
            Button answerDeleteButton = new Button("-");

            answerDeleteButton.setOnAction((actionEvent) -> {
                radioContainer.getChildren().remove(answerBox);
            });


            answerBox.getChildren().addAll(answerButton, answerDeleteButton);
            answerButton.setToggleGroup(toggleAnswer);

            radioContainer.getChildren().add(answerBox);
            isDirty = true;
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
                    .forEach((hBox) -> {

                //each Radiobutton is inside an HBox on the first position
                        HBox answerBox = (HBox) hBox;
                        RadioButton btn = (RadioButton) answerBox.getChildren().get(0);
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

        if(isDirty) {

            isDirty = false;

            saveUpdatedTask();

            updateBlockName();

            updateTaskSwitchButtons();
        }

    }

    @FXML
    void onNextTaskClick(ActionEvent event) {
        loadNextTask();
    }

    @FXML
    void onPrevTaskClick(ActionEvent event) {
        loadPrevTask();
    }

    //wird von MainUIController aufgerufen
    public void initData(EditSession uSession, String category, String taskBlockName) {

        userSession = uSession;

        try {
            block = TaskBlockLoadBehaviour.createBlock(taskBlockName, userSession);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        isDirty = false;

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

        isDirty = false;

        block.switchToNextTask();

        displayTask();

        updateTaskSwitchButtons();

    }

    void loadPrevTask() {

        isDirty = false;

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

    void displayTask() {

        block.printBlock();

        taskBlockTextField.setText(block.getName());

        questionTextarea.setText(block.getCurrTask().getQuestion());

        toggleAnswer = new ToggleGroup();

        this.radioContainer.getChildren().clear();

        for(Answer answer : block.getCurrTask().getAnswers()) {

            HBox answerBox = new HBox();
            answerBox.setAlignment(Pos.CENTER);
            RadioButton rb = new RadioButton(answer.getAnswerText());

            //add Button to delete an answer
            Button answerDeleteButton = new Button("-");
            answerDeleteButton.setOnAction((actionEvent) -> {
                radioContainer.getChildren().remove(answerBox);
                block.getCurrTask().getAnswers().remove(answer);
                isDirty = true;
            });

            answerBox.getChildren().addAll(rb, answerDeleteButton);

            this.radioContainer.getChildren().add(answerBox);

            rb.setToggleGroup(toggleAnswer);

                if(answer.isRight()) {
                    rb.setSelected(true);
                }
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

/**TODO
 * User kann bereits erstellen Blöcken neue Tasks hinzufügen
 * User kann einzelne Tasks löschen
 * User kann hinzgeügte Antworten löschen
 */
