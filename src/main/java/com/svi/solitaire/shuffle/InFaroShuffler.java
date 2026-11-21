package com.svi.solitaire.shuffle;

/** In-faro: the bottom half gives the first card, so the top card moves to position 2. 52 shuffles restore the deck. */
public class InFaroShuffler extends FaroShuffler {

    public InFaroShuffler(int times) {
        super(times);
    }

    @Override
    protected boolean bottomHalfGoesFirst() {
        return true;
    }

    @Override
    protected String getName() {
        return "In-faro";
    }
}
