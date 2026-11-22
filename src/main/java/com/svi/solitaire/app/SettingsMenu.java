package com.svi.solitaire.app;

import com.svi.solitaire.enums.DrawMode;
import com.svi.solitaire.enums.PlayMode;
import com.svi.solitaire.enums.ShuffleType;
import com.svi.solitaire.exception.InvalidCardException;
import com.svi.solitaire.exception.InvalidDeckException;
import com.svi.solitaire.io.InputHandler;
import com.svi.solitaire.io.FileLoader;
import com.svi.solitaire.model.Deck;
import com.svi.solitaire.shuffle.FaroShuffler;
import com.svi.solitaire.shuffle.RandomShuffler;
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

    /** Asks all the questions, one after the other. */
    public GameSettings askSettings() {
        DrawMode drawMode = input.askChoice("Draw mode?", List.of(DrawMode.values()));
        Shuffler shuffler = askShuffler();
        Deck startingDeck = askStartingDeck();
        PlayMode playMode = input.askChoice("What do you want to do?", List.of(PlayMode.values()));
        int gameCount = playMode == PlayMode.TEST_WIN_RATE ? askGameCount(drawMode, shuffler) : 1;
        return new GameSettings(drawMode, shuffler, startingDeck, playMode, gameCount);
    }

    private Shuffler askShuffler() {
        ShuffleType type = input.askChoice("Shuffle type?", List.of(ShuffleType.values()));
        int times = type.isFaro()
                ? input.askNumber("How many faro shuffles", FaroShuffler.MIN_TIMES, FaroShuffler.MAX_TIMES)
                : 1;
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
                MessagePrinter.printError("  No file called \"" + fileName + "\" was found.");
            } catch (InvalidPathException badName) {
                MessagePrinter.printError("  \"" + fileName + "\" is not a valid file name.");
            } catch (InvalidCardException badCard) {
                MessagePrinter.printError("  The file has a wrong card: " + badCard.getMessage());
            } catch (InvalidDeckException badDeck) {
                MessagePrinter.printError("  The file is not a complete deck: " + badDeck.getMessage());
            } catch (IOException unreadable) {
                MessagePrinter.printError("  The file could not be read: " + unreadable.getMessage());
            }
        }
    }

    private int askGameCount(DrawMode drawMode, Shuffler shuffler) {
        if (drawMode == DrawMode.DRAW_THREE) {
            MessagePrinter.printInfo("Note: Draw 3 games take longer than Draw 1, because the player thinks harder."
                    + " 100 games can take about a minute.");
        }
        if (!(shuffler instanceof RandomShuffler)) {
            MessagePrinter.printInfo("Note: only the random shuffle changes the deck, so with this shuffle every game will be the same.");
        }
        return input.askNumber("How many games", MIN_TEST_GAMES, MAX_TEST_GAMES);
    }
}