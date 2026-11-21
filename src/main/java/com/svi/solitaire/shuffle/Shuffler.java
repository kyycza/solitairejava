package com.svi.solitaire.shuffle;

import com.svi.solitaire.enums.ShuffleType;
import com.svi.solitaire.model.Deck;

/** Anything that can shuffle a deck. Shuffling never changes the old deck: it returns a new one. */
public interface Shuffler {

    /**
     * Shuffles a deck.
     *
     * @param deck the deck to shuffle
     * @return a new, shuffled deck
     */
    Deck shuffle(Deck deck);

    /** A short description such as "Random" or "Out-faro x 3". */
    String describe();

    /**
     * Creates the right shuffler for the player's choice.
     *
     * @param type  the kind of shuffle
     * @param times how many times to shuffle (used by the faro shuffles only)
     * @return a ready-to-use shuffler
     */
    static Shuffler create(ShuffleType type, int times) {
        return switch (type) {
            case RANDOM -> new RandomShuffler();
            case OUT_FARO -> new OutFaroShuffler(times);
            case IN_FARO -> new InFaroShuffler(times);
        };
    }
}
