package com.cmpt276.parentapp.application.task.model;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class TaskHistory {
    private final int taskID;
    private final LocalDateTime timeOfCompletion;
    private final Integer childID;

    public TaskHistory(int taskID, LocalDateTime timeOfCompletion, Integer childID) {
        this.taskID = taskID;
        this.timeOfCompletion = timeOfCompletion;
        this.childID = childID;
    }

    public int getTaskID() {
        return taskID;
    }

    public Integer getChildID() {
        return childID;
    }

    public LocalDateTime getTimeOfCompletion() {
        return timeOfCompletion;
    }

    //Saving and loading the Data via Gson
    public static void saveTaskHistory(Context context, ArrayList<TaskHistory> records, int taskID) {
        //Toast.makeText(context, "Saved task:" + taskID, Toast.LENGTH_SHORT).show();
        //Reference from: https://attacomsian.com/blog/gson-write-json-file
        File file = new File(context.getFilesDir(), taskID + ".json");
        Gson gson = createGson();

        try {
            Writer writer = Files.newBufferedWriter(Paths.get(file.getAbsolutePath()));
            gson.toJson(records, writer);
            writer.close();
        }
        catch(IOException exception){
            exception.printStackTrace();
        }
    }

    public static ArrayList<TaskHistory> loadTaskHistory(Context context, int taskID) {
        //Toast.makeText(context, "Saved task:" + taskID, Toast.LENGTH_SHORT).show();
        //Reference from: https://attacomsian.com/blog/gson-read-json-file
        ArrayList<TaskHistory> records = null;
        File file = new File(context.getFilesDir(), taskID + ".json");
        Gson gson = createGson();

        try {
            Reader reader = Files.newBufferedReader(Paths.get(file.getAbsolutePath()));
            records = gson.fromJson(reader, new TypeToken<ArrayList<TaskHistory>>() { // from https://attacomsian.com/blog/gson-read-json-file
            }.getType());

            reader.close();
        }
        catch(IOException exception){
            exception.printStackTrace();
        }

        if(records == null) {return new ArrayList<>();}
        return records;
    }

    /**
     * Creates a Gson object that allows the saving of Game objects
     * @return Gson object capable of saving Game objects
     */
    private static Gson createGson() {
        return new GsonBuilder().registerTypeAdapter(LocalDateTime.class,
                // From https://stackoverflow.com/questions/39192945/serialize-java-8-localdate-as-yyyy-mm-dd-with-gson
                // and from Dr. Victor Cheung's 213 Class Assignment 1 description
                new TypeAdapter<LocalDateTime>() {
                    @Override
                    public void write(JsonWriter jsonWriter,
                                      LocalDateTime localDateTime) throws IOException {
                        jsonWriter.value(localDateTime.toString());
                    }
                    @Override
                    public LocalDateTime read(JsonReader jsonReader) throws IOException {
                        return LocalDateTime.parse(jsonReader.nextString());
                    }
                }).create();
    }
}
