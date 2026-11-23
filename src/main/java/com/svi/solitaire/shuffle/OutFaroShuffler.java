package com.svi.solitaire.shuffle;

/** Out-faro: the top half gives the first card, so the top card stays on top. 8 shuffles restore the deck. */
public class OutFaroShuffler extends FaroShuffler {

    public OutFaroShuffler(int times) {
        super(times);
    }

    @Override
    protected boolean bottomHalfGoesFirst() {
        return false;
    }

    @Override
    protected String getName() {
        return "Out-faro";
    }

    /** An out-faro shuffle returns the deck to its original order after 8 shuffles. */
    private static final int CYCLE_LENGTH = 8;

    @Override
    public int getCycleLength() {
        return CYCLE_LENGTH;
    }

    @Override
    public OutFaroShuffler withTimes(int times) {
        return new OutFaroShuffler(times);
    }
}
