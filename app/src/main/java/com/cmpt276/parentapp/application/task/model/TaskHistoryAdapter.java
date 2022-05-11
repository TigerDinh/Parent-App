package com.cmpt276.parentapp.application.task.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cmpt276.parentapp.R;
import com.cmpt276.parentapp.application.children.model.Child;
import com.cmpt276.parentapp.application.children.model.ChildrenManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class TaskHistoryAdapter extends ArrayAdapter<TaskHistory> {
    private final Context context;
    private final Integer resource;
    private final Integer length;

    public TaskHistoryAdapter(Context context, int resource, ArrayList<TaskHistory> records) {
        super(context, resource, records);
        this.context = context;
        this.resource = resource;
        this.length = records.size();
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        TaskHistory record = getItem(length - position - 1);

        LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("ViewHolder") View recordsView = inflater.inflate(resource, parent, false);

        TextView tvTaskName = recordsView.findViewById(R.id.tvTaskName);
        TextView tvTaskChildName = recordsView.findViewById(R.id.tvTaskChildName);
        TextView tvTaskDate = recordsView.findViewById(R.id.tvTaskDate);
        ImageView ivTaskChild = recordsView.findViewById(R.id.ivTaskChild);

        TaskManager taskManager = new TaskManager(context.getFilesDir());
        Task task = taskManager.getTaskFromId(record.getTaskID());

        ChildrenManager childrenManager = ChildrenManager.getInstance(context);

        Child child = childrenManager.getChildFromID(record.getChildID());

        // If the child is deleted
        if (record.getChildID() == null || record.getChildID() == 0) {
            child = new Child("Anonymous", null, null);
        }
        if (child == null) {
            child = new Child("Deleted Child", null, null);
        }

        tvTaskName.setText(task.getTaskDescription());
        tvTaskChildName.setText(child.getName());

        // If there is a child in the system with this task
        if (child.getImageLocation() != null) {
            File f = new File(child.getImageLocation(), child.getChildId().toString());
            Bitmap b = null;
            try {
                b = BitmapFactory.decodeStream(new FileInputStream(f));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            ivTaskChild.setImageBitmap(b);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd h:mma");
        tvTaskDate.setText(formatter.format(record.getTimeOfCompletion()));


        return recordsView;
    }

}