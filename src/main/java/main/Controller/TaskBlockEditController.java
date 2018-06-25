package main.Controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.Session.EditSession;
import main.models.Answer;
import main.models.Block;
import main.models.Task;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Teacher can edit an already created taskblock. They can add, remove or change tasks in a block.
 */


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

    @FXML
    private Label pageNumberLabel;

    private ToggleGroup toggleAnswer;

    private Button taskDeleteButton;

    private Block block;

    //true if blockname, question or answers were changed.
    private boolean isDirty;

    private EditSession userSession;

    private Stage stage;

    //private boolean newTaskMode = false;

    private BooleanProperty newTaskMode = new SimpleBooleanProperty();

    //wird von MainUIController aufgerufen
    public void initData(EditSession editSession, Block taskBlock) {

        newTaskMode.set(false);

        //bindings for dynamic UI
        nextTaskButton.visibleProperty().bind(newTaskMode.not());
        deleteTaskButton.visibleProperty().bind(newTaskMode.not());

        nextTaskButton.managedProperty().bind(nextTaskButton.visibleProperty());
        deleteTaskButton.managedProperty().bind(deleteTaskButton.visibleProperty());


        stage = (Stage)task_container.getScene().getWindow();
        stage.setOnCloseRequest(ev -> {
            // dialog öffnen falls Änderungen noch nicht gespeichert wurden
            if(isDirty) {

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("");
                alert.setHeaderText("Bearbeitete Aufgabe noch nicht gespeichert!");
                alert.setContentText("Fenster wirklich schließen?");
                alert.initOwner(stage);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() != ButtonType.OK){
                    ev.consume();
                }
            }
        });

        userSession = editSession;

        block = taskBlock;

        isDirty = false;

        toggleAnswer = new ToggleGroup();

        initHiddenElements();

        displayTask();

    }

    private void initHiddenElements() {


        /*used for saving a whole newly created taskblock, not needed here*/
        finalSaveButton.setDisable(true);
        finalSaveButton.setMaxWidth(0);
        finalSaveButton.setMinWidth(0);

        //make buttons for switching tasks visible
        prevTaskButton.setOpacity(0);
        prevTaskButton.setDisable(true);

        if(block.getTasks().size() > 1) {
            nextTaskButton.setOpacity(1);
            nextTaskButton.setDisable(false);
        }

          /*show hidden taskDeleteButton  */
        deleteTaskButton.setMinWidth(addAnswerButton.getMinWidth());
        deleteTaskButton.setPrefWidth(addAnswerButton.getPrefWidth());
        deleteTaskButton.setMaxWidth(addAnswerButton.getMaxWidth());
        deleteTaskButton.setDisable(false);

    }


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


    //element hidden, not used
    @FXML
    void onBlockSaveBtnClicked(ActionEvent event) { }


    private void saveUpdatedTask() {

        if(formsFilled()) {

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

            displayTask();
            updateTaskSwitchButtons();
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

    if(formsFilled()) {

        if (newTaskMode.get()) {
            createNewTask();
        } else {

            if (isDirty) {

                isDirty = false;

                saveUpdatedTask();

                updateBlockName();

                updateTaskSwitchButtons();
            }

        }
    }

    }

    @FXML
    void onNextTaskClick(ActionEvent event) {
        loadNextTask();
    }

    @FXML
    void onPrevTaskClick(ActionEvent event) {
        loadPrevTask();
        newTaskMode.set(false);
    }


    @FXML
    private Button deleteTaskButton;

    @FXML
    void onTaskDeleteButtonClicked(ActionEvent event) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("");
        alert.setHeaderText("");
        alert.setContentText("Aufgabe wirklich löschen?");
        alert.initOwner(stage);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() != ButtonType.OK){
            return;
        }

        try {
            userSession.deleteCurrTask(this.block);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        block.deleteCurrTask();

        if(block.getTasks().size() == 0) {

            stage.close();

        } else {
            updateTaskSwitchButtons();
            displayTask();
        }
    }


    void loadNextTask() {

        isDirty = false;

        if(block.switchToNextTask()) {
            displayTask();
            updateTaskSwitchButtons();
        } else {
            resetForms();
            newTaskMode.set(true);
        }

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
            nextTaskButton.setText("+");

        }
        else {
            nextTaskButton.setOpacity(1);
            nextTaskButton.setDisable(false);
            nextTaskButton.setText(">");
        }

    }


    /**
     * create a new task that is is editable after being added to the db.
     */
    void createNewTask() {

        String question = questionTextarea.getText();
        ArrayList<Answer> answers = new ArrayList<>();

        radioContainer.getChildren()
                .forEach((radioBox) -> {
                    //get button out of hbox
                    RadioButton btn = (RadioButton)((HBox) radioBox).getChildren().get(0);

                    String answerText = btn.getText();
                    boolean isRight = btn.isSelected();

                    answers.add(new Answer(answerText, isRight));
                });

        Task task = new Task(question, answers);

        try {
            userSession.createTask(task, block.getName());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        block.addTask(task);
        updateTaskSwitchButtons();
        newTaskMode.set(false);

    }

    void displayTask() {

        block.printBlock();

        pageNumberLabel.setText("Aufgabe " + (block.getTasks().indexOf(block.getCurrTask()) + 1) + " von " + block.getTasks().size());
        taskBlockTextField.setText(block.getName());
        questionTextarea.setText(block.getCurrTask().getQuestion());

        toggleAnswer = new ToggleGroup();

        this.radioContainer.getChildren().clear();

        for(Answer answer : block.getCurrTask().getAnswers()) {

            HBox answerBox = new HBox();
            answerBox.setAlignment(Pos.CENTER);
            RadioButton rb = new RadioButton(answer.getAnswerText());

            //add Button to delete an answer
            Button answerDeleteButton = new Button(" - ");
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

        return !questionTextarea.getText().isEmpty() && !radioContainer.getChildren().isEmpty()
                && !taskBlockTextField.getText().isEmpty() && toggleAnswer.getSelectedToggle() != null;

    }

}

