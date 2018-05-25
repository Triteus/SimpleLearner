package main.Session;

import sql.SqlLogik;

public class TeacherSession extends EditSession {

    public TeacherSession(SqlLogik sql, String username) {
        super(sql, username);
        editAllowed = true;
    }

}
