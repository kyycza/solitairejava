package com.svi.solitaire.model;

/**
 * The face-up part of the talon. Cards arrive only by drawing from the stock,
 * and only the top card can be played (the default rule of {@link Pile}).
 */
public final class Waste extends Pile {

    Waste() {
        super("Waste");
    }

    private Waste(Waste original) {
        super(original);
    }

    @Override
    public boolean canAccept(Card card) {
        return false;
    }

    @Override
    Waste copy() {
        return new Waste(this);
    }
}
