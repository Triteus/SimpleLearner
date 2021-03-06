package main.models;

import java.util.ArrayList;

public class Block {

    private String name;
    private String category;
    private Task currTask;
    private ArrayList<Task> tasks;


    public Block(String name, ArrayList<Task> tasks) {
        this.name = name;
        this.tasks = tasks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Task getCurrTask() {
        return currTask;
    }

    public void setCurrTask(int index) {
        this.currTask = tasks.get(index);
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;

    }

    public void addTask(Task task) {
        this.tasks.add(task);
    }

    public void deleteTask(int index) {
        this.tasks.remove(index);

        if(tasks.size() > 0)
            setCurrTask(0);
    }

    public void deleteCurrTask() {

        int index = tasks.indexOf(currTask);
        deleteTask(index);
    }

    public boolean switchToNextTask() {

        if(!isLastTask()) {
            int index = tasks.indexOf(currTask);
            currTask = tasks.get(index + 1);
            return true;
        }

        return false;
    }

    public boolean switchToPrevTask() {

        if(!isFirstTask()) {
            int index = tasks.indexOf(currTask);
            currTask = tasks.get(index - 1);
            return true;
        }

        return false;

    }

    public void printBlock() {
        System.out.println("Blockname: " + name);
        System.out.println("Tasks:");
        for(Task task : tasks) {
            System.out.println("Question: " + task.getQuestion());
            for(Answer answer : task.getAnswers()) {
                System.out.println("Answer: " + answer.getAnswerText());
            }

        }
    }

    public boolean isLastTask() {
        return tasks.indexOf(currTask) == tasks.size() - 1;
    }

    public boolean isFirstTask() {
        return tasks.indexOf(currTask) == 0;
    }

}
