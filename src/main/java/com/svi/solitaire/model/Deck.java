package com.svi.solitaire.model;

import com.svi.solitaire.enums.Rank;
import com.svi.solitaire.enums.Suit;
import com.svi.solitaire.exception.InvalidDeckException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A deck of exactly 52 different cards in a fixed order (the first card is the top of the deck).
 * A deck never changes: shuffling a deck creates a new deck.
 */
public final class Deck {

    public static final int SIZE = 52;

    private final List<Card> cards;

    /**
     * Creates a deck and checks that it is complete.
     *
     * @param cards the cards from top to bottom
     * @throws InvalidDeckException if there are not exactly 52 cards or a card appears twice
     */
    public Deck(List<Card> cards) {
        if (cards.size() != SIZE) {
            throw new InvalidDeckException("A deck needs " + SIZE + " cards, but this one has " + cards.size() + ".");
        }
        Set<Card> alreadySeen = new HashSet<>();
        for (Card card : cards) {
            if (!alreadySeen.add(card)) {
                throw new InvalidDeckException("The card " + card.toCode() + " appears more than once.");
            }
        }
        this.cards = List.copyOf(cards);
    }

    /**
     * The required unshuffled order: D-A, D-K, D-Q ... D-2, then hearts, spades and clubs the same way.
     *
     * @return the standard deck
     */
    public static Deck standard() {
        Rank[] ranks = Rank.values();
        List<Card> cards = new ArrayList<>();
        for (Suit suit : Suit.values()) {
            cards.add(new Card(suit, Rank.ACE));
            for (int i = ranks.length - 1; i > 0; i--) {             // King down to Two
                cards.add(new Card(suit, ranks[i]));
            }
        }
        return new Deck(cards);
    }

    /** The cards from top to bottom. The list cannot be changed by the caller. */
    public List<Card> getCards() {
        return cards;
    }

    /** Two decks are equal when they hold the same cards in the same order. */
    @Override
    public boolean equals(Object other) {
        return other instanceof Deck deck && cards.equals(deck.cards);
    }

    @Override
    public int hashCode() {
        return cards.hashCode();
    }
}
