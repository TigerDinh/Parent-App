package com.cmpt276.parentapp.application.coinflip.model;

import android.content.Context;

import com.cmpt276.parentapp.application.children.model.Child;
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

public class CoinFlipRecords {

    private final Child chooser;
    private final int choice;
    private final int result;
    private final boolean wonGame;
    private final LocalDateTime timeOfFlip;

    /**
     * @param chooser: child in this game.
     * @param choice: which side does he/she chose.
     * @param result: which side comes up on the flip.
     */
    public CoinFlipRecords(Child chooser, int choice, int result){
        this.chooser = chooser;
        this.timeOfFlip = LocalDateTime.now();
        this.choice = choice;
        this.result = result;
        this.wonGame = choice == result;
    }

    public Child getChooser() {
        return chooser;
    }

    public int getChoice() {
        return choice;
    }

    public int getResult() {
        return result;
    }

    public boolean hasWonGame() {
        return wonGame;
    }

    public LocalDateTime getTimeOfFlip() {
        return timeOfFlip;
    }

    //Saving and loading the Data via Gson
    public static void saveCoinFlipHistory(Context context,ArrayList<CoinFlipRecords> records) {
        //Reference from: https://attacomsian.com/blog/gson-write-json-file
        File file = new File(context.getFilesDir(), "coinFlip.json");
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

    public static ArrayList<CoinFlipRecords> loadCoinFlipHistory(Context context) {
        //Reference from: https://attacomsian.com/blog/gson-read-json-file
        ArrayList<CoinFlipRecords> records = null;
        File file = new File(context.getFilesDir(), "coinFlip.json");
        Gson gson = createGson();

        try {
            Reader reader = Files.newBufferedReader(Paths.get(file.getAbsolutePath()));
            records = gson.fromJson(reader, new TypeToken<ArrayList<CoinFlipRecords>>() { // from https://attacomsian.com/blog/gson-read-json-file
            }.getType());

            reader.close();
        }
        catch(IOException exception){
            exception.printStackTrace();
        }

        if(records == null){return new ArrayList<>();}
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
