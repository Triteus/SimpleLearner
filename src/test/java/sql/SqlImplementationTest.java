package sql;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class SqlImplementationTest {
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

    @Test
    void getUserInfo() {

    }

    @Test
    void getSubjects() {
    }

    @Test
    void getCategories() {
    }

    @Test
    void getTaskBlocks() {
    }

    @Test
    void getTaskBloksTemp() {
    }

    @Test
    void getQuestions() {
    }

    @Test
    void getAnswersTemp() {
    }

    @Test
    void getStudentsSolved() {
    }

    @Test
    void getAnswersStudentsTemp() {
    }

    @Test
    void isFinished() {
    }

    @Test
    void setFinished() {
    }

    @Test
    void getCurrentUser() {
    }

    @Test
    void setCurrentUser() {
    }

    @Test
    void startBlock() {
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
    void loadFaecher() {
    }

    @Test
    void loadFaecher1() {
    }

    @Test
    void loadFilteredFaecher() {
    }

    @Test
    void loadFilteredFaecher1() {
    }

    @Test
    void loadKategorien() {
    }

    @Test
    void loadFilteredKategorien() {
    }

    @Test
    void loadBloeckeLehrer() {
    }

    @Test
    void loadBloeckeSchueler() {
    }

    @Test
    void loadFilteredBloeckeLehrer() {
    }

    @Test
    void loadFilteredBloeckeSchueler() {
    }

    @Test
    void loadFragen() {
    }

    @Test
    void loadAntworten() {
    }

    @Test
    void deleteBlock() {
    }

    @Test
    void createBlock() {
    }

    @Test
    void createKategorie() {
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

    @Test
    void loadAbsolvierteSchueler() {
    }

    @Test
    void loadAbsolvierteAntworten() {
    }
}