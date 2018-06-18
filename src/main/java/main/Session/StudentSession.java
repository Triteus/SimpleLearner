package main.Session;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.Controller.TaskBlockController;
import main.models.Answer;
import main.models.Block;
import main.models.Task;
import sql.SqlLogik;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class StudentSession extends UserSession {

    public StudentSession(SqlLogik sql, String username) {
        super(sql, username);
        editAllowed = false;
    }

    @Override
    public ArrayList<String> loadSubjects(String filter) throws SQLException {

        if (filter.isEmpty()) {
            sql.loadSubjects();
        } else {
            sql.loadFilteredSubjects(filter);
        }

        return sql.getSubjects();

    }

    @Override
    public ArrayList<String> loadTaskBlocks(String category, String filter) throws Exception {

        if (filter.isEmpty()) {
            sql.loadStudentSections(category, username);
        } else {
            sql.loadFilteredStudentSections(category, username, filter);
        }

        return sql.getTaskSections();

    }

    @Override
    public void openTaskBlock(String blockName, String category, Stage mainStage) throws SQLException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Taskblock.fxml"));

        Stage stage = new Stage();
        try {
            stage.setScene(
                    new Scene(loader.load())
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(mainStage);

        Block taskBlock = loadTaskBlock(blockName);

        TaskBlockController controller = loader.getController();
        controller.initData(taskBlock, this);

        stage.centerOnScreen();
        stage.setMaximized(true);
        stage.show();

    }

    @Override
    public void startBlock(String block) throws SQLException {

        sql.startBlock(block, username);

    }

    @Override
    public boolean checkAnswer(String block, String question, String answer) throws SQLException {

        return sql.checkAnswer(block, username, question, answer);

    }

    @Override
    public ArrayList<String> loadStudentsWhoSolvedTaskBlock(String blockName) throws SQLException {
        return null;
    }

}
