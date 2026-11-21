package com.svi.solitaire.enums;

import com.svi.solitaire.exception.InvalidCardException;

/**
 * The four suits. Each suit owns its own data: the letter used in deck files,
 * the symbol shown on the board, and its color.
 * <p>
 * The order of the constants is also the order of the standard deck:
 * diamonds, hearts, spades, clubs.
 */
public enum Suit {
    DIAMONDS('D', "♦", true),
    HEARTS('H', "♥", true),
    SPADES('S', "♠", false),
    CLUBS('C', "♣", false);

    private final char letter;
    private final String symbol;
    private final boolean red;

    Suit(char letter, String symbol, boolean red) {
        this.letter = letter;
        this.symbol = symbol;
        this.red = red;
    }

    public char getLetter() {
        return letter;
    }

    public String getSymbol() {
        return symbol;
    }

    public boolean isRed() {
        return red;
    }

    /**
     * Finds the suit for a letter such as "D" (not case-sensitive).
     *
     * @param text one letter: D, H, S or C
     * @return the matching suit
     * @throws InvalidCardException if the text is not a known suit letter
     */
    public static Suit fromLetter(String text) {
        String cleaned = text.trim().toUpperCase();
        for (Suit suit : values()) {
            if (cleaned.equals(String.valueOf(suit.letter))) {
                return suit;
            }
        }
        throw new InvalidCardException("Unknown suit: \"" + text + "\"");
    }
}
