/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sql;

import Pdf.AntwortPdfObjekt;
import main.models.Answer;
import main.models.Block;
import main.models.Task;
import main.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/**
 * Stellt die Methoden für das Laden der Daten aus der Datenbank zur Verfügung
 *
 * @author Marcel
 */
public class SqlLogik {

    private Properties userInfo;
    private ArrayList<String> subjects;
    private ArrayList<String> categories;
    private ArrayList<String> taskSections;
    private ArrayList<String> taskSectionsTemp;
    private ArrayList<String> questions;
    private ArrayList<Answer> answersTemp;
    private ArrayList<String> studentsSolvedTask;
    private ArrayList<AntwortPdfObjekt> tempAnswersStudents;
    private String currentUser;
    private boolean isDone;

    private String databaseUrl = DbConfig.url;

    public Properties getUserInfo() {
        return userInfo;
    }

    public ArrayList<String> getSubjects() {
        return subjects;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public ArrayList<String> getTaskSections() {
        return taskSections;
    }

    public ArrayList<String> getTaskSectionsTemp() {
        return taskSectionsTemp;
    }

    public ArrayList<String> getQuestions() {
        return questions;
    }

    public ArrayList<Answer> getAnswersTemp() {
        return answersTemp;
    }

    public ArrayList<String> getStudentsSolvedTask() {
        return studentsSolvedTask;
    }

    public ArrayList<AntwortPdfObjekt> getTempAnswersStudents() {
        return tempAnswersStudents;
    }

    public boolean isIsDone() {
        return isDone;
    }

    public void setIsDone(boolean isFertig) {
        this.isDone = isFertig;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    public void setDatabaseUrl(String dbUrl) {
        this.databaseUrl = dbUrl;
    }

    public String getDatabaseUrl() {
        return databaseUrl;
    }


    public SqlLogik(String dbUrl) {
        this();
        databaseUrl = dbUrl;

    }

    public SqlLogik() {

        userInfo = new Properties();
        userInfo.put("user", DbConfig.username); //Username der MySql-Datenbank
        userInfo.put("password", DbConfig.password); //Passwort der MySql-Datenbank
        subjects = new ArrayList<>();
        categories = new ArrayList<>();
        taskSections = new ArrayList<>();
        taskSectionsTemp = new ArrayList<>();
        questions = new ArrayList<>();
        answersTemp = new ArrayList<>();
        studentsSolvedTask = new ArrayList<>();
        tempAnswersStudents = new ArrayList<>();
        currentUser = null;

    }

    /**
     * Initialisiert eine Tabelle, welche dazu dient, später zu überprüfen ob
     * der Schüler das jeweilige Quiz schon gelöst hat
     *
     * @param sectionName - Der Name des jeweiligen Aufgabenblocks
     * @param aStudent    - Der Name des lösenden Schülers
     * @throws SQLException
     */

    public void startBlock(String sectionName, String aStudent) {

        try {
            Connection myConn = DriverManager.getConnection(databaseUrl, userInfo);
            startBlock(sectionName, aStudent, myConn);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    void startBlock(String sectionName, String aStudent, Connection myConn) {

        String startString = "insert into schuelerloestblock(schueler, block) values(?, ?);";
        String suchString = "select block, schueler from schuelerloestblock;";
        ResultSet rsSuche = null;
        boolean confirm = true;

        try {

            PreparedStatement stmtStart = myConn.prepareStatement(startString);
            Statement stmtSuche = myConn.createStatement();

            rsSuche = stmtSuche.executeQuery(suchString);
            while (rsSuche.next()) {
                if (rsSuche.getString("block").equals(sectionName)) {
                    if (rsSuche.getString("schueler").equals(aStudent)) {
                        confirm = false;
                    }

                }
                if (confirm) {
                    stmtStart.setString(1, aStudent);
                    stmtStart.setString(2, sectionName);

                    stmtStart.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();

        } finally {
            if (rsSuche != null) {
                try {
                    rsSuche.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Überprüft die Antwort des Schülers zur jeweiligen Aufgabenstellung
     *
     * @param blockName - Der Name des Aufgabenblocks
     * @param aStudent  - Der Name(Username) des aktiven Schülers
     * @param aQuestion - Der Fragetext der aktuellen Aufgabe
     * @param aAnswer   - Die Antwort des Schülers
     * @return Gibt einen boolean zur Anzeige zurück
     * @throws SQLException
     */


    public boolean checkAnswer(String blockName, String aStudent, String aQuestion, String aAnswer) throws SQLException {


        try {
            Connection myConn = DriverManager.getConnection(databaseUrl, userInfo);
            return checkAnswer(blockName, aStudent, aQuestion, aAnswer, myConn);

        } catch (SQLException exc) {
            throw exc;
        }
    }


    boolean checkAnswer(String blockName, String aStudent, String aQuestion, String aAnswer, Connection myConn) {

        String antwortString = "insert into schuelerloestaufgabe(aufgabe, schueler, antwortS) values((select aufgabe.aid from aufgabe "
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

        try {
            PreparedStatement setAntwort = myConn.prepareStatement(antwortString);
            PreparedStatement stmtAntwort = myConn.prepareStatement(stmtString);

            setAntwort.setString(1, blockName);
            setAntwort.setString(2, aQuestion);
            setAntwort.setString(3, aStudent);
            setAntwort.setString(4, aAnswer);
            setAntwort.execute();

            stmtAntwort.setString(1, aQuestion);
            stmtAntwort.setString(2, aStudent);
            rsAntwort = stmtAntwort.executeQuery();

            while (rsAntwort.next()) {
                if (rsAntwort.getString("antwort.isTrue").equals("1")) {
                    System.out.println(rsAntwort.getString("antwort.isTrue"));
                    check = true;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {

            if (rsAntwort != null) {
                try {
                    rsAntwort.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return check;
    }

    public boolean[] checkLogin(String user, String password) throws SQLException {
        Connection myConn = DriverManager.getConnection(databaseUrl, userInfo);
        return checkLogin(user, password, myConn);
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
    boolean[] checkLogin(String user, String password, Connection connection) throws SQLException {

        String stringCheck = "select * from lehrer, schueler";
        boolean[] checkPassword = new boolean[2];
        Statement stmtCheck = connection.createStatement();
        ResultSet rsCheck = stmtCheck.executeQuery(stringCheck);

        while (rsCheck.next()) {
            if (rsCheck.getString("lid").equals(user)) {
                checkPassword[0] = rsCheck.getString("lehrer.passwort").equals(password);
                if (checkPassword[0] == true) {
                    checkPassword[1] = true; //1 für Lehrer
                    currentUser = rsCheck.getString("lehrer.vorname") + " " + rsCheck.getString("lehrer.nachname");
                }
            } else if (rsCheck.getString("sid").equals(user)) {
                checkPassword[0] = rsCheck.getString("schueler.passwort").equals(password);
                if (checkPassword[0] == true) {
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
    public void loadSubjects() throws SQLException {
        try (Connection myConn = DriverManager.getConnection(databaseUrl, userInfo);
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
    public void loadSubjects(String lehrer) throws SQLException {
        String faecherString = "select fach from lehrerunterrichtet where lehrer = ?";
        ResultSet rsFach = null;
        try (Connection myConn = DriverManager.getConnection(databaseUrl, userInfo);
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
    public void loadFilteredSubjects(String filter) throws SQLException {
        String faecherString = "select fid from fach where fid like ?";
        ResultSet rsFach = null;
        try (Connection myConn = DriverManager.getConnection(databaseUrl, userInfo);
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
    public void loadFilteredSubjects(String lehrer, String filter) throws SQLException {
        String faecherString = "select fach from lehrerunterrichtet where lehrer = ? and fach like ?;";
        ResultSet rsFach = null;
        try (Connection myConn = DriverManager.getConnection(databaseUrl, userInfo);
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
    public void loadCategories(String fach) throws SQLException {
        String kategorieString = "select kid from kategorie where fach = ?";
        ResultSet rsFach = null;
        try (Connection myConn = DriverManager.getConnection(databaseUrl, userInfo);
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
    public void loadFilteredCategories(String fach, String filter) throws SQLException {
        String kategorieString = "select kid from kategorie where fach = ? and kid like ?";
        ResultSet rsFach = null;
        try (Connection myConn = DriverManager.getConnection(databaseUrl, userInfo);
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
    public void loadTeacherSections(String kategorie, String lehrer) throws SQLException {
        String blockString = "select bid from block where kategorie = ? and lehrer = ?;";
        ResultSet rsBlock = null;
        try (Connection myConn = DriverManager.getConnection(databaseUrl, userInfo);
             PreparedStatement stmtBlock = myConn.prepareStatement(blockString)) {

            stmtBlock.setString(1, kategorie);
            stmtBlock.setString(2, lehrer);
            rsBlock = stmtBlock.executeQuery();
            if (taskSections != null) {
                taskSections.clear();
            }

            while (rsBlock.next()) {
                taskSections.add(rsBlock.getString("bid"));
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
    public void loadStudentSections(String kategorie, String schueler) throws SQLException {
        String blockString = "select bid from block where kategorie = ?";
        String blockStringTwo = "select block.bid, schuelerloestblock.block from block "
                + "left join schuelerloestblock on block.bid = schuelerloestblock.block "
                + "where block.kategorie = ? and schuelerloestblock.schueler = ?;";

        ResultSet rsBlock = null;
        ResultSet rsBlockTwo = null;
        try (Connection myConn = DriverManager.getConnection(databaseUrl, userInfo);
             PreparedStatement stmtBlock = myConn.prepareStatement(blockString);
             PreparedStatement stmtBlockTwo = myConn.prepareStatement(blockStringTwo);) {

            //alle Kategorien werden geladen
            stmtBlock.setString(1, kategorie);
            rsBlock = stmtBlock.executeQuery();
            if (taskSections != null) {
                taskSections.clear();
            }

            while (rsBlock.next()) {
                taskSections.add(rsBlock.getString("bid"));
            }

            //nur eroeffnete Kategorien werden geladen
            stmtBlockTwo.setString(1, kategorie);
            stmtBlockTwo.setString(2, schueler);
            rsBlockTwo = stmtBlockTwo.executeQuery();
            if (taskSectionsTemp != null) {
                taskSectionsTemp.clear();
            }
            while (rsBlockTwo.next()) {
                taskSectionsTemp.add(rsBlockTwo.getString("block.bid"));
            }

            //eroffnete Kategorien werden allen Kategorien entfernt, damit fuer den jeweiligen Schueler nur die Bloecke erscheinen, die er noch nicht bearbeitet hat
            for (int i = 0; i < taskSections.size(); i++) {
                if (!taskSectionsTemp.isEmpty()) {
                    for (int j = 0; j < taskSectionsTemp.size(); j++) {
                        if (taskSections.get(i).equals(taskSectionsTemp.get(j))) {
                            taskSections.remove(i);
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
    public void loadFilteredTeacherSections(String kategorie, String lehrer, String filter) throws SQLException {
        String blockString = "select bid from block where kategorie = ? and lehrer = ? and bid like ?;";
        ResultSet rsBlock = null;
        try (Connection myConn = DriverManager.getConnection(databaseUrl, userInfo);
             PreparedStatement stmtBlock = myConn.prepareStatement(blockString)) {

            stmtBlock.setString(1, kategorie);
            stmtBlock.setString(2, lehrer);
            stmtBlock.setString(3, "%" + filter + "%");
            rsBlock = stmtBlock.executeQuery();
            if (taskSections != null) {
                taskSections.clear();
            }

            while (rsBlock.next()) {
                taskSections.add(rsBlock.getString("bid"));
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
     * @param category - die aktive Kategorie
     * @param schueler - der aktive Schüler
     * @param filter   - der zu filternde String
     * @throws SQLException
     */
    public void loadFilteredStudentSections(String category, String schueler, String filter) throws SQLException {
        String blockString = "select bid from block where kategorie = ?";
        String blockStringTwo = "select block.bid, schuelerloestblock.block from block "
                + "left join schuelerloestblock on block.bid = schuelerloestblock.block "
                + "where block.kategorie = ? and schuelerloestblock.schueler = ?;";

        ResultSet rsBlock = null;
        ResultSet rsBlockTwo = null;
        try (Connection myConn = DriverManager.getConnection(databaseUrl, userInfo);
             PreparedStatement stmtBlock = myConn.prepareStatement(blockString);
             PreparedStatement stmtBlockTwo = myConn.prepareStatement(blockStringTwo);) {

            //alle Kategorien werden geladen
            stmtBlock.setString(1, category);
            rsBlock = stmtBlock.executeQuery();
            if (taskSections != null) {
                taskSections.clear();
            }

            while (rsBlock.next()) {
                taskSections.add(rsBlock.getString("bid"));
            }

            //nur eroeffnete Kategorien werden geladen
            stmtBlockTwo.setString(1, category);
            stmtBlockTwo.setString(2, schueler);
            rsBlockTwo = stmtBlockTwo.executeQuery();
            if (taskSectionsTemp != null) {
                taskSectionsTemp.clear();
            }
            while (rsBlockTwo.next()) {
                taskSectionsTemp.add(rsBlockTwo.getString("block.bid"));
            }

            //eroffnete Kategorien werden allen Kategorien entfernt, damit fuer den jeweiligen Schueler nur die Bloecke erscheinen, die er noch nicht bearbeitet hat
            for (int i = 0; i < taskSections.size(); i++) {
                if (!taskSectionsTemp.isEmpty()) {
                    for (int j = 0; j < taskSectionsTemp.size(); j++) {
                        if (taskSections.get(i).equals(taskSectionsTemp.get(j))) {
                            taskSections.remove(i);
                        }
                    }
                }
            }
            for (int i = 0; i < taskSections.size(); i++) {
                System.out.println(taskSections.get(i) + "in Logik ArrayList davor");
            }
            taskSections = filterList(taskSections, filter);
            for (int i = 0; i < taskSections.size(); i++) {
                System.out.println(taskSections.get(i) + "in Logik ArrayList danach");
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
    public void loadQuestions(String block) throws SQLException {
        String stringFrage = "select frage from aufgabe join block on aufgabe.block = block.bid where block.bid = ?";
        ResultSet rsFrage = null;
        try (Connection myConn = DriverManager.getConnection(databaseUrl, userInfo);
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
    public void loadAnswers(String block, String frage) throws SQLException {
        String stringAntworten = "select antwort.antworttext, antwort.isTrue from antwort join aufgabe on antwort.aufgabe = aufgabe.aid "
                + "join block on aufgabe.block = block.bid where block.bid = ? and aufgabe.frage = ?";
        ResultSet rsAntworten = null;
        try (Connection myConn = DriverManager.getConnection(databaseUrl, userInfo);
             PreparedStatement stmtAntworten = myConn.prepareStatement(stringAntworten)) {

            stmtAntworten.setString(1, block);
            stmtAntworten.setString(2, frage);
            rsAntworten = stmtAntworten.executeQuery();

            answersTemp.clear();

            while (rsAntworten.next()) {

                String answerText = rsAntworten.getString("antworttext");
                boolean isTrue = rsAntworten.getBoolean("isTrue");
                System.out.println("isTrue: " + isTrue);
                answersTemp.add(new Answer(answerText, isTrue));
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
     * @param blockname - der ausgewählte Aufgabenblock
     * @param lehrer    - der durchführende Lehrer
     * @param kategorie - die Kategorie, in welcher sich der Aufgabenblock
     *                  befindet
     * @throws SQLException
     */

    public void deleteBlock(String blockname, String lehrer, String kategorie) throws SQLException {

        try {
            Connection myConn = DriverManager.getConnection(databaseUrl, userInfo);
            deleteBlock(blockname, lehrer, kategorie, myConn);

        } catch (SQLException exc) {
            throw exc;
        }
    }



    void deleteBlock(String blockname, String lehrer, String kategorie, Connection myConn) throws SQLException {
        String loeschenString = "delete from block where bid = ? and lehrer = ? and kategorie = ?;";
        try {
            PreparedStatement stmtLoeschen = myConn.prepareStatement(loeschenString);

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

    public void initBlock(String block, String lehrer, String kategorie) throws SQLException {

        try {
            Connection myConn = DriverManager.getConnection(databaseUrl, userInfo);
            initBlock(block, lehrer, kategorie, myConn);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    void initBlock(String block, String lehrer, String kategorie, Connection myConn) throws SQLException {

        String createString = "insert into block(bid, lehrer, kategorie) values(?, ?, ?);";
        try {
            PreparedStatement stmtUpdate = myConn.prepareStatement(createString);

            stmtUpdate.setString(1, block);
            stmtUpdate.setString(2, lehrer);
            stmtUpdate.setString(3, kategorie);
            stmtUpdate.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    /**
     * Erzeugt eine neue Kategorie im aktiven Fach des Lehrers
     *
     * @param katName  - der Name der neuen Kategorie
     * @param fachName - der Name des aktiven Fachs
     * @throws SQLException
     */

    public void createCategory(String katName, String fachName) throws SQLException {

        try {
            Connection myConn = DriverManager.getConnection(databaseUrl, userInfo);
            createCategory(katName, fachName, myConn);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    void createCategory(String katName, String fachName, Connection myConn) throws SQLException {

        String createString = "insert into kategorie(kid, fach) values(?,?);";
        try {
            PreparedStatement stmtUpdate = myConn.prepareStatement(createString);

            stmtUpdate.setString(1, katName);
            stmtUpdate.setString(2, fachName);

            stmtUpdate.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw ex;
        }
    }


    private void createAnswersInTask(String block, String frage, ArrayList<Answer> answers) throws SQLException {

        for (Answer answer : answers) {
            createAnswer(answer.getAnswerText(), answer.isRight(), block, frage);
        }
    }


    public void createBlock(String block, String teacher,  String category, HashMap<String, ArrayList<Answer>> tasks) throws SQLException {

        try {
            Connection myConn = DriverManager.getConnection(databaseUrl, userInfo);
            createBlock(block, teacher, category, tasks, myConn);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw ex;
        }
    }


    void createBlock(String block, String teacher,  String category, HashMap<String, ArrayList<Answer>> tasks, Connection myConn) throws SQLException {

        //create new entry in table 'block'
        initBlock(block, teacher, category);

        tasks.forEach((question, answers) -> {

            try {
                createTask(question, answers, block, myConn );
            } catch (SQLException e) {
                e.printStackTrace();
            }

        });

    }

    private void createTask(String question, ArrayList<Answer> answers, String block, Connection myConn) throws SQLException {

        String insertTaskQuery = "insert into aufgabe(block, frage) values(?, ?)";
        PreparedStatement stmtNewTask = myConn.prepareStatement(insertTaskQuery);

        stmtNewTask.setString(1, block);
        stmtNewTask.setString(2, question);

        stmtNewTask.executeUpdate();

        for(Answer answer : answers) {
            createAnswer(answer.getAnswerText(), answer.isRight(), block, question, myConn);
        }
    }


    public void createAnswer(String antworttext, boolean isRichtig, String block, String frage) throws SQLException {

        try {
            Connection myConn = DriverManager.getConnection(databaseUrl, userInfo);
            createAnswer(antworttext, isRichtig, block, frage, myConn);

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw ex;
        }

    }

    void createAnswer(String antworttext, boolean isRichtig, String block, String frage, Connection myConn) throws SQLException {
        String insertAntwort = "insert into antwort(antworttext, istrue, aufgabe) values(?, ?, (select aufgabe.aid from aufgabe where "
                + "aufgabe.block = ? and aufgabe.frage = ?));";

        try {
            PreparedStatement stmtNewAntwort = myConn.prepareStatement(insertAntwort);

            stmtNewAntwort.setString(1, antworttext);
            stmtNewAntwort.setBoolean(2, isRichtig);
            stmtNewAntwort.setString(3, block);
            stmtNewAntwort.setString(4, frage);

            System.out.println("createAnswer davor");
            stmtNewAntwort.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw ex;
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


    public void updateTaskBlockName(String blockAlt, String lehrer, String blockNeu) throws SQLException {

        try {
            Connection myConn = DriverManager.getConnection(databaseUrl, userInfo);
            updateTaskBlockName(blockAlt, lehrer, blockNeu, myConn);
        } catch (SQLException ex) {
            throw ex;
        }
    }

    void updateTaskBlockName(String blockAlt, String lehrer, String blockNeu, Connection myConn) throws SQLException {
        String updateString = "update block set block.bid = ? where block.bid = ? and block.lehrer = ?;";
        try {

            PreparedStatement stmtNewName = myConn.prepareStatement(updateString);
            stmtNewName.setString(1, blockNeu);
            stmtNewName.setString(2, blockAlt);
            stmtNewName.setString(3, lehrer);

            stmtNewName.executeUpdate();
        } catch (SQLException ex) {
            throw ex;
        }
    }


    public void updateTask(String block, String lehrer, Task oldTask, Task newTask) throws SQLException {

        try {
            Connection myConn = DriverManager.getConnection(databaseUrl, userInfo);
            updateTask(block, lehrer, oldTask, newTask, myConn);
        } catch (SQLException ex) {

            throw ex;
        }
    }

    void updateTask(String block, String lehrer, Task oldTask, Task newTask, Connection myConn) throws SQLException {

        updateAnswers(block, oldTask.getQuestion(), lehrer, newTask.getAnswers(),  myConn);

        updateQuestion(block, oldTask.getQuestion(), newTask.getQuestion());

    }


    /**
     * Ändert den Fragetext der aktuellen Aufgabe
     *
     * @param block    - der aktuelle Block
     * @param frageAlt - der alte Fragetext
     * @param frageNeu - der neue Fragetext
     * @throws SQLException
     */


    public void updateQuestion(String block, String frageAlt, String frageNeu) throws SQLException {

        try {
            Connection myConn = DriverManager.getConnection(databaseUrl, userInfo);
            updateQuestion(block, frageAlt, frageNeu, myConn);
        } catch (SQLException ex) {

            throw ex;
        }
    }

    void updateQuestion(String block, String frageAlt, String frageNeu, Connection myConn) throws SQLException {

        String deleteSchuelerBlockString = "delete from schuelerloestblock where block = ?;";
        String deleteSchuelerAufgabeString = "delete from schuelerloestaufgabe where schuelerloestaufgabe.aufgabe IN (select aid from aufgabe "
                + "where aufgabe.block = ?);";
        String updateString;
        if (frageAlt != null) {
            updateString = "update aufgabe set aufgabe.frage = ? where aufgabe.block = ? and aufgabe.frage = ?;";
        } else {
            updateString = "insert into aufgabe(block, frage) values(?, ?);";
        }
        try {
            PreparedStatement stmtSchuelerBlock = myConn.prepareStatement(deleteSchuelerBlockString);
            PreparedStatement stmtSchuelerAufgabe = myConn.prepareStatement(deleteSchuelerAufgabeString);
            PreparedStatement stmtNewQuestion = myConn.prepareStatement(updateString);

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

        } catch (
                SQLException ex)

        {
            ex.printStackTrace();
            throw ex;
        }

    }

    /**
     * Aktualisiert die Antwortmöglichkeiten einer Aufgabe
     *
     * @param block         - der aktuelle Block
     * @param frage         - die Frage der Aufgabe
     * @param lehrer        - der bearbeitende Lehrer
     * @param newAnswers - die Liste mit den neuen Antwortmöglichkeiten
     * @throws SQLException
     */

    private void deleteStudentsAnswers(Block block, Connection myConn) throws SQLException {

        String deleteSchuelerBlockString = "delete from schuelerloestblock where block = ?;";
        String deleteSchuelerAufgabeString = "delete from schuelerloestaufgabe where schuelerloestaufgabe.aufgabe IN (select aid from aufgabe "
                + "where aufgabe.block = ?);";


            PreparedStatement stmtSchuelerBlock = myConn.prepareStatement(deleteSchuelerBlockString);
            PreparedStatement stmtSchuelerAufgabe = myConn.prepareStatement(deleteSchuelerAufgabeString);

            stmtSchuelerAufgabe.setString(1, block.getName());
            stmtSchuelerAufgabe.executeUpdate();

            stmtSchuelerBlock.setString(1, block.getName());
            stmtSchuelerBlock.executeUpdate();


    }


    public void updateAnswers(String block, String frage, String lehrer, ArrayList<Answer> newAnswers) throws SQLException {

        try {
            Connection myConn = DriverManager.getConnection(databaseUrl, userInfo);
            updateAnswers(block, frage, lehrer, newAnswers, myConn);
        } catch (SQLException ex) {
            throw ex;
        }
    }

    void updateAnswers(String block, String frage, String lehrer, ArrayList<Answer> newAnswers, Connection myConn) throws SQLException {
        String deleteSchuelerBlockString = "delete from schuelerloestblock where block = ?;";
        String deleteSchuelerAufgabeString = "delete from schuelerloestaufgabe where schuelerloestaufgabe.aufgabe IN (select aid from aufgabe "
                + "where aufgabe.block = ?);";
        String deleteString = "delete from antwort where antwort.aufgabe = (select aufgabe.aid from aufgabe where aufgabe.frage = ? "
                + "and aufgabe.block = (select block.bid from block where block.bid = ? and block.lehrer = ?));";

        try {
            PreparedStatement stmtSchuelerBlock = myConn.prepareStatement(deleteSchuelerBlockString);
            PreparedStatement stmtSchuelerAufgabe = myConn.prepareStatement(deleteSchuelerAufgabeString);
            PreparedStatement stmtDeleteAntwort = myConn.prepareStatement(deleteString);

            stmtSchuelerAufgabe.setString(1, block);
            stmtSchuelerAufgabe.executeUpdate();

            stmtSchuelerBlock.setString(1, block);
            stmtSchuelerBlock.executeUpdate();

            stmtDeleteAntwort.setString(1, frage);
            stmtDeleteAntwort.setString(2, block);
            stmtDeleteAntwort.setString(3, lehrer);

            System.out.println("updateAnswer");
            stmtDeleteAntwort.executeUpdate();

            createAnswersInTask(block, frage, newAnswers);

        } catch (SQLException ex) {
            throw ex;
        }
    }



    public void deleteCurrTask(Block block) throws SQLException {

        try {
            Connection myConn = DriverManager.getConnection(databaseUrl, userInfo);
            deleteCurrTask(block, myConn);
        } catch (SQLException ex) {
            throw ex;
        }
    }


    /**
     * @param block
     * @param myConn
     * @throws SQLException
     */

    void deleteCurrTask(Block block, Connection myConn) throws SQLException {

        String deleteAnswersQuery = "DELETE FROM antwort WHERE aufgabe = (SELECT aid from aufgabe where block = ? AND frage = ?) ; ";
        String deleteTaskQuery = "DELETE FROM aufgabe WHERE Block = ? AND frage = ? ;";

        PreparedStatement deleteAnswersStmt = myConn.prepareStatement(deleteAnswersQuery);
        PreparedStatement deleteTaskStmt = myConn.prepareStatement(deleteTaskQuery);

        deleteAnswersStmt.setString(1, block.getName());
        deleteAnswersStmt.setString(2, block.getCurrTask().getQuestion());

        deleteAnswersStmt.executeUpdate();

        deleteTaskStmt.setString(1, block.getName());
        deleteTaskStmt.setString(2, block.getCurrTask().getQuestion());

        deleteTaskStmt.executeUpdate();

        deleteStudentsAnswers(block, myConn);

    }

    /**
     * Lädt alle Schüler, welche den spezifizierten Aufgabenblock bereits gelöst
     * haben
     *
     * @param blockName - der gewählte Block
     * @param lehrer    - der zuständige Lehrer
     * @throws SQLException
     */
    public void loadStudentsSolvedTask(String blockName, String lehrer) throws SQLException {
        String searchString = "select * from schueler join schuelerloestblock on schueler.sid = schuelerloestblock.schueler "
                + "join block on schuelerloestblock.block = block.bid "
                + "join lehrer on block.lehrer = lehrer.lid "
                + "where block.bid = ? "
                + "and lehrer.lid = ?;";
        ResultSet rsSearchSchueler = null;

        try (Connection myConn = DriverManager.getConnection(databaseUrl, userInfo);
             PreparedStatement stmtSearchSchueler = myConn.prepareStatement(searchString)) {

            stmtSearchSchueler.setString(1, blockName);
            stmtSearchSchueler.setString(2, lehrer);

            rsSearchSchueler = stmtSearchSchueler.executeQuery();

            studentsSolvedTask.clear();
            while (rsSearchSchueler.next()) {
                studentsSolvedTask.add(rsSearchSchueler.getString("vorname") + " " + rsSearchSchueler.getString("nachname"));
            }
        }
    }

    /**
     * Lädt alle Antworten eines Schülers zu dem spezifizierten Block
     *
     * @param section   - der spezifizierte Block
     * @param teacher   - der zuständige Lehrer
     * @param vSchueler - der abgefragte Schülervorname
     * @param nSchueler - der abgefragte Schülernachname
     * @throws SQLException
     */
    public void loadSolvedAnswers(String section, String teacher, String vSchueler, String nSchueler) throws SQLException {
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

        try (Connection myConn = DriverManager.getConnection(databaseUrl, userInfo);
             PreparedStatement stmtSearchAntworten = myConn.prepareStatement(loadString)) {

            stmtSearchAntworten.setString(1, section);
            stmtSearchAntworten.setString(2, teacher);
            stmtSearchAntworten.setString(3, vSchueler);
            stmtSearchAntworten.setString(4, nSchueler);

            rsSearchAntworten = stmtSearchAntworten.executeQuery();

            tempAnswersStudents.clear();
            while (rsSearchAntworten.next()) {
                tempAnswersStudents.add(new AntwortPdfObjekt(rsSearchAntworten.getString("schueler.vorname"),
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


    private void createUser(String insertQuery, Connection myConn, User user) throws SQLException {

        PreparedStatement insertUserStmt = myConn.prepareStatement(insertQuery);

            insertUserStmt.setString(1, user.getUsername());
            insertUserStmt.setString(2, user.getPassword());
            insertUserStmt.setString(3, user.getSurname());
            insertUserStmt.setString(4, user.getName());

            insertUserStmt.executeQuery();

    }

    private void deleteUser(String insertQuery, Connection myConn, String username) throws SQLException {

        PreparedStatement deleteUserStmt = myConn.prepareStatement(insertQuery);

        deleteUserStmt.setString(1, username);

        deleteUserStmt.executeQuery();

    }

    private void editUser(String insertQuery, Connection myConn, User user) {

    }


    public void createTeacher(User user) throws SQLException {

        try {
            Connection myConn = DriverManager.getConnection(databaseUrl, userInfo);
            createTeacher(user, myConn);

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }


    }

    void createTeacher(User user, Connection myConn) throws SQLException {

        String insertQuery = "insert into lehrer(lid, passwort, vorname, nachname) values(?,?,?,?);";
        createUser(insertQuery, myConn, user);

    }

    public void createStudent(User user) throws SQLException {

        try {
            Connection myConn = DriverManager.getConnection(databaseUrl, userInfo);
                createStudent(user, myConn);
            myConn.close();

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    void createStudent(User user, Connection myConn) throws SQLException {

        String insertQuery = "insert into schueler (sid, passwort, vorname, nachname) values(?,?,?,?);";
        createUser(insertQuery, myConn, user);

    }

    public void deleteTeacher(String username) throws SQLException {

        try {
            Connection myConn = DriverManager.getConnection(databaseUrl, userInfo);
            deleteTeacher(username, myConn);

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

    }

    void deleteTeacher(String username, Connection myConn) throws SQLException {

        String insertQuery = "DELETE FROM lehrer WHERE lid = ?;";
        deleteUser(insertQuery, myConn, username);

    }


    public void deleteStudent(String username) throws SQLException {

        try {
            Connection myConn = DriverManager.getConnection(databaseUrl, userInfo);
            deleteStudent(username, myConn);

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }


    }

    void deleteStudent(String username, Connection myConn) throws SQLException {

        String insertQuery = "DELETE FROM schueler WHERE sid = ?;";
        deleteUser(insertQuery, myConn, username);

    }

    public void changeStudent(User user) throws SQLException {

        try {
            Connection myConn = DriverManager.getConnection(databaseUrl, userInfo);
            changeStudent(user, myConn);

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

    }

    void changeStudent(User user, Connection myConn) throws SQLException {

    }

    public void changeTeacher(User user) throws SQLException {

        try {
            Connection myConn = DriverManager.getConnection(databaseUrl, userInfo);
            changeTeacher(user, myConn);

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }

    }

    void changeTeacher(User user, Connection myConn) throws SQLException {

    }



    /*TODO: 
    Lehrer muessen ihre Aufgaben loeschen koennen
    Lehrer muessen Schueler sehen koennen, die ihre Bloecke geloest haben mit Anzahl richtig geloester Fragen
    Lehrer muss seine Bloecke veraendern koennen (loeschen und aendern der Aufgaben)
    Lehrer muessen Bloecke erstellen koennen
    Adminoberflaeche mit Nutzernamen und Passwoertern
     */
}
