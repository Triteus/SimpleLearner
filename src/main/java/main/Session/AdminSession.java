package main.Session;


/*
Has rights to delete all subjects, categories and tasks and can alternate accounts.
 */

import sql.SqlLogik;

import java.sql.SQLException;
import java.util.ArrayList;

public class AdminSession extends EditSession {


    public AdminSession(SqlLogik sql, String username) {
        super(sql, username);
    }

/*
    Sees all subjects like a student

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

    @Override
    public ArrayList<String> loadTaskBlocks(String category, String filter) throws Exception {
        return null;
    }



}
