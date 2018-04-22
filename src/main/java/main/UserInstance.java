package main;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import sql.SqlLogik;

import java.sql.SQLException;
import java.util.ArrayList;


public abstract class UserInstance {


    String username;


    public String getUsername() {
        return username;
    }

    public ArrayList<String> loadCategories(SqlLogik sql, String subject, String filterText) throws Exception {

        if (filterText.isEmpty()) {
            try {
                sql.loadCategories(subject);
            } catch (SQLException exc) {
                System.out.println(exc.getMessage());
                throw exc;
            }
        } else {
            try {
                sql.loadFilteredCategories(subject, filterText);
            } catch (SQLException exc) {
                System.out.println(exc.getMessage());
                throw exc;
            }
        }

        return sql.getCategories();

    }

    public abstract ArrayList<String> loadSubjects(SqlLogik sql, String filterText) throws Exception;

    public abstract ArrayList<String> loadTaskBlocks(SqlLogik sql, String category, String filterText) throws Exception;

    public abstract void fillDirectory();

    public abstract void fillModule();

    public abstract void printResultsAsPdf();





}
