package main.Session;

import sql.SqlLogik;

import java.sql.SQLException;
import java.util.ArrayList;

public class TeacherSession extends EditSession {

    public TeacherSession(SqlLogik sql, String username) {
        super(sql, username);
        editAllowed = true;
    }

    @Override
    public ArrayList<String> loadSubjects(String filter) throws SQLException {

        if (filter.isEmpty()) {
            sql.loadSubjects(username);

        } else {
            sql.loadFilteredSubjects(username, filter);
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

}
