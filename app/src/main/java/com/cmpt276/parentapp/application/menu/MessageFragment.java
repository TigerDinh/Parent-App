package com.cmpt276.parentapp.application.menu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import com.cmpt276.parentapp.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class MessageFragment extends AppCompatDialogFragment {
    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.message_layout,null);

        DialogInterface.OnClickListener listener = (dialogInterface, which) -> {
            switch(which)
            {
                case DialogInterface.BUTTON_POSITIVE:
                    if (getActivity() != null) getActivity().finish();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        };


        return new AlertDialog.Builder(getActivity())
                .setTitle("Are you sure?")
                .setView(view)
                .setPositiveButton("yes",listener)
                .setNegativeButton("no",listener)
                .create();
    }
}
