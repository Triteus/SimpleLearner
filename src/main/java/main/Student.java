package main;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sql.SqlLogik;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class Student extends UserInstance {


    public Student(SqlLogik sql, String username) {
        super(sql, username);
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
}
