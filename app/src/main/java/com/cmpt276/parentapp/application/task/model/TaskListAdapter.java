package com.cmpt276.parentapp.application.task.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.cmpt276.parentapp.R;
import com.cmpt276.parentapp.application.task.NewTaskActivity;


public class TaskListAdapter extends ArrayAdapter<Task> {
    private final Context context;
    private final Integer resource;
    private final TaskManager taskManager;

    public TaskListAdapter(Context context, int resource, TaskManager taskManager) {
        super(context, resource, taskManager.getTaskList());
        this.context = context;
        this.resource = resource;
        this.taskManager = taskManager;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Task task = taskManager.getTask(position);

        LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("ViewHolder") View taskView = inflater.inflate(resource, parent, false);

        TextView tvTaskDescription = taskView.findViewById(R.id.tvTaskDescription);
        TextView tvChildAssigned = taskView.findViewById(R.id.tvChildAssigned);
        Button btnShowTask = taskView.findViewById(R.id.btnShowTask);

        tvTaskDescription.setText(task.getTaskDescription());
        tvChildAssigned.setText(task.getCurrentTurnTaker().getName());

        btnShowTask.setOnClickListener(view -> {
            context.startActivity(NewTaskActivity.makeEditTaskIntent(
                    context, taskManager, position));
        });

        return taskView;
    }
}
