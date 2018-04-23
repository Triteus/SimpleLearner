package main.Session;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.Controller.TaskBlockController;
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
    public ArrayList<String> loadSubjects(String filter) {

        if (filter.isEmpty()) {
            try {
                sql.loadSubjects();
            } catch (SQLException exc) {
                System.out.println(exc.getMessage());
            }
        } else {
            try {
                sql.loadFilteredSubjects(filter);
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
                sql.loadStudentSections(category, username);
            } catch (SQLException exc) {
                System.out.println(exc.getMessage());
            }
        } else {
            try {
                sql.loadFilteredStudentSections(category, username, filter);
            } catch (SQLException exc) {
                System.out.println(exc.getMessage());
            }
        }

        return sql.getTaskSections();

    }

    @Override
    public void loadTaskBlock(String blockName) {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Taskblock.fxml"));

        Stage stage = new Stage();
        try {
            stage.setScene(
                    new Scene(loader.load())
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        TaskBlockController controller = loader.getController();
        controller.initData(blockName, this);

        stage.show();

    }


    @Override
    public void fillDirectory() {

    }

    @Override
    public void fillModule() {

    }

    @Override
    public void printResultsAsPdf() {

    }

    @Override
    public void addCategory(String category, String subject) throws SQLException {

    }

    @Override
    public void openTaskBlockCreator(String category) {

    }

    @Override
    public void createBlock(String block, String category, HashMap<String, ArrayList<String>> tasks) throws SQLException {

    }


}
