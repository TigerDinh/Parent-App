package com.cmpt276.parentapp.application.timeout;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cmpt276.parentapp.R;

public class TimeoutTimerSettingsActivity extends AppCompatActivity {

    public static final int NO_CUSTOM_TIME_ADDED = -1;
    private int timerChosen;
    private int customTime = NO_CUSTOM_TIME_ADDED;

    public static Intent makeIntent(Context context) {
        return new Intent(context, TimeoutTimerSettingsActivity.class);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeout_timer_settings);
        this.setTitle(getString(R.string.timeout_timer_settings_title));

        resetToDefault();
        createRadioButtonsOfTime();
        setupBtnStartTimer();
        addHomeButton();
        setupCustomTimerInput();
    }

    private void resetToDefault() {
        customTime = NO_CUSTOM_TIME_ADDED;
    }

    private void createRadioButtonsOfTime() {
        RadioGroup defaultTimers = findViewById(R.id.radioBtnOfTimes);
        int[] defaultTime = getResources().getIntArray(R.array.default_time);
        ColorStateList myColorStateList = new ColorStateList(
                new int[][]{
                        new int[]{this.getColor(R.color.primary_theme_dark)}
                },
                new int[]{this.getColor(R.color.primary_theme_dark)}
        );

        for (int i = 0; i< defaultTime.length; i++) {
            int currTime = defaultTime[i];

            RadioButton button = new RadioButton(this);
            String displayRadioBtnTime;

            if (i == 0){
                displayRadioBtnTime = currTime + " " + getString(R.string.minute);
            }
            else {
                displayRadioBtnTime = currTime + " " + getString(R.string.minutes);
            }

            button.setText(displayRadioBtnTime);
            button.setTextColor(getColor(R.color.white));
            button.setButtonTintList(myColorStateList);

            button.setOnClickListener((view -> {
                timerChosen = currTime;
            }));

            defaultTimers.addView(button);

            if (currTime == defaultTime[0]){
                timerChosen = currTime;
                button.setChecked(true);
            }
        }
    }

    private void setupBtnStartTimer() {

        // Default timer
        Button btnStartTimer = findViewById(R.id.btnStartTimer);
        btnStartTimer.setOnClickListener((v)->{
            startTimer(timerChosen);
        });

        // Custom Timer
        btnStartTimer = findViewById(R.id.btnStartCustomTimer);
        btnStartTimer.setOnClickListener((v)->{
            if (customTime != NO_CUSTOM_TIME_ADDED){
                startTimer(customTime);
            }
            else{
                Toast.makeText(
                        this,
                        getString(R.string.custom_timer_error_msg),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addHomeButton() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    // User can only input up to 5 digit minutes for custom timer
    private void setupCustomTimerInput() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence == null || charSequence.toString().isEmpty()){
                    customTime = NO_CUSTOM_TIME_ADDED;
                    return;
                }

                saveCustomTimer();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        EditText txtUserCustomTimer = findViewById(R.id.editTxtCustomTimer);
        txtUserCustomTimer.addTextChangedListener(watcher);
    }

    private void startTimer(int timeChosen) {
        Intent newIntent = TimeoutTimerActivity.makeIntent(this, timeChosen);
        startActivity(newIntent);
    }

    private void saveCustomTimer() {
        EditText txtUserCustomTimer = findViewById(R.id.editTxtCustomTimer);

        if(txtUserCustomTimer != null && txtUserCustomTimer.getText() != null){
            customTime = Integer.parseInt(txtUserCustomTimer.getText().toString());
        }
        else{
            customTime = NO_CUSTOM_TIME_ADDED;
        }
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