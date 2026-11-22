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
    /** A code has exactly 3 parts: the suit, the dash, the rank. */
    private static final int PARTS_IN_CODE = 3;

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
        // "true" also returns the dashes as parts, so "D--A", "-D-A" and "D-" are refused instead of slipping through
        StringTokenizer parts = new StringTokenizer(code, CODE_SEPARATOR, true);
        if (parts.countTokens() != PARTS_IN_CODE) {
            throw new InvalidCardException("\"" + code + "\" is not a card.");
        }
        String suitText = parts.nextToken();
        String dash = parts.nextToken();
        String rankText = parts.nextToken();
        if (!dash.equals(CODE_SEPARATOR)) {
            throw new InvalidCardException("\"" + code + "\" is not a card.");
        }
        return new Card(Suit.fromLetter(suitText), Rank.fromLabel(rankText));
    }

    /** The card code used in deck files, for example "D-A". */
    public String toCode() {
        return suit.getLetter() + CODE_SEPARATOR + rank.getLabel();
    }

    public boolean isRed() {
        return suit.isRed();
    }

    /**
     * Can this card be put on top of {@code cardBelow} in a manoeuvre stack?
     * Two rules must both be true:
     * <ol>
     *   <li>the colors are different (red on black, or black on red), and</li>
     *   <li>this card is exactly one rank lower (for example a 9 on a 10).</li>
     * </ol>
     * Examples: H-9 on S-10 is allowed. H-9 on D-10 is not (both red). H-9 on S-J is not (J is two ranks higher).
     *
     * @param cardBelow the card that is already on the stack
     * @return true if this card may be put on it
     */
    public boolean canBePlacedOn(Card cardBelow) {
        return hasDifferentColorThan(cardBelow) && isOneRankLowerThan(cardBelow);
    }

    /** True when one card is red and the other is black. Example: H-9 and S-10. */
    private boolean hasDifferentColorThan(Card otherCard) {
        return this.isRed() != otherCard.isRed();
    }

    /** True when this card's rank is exactly one less than the other card's rank. Example: 9 and 10. */
    private boolean isOneRankLowerThan(Card otherCard) {
        int myValue = this.rank.getValue();
        int otherValue = otherCard.rank.getValue();
        return myValue + 1 == otherValue;
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