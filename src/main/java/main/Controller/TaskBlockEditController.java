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
import java.util.ArrayList;
import java.util.HashMap;

public class TaskBlockEditController extends TaskBlockController {

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


    private ToggleGroup toggleAnswer;
    private HashMap<String, ArrayList<Answer>> tasks;


    private String category;
    private Block block;


    //wird von MainUIController aufgerufen
    public void initData(UserSession userSession, String category, String existingBlock) {

        toggleAnswer = new ToggleGroup();
        tasks = new HashMap<>();
        this.userSession = userSession;
        this.block.setCategory(category);
        this.block.setName(existingBlock);

        //make buttons for switching tasks visible
        prevTaskButton.setOpacity(1);
        nextTaskButton.setOpacity(1);
        nextTaskButton.setDisable(false);

        this.taskBlockTextField.setText( existingBlock);

       displayTask();

    }


    void loadNextTask() {

        if (block.switchToNextTask()) {

        }

    }

    void loadPrevTask() {

    }


    @FXML
    void onAddAnswerBtnClicked(ActionEvent event) {

        String answer = answerTextfield.getText();

        if(!answer.isEmpty()) {
            RadioButton answerButton = new RadioButton(answer);
            answerButton.setToggleGroup(toggleAnswer);
            radioContainer.getChildren().add(answerButton);
        }
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
        stage.close();

    }

    @FXML
    void onSubmitButtonClicked(ActionEvent event) {

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

            tasks.put(question, answers);

            resetForms();
        }

    }


    @Override
    void displayTask() {

        System.out.println("test");

        questionTextarea.setText(block.getCurrTask().getQuestion());

        toggleAnswer = new ToggleGroup();

        this.radioContainer.getChildren().clear();

        for(Answer answer : block.getCurrTask().getAnswers()) {

            RadioButton rb = new RadioButton(answer.getAnswerText());
            rb.setToggleGroup(toggleAnswer);
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
