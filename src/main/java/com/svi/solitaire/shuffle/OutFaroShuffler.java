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
}
