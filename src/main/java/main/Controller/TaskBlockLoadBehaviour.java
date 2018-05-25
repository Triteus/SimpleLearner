package main.Controller;

import main.Session.UserSession;
import main.models.Answer;
import main.models.Block;
import main.models.Task;

import java.sql.SQLException;
import java.util.ArrayList;

 class TaskBlockLoadBehaviour {

    static Block createBlock(String taskBlockName, UserSession uSession) throws SQLException {

        ArrayList<Task> tasks = loadTasks(taskBlockName, uSession);
        Block block = new Block(taskBlockName, tasks);
        block.setCurrTask(0);

        return block;
    }

   private static ArrayList<Task> loadTasks(String taskBlockName, UserSession uSession) throws SQLException {

        ArrayList<String> questions = new ArrayList<>();
        ArrayList<Task> tasks = new ArrayList<>();

            questions = uSession.loadQuestions(taskBlockName );

            for(String question : questions) {
                ArrayList<Answer> answers = new ArrayList<>();
                answers = loadAnswersForCurrentQuestion(taskBlockName, question, uSession);

                //ArrayList needs to be cloned. Otherwise, it will be referenced by every task so that only the answers for the last question remain.
                tasks.add(new Task(question, (ArrayList<Answer>)answers.clone()));

            }

        return tasks;
    }

    private static ArrayList<Answer> loadAnswersForCurrentQuestion(String blockName, String currQuestion, UserSession uSession) throws SQLException {

            return uSession.loadAnswers(blockName, currQuestion );

    }

}
