package com.cmpt276.parentapp.application.coinflip.model;

import java.util.Random;

/**
 * This class is going to handle the coin flip model.
 */
public class CoinFlip {
    public static final int HEADS = 0;
    public static final int TAILS = 1;

    private int sideChoice;
    private int side;

    public CoinFlip(int sideChoice){
        this.sideChoice = sideChoice;
    }

    public Boolean isHeads() {
        return (side == HEADS);
    }

    public void setSide(int side) {
        this.side = side;
    }

    public void setSideChoice(int sideChoice) {
        this.sideChoice = sideChoice;
    }

    public int getSideChoice() {
        return sideChoice;
    }

    public int getSide() {
        return side;
    }


    public int getRandomSide(){
        Random random = new Random();
        return random.nextInt(2);
    }
}