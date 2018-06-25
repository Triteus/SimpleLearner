/*
package sql;

import java.sql.*;

public class StudentSqlLogic extends SqlLogik {

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

    */
/**
     * Überprüft die Antwort des Schülers zur jeweiligen Aufgabenstellung
     *
     * @param blockName - Der Name des Aufgabenblocks
     * @param aStudent  - Der Name(Username) des aktiven Schülers
     * @param aQuestion - Der Fragetext der aktuellen Aufgabe
     * @param aAnswer   - Die Antwort des Schülers
     * @return Gibt einen boolean zur Anzeige zurück
     * @throws SQLException
     *//*



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

        */
/*"select isTrue from antwort join aufgabe on antwort.aufgabe = aufgabe.aid"
                + " join block on aufgabe.block = block.bid where block.bid = ? and aufgabe.frage = ? and antwort.antworttext = ?";*//*

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

    */
/**
     * Überprüft den Login des Users
     *
     * @param user     - der Nutzername
     * @param password - das Nutzerpasswort
     * @return Gibt einen Zweistelligen Boolean-Array zurück. Boolean[0] gibt
     * an, ob die eingegebenen Daten korrekt sind. Boolean[1] gibt an ob es ein
     * Schüler oder ein Lehrer ist um die jeweilige Oberfläche zu laden.
     * @throws SQLException
     *//*

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

    */
/**
     * Lädt alle vorhandenen Fächer für die Schüler
     *
     * @throws SQLException
     *//*

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


}
*/
