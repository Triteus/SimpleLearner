package main.models;

import java.util.ArrayList;

public class Task {

    private String question;

    private ArrayList<Answer> answers;

    public Task(String question, ArrayList<Answer> answers) {
        this.question = question;
        this.answers = answers;

    }


    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public ArrayList<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<Answer> answers) {
        this.answers = answers;
    }

}
