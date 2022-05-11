package com.cmpt276.parentapp.application.children.model;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.cmpt276.parentapp.application.coinflip.model.CoinFlipQueue;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class ChildrenManager implements Iterable<Child> {

    private final static String SHARED_PREFS = "shared preferences";
    private final static String EXTRAS_TASKS = "task list";
    private static ChildrenManager instance;
    private ArrayList<Child> children = new ArrayList<>();
    private CoinFlipQueue queue;

    public static ChildrenManager getInstance(Context context) {
        if(instance==null) {
            return instance = new ChildrenManager();
        }
        instance.loadData(context);
        return instance;
    }

    public void add(Child child)
    {
        this.children.add(child);
        if (queue != null) queue.add(child);
    }

    public Child getChildren(int i)
    {
        return children.get(i);
    }

    public Child getChildFromID(Integer childID) {
        if (childID == null) return null;
        for (Child child : children) {
            if (child.getChildId().equals(childID)) {
                return child;
            }
        }
        return null;
    }

    public void replaceChild(Child newChild, int i)
    {
        Child oldChild = children.set(i, newChild);
        if (queue != null) queue.replace(oldChild, newChild);
    }

    public void deleteChild(int n)
    {
        Child removed = children.remove(n);
        if (queue != null) queue.remove(removed);
    }

    public ArrayList<Child> getList()
    {
        return children;
    }

    // id 0 is reserved for the "anon" child
    public Integer getUniqueID() {
        Random random = new Random();
        int id = random.nextInt();
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i).getChildId() == id || id == 0) {
                id = random.nextInt();
                i = 0;
            }
        }
        return id;
    }

    public ArrayList<String> getListOfNames() {
        ArrayList<String> listOfNames = new ArrayList<>();
        for (Child child : this.children) {
            listOfNames.add(child.getName());
        }
        return listOfNames;
    }

    public void saveData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(children);
        editor.putString(EXTRAS_TASKS, json);
        editor.apply();
    }

    public void loadData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(EXTRAS_TASKS,null);
        Type type = new TypeToken<ArrayList<Child>>() {}.getType();
        if( json== null ) {
            children = new ArrayList<>();
        }
        else {
            children = gson.fromJson(json, type);
        }
    }

    @NonNull
    @Override
    public Iterator<Child> iterator() {
        return children.iterator();
    }

    public CoinFlipQueue getQueue() {
        if (queue == null) {
            queue = new CoinFlipQueue(this.getList());
        }
        return queue;
    }
}

