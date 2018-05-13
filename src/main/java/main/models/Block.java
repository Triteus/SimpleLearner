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


    public boolean switchToNextTask() {
        int index = tasks.indexOf(currTask);
        if(index + 1 < tasks.size()) {
            currTask = tasks.get(index + 1);

            return true;
        }

        return false;
    }

    public boolean switchToPrevTask() {

        int index = tasks.indexOf(currTask);
        if(index - 1 >= 0) {
            currTask = tasks.get(index - 1);
            return true;
        }

        return false;

    }




}
