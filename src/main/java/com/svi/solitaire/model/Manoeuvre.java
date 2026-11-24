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
        return canBePlacedOn(card, getTopCard());
    }

    /**
     * Can {@code card} be put on top of {@code cardBelow} in this stack?
     * Two rules must both be true:
     * <ol>
     *   <li>the colors are different (red on black, or black on red), and</li>
     *   <li>the card is exactly one rank lower (for example a 9 on a 10).</li>
     * </ol>
     * Examples: H-9 on S-10 is allowed. H-9 on D-10 is not (both red). H-9 on S-J is not (J is two ranks higher).
     */
    private static boolean canBePlacedOn(Card card, Card cardBelow) {
        return haveDifferentColors(card, cardBelow) && isOneRankLower(card, cardBelow);
    }

    /** True when one card is red and the other is black. Example: H-9 and S-10. */
    private static boolean haveDifferentColors(Card card, Card otherCard) {
        return card.isRed() != otherCard.isRed();
    }

    /** True when the card's rank is exactly one less than the other card's rank. Example: 9 and 10. */
    private static boolean isOneRankLower(Card card, Card otherCard) {
        return card.rank().getValue() + 1 == otherCard.rank().getValue();
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
