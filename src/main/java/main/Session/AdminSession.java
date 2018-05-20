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
    public ArrayList<String> loadSubjects(String filter) {

        if (filter.isEmpty()) {
            try {
                sql.loadSubjects();
            } catch (SQLException exc) {
                System.out.println(exc.getMessage());
            }
        } else {
            try {
                sql.loadFilteredSubjects(filter);
            } catch (SQLException exc) {
                System.out.println(exc.getMessage());
            }
        }

        return sql.getSubjects();

    }

    @Override
    public void startBlock(String block) {

    }




}
