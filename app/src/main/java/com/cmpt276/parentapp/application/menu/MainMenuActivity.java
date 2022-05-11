package com.cmpt276.parentapp.application.menu;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.cmpt276.parentapp.R;
import com.cmpt276.parentapp.application.HelpScreen.HelpScreenActivity;
import com.cmpt276.parentapp.application.TakeBreath.TakeBreathActivity;
import com.cmpt276.parentapp.application.children.ConfigureActivity;
import com.cmpt276.parentapp.application.children.model.ChildrenManager;
import com.cmpt276.parentapp.application.coinflip.CoinFlipActivity;
import com.cmpt276.parentapp.application.task.ListOfTasksActivity;
import com.cmpt276.parentapp.application.timeout.TimeoutTimerSettingsActivity;

public class MainMenuActivity extends AppCompatActivity {

    private static boolean timerIsRunning = false;
    private static MediaPlayer timerSound;
    private static Vibrator timerVibrate;


    public static Intent makeIntent(Context context) {
        return new Intent(context, MainMenuActivity.class);
    }

    public static Intent makeIntentWhileTimerRunning(Context context) {
        Intent newIntent = new Intent(context, MainMenuActivity.class);
        timerIsRunning = true;
        return newIntent;
    }

    public static Intent makeIntentFromNotification(
            Context context,
            MediaPlayer givenTimerSound,
            Vibrator givenTimerVibrate)
    {
        timerSound = givenTimerSound;
        timerVibrate = givenTimerVibrate;
        return new Intent(context, MainMenuActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setTitle("Main Menu");
        addHomeButton();

        ChildrenManager.getInstance(this);
        setUpCoinFlipButton();
        setUpTimeoutButton();
        setUpConfigureButton();
        setUpBackgroundAnimation();
        setupTaskButton();
        stopTimerSound();
        setupHelpScreenButton();
        setUpTakeBreathButton();
    }

    private void setUpTakeBreathButton() {
        Button btnTimeout = findViewById(R.id.takeBreath);
        MediaPlayer mediaPlayer = MediaPlayer.create(MainMenuActivity.this,R.raw.buttonpress);
        btnTimeout.setOnClickListener((v)-> {
            mediaPlayer.start();
            startActivity(TakeBreathActivity.makeIntent(this));
        });
    }

    private void stopTimerSound() {
        if (timerVibrate != null && timerSound != null){
            timerVibrate.cancel();
            timerSound.stop();
        }
    }

    private void setUpCoinFlipButton() {
        Button btnCoinFlip = findViewById(R.id.buttonCoinFlip);
        MediaPlayer mediaPlayer = MediaPlayer.create(MainMenuActivity.this,R.raw.buttonpress);
        btnCoinFlip.setOnClickListener((v)-> {
            mediaPlayer.start();
            startActivity(CoinFlipActivity.makeIntent(this));
        });
    }

    private void setUpTimeoutButton() {
        Button btnTimeout = findViewById(R.id.btnTimeout);
        MediaPlayer mediaPlayer = MediaPlayer.create(MainMenuActivity.this,R.raw.buttonpress);
        btnTimeout.setOnClickListener((v)-> {
            mediaPlayer.start();
            if (!timerIsRunning){
                startActivity(TimeoutTimerSettingsActivity.makeIntent(this));
            }
            else{
                timerIsRunning = false;
                finish();   // Direct user to TimeoutTimerActivity
            }
        });
    }

    private void setUpConfigureButton() {
        Button btnTimeout = findViewById(R.id.configureBtn);
        MediaPlayer mediaPlayer = MediaPlayer.create(MainMenuActivity.this,R.raw.buttonpress);
        btnTimeout.setOnClickListener((v)-> {
            mediaPlayer.start();
            startActivity(ConfigureActivity.makeIntent(this));
        });
    }

    private void setUpBackgroundAnimation() {
        ConstraintLayout constraintLayout = findViewById(R.id.MainMenuActivity);

        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(0);
        animationDrawable.setExitFadeDuration(0);
        animationDrawable.start();
    }

    private void setupTaskButton() {
        Button btnTask = findViewById(R.id.btnListOfTasks);
        MediaPlayer mediaPlayer = MediaPlayer.create(MainMenuActivity.this,R.raw.buttonpress);
        btnTask.setOnClickListener((v)->{
            mediaPlayer.start();
            startActivity(ListOfTasksActivity.makeIntent(this));
        });
    }

    private void setupHelpScreenButton() {
        Button btnHelpScreen = findViewById(R.id.btnHelpScreen);
        MediaPlayer mediaPlayer = MediaPlayer.create(MainMenuActivity.this,R.raw.buttonpress);
        btnHelpScreen.setOnClickListener((v)->{
            mediaPlayer.start();
            startActivity(HelpScreenActivity.makeIntent(this));
        });
    }

    /**
     * When the home button is clicked treat it as a back press
     * @param item Auto-called object selected
     * @return If an item was selected
     */
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
}