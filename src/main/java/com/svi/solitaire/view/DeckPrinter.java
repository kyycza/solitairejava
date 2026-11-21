package com.svi.solitaire.view;

import com.svi.solitaire.model.Card;
import com.svi.solitaire.model.Deck;

import java.util.List;

/** Prints a whole deck as card codes, 13 per row. */
public final class DeckPrinter {

    private static final int CARDS_PER_ROW = 13;

    private DeckPrinter() {
    }

    /**
     * Prints the deck under a title.
     *
     * @param deck  the deck to print
     * @param title the title, for example "DECK BEFORE SHUFFLE"
     */
    public static void print(Deck deck, String title) {
        System.out.println();
        System.out.println(AnsiColor.YELLOW.paint(title));
        List<Card> cards = deck.getCards();
        StringBuilder row = new StringBuilder();
        for (int i = 0; i < cards.size(); i++) {
            row.append(String.format("%-5s", cards.get(i).toCode()));
            if ((i + 1) % CARDS_PER_ROW == 0) {
                System.out.println(row);
                row.setLength(0);
            }
        }
    }
}
