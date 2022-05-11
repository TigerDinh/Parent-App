package com.cmpt276.parentapp.application.TakeBreath;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.cmpt276.parentapp.R;

public class ConfigureBreathsActivity extends AppCompatActivity {

    public static Intent makeIntent(TakeBreathActivity takeBreathActivity) {

        return new Intent(takeBreathActivity, ConfigureBreathsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_breaths);

        this.setTitle("Configure Number of Breaths");
        createNumberOfBreathsRadioButtons();
        addHomeButton();

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

    private void addHomeButton() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @SuppressLint("SetTextI18n")
    private void createNumberOfBreathsRadioButtons() {
        RadioGroup group = findViewById(R.id.numberBreaths);
        for (int i = 1; i <= 10; i++) {
            int number = i;
            RadioButton button = new RadioButton(this);
            button.setText(number + " Breaths");
            button.setOnClickListener(view -> saveNumberOfBreaths(number));

            group.addView(button);

            if(number == getNumBreaths(this)){
                button.setChecked(true);
            }
        }
    }
    private void saveNumberOfBreaths(int number) {

        SharedPreferences Prefs = this.getSharedPreferences("AppPref",MODE_PRIVATE);
        SharedPreferences.Editor editor = Prefs.edit();
        editor.putInt("Number of breaths",number);
        editor.apply();

    }
    static public int getNumBreaths(Context context)
    {
        SharedPreferences Prefs = context.getSharedPreferences("AppPref",MODE_PRIVATE);
        return Prefs.getInt("Number of breaths",1);
    }
}