/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Pdf;

/**
 *
 * @author Marcel
 */
public class AnswerPdfObject {

    String prename, name, question, answerStudent, answerCorrect, subjectShortening, subjectName, categoryName, teacherPrename, teacherName;

    public AnswerPdfObject(String prename, String name, String question, String answerStudent, String answerCorrect, String subjectShortening, String subjectName, String categoryName, String teacherPrename, String teacherName) {
        this.prename = prename;
        this.name = name;
        this.question = question;
        this.answerStudent = answerStudent;
        this.answerCorrect = answerCorrect;
        this.subjectShortening = subjectShortening;
        this.subjectName = subjectName;
        this.categoryName = categoryName;
        this.teacherPrename = teacherPrename;
        this.teacherName = teacherName;
    }

    public String getTeacherName() {
        return teacherName;		//ex. "zustLehrerNachname"
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getTeacherPrename() {
        return teacherPrename;
    }

    public void setTeacherPrename(String teacherPrename) {
        this.teacherPrename = teacherPrename;
    }

    public String getSubjectShortening() {
        return subjectShortening;
    }

    public void setSubjectShortening(String subjectShortening) {
        this.subjectShortening = subjectShortening;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getPrename() {
        return prename;
    }

    public void setPrename(String prename) {
        this.prename = prename;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswerStudent() {
        return answerStudent;
    }

    public void setAnswerStudent(String answerStudent) {
        this.answerStudent = answerStudent;
    }

    public String getAnswerCorrect() {
        return answerCorrect;
    }

    public void setAnswerCorrect(String answerCorrect) {
        this.answerCorrect = answerCorrect;
    }
}
