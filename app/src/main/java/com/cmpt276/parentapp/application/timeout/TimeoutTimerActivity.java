package com.cmpt276.parentapp.application.timeout;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.cmpt276.parentapp.R;
import com.cmpt276.parentapp.application.menu.MainMenuActivity;

public class TimeoutTimerActivity extends AppCompatActivity {

    private static final int CONVERT_TO_MILLISECONDS = 60000;
    private static final int TIMER_IS_NOT_PAUSED = 1;
    private static final int TIMER_STOPPED = 0;
    private static final int CONVERT_TO_MINUTES = 60000;
    private static final int RESUME_BTN_ENABLED = 0;
    private static final int RESUME_BTN_DISABLED = -1;
    private static final String CHANNEL_ID = "Timer Notification";
    private static final boolean TIMER_IS_FINISHED = true;
    private static final boolean TIMER_IS_NOT_FINISHED = false;

    // These numbers are in milliseconds
    private static final int ADDITIONAL_TIME_TO_SETUP = 250;
    private static final int VIBRATION_DURATION = 1000;
    private static final int NO_VIBRATION_DURATION = 2000;
    public static final int DEFAULT_TIMER_SPEED = 1;
    public static final int DEFAULT_UPDATE_INTERVAL = 1000;
    public static final int DEFAULT_NUMBER_WITH_CHECKMARK = 100;
    public static final int DEFAULT_NUMBER_WITH_CHECKMARK_POSITION = 3;

    private int updateTimerInterval;  // in milliseconds
    private MediaPlayer timerSound;
    private Vibrator timerVibrate;
    private CountDownTimer parentAppTimer;
    private static int desiredTimeInMinutes;
    private long timeLeft;
    private int isTimerPaused;
    private int isResumeBtnDisabled;
    private long desiredTimeInMilliseconds;
    private boolean isTimerFinished;
    private NotificationManager notificationManager;
    private ProgressBar pbTimeRemaining;
    private int numberWithCheckmark;
    private int numberWithCheckmarkItemPosition;
    private Menu timer_speed_menu_settings;
    private double timerSpeed;


    public static Intent makeIntent(Context context, int chosenTime) {
        desiredTimeInMinutes = chosenTime;
        return new Intent(context, TimeoutTimerActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeout_timer);
        this.setTitle(getString(R.string.timeout_timer_title));

        setupDefaultUpdateInterval();
        addHomeButton();
        startCountdownTimer();
        setupTimerSettingsBtn();
        setupResetTimerBtn();
        setupStopOrResumeBtn();
        createNotificationChannel();
        setUpProgressBar();
    }

    private void setupDefaultUpdateInterval() {
        updateTimerInterval = DEFAULT_UPDATE_INTERVAL;
        timerSpeed = DEFAULT_TIMER_SPEED;
        numberWithCheckmark = DEFAULT_NUMBER_WITH_CHECKMARK;
        numberWithCheckmarkItemPosition = DEFAULT_NUMBER_WITH_CHECKMARK_POSITION;
        TextView txtTimerSpeed = findViewById(R.id.txtTimerSpeed);
        txtTimerSpeed.setText(getString(R.string.default_time_speed));
    }

    private void addHomeButton() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void startCountdownTimer() {
        isTimerPaused = TIMER_IS_NOT_PAUSED;
        desiredTimeInMilliseconds = ((long) desiredTimeInMinutes * CONVERT_TO_MILLISECONDS)
                + ADDITIONAL_TIME_TO_SETUP;
        createCountdownTimer(desiredTimeInMilliseconds);
        parentAppTimer.start();
    }

    private void setUpProgressBar() {
        pbTimeRemaining = findViewById(R.id.pbTimeRemaining);
    }

    private void setupTimerSettingsBtn() {
        Button btn = findViewById(R.id.btnChangeTimerSetting);
        btn.setOnClickListener((v)->{
            if (timerVibrate != null && timerSound != null){
                timerVibrate.cancel();
                timerSound.stop();
            }
            cancelNotification();
            parentAppTimer.cancel();
            finish();
        });
    }

    private void setupResetTimerBtn() {
        Button btn = findViewById(R.id.btnResetTimer);
        btn.setOnClickListener((v)->{
            if (timerVibrate != null && timerSound != null){
                timerVibrate.cancel();
                timerSound.stop();
            }

            if (isTimerPaused == TIMER_IS_NOT_PAUSED){
                parentAppTimer.cancel();
                timeLeft = desiredTimeInMilliseconds;
                timeLeft = Math.round(timeLeft/timerSpeed);
                createCountdownTimer(timeLeft);
                parentAppTimer.start();
            }
            else{
                isResumeBtnDisabled = RESUME_BTN_ENABLED;
                timeLeft = desiredTimeInMilliseconds;
                timeLeft = Math.round(timeLeft/timerSpeed);
                createCountdownTimer(timeLeft);
                parentAppTimer.start();
                pauseCountdownTimer();
                showResetTime();
            }

            cancelNotification();
        });
    }

