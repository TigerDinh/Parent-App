package com.cmpt276.parentapp.application.task.model;

import com.cmpt276.parentapp.application.children.model.Child;

public class Task {
    private final String taskDescription;
    private final Child currentTurnTaker;
    private final int taskID;

    public Task(String taskDescription, Child currentTurnTaker, int taskID) {
        this.taskDescription = taskDescription;
        this.currentTurnTaker = currentTurnTaker;
        this.taskID = taskID;
    }

    public String getTaskDescription(){
        return taskDescription;
    }

    public Child getCurrentTurnTaker() {
        return currentTurnTaker;
    }

    public int getTaskID() {
        return taskID;
    }
}
