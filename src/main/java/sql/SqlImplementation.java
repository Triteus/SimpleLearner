/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sql;

import Pdf.AntwortPdfObjekt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Stellt die Methoden für das Laden der Daten aus der Datenbank zur Verfügung
 *
 * @author Marcel
 */
public class SqlImplementation {

    private Properties userInfo;
    private ArrayList<String> subjects;
    private ArrayList<String> categories;
    private ArrayList<String> taskBlocks;
    private ArrayList<String> taskBloksTemp;
    private ArrayList<String> questions;
    private ArrayList<String> answersTemp;
    private ArrayList<String> studentsSolved;
    private ArrayList<AntwortPdfObjekt> answersStudentsTemp;
    private String currentUser;
    private boolean finished;

    public Properties getUserInfo() {
        return userInfo;
    }

    public ArrayList<String> getSubjects() {
        return subjects;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public ArrayList<String> getTaskBlocks() {
        return taskBlocks;
    }

    public ArrayList<String> getTaskBloksTemp() {
        return taskBloksTemp;
    }

    public ArrayList<String> getQuestions() {
        return questions;
    }

    public ArrayList<String> getAnswersTemp() {
        return answersTemp;
    }

    public ArrayList<String> getStudentsSolved() {
        return studentsSolved;
    }

    public ArrayList<AntwortPdfObjekt> getAnswersStudentsTemp() {
        return answersStudentsTemp;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    public SqlImplementation() {
        userInfo = new Properties();
        userInfo.put("user", "root"); //Username of the MySQL Database
        userInfo.put("password", "***********"); //Password of the MySQL Database
        subjects = new ArrayList<>();
        categories = new ArrayList<>();
        taskBlocks = new ArrayList<>();
        taskBloksTemp = new ArrayList<>();
        questions = new ArrayList<>();
        answersTemp = new ArrayList<>();
        studentsSolved = new ArrayList<>();
        answersStudentsTemp = new ArrayList<>();
    }

    /**
     * Initializes a table that is used to check whether a student has solved a task
     *
     * @param blockDesignator - The name of the task block
     * @param aStudent        - The name of the student solving
     * @throws SQLException
     */
    public void startBlock(String blockDesignator, String aStudent) throws SQLException {
        String startString = "insert into schuelerloestblock(schueler, block) values(?, ?);";
        String searchString = "select block, schueler from schuelerloestblock;";
        ResultSet rsSearch = null;
        boolean confirm = true;

        try (Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/SimpleLearner?useSSL=true", userInfo);
             PreparedStatement stmtStart = myConn.prepareStatement(startString);
             Statement stmtSearch = myConn.createStatement()) {

            rsSearch = stmtSearch.executeQuery(searchString);
            while (rsSearch.next()) {
                if (rsSearch.getString("block").equals(blockDesignator)) {
                    if (rsSearch.getString("student").equals(aStudent)) {
                        confirm = false;
                    }
                }
            }
            if (confirm) {
                stmtStart.setString(1, aStudent);
                stmtStart.setString(2, blockDesignator);

                stmtStart.executeUpdate();
            }
        } finally {
            if (rsSearch != null) {
                rsSearch.close();
            }
        }
    }

    /**
     * Überprüft die Antwort des Schülers zur jeweiligen Aufgabenstellung
     *
     * @param blockDesignator - Der Name des Aufgabenblocks
     * @param student         - Der Name(Username) des aktiven Schülers
     * @param question        - Der Fragetext der aktuellen Aufgabe
     * @param answer          - Die Antwort des Schülers
     * @return Gibt einen boolean zur Anzeige zurück
     * @throws SQLException
     */
    public boolean checkAnswer(String blockDesignator, String student, String question, String answer) throws SQLException {

        String answerQuery = "insert into schuelerloestaufgabe(aufgabe, schueler, antwortS) values((select aufgabe.aid from aufgabe "
                + "where aufgabe.block = ? and aufgabe.frage = ?), ?, ?);";

        String stmtString = "select schuelerloestaufgabe.schueler, schuelerloestaufgabe.antwortS, antwort.isTrue from schuelerloestaufgabe "
                + "join aufgabe on schuelerloestaufgabe.aufgabe = aufgabe.aid "
                + "join schueler on schuelerloestaufgabe.schueler = schueler.SID "
                + "join antwort on aufgabe.aid = antwort.aufgabe "
                + "where aufgabe.frage = ? and antwort.antworttext = schuelerloestaufgabe.antwortS and schuelerloestaufgabe.schueler = ?;";

        /*"select isTrue from antwort join aufgabe on antwort.aufgabe = aufgabe.aid"
                + " join block on aufgabe.block = block.bid where block.bid = ? and aufgabe.frage = ? and antwort.antworttext = ?";*/
        ResultSet rsAntwort = null;

        boolean check = false;

        try (Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/SimpleLearner?useSSL=true", userInfo);
             PreparedStatement setAntwort = myConn.prepareStatement(answerQuery);
             PreparedStatement stmtAntwort = myConn.prepareStatement(stmtString)) {

            setAntwort.setString(1, blockDesignator);
            setAntwort.setString(2, question);
            setAntwort.setString(3, student);
            setAntwort.setString(4, answer);
            setAntwort.execute();

            stmtAntwort.setString(1, question);
            stmtAntwort.setString(2, student);
            rsAntwort = stmtAntwort.executeQuery();

            while (rsAntwort.next()) {
                if (rsAntwort.getString("antwort.isTrue").equals("1")) {
                    System.out.println(rsAntwort.getString("antwort.isTrue"));
                    check = true;
                }
            }
        } catch (SQLException exc) {
            throw exc;
        } finally {
            if (rsAntwort != null) {
                rsAntwort.close();
            }
        }
        return check;
    }

    /**
     * Überprüft den Login des Users
     *
     * @param user     - der Nutzername
     * @param password - das Nutzerpasswort
     * @return Gibt einen Zweistelligen Boolean-Array zurück. Boolean[0] gibt
     * an, ob die eingegebenen Daten korrekt sind. Boolean[1] gibt an ob es ein
     * Schüler oder ein Lehrer ist um die jeweilige Oberfläche zu laden.
     * @throws SQLException
     */
    public boolean[] checkLogin(String user, String password, Connection connection) throws SQLException {

        String stringCheck = "select * from lehrer, schueler";
        boolean[] checkPassword = new boolean[2];

        Statement stmtCheck = connection.createStatement();
        ResultSet rsCheck = stmtCheck.executeQuery(stringCheck);

        while (rsCheck.next()) {
            if (rsCheck.getString("lid").equals(user)) {
                checkPassword[0] = rsCheck.getString("lehrer.passwort").equals(password);
                if (checkPassword[0]) {
                    checkPassword[1] = true; //1 für Lehrer
                    currentUser = rsCheck.getString("lehrer.vorname") + " " + rsCheck.getString("lehrer.nachname");
                }
            } else if (rsCheck.getString("sid").equals(user)) {
                checkPassword[0] = rsCheck.getString("schueler.passwort").equals(password);
                if (checkPassword[0]) {
                    checkPassword[1] = false;//0 für Schüler
                    currentUser = rsCheck.getString("schueler.vorname") + " " + rsCheck.getString("schueler.nachname");
                }
            }
        }

        return checkPassword;
    }

    /**
     * Lädt alle vorhandenen Fächer für die Schüler
     *
     * @throws SQLException
     */
    public void loadFaecher() throws SQLException {
        try (Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/SimpleLearner?useSSL=true", userInfo);
             Statement stmtFach = myConn.createStatement();
             ResultSet rsFach = stmtFach.executeQuery("select fid from fach;")) {

            if (subjects != null) {
                subjects.clear();
            }

            while (rsFach.next()) {
                subjects.add(rsFach.getString("fid"));
            }

        } catch (SQLException exc) {
            throw exc;
        }
    }

    /**
     * Lädt nur Fächer, für welche der Lehrer eine Qualifikation besitzt, für
     * den Lehrer
     *
     * @param lehrer
     * @throws SQLException
     */
    public void loadFaecher(String lehrer) throws SQLException {
        String faecherString = "select fach from lehrerunterrichtet where lehrer = ?";
        ResultSet rsFach = null;
        try (Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/SimpleLearner?useSSL=true", userInfo);
             PreparedStatement stmtFach = myConn.prepareStatement(faecherString)) {

            stmtFach.setString(1, lehrer);
            rsFach = stmtFach.executeQuery();

            if (subjects != null) {
                subjects.clear();
            }

            while (rsFach.next()) {
                subjects.add(rsFach.getString("fach"));
            }

        } catch (SQLException exc) {
            throw exc;
        } finally {
            if (rsFach != null) {
                rsFach.close();
            }
        }
    }

    /**
     * Filtert alle Fächer für die Schüler
     *
     * @param filter - der zu filternde String
     * @throws SQLException
     */
    public void loadFilteredFaecher(String filter) throws SQLException {
        String faecherString = "select fid from fach where fid like ?";
        ResultSet rsFach = null;
        try (Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/SimpleLearner?useSSL=true", userInfo);
             PreparedStatement stmtFach = myConn.prepareStatement(faecherString)) {

            stmtFach.setString(1, "%" + filter + "%");
            rsFach = stmtFach.executeQuery();

            if (subjects != null) {
                subjects.clear();
            }

            while (rsFach.next()) {
                subjects.add(rsFach.getString("fid"));
            }

        } catch (SQLException exc) {
            throw exc;
        } finally {
            if (rsFach != null) {
                rsFach.close();
            }
        }
    }

    /**
     * Filtert alle Fächer für den Lehrer
     *
     * @param lehrer - der jeweilige Lehrer
     * @param filter - der zu filternde String
     * @throws SQLException
     */
    public void loadFilteredFaecher(String lehrer, String filter) throws SQLException {
        String faecherString = "select fach from lehrerunterrichtet where lehrer = ? and fach like ?;";
        ResultSet rsFach = null;
        try (Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/SimpleLearner?useSSL=true", userInfo);
             PreparedStatement stmtFach = myConn.prepareStatement(faecherString)) {

            stmtFach.setString(1, lehrer);
            stmtFach.setString(2, "%" + filter + "%");
            rsFach = stmtFach.executeQuery();

            if (subjects != null) {
                subjects.clear();
            }

            while (rsFach.next()) {
                subjects.add(rsFach.getString("fach"));
            }

        } catch (SQLException exc) {
            throw exc;
        } finally {
            if (rsFach != null) {
                rsFach.close();
            }
        }
    }

    /**
     * Lädt alle vorhandenen Kategorien für das aufgerufene Fach
     *
     * @param fach - das spezifische Fach
     * @throws SQLException
     */
    public void loadKategorien(String fach) throws SQLException {
        String kategorieString = "select kid from kategorie where fach = ?";
        ResultSet rsFach = null;
        try (Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/SimpleLearner?useSSL=true", userInfo);
             PreparedStatement stmtFach = myConn.prepareStatement(kategorieString)) {

            stmtFach.setString(1, fach);
            rsFach = stmtFach.executeQuery();

            if (categories != null) {
                categories.clear();
            }

            while (rsFach.next()) {
                categories.add(rsFach.getString("kid"));
            }

        } catch (SQLException exc) {
            throw exc;
        } finally {
            if (rsFach != null) {
                rsFach.close();
            }
        }
    }

    /**
     * Filtert alle Kategorien
     *
     * @param fach   - das ausgewählte Fach (benötigt, da Liste komplett neu aus
     *               der Datenbank geladen wird)
     * @param filter - der zu filternde String
     * @throws SQLException
     */
    public void loadFilteredKategorien(String fach, String filter) throws SQLException {
        String kategorieString = "select kid from kategorie where fach = ? and kid like ?";
        ResultSet rsFach = null;
        try (Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/SimpleLearner?useSSL=true", userInfo);
             PreparedStatement stmtFach = myConn.prepareStatement(kategorieString)) {

            stmtFach.setString(1, fach);
            stmtFach.setString(2, "%" + filter + "%");
            rsFach = stmtFach.executeQuery();

            if (categories != null) {
                categories.clear();
            }

            while (rsFach.next()) {
                categories.add(rsFach.getString("kid"));
            }

        } catch (SQLException exc) {
            throw exc;
        } finally {
            if (rsFach != null) {
                rsFach.close();
            }
        }
    }

    /**
     * Lädt alle Blöcke, die der Lehrer selbst erstellt hat
     *
     * @param kategorie - die aktive Kategorie
     * @param lehrer    - der aktive Lehrer
     * @throws SQLException
     */
    public void loadBloeckeLehrer(String kategorie, String lehrer) throws SQLException {
        String blockString = "select bid from block where kategorie = ? and lehrer = ?;";
        ResultSet rsBlock = null;
        try (Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/SimpleLearner?useSSL=true", userInfo);
             PreparedStatement stmtBlock = myConn.prepareStatement(blockString)) {

            stmtBlock.setString(1, kategorie);
            stmtBlock.setString(2, lehrer);
            rsBlock = stmtBlock.executeQuery();
            if (taskBlocks != null) {
                taskBlocks.clear();
            }

            while (rsBlock.next()) {
                taskBlocks.add(rsBlock.getString("bid"));
            }
        } catch (SQLException exc) {
            throw exc;
        } finally {
            if (rsBlock != null) {
                rsBlock.close();
            }
        }
    }

    /**
     * Lädt alle vorhandenen Blöcke der aktiven Kategorie
     *
     * @param kategorie - aktive Kategorie
     * @param schueler  - aktiver Schüler
     * @throws SQLException
     */
    public void loadBloeckeSchueler(String kategorie, String schueler) throws SQLException {
        String blockString = "select bid from block where kategorie = ?";
        String blockStringTwo = "select block.bid, schuelerloestblock.block from block "
                + "left join schuelerloestblock on block.bid = schuelerloestblock.block "
                + "where block.kategorie = ? and schuelerloestblock.schueler = ?;";

        ResultSet rsBlock = null;
        ResultSet rsBlockTwo = null;
        try (Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/SimpleLearner?useSSL=true", userInfo);
             PreparedStatement stmtBlock = myConn.prepareStatement(blockString);
             PreparedStatement stmtBlockTwo = myConn.prepareStatement(blockStringTwo);) {

            //alle Kategorien werden geladen
            stmtBlock.setString(1, kategorie);
            rsBlock = stmtBlock.executeQuery();
            if (taskBlocks != null) {
                taskBlocks.clear();
            }

            while (rsBlock.next()) {
                taskBlocks.add(rsBlock.getString("bid"));
            }

            //nur eroeffnete Kategorien werden geladen
            stmtBlockTwo.setString(1, kategorie);
            stmtBlockTwo.setString(2, schueler);
            rsBlockTwo = stmtBlockTwo.executeQuery();
            if (taskBloksTemp != null) {
                taskBloksTemp.clear();
            }
            while (rsBlockTwo.next()) {
                taskBloksTemp.add(rsBlockTwo.getString("block.bid"));
            }

            //eroffnete Kategorien werden allen Kategorien entfernt, damit fuer den jeweiligen Schueler nur die Bloecke erscheinen, die er noch nicht bearbeitet hat
            for (int i = 0; i < taskBlocks.size(); i++) {
                if (!taskBloksTemp.isEmpty()) {
                    for (int j = 0; j < taskBloksTemp.size(); j++) {
                        if (taskBlocks.get(i).equals(taskBloksTemp.get(j))) {
                            taskBlocks.remove(i);
                        }
                    }
                }
            }

        } catch (SQLException exc) {
            throw exc;
        } finally {
            if (rsBlock != null) {
                rsBlock.close();
            }
            if (rsBlockTwo != null) {
                rsBlockTwo.close();
            }
        }
    }

    /**
     * Filtert die Blöcke des Lehrers
     *
     * @param kategorie - die aktive Kategorie
     * @param lehrer    - der aktive Lehrer
     * @param filter    - der zu filternde String
     * @throws SQLException
     */
    public void loadFilteredBloeckeLehrer(String kategorie, String lehrer, String filter) throws SQLException {
        String blockString = "select bid from block where kategorie = ? and lehrer = ? and bid like ?;";
        ResultSet rsBlock = null;
        try (Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/SimpleLearner?useSSL=true", userInfo);
             PreparedStatement stmtBlock = myConn.prepareStatement(blockString)) {

            stmtBlock.setString(1, kategorie);
            stmtBlock.setString(2, lehrer);
            stmtBlock.setString(3, "%" + filter + "%");
            rsBlock = stmtBlock.executeQuery();
            if (taskBlocks != null) {
                taskBlocks.clear();
            }

            while (rsBlock.next()) {
                taskBlocks.add(rsBlock.getString("bid"));
            }
        } catch (SQLException exc) {
            throw exc;
        } finally {
            if (rsBlock != null) {
                rsBlock.close();
            }
        }
    }

    /**
     * Filtert alle Blöcke der aktiven Kategorie für den Schüler
     *
     * @param kategorie - die aktive Kategorie
     * @param schueler  - der aktive Schüler
     * @param filter    - der zu filternde String
     * @throws SQLException
     */
    public void loadFilteredBloeckeSchueler(String kategorie, String schueler, String filter) throws SQLException {
        String blockString = "select bid from block where kategorie = ?";
        String blockStringTwo = "select block.bid, schuelerloestblock.block from block "
                + "left join schuelerloestblock on block.bid = schuelerloestblock.block "
                + "where block.kategorie = ? and schuelerloestblock.schueler = ?;";

        ResultSet rsBlock = null;
        ResultSet rsBlockTwo = null;
        try (Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/SimpleLearner?useSSL=true", userInfo);
             PreparedStatement stmtBlock = myConn.prepareStatement(blockString);
             PreparedStatement stmtBlockTwo = myConn.prepareStatement(blockStringTwo);) {

            //alle Kategorien werden geladen
            stmtBlock.setString(1, kategorie);
            rsBlock = stmtBlock.executeQuery();
            if (taskBlocks != null) {
                taskBlocks.clear();
            }

            while (rsBlock.next()) {
                taskBlocks.add(rsBlock.getString("bid"));
            }

            //nur eroeffnete Kategorien werden geladen
            stmtBlockTwo.setString(1, kategorie);
            stmtBlockTwo.setString(2, schueler);
            rsBlockTwo = stmtBlockTwo.executeQuery();
            if (taskBloksTemp != null) {
                taskBloksTemp.clear();
            }
            while (rsBlockTwo.next()) {
                taskBloksTemp.add(rsBlockTwo.getString("block.bid"));
            }

            //eroffnete Kategorien werden allen Kategorien entfernt, damit fuer den jeweiligen Schueler nur die Bloecke erscheinen, die er noch nicht bearbeitet hat
            for (int i = 0; i < taskBlocks.size(); i++) {
                if (!taskBloksTemp.isEmpty()) {
                    for (int j = 0; j < taskBloksTemp.size(); j++) {
                        if (taskBlocks.get(i).equals(taskBloksTemp.get(j))) {
                            taskBlocks.remove(i);
                        }
                    }
                }
            }
            for (int i = 0; i < taskBlocks.size(); i++) {
                System.out.println(taskBlocks.get(i) + "in Logik ArrayList davor");
            }
            taskBlocks = filterList(taskBlocks, filter);
            for (int i = 0; i < taskBlocks.size(); i++) {
                System.out.println(taskBlocks.get(i) + "in Logik ArrayList danach");
            }

        } catch (SQLException exc) {
            throw exc;
        } finally {
            if (rsBlock != null) {
                rsBlock.close();
            }
            if (rsBlockTwo != null) {
                rsBlockTwo.close();
            }
        }
    }

    /**
     * Lädt alle Fragen innerhalb eines Aufgabenblocks in eine Liste
     *
     * @param block - der spezifizierte Block
     * @throws SQLException
     */
    public void loadFragen(String block) throws SQLException {
        String stringFrage = "select frage from aufgabe join block on aufgabe.block = block.bid where block.bid = ?";
        ResultSet rsFrage = null;
        try (Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/SimpleLearner?useSSL=true", userInfo);
             PreparedStatement stmtFrage = myConn.prepareStatement(stringFrage)) {

            stmtFrage.setString(1, block);
            rsFrage = stmtFrage.executeQuery();

            questions.clear();

            while (rsFrage.next()) {
                questions.add(rsFrage.getString("frage"));
            }
            for (int i = 0; i < questions.size(); i++) {
                System.out.println(questions.get(i) + " in Logik");
            }
        } catch (SQLException exc) {
            throw exc;
        } finally {
            if (rsFrage != null) {
                rsFrage.close();
            }
        }
    }

    /**
     * Lädt alle Antwortmöglichkeiten der jeweiligen Frage temporär in eine
     * Liste
     *
     * @param block - der aktuelle Block
     * @param frage - die aktuelle Frage
     * @throws SQLException
     */
    public void loadAntworten(String block, String frage) throws SQLException {
        String stringAntworten = "select antworttext from antwort join aufgabe on antwort.aufgabe = aufgabe.aid "
                + "join block on aufgabe.block = block.bid where block.bid = ? and aufgabe.frage = ?";
        ResultSet rsAntworten = null;
        try (Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/SimpleLearner?useSSL=true", userInfo);
             PreparedStatement stmtAntworten = myConn.prepareStatement(stringAntworten)) {

            stmtAntworten.setString(1, block);
            stmtAntworten.setString(2, frage);
            rsAntworten = stmtAntworten.executeQuery();

            answersTemp.clear();

            while (rsAntworten.next()) {
                answersTemp.add(rsAntworten.getString("antworttext"));
            }
        } catch (SQLException exc) {
            throw exc;
        } finally {
            if (rsAntworten != null) {
                rsAntworten.close();
            }
        }
    }

    /**
     * Löscht den gewünschten Aufgabenblock
     *
     * @param blockname - der ausgewählte Aufgabenblock
     * @param lehrer    - der durchführende Lehrer
     * @param kategorie - die Kategorie, in welcher sich der Aufgabenblock
     *                  befindet
     * @throws SQLException
     */
    public void deleteBlock(String blockname, String lehrer, String kategorie) throws SQLException {
        String loeschenString = "delete from block where bid = ? and lehrer = ? and kategorie = ?;";
        try (Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/SimpleLearner?useSSL=true", userInfo);
             PreparedStatement stmtLoeschen = myConn.prepareStatement(loeschenString)) {

            stmtLoeschen.setString(1, blockname);
            stmtLoeschen.setString(2, lehrer);
            stmtLoeschen.setString(3, kategorie);

            stmtLoeschen.executeUpdate();

        } catch (SQLException exc) {
            throw exc;
        }
    }

    /**
     * Erzeugt einen neuen Aufgabenblock für den Lehrer
     *
     * @param block     - der Name des neuen Blocks
     * @param lehrer    - der aktive Lehrer
     * @param kategorie - die Kategorie, in welcher sich der Aufgabenblock
     *                  befindet
     * @throws SQLException
     */
    public void createBlock(String block, String lehrer, String kategorie) throws SQLException {
        String createString = "insert into block(bid, lehrer, kategorie) values(?, ?, ?);";
        try (Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/SimpleLearner?useSSL=true", userInfo);
             PreparedStatement stmtUpdate = myConn.prepareStatement(createString)) {

            stmtUpdate.setString(1, block);
            stmtUpdate.setString(2, lehrer);
            stmtUpdate.setString(3, kategorie);

            stmtUpdate.executeUpdate();

        }

    }

    /**
     * Erzeugt eine neue Kategorie im aktiven Fach des Lehrers
     *
     * @param katName  - der Name der neuen Kategorie
     * @param fachName - der Name des aktiven Fachs
     * @throws SQLException
     */
    public void createKategorie(String katName, String fachName) throws SQLException {
        String createString = "insert into kategorie(kid, fach) values(?,?);";
        try (Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/SimpleLearner?useSSL=true", userInfo);
             PreparedStatement stmtUpdate = myConn.prepareStatement(createString)) {

            stmtUpdate.setString(1, katName);
            stmtUpdate.setString(2, fachName);

            stmtUpdate.executeUpdate();
        }
    }

    private void createAntwortenInAufgabe(String block, String frage, VBox vb) throws SQLException {
        for (int i = 0; i < vb.getChildren().size(); i++) {
            HBox hb = (HBox) vb.getChildren().get(i);
            for (int j = 0; j < hb.getChildren().size(); j++) {
                RadioButton rb = (RadioButton) hb.getChildren().get(j);
                j++;
                TextField tf = (TextField) hb.getChildren().get(j);
                if (rb.isSelected()) {
                    System.out.println("createAntwortenInAufgabe true");
                    createAntwort(tf.getText(), true, block, frage);
                } else {
                    System.out.println("createAntwortenInAufgabe false");
                    createAntwort(tf.getText(), false, block, frage);
                }
            }
        }
    }

    private void createAntwort(String antworttext, boolean isRichtig, String block, String frage) throws
            SQLException {
        String insertAntwort = "insert into antwort(antworttext, istrue, aufgabe) values(?, ?, (select aufgabe.aid from aufgabe where "
                + "aufgabe.block = ? and aufgabe.frage = ?));";

        try (Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/SimpleLearner?useSSL=true", userInfo);
             PreparedStatement stmtNewAntwort = myConn.prepareStatement(insertAntwort)) {

            stmtNewAntwort.setString(1, antworttext);
            stmtNewAntwort.setBoolean(2, isRichtig);
            stmtNewAntwort.setString(3, block);
            stmtNewAntwort.setString(4, frage);

            System.out.println("createAntwort davor");
            stmtNewAntwort.executeUpdate();
        }

    }

    /**
     * Ändert den Namen des aktiven Blocks
     *
     * @param blockAlt - alter Blockname
     * @param lehrer   - bearbeitender Lehrer
     * @param blockNeu - neuer Blockname
     * @throws SQLException
     */
    public void updateQuiz(String blockAlt, String lehrer, String blockNeu) throws SQLException {
        String updateString = "update block set block.bid = ? where block.bid = ? and block.lehrer = ?;";
        try (Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/SimpleLearner?useSSL=true", userInfo);
             PreparedStatement stmtNewName = myConn.prepareStatement(updateString)) {
            stmtNewName.setString(1, blockNeu);
            stmtNewName.setString(2, blockAlt);
            stmtNewName.setString(3, lehrer);

            stmtNewName.executeUpdate();
        }
    }

    /**
     * Ändert den Fragetext der aktuellen Aufgabe
     *
     * @param block    - der aktuelle Block
     * @param frageAlt - der alte Fragetext
     * @param frageNeu - der neue Fragetext
     * @throws SQLException
     */
    public void updateTask(String block, String frageAlt, String frageNeu) throws SQLException {
        String deleteSchuelerBlockString = "delete from schuelerloestblock where block = ?;";
        String deleteSchuelerAufgabeString = "delete from schuelerloestaufgabe where schuelerloestaufgabe.aufgabe IN (select aid from aufgabe "
                + "where aufgabe.block = ?);";
        String updateString;
        if (frageAlt != null) {
            updateString = "update aufgabe set aufgabe.frage = ? where aufgabe.block = ? and aufgabe.frage = ?;";
        } else {
            updateString = "insert into aufgabe(block, frage) values(?, ?);";
        }
        try (Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/SimpleLearner?useSSL=true", userInfo);
             PreparedStatement stmtSchuelerBlock = myConn.prepareStatement(deleteSchuelerBlockString);
             PreparedStatement stmtSchuelerAufgabe = myConn.prepareStatement(deleteSchuelerAufgabeString);
             PreparedStatement stmtNewQuestion = myConn.prepareStatement(updateString)) {

            stmtSchuelerAufgabe.setString(1, block);
            stmtSchuelerAufgabe.executeUpdate();

            stmtSchuelerBlock.setString(1, block);
            stmtSchuelerBlock.executeUpdate();
            if (frageAlt != null) {
                stmtNewQuestion.setString(1, frageNeu);
                stmtNewQuestion.setString(2, block);
                stmtNewQuestion.setString(3, frageAlt);
            } else {
                stmtNewQuestion.setString(1, block);
                stmtNewQuestion.setString(2, frageNeu);
            }

            stmtNewQuestion.executeUpdate();
        }
    }

    /**
     * Aktualisiert die Antwortmöglichkeiten einer Aufgabe
     *
     * @param block         - der aktuelle Block
     * @param frage         - die Frage der Aufgabe
     * @param lehrer        - der bearbeitende Lehrer
     * @param neueAntworten - die Liste mit den neuen Antwortmöglichkeiten
     * @throws SQLException
     */
    public void updateAnswers(String block, String frage, String lehrer, VBox neueAntworten) throws SQLException {
        String deleteSchuelerBlockString = "delete from schuelerloestblock where block = ?;";
        String deleteSchuelerAufgabeString = "delete from schuelerloestaufgabe where schuelerloestaufgabe.aufgabe IN (select aid from aufgabe "
                + "where aufgabe.block = ?);";
        String deleteString = "delete from antwort where antwort.aufgabe = (select aufgabe.aid from aufgabe where aufgabe.frage = ? "
                + "and aufgabe.block = (select block.bid from block where block.bid = ? and block.lehrer = ?));";

        try (Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/SimpleLearner?useSSL=true", userInfo);
             PreparedStatement stmtSchuelerBlock = myConn.prepareStatement(deleteSchuelerBlockString);
             PreparedStatement stmtSchuelerAufgabe = myConn.prepareStatement(deleteSchuelerAufgabeString);
             PreparedStatement stmtDeleteAntwort = myConn.prepareStatement(deleteString)) {

            stmtSchuelerAufgabe.setString(1, block);
            stmtSchuelerAufgabe.executeUpdate();

            stmtSchuelerBlock.setString(1, block);
            stmtSchuelerBlock.executeUpdate();

            stmtDeleteAntwort.setString(1, frage);
            stmtDeleteAntwort.setString(2, block);
            stmtDeleteAntwort.setString(3, lehrer);

            System.out.println("updateAnswer");
            stmtDeleteAntwort.executeUpdate();

            createAntwortenInAufgabe(block, frage, neueAntworten);
        }
    }

    /**
     * Lädt alle Schüler, welche den spezifizierten Aufgabenblock bereits gelöst
     * haben
     *
     * @param blockName - der gewählte Block
     * @param lehrer    - der zuständige Lehrer
     * @throws SQLException
     */
    public void loadAbsolvierteSchueler(String blockName, String lehrer) throws SQLException {
        String searchString = "select * from schueler join schuelerloestblock on schueler.sid = schuelerloestblock.schueler "
                + "join block on schuelerloestblock.block = block.bid "
                + "join lehrer on block.lehrer = lehrer.lid "
                + "where block.bid = ? "
                + "and lehrer.lid = ?;";
        ResultSet rsSearchSchueler = null;

        try (Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/SimpleLearner?useSSL=true", userInfo);
             PreparedStatement stmtSearchSchueler = myConn.prepareStatement(searchString)) {

            stmtSearchSchueler.setString(1, blockName);
            stmtSearchSchueler.setString(2, lehrer);

            rsSearchSchueler = stmtSearchSchueler.executeQuery();

            studentsSolved.clear();
            while (rsSearchSchueler.next()) {
                studentsSolved.add(rsSearchSchueler.getString("vorname") + " " + rsSearchSchueler.getString("nachname"));
            }
        }
    }

    /**
     * Lädt alle Antworten eines Schülers zu dem spezifizierten Block
     *
     * @param block     - der spezifizierte Block
     * @param lehrer    - der zuständige Lehrer
     * @param vSchueler - der abgefragte Schülervorname
     * @param nSchueler - der abgefragte Schülernachname
     * @throws SQLException
     */
    public void loadAbsolvierteAntworten(String block, String lehrer, String vSchueler, String nSchueler) throws
            SQLException {
        String loadString = "select lehrer.vorname, lehrer.nachname, schueler.vorname, schueler.nachname, aufgabe.frage, schuelerloestaufgabe.antwortS, antwort.antworttext, fach.kuerzel, fach.fid, kategorie.kid "
                + "from schueler "
                + "join schuelerloestaufgabe on schueler.sid = schuelerloestaufgabe.schueler "
                + "join aufgabe on schuelerloestaufgabe.aufgabe = aufgabe.aid "
                + "join block on aufgabe.block = block.bid "
                + "join kategorie on block.kategorie = kategorie.kid "
                + "join fach on kategorie.fach = fach.fid "
                + "join lehrer on block.lehrer = lehrer.lid "
                + "join antwort on antwort.aufgabe = aufgabe.aid "
                + "where antwort.istrue = true "
                + "and block.bid = ? "
                + "and lehrer.lid = ? "
                + "and schueler.vorname = ?"
                + "and schueler.nachname = ?;";
        ResultSet rsSearchAntworten = null;

        try (Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/SimpleLearner?useSSL=true", userInfo);
             PreparedStatement stmtSearchAntworten = myConn.prepareStatement(loadString)) {

            stmtSearchAntworten.setString(1, block);
            stmtSearchAntworten.setString(2, lehrer);
            stmtSearchAntworten.setString(3, vSchueler);
            stmtSearchAntworten.setString(4, nSchueler);

            rsSearchAntworten = stmtSearchAntworten.executeQuery();

            answersStudentsTemp.clear();
            while (rsSearchAntworten.next()) {
                answersStudentsTemp.add(new AntwortPdfObjekt(rsSearchAntworten.getString("schueler.vorname"),
                        rsSearchAntworten.getString("schueler.nachname"),
                        rsSearchAntworten.getString("aufgabe.frage"),
                        rsSearchAntworten.getString("schuelerloestaufgabe.antwortS"),
                        rsSearchAntworten.getString("antwort.antworttext"),
                        rsSearchAntworten.getString("fach.kuerzel"),
                        rsSearchAntworten.getString("fach.fid"),
                        rsSearchAntworten.getString("kategorie.kid"),
                        rsSearchAntworten.getString("lehrer.vorname"),
                        rsSearchAntworten.getString("lehrer.nachname")));
            }
        } finally {
            if (rsSearchAntworten != null) {
                rsSearchAntworten.close();
            }
        }
    }

    private ArrayList<String> filterList(ArrayList<String> inputList, String filter) {
        ArrayList<String> outputList = new ArrayList<>();
        for (int index = 0; index < inputList.size(); index++) {
            String filteredString = inputList.get(index);
            if (filteredString.contains(filter)) {
                outputList.add(filteredString);
            }
        }
        return outputList;
    }
    /*TODO: 
    Lehrer muessen ihre Aufgaben loeschen koennen
    Lehrer muessen Schueler sehen koennen, die ihre Bloecke geloest haben mit Anzahl richtig geloester Fragen
    Lehrer muss seine Bloecke veraendern koennen (loeschen und aendern der Aufgaben)
    Lehrer muessen Bloecke erstellen koennen
    Adminoberflaeche mit Nutzernamen und Passwoertern
     */
}
