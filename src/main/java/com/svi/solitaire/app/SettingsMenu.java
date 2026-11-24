package com.svi.solitaire.app;

import com.svi.solitaire.enums.DeckFile;
import com.svi.solitaire.enums.DrawMode;
import com.svi.solitaire.enums.PlayMode;
import com.svi.solitaire.enums.ShuffleType;
import com.svi.solitaire.exception.InvalidCardException;
import com.svi.solitaire.exception.InvalidDeckException;
import com.svi.solitaire.io.InputHandler;
import com.svi.solitaire.io.FileLoader;
import com.svi.solitaire.model.Deck;
import com.svi.solitaire.shuffle.FaroShuffler;
import com.svi.solitaire.shuffle.Shuffler;
import com.svi.solitaire.view.MessagePrinter;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.util.List;

/** Asks the user for every option of a game and returns them as {@link GameSettings}. */
public class SettingsMenu {

    private static final int MIN_TEST_GAMES = 1;
    private static final int MAX_TEST_GAMES = 100;

    private final InputHandler input;

    public SettingsMenu(InputHandler input) {
        this.input = input;
    }

    /**
     * The first time: asks for the deck file first, so a wrong file name is fixed before the other questions,
     * then asks all the other options.
     */
    public GameSettings askSettings() {
        return askOptions(askStartingDeck());
    }

    /**
     * Playing again with new options: lets the user keep the deck that is already loaded,
     * so the file does not have to be typed and read again.
     *
     * @param currentDeck the deck of the last game, before shuffling
     */
    public GameSettings askSettings(Deck currentDeck) {
        DeckFile deckChoice = input.askChoice("Which deck?", List.of(DeckFile.values()));
        Deck startingDeck = deckChoice == DeckFile.SAME_DECK ? currentDeck : askStartingDeck();
        return askOptions(startingDeck);
    }

    /** Asks every option except the deck, one after the other. */
    private GameSettings askOptions(Deck startingDeck) {
        DrawMode drawMode = input.askChoice("Draw mode?", List.of(DrawMode.values()));
        PlayMode playMode = input.askChoice("What do you want to do?", List.of(PlayMode.values()));
        Shuffler shuffler = askShuffler(playMode);
        int gameCount = playMode == PlayMode.TEST_WIN_RATE ? askGameCount(shuffler) : 1;
        return new GameSettings(drawMode, shuffler, startingDeck, playMode, gameCount);
    }

    /**
     * Asks for the kind of shuffle. The number of faro shuffles is only asked when watching one game:
     * the win-rate test plays every number of shuffles by itself.
     *
     * @param playMode what the user chose to do
     * @return the chosen shuffler
     */
    private Shuffler askShuffler(PlayMode playMode) {
        ShuffleType type = input.askChoice("Shuffle type?", List.of(ShuffleType.values()));
        boolean askTimes = type.isFaro() && playMode == PlayMode.WATCH_ONE_GAME;
        int times = askTimes
                ? input.askNumber("How many faro shuffles", FaroShuffler.MIN_TIMES, FaroShuffler.MAX_TIMES)
                : FaroShuffler.MIN_TIMES;
        return Shuffler.create(type, times);
    }
    /**
     * Asks for a deck file until it can be read, or uses the standard deck when the user presses Enter.
     * Each kind of problem has its own message, because each one has a different fix.
     */
    private Deck askStartingDeck() {
        while (true) {
            String fileName = input.askText("Deck file name (press Enter for the standard deck)");
            if (fileName.isEmpty()) {
                return Deck.standard();
            }
            try {
                return FileLoader.loadDeck(fileName);
            } catch (NoSuchFileException missing) {
                MessagePrinter.printError("No file called \"" + fileName + "\" was found.");
            } catch (InvalidPathException badName) {
                MessagePrinter.printError("\"" + fileName + "\" is not a valid file name.");
            } catch (InvalidCardException badCard) {
                MessagePrinter.printError("The file has a wrong card: " + badCard.getMessage());
            } catch (InvalidDeckException badDeck) {
                MessagePrinter.printError("The file is not a complete deck: " + badDeck.getMessage());
            } catch (IOException unreadable) {
                MessagePrinter.printError("The file could not be read: " + unreadable.getMessage());
            }
        }
    }

    /**
     * Asks how many games to test. A faro shuffle is not asked: it can only make a fixed number of
     * different decks, so every one of them is played once.
     *
     * @param shuffler the chosen shuffler
     * @return the number of games to play
     */
    private int askGameCount(Shuffler shuffler) {
        int differentDecks = shuffler.getCycleLength();
        if (differentDecks > 0) {
            MessagePrinter.printInfo(differentDecks == 1
                    ? "Without a shuffle, there is only one deck, so it is played once."
                    : "This shuffle can only make " + differentDecks + " different decks, so each one is played once.");
            return differentDecks;
        }
        return input.askNumber("How many games", MIN_TEST_GAMES, MAX_TEST_GAMES);
    }
}