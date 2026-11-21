package com.svi.solitaire.shuffle;

import com.svi.solitaire.model.Card;
import com.svi.solitaire.model.Deck;

import java.util.ArrayList;
import java.util.List;

/**
 * A perfect (faro) shuffle: the deck is cut into two equal halves of 26 cards,
 * which are then merged by taking one card from each half in turn.
 * <p>
 * The two kinds of faro shuffle differ in one thing only: which half gives the first card.
 * This class writes the algorithm once; each subclass only answers {@link #bottomHalfGoesFirst()}.
 */
public abstract class FaroShuffler implements Shuffler {

    public static final int MIN_TIMES = 1;
    public static final int MAX_TIMES = 52;

    private final int times;

    protected FaroShuffler(int times) {
        if (times < MIN_TIMES || times > MAX_TIMES) {
            throw new IllegalArgumentException("Shuffle " + MIN_TIMES + " to " + MAX_TIMES
                    + " times, but got " + times + ".");
        }
        this.times = times;
    }

    /** True if the bottom half gives the first card (in-faro), false if the top half does (out-faro). */
    protected abstract boolean bottomHalfGoesFirst();

    /** The name of this kind of faro shuffle, such as "Out-faro". */
    protected abstract String getName();

    @Override
    public Deck shuffle(Deck deck) {
        List<Card> cards = deck.getCards();
        for (int i = 0; i < times; i++) {
            cards = mergeHalves(cards);
        }
        return new Deck(cards);
    }

    /** One perfect shuffle: cut in two halves, then take one card from each half in turn. */
    private List<Card> mergeHalves(List<Card> cards) {
        int half = cards.size() / 2;
        List<Card> topHalf = cards.subList(0, half);
        List<Card> bottomHalf = cards.subList(half, cards.size());
        List<Card> firstHalf = bottomHalfGoesFirst() ? bottomHalf : topHalf;
        List<Card> secondHalf = bottomHalfGoesFirst() ? topHalf : bottomHalf;

        List<Card> merged = new ArrayList<>(cards.size());
        for (int i = 0; i < half; i++) {
            merged.add(firstHalf.get(i));
            merged.add(secondHalf.get(i));
        }
        return merged;
    }

    @Override
    public String describe() {
        return getName() + " x " + times;
    }
}
