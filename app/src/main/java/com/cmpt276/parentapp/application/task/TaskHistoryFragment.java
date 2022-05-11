package com.cmpt276.parentapp.application.task;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cmpt276.parentapp.R;
import com.cmpt276.parentapp.application.task.model.TaskHistory;
import com.cmpt276.parentapp.application.task.model.TaskHistoryAdapter;

import java.io.IOException;
import java.util.ArrayList;

public class TaskHistoryFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layoutView = inflater.inflate(R.layout.fragment_task_history, container, false);
        try {
            setUpList(layoutView);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Button btnTaskHistory = layoutView.findViewById(R.id.btnTaskHistoryFragment);
        btnTaskHistory.setOnClickListener((v) -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return layoutView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void setUpList(View layoutView) throws IOException {
        NewTaskActivity activity = (NewTaskActivity) getActivity();

        if (activity != null) {
            ListView lvTaskHistory = layoutView.findViewById(R.id.lvTaskHistory);
            ArrayList<TaskHistory> records = TaskHistory.loadTaskHistory(activity, activity.getTaskID());
            TaskHistoryAdapter adapter = new TaskHistoryAdapter(getContext(), R.layout.histrory_adapter_view, records);
            lvTaskHistory.setAdapter(adapter);
        }
    }
}