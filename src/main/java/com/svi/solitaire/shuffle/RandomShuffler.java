package com.svi.solitaire.shuffle;

import com.svi.solitaire.model.Card;
import com.svi.solitaire.model.Deck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/** Puts the cards in a random order; every order is equally likely. */
public class RandomShuffler implements Shuffler {

    @Override
    public Deck shuffle(Deck deck) {
        List<Card> cards = new ArrayList<>(deck.getCards());
        // ThreadLocalRandom is safe when many games are shuffled at the same time
        Collections.shuffle(cards, ThreadLocalRandom.current());
        return new Deck(cards);
    }

    @Override
    public String describe() {
        return "Random";
    }
}
