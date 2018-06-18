package main.Session;

import javafx.stage.Stage;
import main.models.Answer;
import main.models.Block;
import main.models.Task;
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

    public abstract void openTaskBlock(String blockName, String category, Stage mainStage) throws SQLException;

    public ArrayList<String>loadQuestions(String block) throws SQLException {
        sql.loadQuestions(block);
        return sql.getQuestions();
    }

    public ArrayList<Answer> loadAnswers(String block, String question) throws SQLException {
        sql.loadAnswers(block, question);
        return sql.getAnswersTemp();
    }

    public abstract void startBlock(String block) throws SQLException;

    public abstract boolean checkAnswer(String block, String question, String answer) throws SQLException;

    public abstract ArrayList<String> loadStudentsWhoSolvedTaskBlock(String blockName) throws SQLException;

    public Block loadTaskBlock(String taskBlockName) throws SQLException {

        ArrayList<Task> tasks = loadTasks(taskBlockName);
        Block block = new Block(taskBlockName, tasks);
        block.setCurrTask(0);

        return block;
    }

    private ArrayList<Task> loadTasks(String taskBlockName) throws SQLException {

        ArrayList<String> questions = new ArrayList<>();
        ArrayList<Task> tasks = new ArrayList<>();

        questions = loadQuestions(taskBlockName );

        for(String question : questions) {
            ArrayList<Answer> answers = new ArrayList<>();
            answers = loadAnswersForCurrentQuestion(taskBlockName, question);

            //ArrayList needs to be cloned. Otherwise, it will be referenced by every task so that only the answers for the last question remain.
            tasks.add(new Task(question, (ArrayList<Answer>)answers.clone()));

        }
        return tasks;
    }

    private ArrayList<Answer> loadAnswersForCurrentQuestion(String blockName, String currQuestion) throws SQLException {
        return loadAnswers(blockName, currQuestion );
    }

}
