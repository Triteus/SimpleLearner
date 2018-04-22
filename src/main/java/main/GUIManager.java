package main;


import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import sql.SqlImplementation;
import sql.SqlLogik;

import java.sql.SQLException;
import java.util.ArrayList;

public class GUIManager {

    private static String username;
    private static UserInstance userInstance;
    private static SqlLogik sql = new SqlLogik();

    public static void setUsername( String uname) {
        username = uname;
    }

    public static String getUsername() {
        return userInstance.getUsername();
    }

    static boolean initUser(String username, String password) throws Exception {

        boolean [] check = sql.checkLogin(username, password);

        if(!check[0]) {
            return false;
        } else {
            if(check[1]) {
                userInstance = new Teacher(username);
            } else {
                userInstance = new Student(username);
            }
        }
        return true;

    }


    static ArrayList<String> loadSubjects() throws Exception {

        return userInstance.loadSubjects(sql, "");
    }

    static ArrayList<String> loadCategories(String subjectName) throws Exception {


        //return userInstance.loadCategories(sql, catName, "" );

        return userInstance.loadCategories(sql, subjectName, "");
    }

    static ArrayList<String> loadTaskBlocks(String catName ) throws Exception {

        return userInstance.loadTaskBlocks(sql, catName, "");
    }

}
