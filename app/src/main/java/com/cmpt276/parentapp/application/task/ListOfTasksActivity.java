package com.cmpt276.parentapp.application.task;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.cmpt276.parentapp.application.task.model.TaskListAdapter;
import com.cmpt276.parentapp.application.task.model.TaskManager;
import com.cmpt276.parentapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ListOfTasksActivity extends AppCompatActivity {

    private static TaskManager taskManager;

    public static Intent makeIntent(Context context){
        return new Intent(context, ListOfTasksActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        this.setTitle(getString(R.string.list_of_tasks));

        startNewTaskManager();
        setupNewTaskFloatingBtn();
        addHomeButton();
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayListOfTasks();
    }

    private void startNewTaskManager() {
        if (taskManager == null){
            taskManager = new TaskManager(getFilesDir());
        }
    }

    private void setupNewTaskFloatingBtn() {
        FloatingActionButton fabAddNewTask = findViewById(R.id.fabAddNewTask);
        fabAddNewTask.setOnClickListener((v)->{
            Intent newTaskIntent = NewTaskActivity.makeNewTaskIntent(
                    this,
                    taskManager
            );
            startActivity(newTaskIntent);
        });
    }

    private void addHomeButton() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void displayListOfTasks() {
        ListView taskListView = findViewById(R.id.taskListView);
        TaskListAdapter taskListAdapter = new TaskListAdapter(
                this, R.layout.tasks_adapter_view, taskManager);
        taskListView.setAdapter(taskListAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();

        // If the arrow (top left) is pressed go back a screen
        if (itemID == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}