package com.cmpt276.parentapp.application.coinflip;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.cmpt276.parentapp.application.coinflip.model.CoinFlipRecords;
import com.cmpt276.parentapp.application.coinflip.model.CoinFlipRecordsAdapter;
import com.cmpt276.parentapp.R;

import java.io.IOException;
import java.util.ArrayList;


public class CoinFlipHistoryFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layoutView = inflater.inflate(R.layout.fragment_coin_flip_history, container, false);
        try {
            setUpList(layoutView);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return layoutView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void setUpList(View layoutView) throws IOException {
        Activity activity = getActivity();

        if (activity != null) {
            ListView lvList = layoutView.findViewById(R.id.lvList);
            ArrayList<CoinFlipRecords> records = CoinFlipRecords.loadCoinFlipHistory(activity);
            CoinFlipRecordsAdapter adapter = new CoinFlipRecordsAdapter(getContext(), R.layout.records_adapter_view, records);
            lvList.setAdapter(adapter);
        }
    }
}