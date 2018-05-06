package main.Session;


import sql.Answer;
import sql.SqlLogik;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;


/*

    Can be refactored even further by applying the Strategy Pattern

 */

public abstract class UserSession {

    SqlLogik sql;
    String username;
    boolean editAllowed = false;

    public UserSession(SqlLogik sql, String username) {
        this.sql = sql;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public boolean isEditAllowed() {
        return editAllowed;
    }

    public ArrayList<String> loadCategories(String subject, String filterText) throws Exception {

        if (filterText.isEmpty()) {

                sql.loadCategories(subject);

        } else {

                sql.loadFilteredCategories(subject, filterText);
        }

        return sql.getCategories();

    }

    public abstract ArrayList<String> loadSubjects(String filterText) throws Exception;

    public abstract ArrayList<String> loadTaskBlocks(String category, String filterText) throws Exception;

    public abstract void loadTaskBlock(String blockName, String category);

    public ArrayList<String>loadQuestions(String block) throws SQLException {
        sql.loadQuestions(block);
        return sql.getQuestions();
    }

    public ArrayList<String> loadAnswers(String block, String question) throws SQLException {
        sql.loadAnswers(block, question);
        return sql.getAnswersTemp();
    }

    public abstract void addCategory(String category, String subject) throws SQLException;

    public abstract void openTaskBlockCreator(String category);

    public abstract void createBlock(String block, String category, HashMap<String, ArrayList<Answer>> tasks) throws SQLException;

    public abstract void updateBlock(String block, String category, HashMap<String, ArrayList<Answer>> tasks) throws SQLException;

    public abstract void startBlock(String block) throws SQLException;

    public abstract void checkAnswer(String block, String question, String answer) throws SQLException;

}
