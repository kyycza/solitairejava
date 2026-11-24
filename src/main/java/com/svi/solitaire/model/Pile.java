package com.svi.solitaire.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

/**
 * What every pile on the table has in common: a name and its cards.
 * <p>
 * The cards are kept in an {@link ArrayDeque}: the FIRST element is the bottom card and the LAST element
 * is the top card. Putting a card on top ({@code addLast}) and taking the top card ({@code removeLast})
 * are instant and never shift the other cards, however many cards the pile holds.
 * <p>
 * Each kind of pile has its own rules, so the rule methods are abstract or overridable:
 * {@link #canAccept(Card)} says which card may be put on it and
 * {@link #countMovableCards()} says how many top cards may be picked up.
 * <p>
 * Only classes in this package can change a pile. Every other class can only look at it.
 */
public abstract class Pile {

    private final String name;
    private final Deque<Card> cards;

    protected Pile(String name) {
        this.name = name;
        this.cards = new ArrayDeque<>();
    }

    /** Copy constructor: a new pile with the same name and the same cards. */
    protected Pile(Pile original) {
        this.name = original.name;
        this.cards = new ArrayDeque<>(original.cards);
    }

    /** True if the card may be put on top of this pile. */
    public abstract boolean canAccept(Card card);

    /**
     * True if a run of cards starting with {@code firstCard} may be put on this pile.
     * By default a pile takes a run when it takes its first card; a foundation overrides this.
     */
    public boolean canAcceptRun(Card firstCard, int runSize) {
        return canAccept(firstCard);
    }

    /** How many cards from the top may be picked up together. By default only the top card. */
    public int countMovableCards() {
        return isEmpty() ? 0 : 1;
    }

    /** Creates an independent copy, so the player can think ahead without touching the real table. */
    abstract Pile copy();

    /** Called after cards were taken from this pile. A manoeuvre stack uses it to turn a hidden card. */
    protected void afterCardsTaken() {
        // most piles have nothing to do
    }

    // ----- reading the pile (allowed for everyone) -----

    public String getName() {
        return name;
    }

    public int size() {
        return cards.size();
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /** The top card, or null when the pile is empty. */
    public Card getTopCard() {
        return cards.peekLast();
    }

    /** The card at a position, where 0 is the bottom card. */
    public Card getCard(int position) {
        Iterator<Card> fromBottom = cards.iterator();
        for (int cardsSkipped = 0; cardsSkipped < position; cardsSkipped++) {
            fromBottom.next();
        }
        return fromBottom.next();
    }

    /**
     * The card that would be at the bottom of a run of {@code runSize} cards taken from the top.
     * It walks down from the top, so it costs only as much as the run is long.
     */
    public Card getRunStart(int runSize) {
        Iterator<Card> fromTop = cards.descendingIterator();
        for (int cardsSkipped = 1; cardsSkipped < runSize; cardsSkipped++) {
            fromTop.next();
        }
        return fromTop.next();
    }

    /** A read-only copy of the cards, bottom first. */
    public List<Card> getCards() {
        return List.copyOf(cards);
    }

    // ----- changing the pile (only for the model package) -----

    void putCard(Card card) {
        cards.addLast(card);
    }

    /** Puts a run of cards on top, keeping their order (the first card of the run goes in first). */
    void putCards(List<Card> run) {
        cards.addAll(run);
    }

    /** Removes the top {@code count} cards and returns them, bottom card of the run first. */
    List<Card> takeCards(int count) {
        Deque<Card> run = new ArrayDeque<>();
        for (int cardsTaken = 0; cardsTaken < count; cardsTaken++) {
            run.addFirst(cards.removeLast());
        }
        afterCardsTaken();
        return new ArrayList<>(run);
    }

    /** Removes the top card and returns it. */
    Card takeTopCard() {
        Card top = cards.removeLast();
        afterCardsTaken();
        return top;
    }

    /** Swaps the card at a position for another one (used only when imagining the face-down cards). */
    void replaceCard(int position, Card card) {
        List<Card> all = new ArrayList<>(cards);
        all.set(position, card);
        cards.clear();
        cards.addAll(all);
    }

    @Override
    public String toString() {
        return name + " " + cards;
    }
}