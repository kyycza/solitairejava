package com.svi.solitaire.model;

import com.svi.solitaire.enums.Rank;
import com.svi.solitaire.enums.Suit;
import com.svi.solitaire.exception.InvalidCardException;

import java.util.Objects;
import java.util.StringTokenizer;

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
    private static final int PARTS_IN_CODE = 2;

    /** Refuses to create a card without a suit or a rank. */
    public Card {
        Objects.requireNonNull(suit, "suit cannot be null");
        Objects.requireNonNull(rank, "rank cannot be null");
    }

    /**
     * Reads a card code such as "D-A" (Ace of Diamonds) or "S-10" (Ten of Spades).
     *
     * @param code the card code: suit letter, a dash, then the rank label
     * @return the card
     * @throws InvalidCardException if the code is not a valid card
     */
    public static Card fromCode(String code) {
        StringTokenizer parts = new StringTokenizer(code, CODE_SEPARATOR);
        if (parts.countTokens() != PARTS_IN_CODE) {
            throw new InvalidCardException("\"" + code + "\" is not a card. Expected a code like D-A or S-10.");
        }
        Suit suit = Suit.fromLetter(parts.nextToken());
        Rank rank = Rank.fromLabel(parts.nextToken());
        return new Card(suit, rank);
    }

    /** The card code used in deck files, for example "D-A". */
    public String toCode() {
        return suit.getLetter() + CODE_SEPARATOR + rank.getLabel();
    }

    public boolean isRed() {
        return suit.isRed();
    }

    public boolean isOppositeColorOf(Card other) {
        return isRed() != other.isRed();
    }

    /** True when this card is exactly one rank lower than the other card (for example 9 below 10). */
    public boolean isOneRankBelow(Card other) {
        return rank.getValue() + 1 == other.rank.getValue();
    }

    /** True when this card can be placed on the other card in a manoeuvre stack. */
    public boolean canBePlacedOn(Card other) {
        return isOneRankBelow(other) && isOppositeColorOf(other);
    }

    /**
     * The same number for the same card in every run of the program (0 to 51).
     * The default hashCode of an enum changes from run to run, which made the same deck play differently.
     */
    @Override
    public int hashCode() {
        return suit.ordinal() * Rank.values().length + rank.ordinal();
    }
    @Override
    public String toString() {
        return rank.getLabel() + suit.getSymbol();
    }
}
