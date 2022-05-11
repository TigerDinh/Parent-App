package com.cmpt276.parentapp.application.coinflip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmpt276.parentapp.application.children.model.Child;
import com.cmpt276.parentapp.application.children.model.ChildrenManager;
import com.cmpt276.parentapp.application.coinflip.model.CoinFlip;
import com.cmpt276.parentapp.application.coinflip.model.CoinFlipQueue;
import com.cmpt276.parentapp.application.coinflip.model.CoinFlipRecords;
import com.cmpt276.parentapp.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Activity that controls the coin flipping mechanism of this app
 */
public class CoinFlipActivity extends AppCompatActivity {

    private final CoinFlip coin = new CoinFlip(CoinFlip.HEADS);
    private ArrayList<CoinFlipRecords> coinFlipRecords;
    private Child chosen;

    private Boolean isChoosing = true;
    private Boolean doneFlipping = false;
    private Boolean isAnimating = false;
    private Boolean isFlipping = false;
    private int flipCount;

    private ImageView ivCoin;
    private TextView tvChildName;
    private TextView tvTapToFlip;
    private TextView tvChoosingPrompt;
    private TextView tvCurrentCoinFace;
    private ImageView ivChooseHeads;
    private ImageView ivChooseTails;
    private ConstraintLayout layoutChoosing;
    private ConstraintLayout layoutFlipping;
    private Button btnHistory;
    private ImageView portrait;

    private CoinFlipQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ChildrenManager manager = ChildrenManager.getInstance(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_flip);
        this.setTitle(getString(R.string.coin_flip_title));

        try {
            loadHistory();
        } catch (IOException e) {
            e.printStackTrace();
        }

