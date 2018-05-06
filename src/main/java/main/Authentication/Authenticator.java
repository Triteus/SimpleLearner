package main.Authentication;

import main.Session.StudentSession;
import main.Session.TeacherSession;
import main.Session.UserSession;
import sql.SqlLogik;


public class Authenticator {

    public static UserSession login(String username, String password) throws Exception {

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
