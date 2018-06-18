package main.Session;


/*
Has rights to delete all subjects, categories and tasks and can alternate accounts.
 */

import main.models.User;
import sql.SqlLogik;

import java.sql.SQLException;
import java.util.ArrayList;

public class AdminSession extends EditSession {


    public AdminSession(SqlLogik sql, String username) {
        super(sql, username);
    }

/*
    Sees all subjects like a student (todo: Strategy-Pattern)
 */

    @Override
    public ArrayList<String> loadSubjects(String filter) throws SQLException {

        if (filter.isEmpty()) {
            sql.loadSubjects();

        } else {
            sql.loadFilteredSubjects(filter);
        }

        return sql.getSubjects();

    }

    /* Admin needs to see every single taskblock in a category */

    @Override
    public ArrayList<String> loadTaskBlocks(String category, String filter) throws Exception {
        return null;
    }

    public void createTeacher(User user) {

    }

    public void createStudent(User user) {

    }

    public void deleteTeacher(String username) {

    }

    public void deleteStudent(String username) {

    }

    public void changeStudent(User user) {

    }

    public void changeTeacher(User user) {

    }


}
