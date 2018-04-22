package main;

import sql.SqlLogik;


class Authenticator {

    static UserInstance login(String username, String password) throws Exception {

        SqlLogik sql = new SqlLogik();

        boolean [] check = sql.checkLogin(username, password);

        if(check[0]) {
            if(check[1]) {
                return new Teacher(sql);
            } else {
                return new Student(sql);
            }

        } else {
            return null;
        }

    }

}
