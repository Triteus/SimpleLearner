package main;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import sql.SqlLogik;

import java.sql.SQLException;
import java.util.ArrayList;


public abstract class UserInstance {


    SqlLogik sql;
    String username;

    public UserInstance(SqlLogik sql) {
        this.sql = sql;
        this.username = sql.getCurrentUser();
    }

    public String getUsername() {
        return username;
    }


    public ArrayList<String> loadCategories(String subject, String filterText) throws Exception {

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

    public abstract ArrayList<String> loadSubjects(String filterText) throws Exception;

    public abstract ArrayList<String> loadTaskBlocks(String category, String filterText) throws Exception;

    public abstract void fillDirectory();

    public abstract void fillModule();

    public abstract void printResultsAsPdf();





}
