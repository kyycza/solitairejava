package com.svi.solitaire.enums;

import com.svi.solitaire.exception.InvalidCardException;

/**
 * The four suits. Each suit owns its own data: its letter and its color.
 * The letter is used both in deck files and on the board. Plain letters are used instead of
 * suit symbols, because many consoles (for example Windows cmd running the jar) show the symbols as "?".
 * <p>
 * The order of the constants is also the order of the standard deck:
 * diamonds, hearts, spades, clubs.
 */
public enum Suit {
    DIAMONDS('D', true),
    HEARTS('H', true),
    SPADES('S', false),
    CLUBS('C', false);

    private final char letter;
    private final boolean red;

    Suit(char letter, boolean red) {
        this.letter = letter;
        this.red = red;
    }

    public char getLetter() {
        return letter;
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
