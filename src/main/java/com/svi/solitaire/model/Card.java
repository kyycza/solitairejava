package com.svi.solitaire.model;

import com.svi.solitaire.enums.Rank;
import com.svi.solitaire.enums.Suit;

import java.util.Objects;

/**
 * One playing card. A record, so it can never change after it is created,
 * and two cards with the same suit and rank are equal (equals come for free).
 *
 * @param suit the suit of the card
 * @param rank the rank of the card
 */
public record Card(Suit suit, Rank rank) {

    /** Separates the suit from the rank in a card code such as "D-A". */
    private static final String CODE_SEPARATOR = "-";

    /** Refuses to create a card without a suit or a rank. */
    public Card {
        Objects.requireNonNull(suit, "suit cannot be null");
        Objects.requireNonNull(rank, "rank cannot be null");
    }

    /** The card code used in deck files, for example "D-A". */
    public String toCode() {
        return suit.getLetter() + CODE_SEPARATOR + rank.getLabel();
    }

    public boolean isRed() {
        return suit.isRed();
    }

    /**
     * The same number for the same card in every run of the program (0 to 51).
     * The default hashCode of an enum changes from run to run, which made the same deck play differently.
     */
    @Override
    public int hashCode() {
        return suit.ordinal() * Rank.values().length + rank.ordinal();
    }

    /** Shown the same way as in deck files, for example "D-A" or "S-10". */
    @Override
    public String toString() {
        return toCode();
    }
}