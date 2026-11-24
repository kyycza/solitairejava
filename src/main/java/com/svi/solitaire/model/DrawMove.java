package com.svi.solitaire.model;

import java.util.List;

/**
 * Draws from the stock one or more times in a row. When the stock is empty,
 * a draw turns the waste over to become the stock again.
 *
 * @param times how many draws in a row (1 for a normal draw)
 */
public record DrawMove(int times) implements Move {

    public DrawMove {
        if (times < 1) {
            throw new IllegalArgumentException("Draw at least once, but got " + times + ".");
        }
    }

    @Override
    public void applyTo(Tableau tableau) {
        for (int drawsDone = 0; drawsDone < times; drawsDone++) {
            tableau.draw();
        }
    }

    @Override
    public String describe(Tableau tableau) {
        if (times == 1) {
            return "Draw from the stock";
        }
        String description = "Draw " + times + " times from the stock";
        if (times >= tableau.getTalon().countDrawsToTurnOver()) {
            description += " (turning the waste over on the way)";
        }
        return description;
    }

    /** After a draw, the card to highlight is the new top of the waste. */
    @Override
    public List<Card> cardsToHighlight(Tableau tableauAfterMove) {
        Card topOfWaste = tableauAfterMove.getTalon().getWaste().getTopCard();
        return topOfWaste == null ? List.of() : List.of(topOfWaste);
    }
}