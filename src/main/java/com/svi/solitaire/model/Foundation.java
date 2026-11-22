package com.svi.solitaire.model;

import com.svi.solitaire.enums.Rank;
import com.svi.solitaire.enums.Suit;

/** The foundation of one suit. It is built up one card at a time, from Ace to King. */
public final class Foundation extends Pile {

    private final Suit suit;

    Foundation(Suit suit) {
        super(suit.getLetter() + " Foundation");
        this.suit = suit;
    }

    private Foundation(Foundation original) {
        super(original);
        this.suit = original.suit;
    }

    /** Only the next card of this suit: the Ace first, then 2, 3 ... King. */
    @Override
    public boolean canAccept(Card card) {
        return card.suit() == suit && card.rank().getValue() == size() + 1;
    }

    /** A foundation takes single cards only, never a run. */
    @Override
    public boolean canAcceptRun(Card firstCard, int runSize) {
        return runSize == 1 && canAccept(firstCard);
    }

    @Override
    Foundation copy() {
        return new Foundation(this);
    }

    public Suit getSuit() {
        return suit;
    }

    /** True when the foundation holds all 13 cards of its suit. */
    public boolean isComplete() {
        return size() == Rank.values().length;
    }
}