package com.cmpt276.parentapp.application.task.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.cmpt276.parentapp.application.children.model.Child;
import com.cmpt276.parentapp.application.children.model.ChildrenManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

public class TaskManager {

    private static final String SHARED_PREFS = "shared preferences";
    private static final String TURN_TAKER_KEY = "turn taker key";
    private static final String TASKS_FILE = "tasks.json";

    private ArrayList<Task> taskList;
    private final File fileDirectory;

    public TaskManager(File fileDirectory) {
        this.fileDirectory = fileDirectory;
        loadsTasks();
    }

    public void addTask(String taskDescription, Child turnTaker){
        Task newTask = new Task(taskDescription, turnTaker, getUniqueID());
        taskList.add(newTask);
        saveTasks();
    }

    public ArrayList<Task> getTaskList() {
        return taskList;
    }

    public void removeTask(int position){
        taskList.remove(position);
        saveTasks();
    }


    public Task getTask(int position){
        return taskList.get(position);
    }

    public Task getTaskFromId(int taskID) {
        for (Task task : taskList) {
            if (task.getTaskID() == taskID) {
                return task;
            }
        }
        return null;
    }

    public void replaceTask(Task newTask, int position){
        taskList.set(position, newTask);
        saveTasks();
    }

    public int getNumOfTasks() {
        return taskList.size();
    }

    public Child getFirstTurnTaker(Context context) {
        ChildrenManager childrenManager = ChildrenManager.getInstance(context);
        Child turnTaker;
        int childIndex;

        if (childrenManager.getList().size() > 0) {
            SharedPreferences sharedPreferences =
                    context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
            childIndex = sharedPreferences.getInt(TURN_TAKER_KEY, 0);
            turnTaker = childrenManager.getChildren(childIndex);
        }
        else {
            turnTaker = new Child("anonymous", null, null);
        }

        return turnTaker;
    }

    public Integer getUniqueID() {
        Random random = new Random();
        int id = random.nextInt();
        for (int i = 0; i < taskList.size(); i++) {
            if (taskList.get(i).getTaskID() == id) {
                id = random.nextInt();
                i = 0;
            }
        }
        return id;
    }

    public void incrementFirstTurnTaker(Context context) {
        ChildrenManager childrenManager = ChildrenManager.getInstance(context);
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        int childIndex = sharedPreferences.getInt(TURN_TAKER_KEY, 0);
        childIndex++;

        // Child index keep track of which child is choosing next
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (childIndex < childrenManager.getList().size()) {
            editor.putInt(TURN_TAKER_KEY, childIndex);
        }
        else {
            editor.putInt(TURN_TAKER_KEY, 0);
        }
        editor.apply();
    }

    //Reference from: https://attacomsian.com/blog/gson-write-json-file
    private void saveTasks() {
        File file = new File(fileDirectory, TASKS_FILE);
        Gson gson = new Gson();

        try {
            Writer writer = Files.newBufferedWriter(Paths.get(file.getAbsolutePath()));
            gson.toJson(taskList, writer);
            writer.close();
        }
        catch(IOException exception){
            exception.printStackTrace();
        }
    }

    //Reference from: https://attacomsian.com/blog/gson-read-json-file
    private void loadsTasks() {
        taskList = null;
        File file = new File(fileDirectory, TASKS_FILE);
        Gson gson = new Gson();

        try {
            Reader reader = Files.newBufferedReader(Paths.get(file.getAbsolutePath()));
            taskList = gson.fromJson(reader, new TypeToken<ArrayList<Task>>() {
            }.getType());

            reader.close();
        }
        catch(IOException exception){
            exception.printStackTrace();
        }

        if (taskList == null) taskList = new ArrayList<>();
    }
}
