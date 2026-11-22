package com.svi.solitaire.view;

import com.svi.solitaire.enums.Suit;
import com.svi.solitaire.model.Card;

import java.util.List;

/** Turns cards into the small boxes shown on the table: "[ S-A]", "[H-10]", "[####]" or "[    ]". */
final class CardText {

    static final int BOX_WIDTH = 6;
    static final String BLANK = " ".repeat(BOX_WIDTH);
    private static final String HIDDEN = "[####]";
    private static final String EMPTY = "[    ]";

    private CardText() {
    }

    /** A face-up card: blue if it just moved, otherwise red or plain. */
    static String faceUp(Card card, List<Card> highlighted) {
        String box = String.format("[%4s]", card);
        if (highlighted.contains(card)) {
            return AnsiColor.BLUE.paint(box);
        }
        return card.isRed() ? AnsiColor.RED.paint(box) : box;
    }

    static String faceDown() {
        return AnsiColor.GRAY.paint(HIDDEN);
    }

    static String empty() {
        return EMPTY;
    }

    /** An empty foundation shows the letter of its suit, for example "[ H  ]". */
    static String emptyFoundation(Suit suit) {
        String box = "[ " + suit.getLetter() + "  ]";
        return suit.isRed() ? AnsiColor.RED.paint(box) : box;
    }
}