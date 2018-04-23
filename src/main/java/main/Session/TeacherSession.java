package main.Session;

import main.Session.UserSession;
import sql.SqlLogik;
import java.sql.SQLException;
import java.util.ArrayList;

public class TeacherSession extends UserSession {

    public TeacherSession(SqlLogik sql, String username) {
        super(sql, username);
        editAllowed = true;
    }

    @Override
    public ArrayList<String> loadSubjects(String filter) {

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
    public ArrayList<String> loadTaskBlocks(String category, String filter) throws Exception {
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
    public void loadTaskBlock(String blockName) {

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

    @Override
    public void addCategory(String category, String subject) throws SQLException {

        sql.createCategory(category, subject);
    }
}
