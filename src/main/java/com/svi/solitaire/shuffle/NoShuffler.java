package com.svi.solitaire.shuffle;

import com.svi.solitaire.model.Deck;

/** Keeps the deck exactly as it is, so the game is dealt from the deck in its original order. */
public class NoShuffler implements Shuffler {

    @Override
    public Deck shuffle(Deck deck) {
        return deck;                // a Deck can never change, so giving back the same deck is safe
    }

    @Override
    public String describe() {
        return "No shuffle";
    }

    /** Without shuffling there is only one deck, so the win-rate test plays it once instead of many times. */
    @Override
    public int getCycleLength() {
        return 1;
    }
}