package com.svi.solitaire.enums;

import com.svi.solitaire.exception.InvalidCardException;

/** The 13 ranks from Ace (1) to King (13). Each rank owns its number and its label. */
public enum Rank {
    ACE(1, "A"), TWO(2, "2"), THREE(3, "3"), FOUR(4, "4"), FIVE(5, "5"),
    SIX(6, "6"), SEVEN(7, "7"), EIGHT(8, "8"), NINE(9, "9"), TEN(10, "10"),
    JACK(11, "J"), QUEEN(12, "Q"), KING(13, "K");

    private final int value;
    private final String label;

    Rank(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public int getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    /**
     * Finds the rank for a label such as "A", "10" or "k" (not case-sensitive).
     *
     * @param text the label of the rank
     * @return the matching rank
     * @throws InvalidCardException if the text is not a known rank label
     */
    public static Rank fromLabel(String text) {
        String cleaned = text.trim().toUpperCase();
        for (Rank rank : values()) {
            if (rank.label.equals(cleaned)) {
                return rank;
            }
        }
        throw new InvalidCardException("Unknown rank: \"" + text + "\"");
    }
}
