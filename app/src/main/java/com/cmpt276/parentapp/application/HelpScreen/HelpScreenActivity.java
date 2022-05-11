package com.cmpt276.parentapp.application.HelpScreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.cmpt276.parentapp.R;

public class HelpScreenActivity extends AppCompatActivity {

    public static Intent makeIntent(Context context){
        return new Intent(context, HelpScreenActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_screen);

        setupTxtAboutUs();
        setupTxtAboutResources();
        addHomeButton();
    }

    private void setupTxtAboutUs() {
        TextView txtAboutUsDescription = findViewById(R.id.txtTeamDescription);
        txtAboutUsDescription.setText(getString(R.string.introduction_about_us));
    }

    private void setupTxtAboutResources() {
        TextView txtAboutResource = findViewById(R.id.txtResourceDescription);
        String resourceImageUsed = getString(R.string.resource_image_used);
        String resourceSoundUsed = getString(R.string.resource_sound_used);
        String resourceCodeUsed = getString(R.string.resource_code_used);
        String displayThisDescription = resourceImageUsed + resourceSoundUsed + resourceCodeUsed;

        txtAboutResource.setText(displayThisDescription);
    }

    private void addHomeButton() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
}