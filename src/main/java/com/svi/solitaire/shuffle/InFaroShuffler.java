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

    /** An in-faro shuffle returns the deck to its original order after 52 shuffles. */
    private static final int CYCLE_LENGTH = 52;

    @Override
    public int getCycleLength() {
        return CYCLE_LENGTH;
    }

    @Override
    public InFaroShuffler withTimes(int times) {
        return new InFaroShuffler(times);
    }
}
