package sql;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class SqlLogikTest {

    private static Connection connection;
    private static SqlImplementation sqlImplementation;

    @BeforeAll
    static void initialize() throws SQLException {
        String dbURL = "jdbc:mysql://localhost:3306/simple_learner_test?useSSL=true";
        Properties userInfo = new Properties();
        userInfo.put("user", "simple-learner-test");
        userInfo.put("password", "SimpleLearner");
        connection = DriverManager.getConnection(dbURL, userInfo);

        sqlImplementation = new SqlImplementation();

        Statement statement = connection.createStatement();
        statement.execute("INSERT INTO schueler(sid, passwort, vorname, nachname) VALUES " +
                "('test', 'password123', 'Test', 'User')");
    }

    @AfterAll
    static void finish() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("DELETE FROM schueler WHERE sid = 'test'");
    }

    @Test
    void checkAnswer() {
    }

    @Test
    void checkLogin1() throws SQLException {
        boolean[] checkedLogin = sqlImplementation.checkLogin("test", "password123", connection);
        assertTrue(checkedLogin[0]);
        assertFalse(checkedLogin[1]);
    }

    @Test
    void checkLogin2() throws SQLException {
        boolean[] checkedLogin = sqlImplementation.checkLogin("test", "password456", connection);
        assertFalse(checkedLogin[0]);
    }

    @Test
    void checkLogin3() throws SQLException {
        boolean[] checkedLogin = sqlImplementation.checkLogin("test", "", connection);
        assertFalse(checkedLogin[0]);
    }

    @Test
    void checkLogin4() throws SQLException {
        boolean[] checkedLogin = sqlImplementation.checkLogin("", "password123", connection);
        assertFalse(checkedLogin[0]);
    }

    @Test
    void deleteBlock() {
    }

    @Test
    void createBlock() {
    }

    @Test
    void createCategory() {
    }

    @Test
    void createAnswer() {
    }

    @Test
    void updateQuiz() {
    }

    @Test
    void updateTask() {
    }

    @Test
    void updateAnswers() {
    }
}