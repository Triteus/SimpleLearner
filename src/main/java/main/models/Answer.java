package main.models;

public class Answer {

    public Answer(String ansText, boolean isRight) {
        this.answerText = ansText;
        this.isRight = isRight;
    }

    private String answerText;
    private boolean isRight;

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public boolean isRight() {
        return isRight;
    }

    public void setRight(boolean right) {
        isRight = right;
    }



}
