package sql;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class SqlLogikTest {

    private static Connection connection;
    private static SqlLogik sqlLogik;

    @BeforeAll
    static void initialize() throws SQLException {
        String dbURL = "jdbc:mysql://localhost:3306/simple_learner_test?useSSL=true";
        Properties userInfo = new Properties();
        userInfo.put("user", "simple-learner-test");
        userInfo.put("password", "SimpleLearner");
        connection = DriverManager.getConnection(dbURL, userInfo);

        sqlLogik = new SqlLogik();

        Statement statement = connection.createStatement();
        statement.execute("INSERT INTO schueler(sid, passwort, vorname, nachname) VALUES " +
                "('test', 'password123', 'Test', 'User')");
    }

    @AfterAll
    static void finish() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("DELETE FROM schueler WHERE sid = 'test'");
        statement.execute("DELETE FROM kategorie WHERE kid='Analysis'");
    }

    @Test
    void checkAnswer() {
    }

    @Test
    void checkLogin1() throws SQLException {
        boolean[] checkedLogin = sqlLogik.checkLogin("test", "password123", connection);
        assertTrue(checkedLogin[0]);
        assertFalse(checkedLogin[1]);
    }

    @Test
    void checkLogin2() throws SQLException {
        boolean[] checkedLogin = sqlLogik.checkLogin("test", "password456", connection);
        assertFalse(checkedLogin[0]);
    }

    @Test
    void checkLogin3() throws SQLException {
        boolean[] checkedLogin = sqlLogik.checkLogin("test", "", connection);
        assertFalse(checkedLogin[0]);
    }

    @Test
    void checkLogin4() throws SQLException {
        boolean[] checkedLogin = sqlLogik.checkLogin("", "password123", connection);
        assertFalse(checkedLogin[0]);
    }

    @Test
    void deleteBlock() {
    }

    @Test
    void createBlock() {
    }

    @Test
    void createCategory1() throws SQLException {
        sqlLogik.createCategory("Analysis", "Mathematik", connection);

        Statement statement = connection.createStatement();
        ResultSet resultSet =  statement.executeQuery("SELECT COUNT(*) FROM kategorie WHERE kid='Analysis'");

        resultSet.next();
        assertEquals(1, resultSet.getInt("COUNT(*)"));
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