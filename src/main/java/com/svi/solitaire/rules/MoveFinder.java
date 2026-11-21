package com.svi.solitaire.rules;

import com.svi.solitaire.model.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Lists every legal move on a board. It never changes the board.
 * <p>
 * It does not need to know the rules of each pile: it simply asks every pile
 * how many cards it can give ({@code countMovableCards}) and whether it can take a run ({@code canAcceptRun}).
 */
public final class MoveFinder {

    private MoveFinder() {
    }

    /**
     * Finds every legal move.
     *
     * @param tableau the board to look at
     * @return all legal card moves, plus the useful draws
     */
    public static List<Move> findLegalMoves(Tableau tableau) {
        List<Move> moves = new ArrayList<>();
        addCardMoves(tableau, moves);
        addDrawMoves(tableau, moves);
        return moves;
    }

    /** True if the card could be placed on any foundation or any manoeuvre stack right now. */
    public static boolean canBePlayedSomewhere(Tableau tableau, Card card) {
        for (Pile pile : tableau.getPiles()) {
            if (pile.canAccept(card)) {
                return true;
            }
        }
        return false;
    }

    private static void addCardMoves(Tableau tableau, List<Move> moves) {
        List<Pile> piles = tableau.getPiles();
        for (int from = 0; from < piles.size(); from++) {
            Pile source = piles.get(from);
            for (int runSize = 1; runSize <= source.countMovableCards(); runSize++) {
                Card firstCard = source.getRunStart(runSize);
                for (int to = 0; to < piles.size(); to++) {
                    if (to != from && piles.get(to).canAcceptRun(firstCard, runSize)) {
                        moves.add(new CardMove(from, to, runSize));
                    }
                }
            }
        }
    }

    /**
     * While the talon has not been seen completely, the only draw is a single draw.
     * Once every talon card is known, drawing several times to reach a useful card counts as ONE move:
     * a "Draw k times" move is added for every waste card that could then be played.
     */
    private static void addDrawMoves(Tableau tableau, List<Move> moves) {
        Talon talon = tableau.getTalon();
        if (!talon.canDraw()) {
            return;
        }
        if (!talon.isFullySeen()) {
            moves.add(new DrawMove(1));
            return;
        }
        List<Card> wasteTops = talon.previewWasteTops();
        Set<Card> cardsAlreadyOffered = new HashSet<>();
        for (int draws = 1; draws <= wasteTops.size(); draws++) {
            Card card = wasteTops.get(draws - 1);
            if (card != null && cardsAlreadyOffered.add(card) && canBePlayedSomewhere(tableau, card)) {
                moves.add(new DrawMove(draws));
            }
        }
    }
}