        queue = manager.getQueue();
        setLayoutViews();
        addHomeButton();
        chooseCandidate();
        setUpCoinFlipButton();
        setUpChooseFace();
        setUpHistoryList();
    }

    private void setAnonymous() {
        queue.cleanCandidate();
        freshCandidate();
        ImageView mainImage = findViewById(R.id.portrait);
        mainImage.setImageResource(R.drawable.default_child_image);
    }

    private void setLayoutViews() {
        ImageView anonymous = findViewById(R.id.anonymous);
        anonymous.setOnClickListener(view -> setAnonymous());

        tvChildName = findViewById(R.id.tvChildName);
        portrait = findViewById(R.id.portrait);
        tvTapToFlip = findViewById(R.id.tvTapToFlip);
        tvChoosingPrompt = findViewById(R.id.tvChoosingPrompt);
        tvCurrentCoinFace = findViewById(R.id.tvCurrentCoinFace);
        ivCoin = findViewById(R.id.ivCoin);
        ivChooseHeads = findViewById(R.id.ivChooseHeads);
        ivChooseTails = findViewById(R.id.ivChooseTails);
        layoutChoosing = findViewById(R.id.layoutChoosing);
        layoutFlipping = findViewById(R.id.layoutFlipping);
        btnHistory = findViewById(R.id.btnHistory);
    }

    private void loadHistory() throws IOException {
        coinFlipRecords = CoinFlipRecords.loadCoinFlipHistory(this);
    }

    private void saveHistory() {
        CoinFlipRecords.saveCoinFlipHistory(this, coinFlipRecords);
    }

    private void chooseCandidate() {
        tvChildName.setOnClickListener(view -> startActivity(new Intent(this, QueueActivity.class)));
        portrait.setOnClickListener(view -> startActivity(new Intent(this, QueueActivity.class)));
        freshCandidate();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void freshCandidate() {
        chosen = queue.getCandidate();
        tvChildName.setText(chosen.getName());
        try {
            if (chosen.getChildId() != null) {
                File f = new File(chosen.getImageLocation(), chosen.getChildId().toString());
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                portrait.setImageBitmap(b);
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            portrait.setImageDrawable(getDrawable(R.drawable.default_child_image));
        }
    }

    private void setUpCoinFlipButton() {
        ivCoin.setOnClickListener((v)-> {
            if (!isFlipping) {
                fadeOut(tvChildName);
                fadeOut(tvChoosingPrompt);
                tvTapToFlip.setVisibility(View.INVISIBLE);
                flipCoinAnimation();
            }
            if (doneFlipping) {
                resetViews();
            }
        });
    }

    private void flipCoinAnimation() {
        int magicNumberGodKnowsWhat = 8;
        isFlipping = true;
        flipCount = magicNumberGodKnowsWhat + coin.getRandomSide();
        Handler flipping = new Handler();

        Runnable flippingCoin = new Runnable() {
            @Override
            public void run() {
                // Restart animation when animation ends
                if (!isAnimating && flipCount != 0) {
                    flippingAnimation();
                }

                // Keep the loop running
                if (flipCount > 0) {
                    flipping.post(this);
                }

                // End the loop
                else if (flipCount == 0) {
                    try {
                        determineWinner();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        flipping.post(flippingCoin);
    }

    // Button for choosing which side you want
    private void setUpChooseFace() {
            ivChooseTails.setOnClickListener((v) -> {
                if (isChoosing) {
                    isChoosing = false;
                    setChoice(CoinFlip.TAILS);
                    fadeIn(layoutFlipping);
                    fadeIn(tvTapToFlip);
                    fadeOut(layoutChoosing);
                }
            });

            ivChooseHeads.setOnClickListener((v) -> {
                if (isChoosing) {
                    isChoosing = false;
                    setChoice(CoinFlip.HEADS);
                    fadeIn(layoutFlipping);
                    fadeIn(tvTapToFlip);
                    fadeOut(layoutChoosing);
                }
            });
    }

    // When the arrow button is clicked show the history fragment
    private void setUpHistoryList() {
        final String FRAGMENT__BACKSTACK_OPEN = "open";

        btnHistory.setOnClickListener((v)-> {

            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                btnHistory.setRotation(0);
                getSupportFragmentManager().popBackStack();
            }
            else {
                btnHistory.setRotation(180);
                // https://developer.android.com/guide/fragments/animate#java
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(
                                R.anim.enter_from_bottom,  // enter
                                R.anim.exit_from_bottom,   // exit
                                R.anim.enter_from_bottom,  // popEnter
                                R.anim.exit_from_bottom    // popExit
                        )
                        .replace(R.id.flHistory, new CoinFlipHistoryFragment())
                        .addToBackStack(FRAGMENT__BACKSTACK_OPEN)
                        .commit();
            }
        });
    }

    private void flipCoin() {
        if (coin.isHeads()) setCoinFace(CoinFlip.TAILS);
        else setCoinFace(CoinFlip.HEADS);
    }

    private void setCoinFace(int choice) {
        String currentFace;

        if (choice == CoinFlip.TAILS) {
            currentFace = getString(R.string.tails);
            ivCoin.setImageResource(R.drawable.loonie_tail);
            coin.setSide(CoinFlip.TAILS);
        }
        else {
            currentFace = getString(R.string.heads);
            ivCoin.setImageResource(R.drawable.loonie_head);
            coin.setSide(CoinFlip.HEADS);
        }

        tvCurrentCoinFace.setText(currentFace);
    }

    private void setChoice(int choice) {
        coin.setSideChoice(choice);
        setCoinFace(choice);
    }

    private void determineWinner() throws IOException {
        doneFlipping = true;
        tvChoosingPrompt.setVisibility(View.VISIBLE);
        tvChildName.setVisibility(View.VISIBLE);
        //tvChildName.setText(chosen.getName());
        tvChildName.setOnClickListener(null);
        portrait.setOnClickListener(null);
        coinFlipRecords.add(new CoinFlipRecords(chosen, coin.getSideChoice(), coin.getSide()));
        saveHistory();
        queue.pushBack(chosen);
        if (coin.getSide() == coin.getSideChoice()) {
            tvChoosingPrompt.setText(getString(R.string.winner));
        }
        else {
            tvChoosingPrompt.setText(getString(R.string.loser));
        }
    }

    private void resetViews() {
        isChoosing = true;
        doneFlipping = false;
        isAnimating = false;
        isFlipping = false;

        layoutFlipping.setVisibility(View.INVISIBLE);
        layoutChoosing.setVisibility(View.VISIBLE);

        layoutChoosing.postOnAnimationDelayed(() -> tvTapToFlip.setVisibility(View.VISIBLE), 1000);
        tvChoosingPrompt.setText(getString(R.string.choosing));
        chooseCandidate();
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

    private void fadeOut(View view) {
        view.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
        view.animate();
        view.setVisibility(View.INVISIBLE);
    }

    private void fadeIn(View view) {
        view.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        view.animate();
        view.setVisibility(View.VISIBLE);
    }

    private void flippingAnimation() {
        MediaPlayer flipSound = MediaPlayer.create(getApplicationContext(), R.raw.coindrop);

        Animation compressAnim = new ScaleAnimation(1,1,1, 0.1f,
                Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        compressAnim.setDuration(100);

        Animation expandAnim = new ScaleAnimation(1,1,0.1f,1,
                Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        expandAnim.setDuration(100);

        compressAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                flipSound.start();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                flipSound.reset();
                flipCoin();
                flipCount--;
                System.out.println(flipCount);
                ivCoin.startAnimation(expandAnim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        ivCoin.startAnimation(compressAnim);
        isAnimating = true;
        ivCoin.postOnAnimationDelayed(() -> {
            isAnimating = false;
        }, 100);
    }

    protected void onResume(){
        super.onResume();
        refreshUI();
    }

    void refreshUI(){
        chooseCandidate();
    }

    /**
     * Creates the intent to start this activity
     * @param context Context of the current activity
     * @return The intent to start this activity
     */
    public static Intent makeIntent(Context context) {
        return new Intent(context, CoinFlipActivity.class);
    }
}