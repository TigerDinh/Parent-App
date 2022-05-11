package com.cmpt276.parentapp.application.menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cmpt276.parentapp.R;

public class StartingActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);

        setUpMainActivityButton();
        setUpQuitButton();
       setUpAnimation();

    }


    private void setUpAnimation() {
        ConstraintLayout constraintLayout = findViewById(R.id.startingActivity);

        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(0);
        animationDrawable.setExitFadeDuration(0);
        animationDrawable.start();
    }

    private void setUpQuitButton() {
        Button btn = findViewById(R.id.quitButton);
        MediaPlayer mediaPlayer = MediaPlayer.create(StartingActivity.this,R.raw.buttonpress);

        btn.setOnClickListener(view -> {
            mediaPlayer.start();
            FragmentManager manager = getSupportFragmentManager();
            MessageFragment dialog = new MessageFragment();
            dialog.show(manager,"MessageDialog");
        });
    }


    private void setUpMainActivityButton() {
        Button btnTimeout = findViewById(R.id.mainMenuBtn);
        MediaPlayer mediaPlayer = MediaPlayer.create(StartingActivity.this,R.raw.buttonpress);
        btnTimeout.setOnClickListener((v)-> {
            mediaPlayer.start();
            startActivity(MainMenuActivity.makeIntent(this));
        });
    }


}