/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Pdf;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import sql.SqlLogik;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Erzeugt eine PDF-Datei die zur Evaluation der Antworten für den Lehrer gedacht ist
 * @author Marcel
 */
public class EvaluationsPdf {
    SqlLogik sql;
    File file;
    Document document;
    
    public EvaluationsPdf(SqlLogik sql, File f) {
        this.sql = sql;
        this.file = f;
        this.document = new Document();
    }
    
    /**
     * Erzeugt die notwendigen Informationen für den Lehrer.
     * 
     * @param block - Der Name des Aufgabenblocks
     * @param teacher - Der Name des zuständigen Lehrers
     * @param schueler - Der Name des jeweiligen Schülers
     * @throws DocumentException
     * @throws IOException 
     * @throws SQLException 
     */
    public void createTable(String block, String teacher, String studentName, String studentLastName) throws DocumentException, IOException, SQLException {
        if (!document.isOpen()) {
            document = new Document();
        }
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file)); //irrelevant ob \\ oder /
            //PdfWriter writer = PdfCreator.getInstance(document, new FileOutputStream(System.getProperty("user.home") + "/Desktop/blabla.pdf"));
            document.open();
            
            sql.loadSolvedAnswers(block, teacher, studentName, studentLastName);
            
            Font chapterFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font chapterFont2 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font chapterFont3 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

            PdfPTable tableTitel = new PdfPTable(1);
            tableTitel.setHorizontalAlignment(Element.ALIGN_LEFT);
            tableTitel.setWidthPercentage(100);
            tableTitel.setSpacingAfter(20f);

            Chunk chunk1 = new Chunk("Ergebnisse: " + sql.getTempAnswersStudents().get(0).getVorname() + " " + sql.getTempAnswersStudents().get(0).getNachname(), chapterFont);

            Paragraph paragraph1 = new Paragraph(chunk1);

            PdfPCell titleC1 = new PdfPCell(paragraph1);
            Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 7);
            PdfPCell titleC2 = new PdfPCell(new Paragraph("Aufgabenblock: " + block, chapterFont2));
            PdfPCell titleC3 = new PdfPCell(new Paragraph("in Kategorie: " + sql.getTempAnswersStudents().get(0).getKategorieName(), chapterFont3));
            PdfPCell titleC4 = new PdfPCell(new Paragraph("im Fach: " + sql.getTempAnswersStudents().get(0).getFachKuerzel() + " - " + sql.getTempAnswersStudents().get(0).getFachName(), chapterFont3));

            titleC1.setBorderColor(BaseColor.WHITE);
            titleC2.setBorderColor(BaseColor.WHITE);
            titleC3.setBorderColor(BaseColor.WHITE);
            titleC4.setBorderColor(BaseColor.WHITE);

            tableTitel.addCell(titleC1);
            tableTitel.addCell(titleC2);
            tableTitel.addCell(titleC3);
            tableTitel.addCell(titleC4);

            document.add(tableTitel);
            
            document.add(new Paragraph("Zuständiger Lehrer: " + sql.getTempAnswersStudents().get(0).getZustLehrerVorname() + " " + sql.getTempAnswersStudents().get(0).getZustLehrerNachname()));
            
            float[] est = {3, 3, 3};
            PdfPTable tableResults = new PdfPTable(est);
            tableResults.setWidthPercentage(100);
            tableResults.setSpacingBefore(5f);
            tableResults.getDefaultCell().setBackgroundColor(BaseColor.LIGHT_GRAY);
            tableResults.getDefaultCell().setBorderColor(BaseColor.WHITE);
            tableResults.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            tableResults.getDefaultCell().setPadding(5);
            //HeaderSpalten der Haupttabelle
            tableResults.addCell("Frage");
            tableResults.addCell("Antwort des Schülers");
            tableResults.addCell("Richtige Antwort");

            tableResults.setHeaderRows(1);
            tableResults.getDefaultCell().setBackgroundColor(BaseColor.WHITE);
            tableResults.getDefaultCell().setBorderColor(BaseColor.LIGHT_GRAY);
            tableResults.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            tableResults.getDefaultCell().setPadding(5);
            
            for(AntwortPdfObjekt apo: sql.getTempAnswersStudents()){
                tableResults.addCell(apo.getFrage());
                if(apo.getAntwortS().equals(apo.getRichtigeAntwort())){
                    tableResults.getDefaultCell().setBackgroundColor(new BaseColor(0x00,0xFF, 0x7F)); //Hexcode
                } else{
                    tableResults.getDefaultCell().setBackgroundColor(new BaseColor(0xCD,0x5C, 0x5C)); //Hexcode
                }
                tableResults.addCell(apo.getAntwortS());
                tableResults.getDefaultCell().setBackgroundColor(BaseColor.WHITE);
                tableResults.addCell(apo.getRichtigeAntwort());
            }
            
            document.add(tableResults);
            
            document.close();
            writer.close();
        } catch (DocumentException | IOException | SQLException e2) {
            throw e2;
        }
    }
    
}
