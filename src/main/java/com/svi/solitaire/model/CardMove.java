package com.svi.solitaire.model;

import java.util.List;
import java.util.StringJoiner;

/**
 * Moves one card or a run of cards from one pile to another.
 *
 * @param fromPile  index of the pile the cards come from (see {@link Tableau#getPiles()})
 * @param toPile    index of the pile the cards go to
 * @param cardCount how many cards move together (1 for a single card)
 */
public record CardMove(int fromPile, int toPile, int cardCount) implements Move {

    public CardMove {
        if (cardCount < 1) {
            throw new IllegalArgumentException("A move needs at least 1 card, but got " + cardCount + ".");
        }
    }

    @Override
    public void applyTo(Tableau tableau) {
        tableau.moveCards(fromPile, toPile, cardCount);
    }

    @Override
    public String describe(Tableau tableau) {
        Pile from = tableau.getPile(fromPile);
        StringJoiner cards = new StringJoiner(" ");
        for (int position = from.size() - cardCount; position < from.size(); position++) {
            cards.add(from.getCard(position).toString());
        }
        return from.getName() + " -> " + tableau.getPile(toPile).getName() + ": " + cards;
    }

    /** After the move, the moved cards are the top cardCount cards of the destination pile. */
    @Override
    public List<Card> cardsToHighlight(Tableau tableauAfterMove) {
        Pile to = tableauAfterMove.getPile(toPile);
        return to.getCards().subList(to.size() - cardCount, to.size());
    }
}