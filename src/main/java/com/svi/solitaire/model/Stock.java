package com.svi.solitaire.model;

/**
 * The face-down part of the talon. Cards leave it only by drawing, and come back only when the
 * waste is turned over, so no card can ever be played onto it.
 */
public final class Stock extends Pile {

    Stock() {
        super("Stock");
    }

    private Stock(Stock original) {
        super(original);
    }

    @Override
    public boolean canAccept(Card card) {
        return false;
    }

    /** Stock cards are face down, so none of them can be picked up. */
    @Override
    public int countMovableCards() {
        return 0;
    }

    @Override
    Stock copy() {
        return new Stock(this);
    }
}
