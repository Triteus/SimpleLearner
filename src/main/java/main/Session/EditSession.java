package main.Session;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.Controller.TaskBlockEditController;
import main.Controller.TaskBlockNewController;
import main.models.Answer;
import main.models.Block;
import main.models.Task;
import sql.SqlLogik;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class EditSession extends UserSession {

    public EditSession(SqlLogik sql, String username) {
        super(sql, username);
        editAllowed = true;
    }

    @Override
    public ArrayList<String> loadSubjects(String filter) {

        if (filter.isEmpty()) {
            try {
                sql.loadSubjects(username);
            } catch (SQLException exc) {
                System.out.println(exc.getMessage());
            }
        } else {
            try {
                sql.loadFilteredSubjects(username, filter);
            } catch (SQLException exc) {
                System.out.println(exc.getMessage());
            }
        }

        return sql.getSubjects();

    }

    @Override
    public ArrayList<String> loadTaskBlocks(String category, String filter) throws Exception {
        if (filter.isEmpty()) {
            try {
                sql.loadTeacherSections(category, username);
            } catch (SQLException exc) {
                System.out.println(exc.getMessage());
                throw exc;
            }
        } else {
            try {
                sql.loadFilteredTeacherSections(category, username, filter);
            } catch (SQLException exc) {
                System.out.println(exc.getMessage());
                throw exc;
            }
        }
        return sql.getTaskSections();
    }


    /*
    The controller is set manually depending on the teacher either choosing to create or edit a block.
     */

    /**
     * Taskblock is loaded into a new stage.
     * @param block
     * @param category
     */

    @Override
    public void loadTaskBlock(String block, String category) {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Taskblock_new.fxml"));
        loader.setController(new TaskBlockEditController());

        Stage stage = new Stage();
        try {
            stage.setScene(
                    new Scene(loader.load())
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        TaskBlockEditController controller = loader.getController();
        controller.initData(this, category, block);

        stage.show();


    }

    @Override
    public void openTaskBlockCreator(String category) {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Taskblock_new.fxml"));
        loader.setController(new TaskBlockNewController());

        Stage stage = new Stage();
        try {
            stage.setScene(
                    new Scene(loader.load())
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        TaskBlockNewController controller = loader.getController();
        controller.initData(this, category);

        stage.show();

    }

    @Override
    public void createBlock(String block, String category, HashMap<String, ArrayList<Answer>> tasks) throws SQLException {

        sql.createBlock(block, this.username, category, tasks);

    }


    @Override
    public void addCategory(String category, String subject) throws SQLException {

        sql.createCategory(category, subject);
    }

    @Override
    public void updateBlock(Block oldBlock, Block newBlock, String category) throws SQLException {


        sql.updateQuiz(oldBlock.getName(), username, newBlock.getName());

    }

}
