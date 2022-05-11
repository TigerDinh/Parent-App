package com.cmpt276.parentapp.application.TakeBreath;

import androidx.appcompat.app.AppCompatActivity;
import com.cmpt276.parentapp.R;

import android.view.KeyEvent;
import android.widget.ImageView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.TextView;

public class TakeBreathActivity extends AppCompatActivity {

    private int numberOfBreaths = 0;
    private TextView bigButtonStatusTv;
    private TextView progressTv;
    private CountDownTimer timer;
    private int count = 0;
    private Button button;
    private ImageView breath;
    private Animation inhaleAnimation;
    private Animation exhaleAnimation;
    private int chosenBreaths;
    MediaPlayer mediaPlayer;

    TextView textView;

    public static Intent makeIntent(Context context) {
        return new Intent(context, TakeBreathActivity.class);
    }
    @SuppressLint({"ClickableViewAccessibility", "ResourceType"})

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_breath);

        textView = findViewById(R.id.helpText);

        setUpConfigureButton();
        setUpTextView();

        refreshScreen();

        setUpBeginButton();
        statusMonitor();
        addHomeButton();

        breath = findViewById(R.id.breath);
        breath.setImageResource(R.drawable.inhale);
        inhaleAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.inhale);
        exhaleAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.exhale);

    }

    private void startInhaleAnimation() {
        breath.setImageResource(R.drawable.inhale);
        breath.startAnimation(inhaleAnimation);
    }

    private void startExhaleAnimation() {
        breath.setImageResource(R.drawable.exhale);
        breath.startAnimation(exhaleAnimation);
    }

    private void resetInhaleAnimation() {
        inhaleAnimation.reset();
        breath.clearAnimation();
    }

    private void addHomeButton() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();

        if (itemID == android.R.id.home) {
            onBackPressed();
            mediaPlayer.stop();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void statusMonitor() {
        progressTv = findViewById(R.id.progressTv);
        bigButtonStatusTv = findViewById(R.id.bigButtonStatusTv);
    }


    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    private void setUpBeginButton() {
        button = findViewById(R.id.bigBtn);
        button.setOnTouchListener((v, event) -> {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                bigButtonStatusTv.setText("Press");
                count = 0;
                progressTv.setText("" + count + "s");
                timer = new CountDownTimer(10000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        count++;
                        if(count == 1)
                            button.setText("IN");
                        textView.setText("Hold Button and Breathe in");
                        if(count > 3)
                            textView.setText("Nice inhale! more than 3s");
                        progressTv.setText("" + count + "s");
                    }
                    @SuppressLint("ResourceType")
                    @Override
                    public void onFinish() {
                    }
                };
                if(mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer = null;
                }
                mediaPlayer = MediaPlayer.create(TakeBreathActivity.this,R.raw.inhale);
                timer.start();
                mediaPlayer.start();
                startInhaleAnimation();
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                bigButtonStatusTv.setText("Release");
                if(count < 3){
                    count = 0;
                    progressTv.setText("" + count + "s");
                    timer.cancel();
                    resetInhaleAnimation();
                    mediaPlayer.stop();
                }
                else{
                    button.setText("OUT");
                    timer.cancel();
                    count = 10;
                    progressTv.setText("" + count + "s");
                    timer = new CountDownTimer(10000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            textView.setText("Release Button and Breathe out");
                            count--;
                            progressTv.setText("" + count + "s");
                            button.setEnabled(false);
                        }
                        @Override
                        public void onFinish() {
                            button.setEnabled(true);
                            button.setText("IN");
                            textView.setText(" ");
                            numberOfBreaths--;
                            setUpTextView();
                            if(numberOfBreaths ==0)
                            {
                                button.setText("GOOD JOB");
                                TextView tv = findViewById(R.id.breathTxt);
                                tv.setText("WELL DONE");
                                numberOfBreaths = chosenBreaths;
                            }
                        }
                    };
                    if(mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        mediaPlayer = null;
                    }
                    mediaPlayer = MediaPlayer.create(TakeBreathActivity.this,R.raw.exhale);
                    timer.start();
                    mediaPlayer.start();
                    startExhaleAnimation();
                }
            }
            return true;
        });
    }

    protected void onResume(){
        super.onResume();
        refreshScreen();
    }
    @SuppressLint("SetTextI18n")
    private void setUpTextView() {
        TextView textView = findViewById(R.id.breathTxt);
        textView.setText("Lets take "+numberOfBreaths +" breaths together");
    }

    private void setUpConfigureButton() {
        Button btn = findViewById(R.id.configure);
        btn.setOnClickListener(view -> {
            Intent intent = ConfigureBreathsActivity.makeIntent(TakeBreathActivity.this);
            startActivity(intent);
        });
    }

    private void refreshScreen() {
        numberOfBreaths = ConfigureBreathsActivity.getNumBreaths(this);
        chosenBreaths = numberOfBreaths;
        setUpTextView();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            //Log.d(this.getClass().getName(), "Back button pressed Song paused");
            mediaPlayer.stop();
        }
        return super.onKeyDown(keyCode, event);
    }

}