package com.cmpt276.parentapp.application.task;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cmpt276.parentapp.R;
import com.cmpt276.parentapp.application.children.model.Child;
import com.cmpt276.parentapp.application.children.model.ChildrenManager;
import com.cmpt276.parentapp.application.coinflip.CoinFlipHistoryFragment;
import com.cmpt276.parentapp.application.coinflip.model.CoinFlipRecords;
import com.cmpt276.parentapp.application.task.model.Task;
import com.cmpt276.parentapp.application.task.model.TaskHistory;
import com.cmpt276.parentapp.application.task.model.TaskManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class NewTaskActivity extends AppCompatActivity {

    public static final boolean NOT_EDITING_TASK = false;
    public static final boolean EDITING_TASK = true;

    private static TaskManager taskManager;
    private static boolean isEditingTask;
    private boolean changesWereMade;
    private static int editingTaskIndex;
    private static Task editingTask;

    private ArrayList<TaskHistory> taskHistory;
    private Button btnTaskHistory;
    public int taskID;

    private String userInput;
    private Child turnTaker;

    public static Intent makeNewTaskIntent(Context context, TaskManager givenTaskManager){
        taskManager = givenTaskManager;
        isEditingTask = NOT_EDITING_TASK;
        return new Intent(context, com.cmpt276.parentapp.application.task.NewTaskActivity.class);
    }

    public static Intent makeEditTaskIntent(Context context, TaskManager givenTaskManager, int position) {
        taskManager = givenTaskManager;
        editingTaskIndex = position;
        editingTask = taskManager.getTask(position);
        isEditingTask = EDITING_TASK;
        return new Intent(context, com.cmpt276.parentapp.application.task.NewTaskActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        this.setTitle(getString(R.string.creating_new_task));

        addHomeButton();
        editingTask();
        addDetectionToUserInput();
        setUpCompleteTask();
        setUpDeleteTask();

        try {
            establishTurnTaker();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public int getTaskID() {
        return taskID;
    }

    private void saveHistory() {
        TaskHistory.saveTaskHistory(this, taskHistory, taskID);
    }

    private void loadHistory() {
        taskHistory = TaskHistory.loadTaskHistory(this, taskID);
    }

    private void establishTurnTaker() throws FileNotFoundException {
        if (isEditingTask) {
            turnTaker = editingTask.getCurrentTurnTaker();
        }
        else {
            turnTaker = taskManager.getFirstTurnTaker(this);
        }

        TextView tvChildrenName = findViewById(R.id.tvChildrenName);
        tvChildrenName.setText(turnTaker.getName());

        if (turnTaker.getChildId() != null) {
            ImageView ivChildrenPicture = findViewById(R.id.ivChildrenPicture);
            File file = new File(turnTaker.getImageLocation(), turnTaker.getChildId().toString());
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            ivChildrenPicture.setImageBitmap(bitmap);
        }

    }

    private void editingTask() {
        TextView taskDescription = findViewById(R.id.txtTaskDescription);

        if (isEditingTask){
            String currentTaskDescription = editingTask.getTaskDescription();
            taskDescription.setText(currentTaskDescription);
            this.setTitle(getString(R.string.editing_task));
            taskID = editingTask.getTaskID();
            btnTaskHistory = findViewById(R.id.btnTaskHistory);
            btnTaskHistory.setVisibility(View.VISIBLE);
            loadHistory();
            setUpTaskHistoryButton();

        }
        else{
            Button btnCompletedTask = findViewById(R.id.btnCompletedTask);
            Button btnDeleteTask = findViewById(R.id.btnDeleteTask);
            btnCompletedTask.setVisibility(View.INVISIBLE);
            btnDeleteTask.setVisibility(View.INVISIBLE);
            taskDescription.setText("");
        }
    }

    private void setUpTaskHistoryButton() {
        final String FRAGMENT_BACKSTACK_OPEN = "open";

        btnTaskHistory.setOnClickListener((v) -> {
            // https://developer.android.com/guide/fragments/animate#java
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.enter_from_bottom,  // enter
                            R.anim.exit_from_bottom,   // exit
                            R.anim.enter_from_bottom,  // popEnter
                            R.anim.exit_from_bottom    // popExit
                    )
                    .replace(R.id.fragmentHistory, new TaskHistoryFragment())
                    .addToBackStack(FRAGMENT_BACKSTACK_OPEN)
                    .commit();
        });
    }

    private Child getNextTurnTaker() {
        ChildrenManager childrenManager = ChildrenManager.getInstance(this);

        if (childrenManager.getList().size() == 0) {
            return new Child("anonymous", null, 0);
        }

        int childIndex = 0;
        for (int i = 0; i < childrenManager.getList().size(); i++) {
            if (childrenManager.getChildren(i).getChildId().equals(turnTaker.getChildId())) {
                childIndex = i;
                break;
            }
        }
        childIndex++;

        if (childIndex >= childrenManager.getList().size()) {
            childIndex = 0;
        }

        return childrenManager.getChildren(childIndex);
    }

    private void setUpCompleteTask() {
        Button btnCompletedTask = findViewById(R.id.btnCompletedTask);
        btnCompletedTask.setOnClickListener((v) -> {
            // No child in system
            if (turnTaker == null) {
                taskHistory.add(new TaskHistory(taskID, LocalDateTime.now(), null));
            }
            else {
                taskHistory.add(new TaskHistory(taskID, LocalDateTime.now(), turnTaker.getChildId()));
            }
            turnTaker = getNextTurnTaker();
            saveHistory();
            saveChanges();
            finish();
        });
    }

    private void setUpDeleteTask() {
        Button btnDeleteTask = findViewById(R.id.btnDeleteTask);
        btnDeleteTask.setOnClickListener((v) -> {
            AlertDialog.Builder confirmEditCancel = new AlertDialog.Builder(NewTaskActivity.this);
            confirmEditCancel.setMessage("Are you sure you want to delete this task?");
            confirmEditCancel.setCancelable(false);
            confirmEditCancel.setPositiveButton("Yes", (dialogInterface, i) -> {
                taskManager.removeTask(editingTaskIndex);
                finish();
            });
            confirmEditCancel.setNegativeButton("No", null);
            confirmEditCancel.show();
        });
    }

    private void addDetectionToUserInput() {
        TextView txtTaskDescription = findViewById(R.id.txtTaskDescription);
        userInput = txtTaskDescription.getText().toString();
        TextWatcher detectUserInput = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence == null || charSequence.toString().isEmpty()){
                    changesWereMade = false;
                    return;
                }
                changesWereMade = true;
                userInput = txtTaskDescription.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        };

        txtTaskDescription.addTextChangedListener(detectUserInput);
    }

    private void addHomeButton() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    // Creates a save icon in the toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_modify,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();

        // If the arrow (top left) is pressed go back a screen
        if (itemID == android.R.id.home) {
            onBackPressed();
            return true;
        }

        if (itemID == R.id.save_game) {
            if (changesWereMade){
                saveChanges();
            }
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveChanges() {
        if (isEditingTask) {
            Task editedTask = new Task(userInput, turnTaker, taskID);
            taskManager.replaceTask(editedTask, editingTaskIndex);
        }
        else {
            taskManager.addTask(userInput, taskManager.getFirstTurnTaker(this));
            taskManager.incrementFirstTurnTaker(this);
        }
    }

    @Override
    public void onBackPressed() {
        if (changesWereMade){
            AlertDialog.Builder confirmEditCancel = new AlertDialog.Builder(NewTaskActivity.this);
            confirmEditCancel.setMessage("Are you sure you want to exit and cancel your unsaved changes?");
            confirmEditCancel.setCancelable(false);
            confirmEditCancel.setPositiveButton("Yes", (dialogInterface, i) -> finish());
            confirmEditCancel.setNegativeButton("No", null);
            confirmEditCancel.show();
        }
        else{
            finish();
        }
    }
}