    private void setupStopOrResumeBtn() {
        Button btn = findViewById(R.id.btnStopOrResume);
        btn.setOnClickListener((v)->{
            if (isTimerPaused == TIMER_IS_NOT_PAUSED){
                btn.setText(getString(R.string.resume));
                pauseCountdownTimer();
                cancelNotification();
            }
            else{
                if (isResumeBtnDisabled != RESUME_BTN_DISABLED){
                    btn.setText(getString(R.string.stop));
                    resumeCountdownTimer();
                }
            }
        });
    }

    private void createNotificationChannel() {
        CharSequence name = getString(R.string.notification_channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private void createCountdownTimer(long desiredTimeInMillisec){
        parentAppTimer = new CountDownTimer(desiredTimeInMillisec, updateTimerInterval)
        {
            final TextView txtCountdown = findViewById(R.id.txtTimerCountdown);
            double progress;

            public void onTick(long millisecondUntilFinished) {
                progress =  ((desiredTimeInMilliseconds-(millisecondUntilFinished*timerSpeed))*100) /desiredTimeInMilliseconds;
                pbTimeRemaining.setProgress((int) progress, true);
                isTimerFinished = TIMER_IS_NOT_FINISHED;
                isResumeBtnDisabled = RESUME_BTN_ENABLED;
                timeLeft = millisecondUntilFinished;
                long minutesLeft = Math.round(millisecondUntilFinished*timerSpeed) / CONVERT_TO_MINUTES;
                long secondsLeft = Math.round(millisecondUntilFinished*timerSpeed / 1000) - (minutesLeft*60);
                String displayTimer;

                if (secondsLeft < 10){
                    displayTimer = minutesLeft + ":0"
                            + secondsLeft;
                }
                else{
                    displayTimer = "" + minutesLeft + ":"
                            + secondsLeft;
                }

                txtCountdown.setText(displayTimer);
            }

            public void onFinish() {
                txtCountdown.setText(getString(R.string.end_of_timer));
                isResumeBtnDisabled = RESUME_BTN_DISABLED;
                isTimerFinished = TIMER_IS_FINISHED;
                pbTimeRemaining.setProgress(100);

                timerVibrate = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                long[] vibrationPattern = {NO_VIBRATION_DURATION, VIBRATION_DURATION};
                timerVibrate.vibrate(VibrationEffect.createWaveform(vibrationPattern,0));

                timerSound = MediaPlayer.create(getApplicationContext(),
                        R.raw.bell_ringing);
                timerSound.setLooping(true);
                timerSound.start();

                showTimerNotification(desiredTimeInMinutes);
            }
        };
    }

    private void pauseCountdownTimer() {
        if (timerVibrate != null && timerSound != null){
            timerVibrate.cancel();
            timerSound.stop();
        }

        TextView txtStopped = findViewById(R.id.txtTimerStopped);
        txtStopped.setText(getString(R.string.timer_stopped));
        isTimerPaused = TIMER_STOPPED;
        parentAppTimer.cancel();
    }

    private void resumeCountdownTimer() {
        TextView txtStopped = findViewById(R.id.txtTimerStopped);
        txtStopped.setText("");
        createCountdownTimer(timeLeft);
        parentAppTimer.start();
        isTimerPaused = TIMER_IS_NOT_PAUSED;
    }

    private void showResetTime() {
        pbTimeRemaining.setProgress(0, true);
        TextView txtCountdown = findViewById(R.id.txtTimerCountdown);
        String displayBeginningTime = "" + desiredTimeInMinutes + ":00";
        txtCountdown.setText(displayBeginningTime);
    }

    private void showTimerNotification(int howManyMinutesPassed){

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        long[] vibrationPattern = {NO_VIBRATION_DURATION, VIBRATION_DURATION};

        Intent timerSettingIntent = MainMenuActivity.makeIntentFromNotification(
                this,
                timerSound,
                timerVibrate
        );
        timerSettingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent stopTimerIntent = PendingIntent.getActivity(this,
                0,
                timerSettingIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder timeNotificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_timer)
                .setContentTitle(getString(R.string.timeout_timer_title))
                .setContentText("" + howManyMinutesPassed + " minutes has passed")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .addAction(R.drawable.ic_baseline_check_24, "Stop Timer", stopTimerIntent)
                .setAutoCancel(true)
                .setVibrate(vibrationPattern)
                .setContentIntent(stopTimerIntent);

        if (howManyMinutesPassed == 1 || howManyMinutesPassed == 0){
            timeNotificationBuilder.setContentText("" + howManyMinutesPassed + " minute has passed");
        }

        notificationManager.notify(100, timeNotificationBuilder.build());
    }

    public void cancelNotification() {
        if (isTimerFinished == TIMER_IS_FINISHED) {
            notificationManager.cancel(100);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.timer_speed_setting_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        timer_speed_menu_settings = menu;
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();

        if (itemID == android.R.id.home) {
            onBackPressed();
            return true;
        }

        if (itemID == android.R.id.accessibilityActionShowTooltip ){
            return true;
        }

        if (itemID == R.id.timer_speed_at_25_percent){
            changeNumberWithCheckmark(25, 0);
            updateTimerSpeedUI();
            adjustTimerSpeed(0.25);
        }

        if (itemID == R.id.timer_speed_at_50_percent){
            changeNumberWithCheckmark(50, 1);
            adjustTimerSpeed(0.5);
            updateTimerSpeedUI();
        }

        if (itemID == R.id.timer_speed_at_75_percent){
            changeNumberWithCheckmark(75, 2);
            adjustTimerSpeed(0.75);
            updateTimerSpeedUI();
        }

        if (itemID == R.id.timer_speed_at_100_percent){
            changeNumberWithCheckmark(100, 3);
            adjustTimerSpeed(1);
            updateTimerSpeedUI();
        }

        if (itemID == R.id.timer_speed_at_200_percent){
            changeNumberWithCheckmark(200, 4);
            adjustTimerSpeed(2);
            updateTimerSpeedUI();
        }

        if (itemID == R.id.timer_speed_at_300_percent){
            changeNumberWithCheckmark(300, 5);
            adjustTimerSpeed(3);
            updateTimerSpeedUI();
        }

        if (itemID == R.id.timer_speed_at_400_percent){
            changeNumberWithCheckmark(400, 6);
            adjustTimerSpeed(4);
            updateTimerSpeedUI();
        }

        return super.onOptionsItemSelected(item);
    }

    private void adjustTimerSpeed(double desireSpeed) {
        if (isTimerPaused == TIMER_IS_NOT_PAUSED) {
            parentAppTimer.cancel();
        }

        // Reverting back to default before making changes to timer speed
        if (timerSpeed != 1){
            timeLeft = Math.round(timeLeft*timerSpeed);
            timerSpeed = DEFAULT_TIMER_SPEED;
            updateTimerInterval = DEFAULT_UPDATE_INTERVAL;
        }

        timerSpeed = desireSpeed;
        updateTimerInterval = (int) Math.round(updateTimerInterval/desireSpeed);
        timeLeft = Math.round(timeLeft/timerSpeed);

        createCountdownTimer(timeLeft);
        if (isTimerPaused == TIMER_IS_NOT_PAUSED) {
            parentAppTimer.start();
        }
    }

    private void updateTimerSpeedUI() {
        TextView txtTimerSpeed = findViewById(R.id.txtTimerSpeed);
        String timerSpeedDescription = "Time @" + numberWithCheckmark + "%";
        txtTimerSpeed.setText(timerSpeedDescription);
    }

    private void changeNumberWithCheckmark(int numberChosen, int itemPosition) {

        // Removing checkmark
        String removeCheckmarkFromThisNum = "percent_" + numberWithCheckmark;
        int itemID = getResources().getIdentifier(
                removeCheckmarkFromThisNum,
                "string",
                TimeoutTimerActivity.this.getPackageName()
        );
        MenuItem removeCheckmarkFromThisItem = timer_speed_menu_settings.getItem(numberWithCheckmarkItemPosition);
        removeCheckmarkFromThisItem.setTitle(getString(itemID));

        // Adding checkmark to chosen number
        String addCheckmarkToThisNum = "percent_" + numberChosen + "_with_checkmark";
        itemID = getResources().getIdentifier(
                addCheckmarkToThisNum,
                "string",
                TimeoutTimerActivity.this.getPackageName()
        );
        MenuItem addCheckmarkToThisItem = timer_speed_menu_settings.getItem(itemPosition);
        addCheckmarkToThisItem.setTitle(getString(itemID));

        numberWithCheckmark = numberChosen;
        numberWithCheckmarkItemPosition = itemPosition;
    }

    @Override
    public void onBackPressed() {
        if (isTimerFinished == TIMER_IS_NOT_FINISHED){
            Intent newIntent = MainMenuActivity.makeIntentWhileTimerRunning(this);
            startActivity(newIntent);
        }
        else{
            if (timerVibrate != null && timerSound != null){
                timerVibrate.cancel();
                timerSound.stop();
                notificationManager.cancel(100);
            }
            parentAppTimer.cancel();
            finish();
        }
    }
}