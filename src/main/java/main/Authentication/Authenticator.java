package main.Authentication;

import main.Session.StudentSession;
import main.Session.TeacherSession;
import main.Session.UserSession;
import sql.SqlLogik;

import java.sql.SQLException;


public class Authenticator {


    /** Creates a user-specific session-object that is primarily used to access the database.
     * Used original login-implementation in SqlLogik from the old project, admin-login not possible yet.
     * @param username
     * @param password
     * @return A session encapsulating the user's priviliges or null if the login fails.
     * @throws Exception
     */
    public static UserSession login(String username, String password) throws SQLException {

        SqlLogik sql = new SqlLogik();

        boolean [] check = sql.checkLogin(username, password);

        if(check[0]) {

            if(check[1]) {
                return new TeacherSession(sql, username);
            } else {
                return new StudentSession(sql, username);
            }

        } else {
            return null;
        }

    }

}
