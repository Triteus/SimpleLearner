package main;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import sql.SqlLogik;

import java.sql.SQLException;
import java.util.ArrayList;

public class Student extends UserInstance {


    public Student(SqlLogik sql) {
        super(sql);
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
    public void fillDirectory() {

    }

    @Override
    public void fillModule() {

    }

    @Override
    public void printResultsAsPdf() {

    }
}
