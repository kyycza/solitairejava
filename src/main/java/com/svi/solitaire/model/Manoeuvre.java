package com.svi.solitaire.model;

import com.svi.solitaire.enums.Rank;

/**
 * One of the 7 manoeuvre stacks. The bottom cards are face down; on top of them is a run of
 * face-up cards going down in rank with alternating colors.
 */
public final class Manoeuvre extends Pile {

    /** The first {@code hiddenCount} cards (from the bottom) are face down. */
    private int hiddenCount;

    Manoeuvre(int number) {
        super("Stack " + number);
    }

    private Manoeuvre(Manoeuvre original) {
        super(original);
        this.hiddenCount = original.hiddenCount;
    }

    /** An empty stack takes only a King; otherwise one rank lower in the opposite color. */
    @Override
    public boolean canAccept(Card card) {
        if (isEmpty()) {
            return card.rank() == Rank.KING;
        }
        return card.canBePlacedOn(getTopCard());
    }

    /** All the face-up cards can be moved together as one run. */
    @Override
    public int countMovableCards() {
        return size() - hiddenCount;
    }

    /** When the top card is taken away, the face-down card under it is turned face up. */
    @Override
    protected void afterCardsTaken() {
        if (!isEmpty() && hiddenCount == size()) {
            hiddenCount--;
        }
    }

    @Override
    Manoeuvre copy() {
        return new Manoeuvre(this);
    }

    public int getHiddenCount() {
        return hiddenCount;
    }

    /** True if the card at this position (0 = bottom) is face down. */
    public boolean isHidden(int position) {
        return position < hiddenCount;
    }

    /** Used only while dealing: the card goes in face down. */
    void putFaceDown(Card card) {
        putCard(card);
        hiddenCount++;
    }

    /** Used only at the end of dealing: the top card is turned face up. */
    void turnTopCardFaceUp() {
        if (hiddenCount == size() && hiddenCount > 0) {
            hiddenCount--;
        }
    }
}
