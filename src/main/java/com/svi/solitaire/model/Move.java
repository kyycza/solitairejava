package com.svi.solitaire.model;

import java.util.List;

/**
 * One action of the player. Each kind of move knows how to play itself and how to describe itself
 * (polymorphism: the caller never needs to check which kind of move it has).
 * <p>
 * Sealed: these two records are the only kinds of move.
 */
public sealed interface Move permits CardMove, DrawMove {

    /** Plays this move on the board. Use {@link Tableau#apply(Move)} instead of calling this directly. */
    void applyTo(Tableau tableau);

    /**
     * A short sentence about this move, such as "Stack 2 -> Stack 5: 9♥ 8♠".
     *
     * @param tableau the board BEFORE the move is played
     */
    String describe(Tableau tableau);

    /**
     * The cards to highlight after this move: the cards that just moved, or the card just drawn.
     *
     * @param tableauAfterMove the board AFTER the move was played
     */
    List<Card> cardsToHighlight(Tableau tableauAfterMove);
}

