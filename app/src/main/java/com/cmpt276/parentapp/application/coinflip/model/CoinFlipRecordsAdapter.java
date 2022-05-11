package com.cmpt276.parentapp.application.coinflip.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmpt276.parentapp.R;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CoinFlipRecordsAdapter extends ArrayAdapter<CoinFlipRecords> {
    private final Context context;
    private final Integer resource;
    private final Integer length;

    public CoinFlipRecordsAdapter(Context context, int resource, ArrayList<CoinFlipRecords> records) {
        super(context, resource, records);
        this.context = context;
        this.resource = resource;
        this.length = records.size();
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        CoinFlipRecords record = getItem(length - position - 1);

        LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("ViewHolder") View recordsView = inflater.inflate(resource, parent, false);

        TextView tvChooserName = recordsView.findViewById(R.id.tvChooserName);
        TextView tvDate = recordsView.findViewById(R.id.tvDate);
        ImageView ivChoiceMade = recordsView.findViewById(R.id.ivChoiceMade);
        ImageView ivResult = recordsView.findViewById(R.id.ivResult);
        ImageView ivWonFlip = recordsView.findViewById(R.id.ivWonFlip);

        String chooserPrompt = "Chooser: " + record.getChooser().getName();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd h:mma");
        tvDate.setText(formatter.format(record.getTimeOfFlip()));
        tvChooserName.setText(chooserPrompt);

        // Settings the coin for choice
        if (record.getChoice() == CoinFlip.HEADS)
            ivChoiceMade.setBackgroundResource(R.drawable.loonie_head);
        else
            ivChoiceMade.setBackgroundResource(R.drawable.loonie_tail);

        // Settings the coin for result
        if (record.getResult() == CoinFlip.HEADS)
            ivResult.setBackgroundResource(R.drawable.loonie_head);
        else
            ivResult.setBackgroundResource(R.drawable.loonie_tail);

        // Settings the checkmark/x-mark in the middle
        if (record.hasWonGame())
            ivWonFlip.setBackgroundResource(R.drawable.ic_baseline_check_24);
        else
            ivWonFlip.setBackgroundResource(R.drawable.ic_baseline_close_24);

        return recordsView;
    }

 }
