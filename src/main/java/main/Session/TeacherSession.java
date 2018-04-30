package main.Session;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.Controller.TaskBlockEditController;
import sql.Answer;
import sql.SqlLogik;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class TeacherSession extends EditSession {

    public TeacherSession(SqlLogik sql, String username) {
        super(sql, username);
        editAllowed = true;
    }

    @Override
    public void startBlock(String block) {

    }

    @Override
    public void checkAnswer(String block, String question, String answer) {

    }


}
