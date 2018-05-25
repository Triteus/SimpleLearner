package main.Session;

import Pdf.EvaluationsPdf;
import com.itextpdf.text.DocumentException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.Controller.TaskBlockEditController;
import main.Controller.TaskBlockNewController;
import main.models.Answer;
import main.models.Block;
import main.models.Task;
import sql.SqlLogik;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class EditSession extends UserSession {

    public EditSession(SqlLogik sql, String username) {
        super(sql, username);
        editAllowed = true;
    }



    @Override
    public ArrayList<String> loadTaskBlocks(String category, String filter) throws Exception {
        if (filter.isEmpty()) {
            try {
                sql.loadTeacherSections(category, username);
            } catch (SQLException exc) {
                System.out.println(exc.getMessage());
                throw exc;
            }
        } else {
            try {
                sql.loadFilteredTeacherSections(category, username, filter);
            } catch (SQLException exc) {
                System.out.println(exc.getMessage());
                throw exc;
            }
        }
        return sql.getTaskSections();
    }


    /*
    The controller is set manually depending on the teacher either choosing to create or edit a block.
     */

    /**
     * Taskblock is loaded into a new stage.
     * @param block
     * @param category
     */

    @Override
    public void loadTaskBlock(String block, String category) {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Taskblock_new.fxml"));
        loader.setController(new TaskBlockEditController());

        Stage stage = new Stage();
        try {
            stage.setScene(
                    new Scene(loader.load())
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        TaskBlockEditController controller = loader.getController();
        controller.initData( this, category, block);
        stage.setAlwaysOnTop(true);

        stage.show();

    }



    public void createBlock(String block, String category, HashMap<String, ArrayList<Answer>> tasks) throws SQLException {

        sql.createBlock(block, this.username, category, tasks);

    }


    public void addCategory(String category, String subject) throws SQLException {

        sql.createCategory(category, subject);
    }


    public void updateBlock(String oldBlockName, String newBlockName) throws SQLException {


        sql.updateTaskBlockName(oldBlockName, username, newBlockName);

    }

    @Override
    public boolean checkAnswer(String block, String question, String answer) throws SQLException {

        return sql.checkAnswer(block, username, question, answer);

    }


    public void updateTask(Task oldTask, Task newTask, String teacher, String blockName) throws SQLException {

        sql.updateTask(blockName, teacher, oldTask, newTask);


    }

    @Override
    public void startBlock(String block) throws SQLException {

        sql.startBlock(block, username);

    }

    @Override
    public ArrayList<String> loadStudentsWhoSolvedTaskBlock(String blockName) throws SQLException {

        sql.loadStudentsSolvedTask(blockName, username);

        return sql.getStudentsSolvedTask();

    }

    public void saveResultsAsPDF(String studentName, String blockName) throws DocumentException, SQLException, IOException {

        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF (*.pdf)", "*pdf"));
        File f = fc.showSaveDialog(new Stage());
        if (f != null && !f.getName().contains(".")) {
            f = new File(f.getAbsolutePath() + ".pdf");
        }
        if (f != null) {
            try {
                EvaluationsPdf pdf = new EvaluationsPdf(sql, f);
                //Vor- und Nachname aus dem Button filtern
                String vorname = "";
                String nachname = "";

                int j = 0;
                for (int k = 0; k < studentName.length(); k++) {
                    if (Character.isWhitespace(studentName.charAt(k))) {
                        j = k + 1;
                        break;
                    }
                    vorname += studentName.charAt(k);
                }
                for (int l = j; l < studentName.length(); l++) {
                    nachname += studentName.charAt(l);
                }
                pdf.createTable(blockName, username, vorname, nachname);
            } catch (IOException | DocumentException | SQLException exc) {
                System.out.println(exc.getMessage());
            }
        }

    }

}
