package com.svi.solitaire.app;

import com.svi.solitaire.enums.DrawMode;
import com.svi.solitaire.enums.PlayMode;
import com.svi.solitaire.model.Deck;
import com.svi.solitaire.shuffle.Shuffler;

/**
 * Everything the user chose before playing. A record, so the settings cannot change by accident.
 *
 * @param drawMode     draw one or draw three
 * @param shuffler     how the deck is shuffled
 * @param startingDeck the deck before shuffling (from a file or the standard deck)
 * @param playMode     watch one game or test the win rate
 * @param gameCount    how many games the win-rate test plays (1 when watching)
 */
public record GameSettings(DrawMode drawMode, Shuffler shuffler, Deck startingDeck,
                           PlayMode playMode, int gameCount) {
}