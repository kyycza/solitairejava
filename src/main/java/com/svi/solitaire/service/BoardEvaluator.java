package com.svi.solitaire.service;

import com.svi.solitaire.model.Tableau;
import com.svi.solitaire.model.Manoeuvre;

/**
 * Gives a board a score: the higher, the closer the board is to a win.
 * It only uses what a real player can see (how many cards are face down, never which ones).
 */
public class BoardEvaluator {

    private static final int POINTS_PER_FOUNDATION_CARD = 12;
    private static final int PENALTY_PER_HIDDEN_CARD = 10;
    private static final int PENALTY_PER_TALL_HIDDEN_STACK_UNIT = 20;
    private static final int POINTS_PER_EMPTY_STACK = 5;

    /**
     * Scores a board.
     *
     * @param tableau the board to score
     * @return a higher number for a better board
     */
    public int score(Tableau tableau) {
        return tableau.countFoundationCards() * POINTS_PER_FOUNDATION_CARD
                - tableau.countHiddenCards() * PENALTY_PER_HIDDEN_CARD
                - deepHiddenStacksPenalty(tableau)
                + tableau.countEmptyStacks() * POINTS_PER_EMPTY_STACK;
    }

    /**
     * Stacks with many face-down cards are the hardest to dig out, so they cost extra:
     * a stack with h face-down cards counts h*(h-1)/2 units (0, 0, 1, 3, 6, 10, 15 ...).
     */
    private int deepHiddenStacksPenalty(Tableau tableau) {
        int units = 0;
        for (Manoeuvre stack : tableau.getStacks()) {
            int hidden = stack.getHiddenCount();
            units += hidden * (hidden - 1) / 2;
        }
        return units * PENALTY_PER_TALL_HIDDEN_STACK_UNIT;
    }
}
