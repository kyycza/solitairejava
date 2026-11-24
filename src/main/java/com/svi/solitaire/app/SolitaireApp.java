package com.svi.solitaire.app;

import com.svi.solitaire.enums.MenuChoice;
import com.svi.solitaire.enums.PlayMode;
import com.svi.solitaire.io.InputHandler;
import com.svi.solitaire.view.MessagePrinter;

import java.util.List;
import com.svi.solitaire.exception.InputClosedException;

/** The whole program: ask for the options, play, then ask what to do next, until the user quits. */
public class SolitaireApp {

    public static final int EXIT_OK = 0;
    public static final int EXIT_ERROR = 1;

    /**
     * Runs the program.
     *
     * @return the exit code: {@link #EXIT_OK} or {@link #EXIT_ERROR}
     */
    public int run() {
        MessagePrinter.printBanner();
        try (InputHandler input = new InputHandler(System.in)) {
            SettingsMenu menu = new SettingsMenu(input);
            GameSettings settings = menu.askSettings();
            while (true) {
                play(settings);
                MenuChoice choice = input.askChoice("What next?", List.of(MenuChoice.values()));
                if (choice == MenuChoice.QUIT) {
                    MessagePrinter.printInfo("Thanks for playing. Goodbye!");
                    return EXIT_OK;
                }
                if (choice == MenuChoice.PLAY_AGAIN_NEW_OPTIONS) {
                    settings = menu.askSettings(settings.startingDeck());
                }
            }
        } catch (InputClosedException inputClosed) {
            MessagePrinter.printInfo("The input was closed. Goodbye!");
            return EXIT_OK;
        } catch (RuntimeException unexpected) {
            MessagePrinter.printError("Unexpected error, the program has to stop: " + unexpected);
            return EXIT_ERROR;
        }
    }

    /** Plays one round. A problem in one round is reported, and the user can still choose what to do next. */
    private void play(GameSettings settings) {
        try {
            if (settings.playMode() == PlayMode.WATCH_ONE_GAME) {
                GameRunner.watchOneGame(settings);
            } else {
                GameRunner.testWinRate(settings);
            }
        } catch (IllegalStateException illegalMove) {
            // thrown by the board (Tableau, Talon) when a move does not follow the rules
            MessagePrinter.printError("This round stopped because of an illegal move: " + illegalMove.getMessage());
        } catch (IllegalArgumentException wrongValue) {
            // thrown when a wrong value is given, for example a faro shuffle count outside 1-52
            MessagePrinter.printError("This round stopped because of a wrong value: " + wrongValue.getMessage());
        }
    }
}
