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
import java.util.Optional;

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
    private Label pageNumberLabel;


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
    Stage stage;

    //wird vom MainUIController aufgerufen
    public void initData(Block taskBlock, UserSession userInstance) {

        stage = (Stage)taskContainer.getScene().getWindow();
        stage.setOnCloseRequest(ev -> {
            // dialog öffnen falls Nutzer vorzeitig abbrechen möchte

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("");
                alert.setHeaderText("Test noch nicht beendet. Nach Schließen des Tests ist er nicht mehr verfügbar!");
                alert.setContentText("Fenster wirklich schließen?");
                alert.initOwner(stage);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() != ButtonType.OK){
                    ev.consume();
                }
        });

        this.taskBlockName = taskBlockName;
        this.userSession = userInstance;

        block = taskBlock;

        try {
            userInstance.startBlock(taskBlock.getName());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        toggleGroup_answers = new ToggleGroup();

        label_taskblock.setText(block.getName());

        displayTask();

    }


    void displayTask() {

        pageNumberLabel.setText("Aufgabe " + (block.getTasks().indexOf(block.getCurrTask()) + 1) + " von " + block.getTasks().size());

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
