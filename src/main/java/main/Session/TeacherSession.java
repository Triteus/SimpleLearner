package main.Session;

import sql.SqlLogik;

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
