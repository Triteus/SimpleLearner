package main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

public class TaskBlockController {

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
    private ToggleGroup test;

    @FXML
    private Button button_next;




    @FXML
    void onSubmitClick(ActionEvent event) {

    }

    private String taskBlockName;

    public void initData(String taskBlockName) {
        this.taskBlockName = taskBlockName;

        label_taskblock.setText(taskBlockName);

    }

}
