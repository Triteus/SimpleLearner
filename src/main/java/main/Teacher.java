package main;

import javafx.scene.control.TextField;

import sql.SqlLogik;
import java.sql.SQLException;
import java.util.ArrayList;


public class Teacher extends UserInstance {


    public Teacher (String name) {
        this.username = name;
    }

    @Override
    public ArrayList<String> loadSubjects(SqlLogik sql, String filter) {

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
    public ArrayList<String> loadTaskBlocks(SqlLogik sql, String category, String filter) throws Exception {
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
