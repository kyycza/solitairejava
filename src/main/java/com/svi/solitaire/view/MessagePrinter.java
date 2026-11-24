package com.svi.solitaire.view;

import com.svi.solitaire.model.Tableau;
import com.svi.solitaire.model.Deck;

/** All the fixed messages of the program: the banner, the result of a game and the win-rate report. */
public final class MessagePrinter {

    private MessagePrinter() {
    }

    public static void printBanner() {
        System.out.println(AnsiColor.CYAN.paint("========================================="));
        System.out.println(AnsiColor.CYAN.paint("           KLONDIKE  SOLITAIRE"));
        System.out.println(AnsiColor.CYAN.paint("========================================="));
        System.out.println();
    }

    /**
     * Prints how one game ended.
     *
     * @param tableau     the final table
     * @param moveCount how many moves were played
     */
    public static void printGameResult(Tableau tableau, int moveCount) {
        System.out.println();
        if (tableau.isWon()) {
            System.out.println(AnsiColor.GREEN.paint("*** GAME WON! All 52 cards are on the foundations after "
                    + moveCount + " moves. ***"));
        } else {
            System.out.println(AnsiColor.RED.paint("Game lost: no useful move left. "
                    + tableau.countFoundationCards() + " of " + Deck.SIZE + " cards reached the foundations."));
        }
    }

    /**
     * Prints the result of a win-rate test.
     *
     * @param games   how many games were played
     * @param wins    how many were won
     * @param errors  how many stopped because of an unexpected error
     * @param seconds how long the test took
     */
    public static void printWinRate(int games, int wins, int errors, double seconds) {
        double percent = 100.0 * wins / games;
        System.out.println();
        System.out.println(AnsiColor.YELLOW.paint("WIN-RATE TEST"));
        System.out.printf("Games played : %d%n", games);
        System.out.printf("Games won    : %d%n", wins);
        System.out.printf("Win rate     : %.1f%%%n", percent);
        if (errors > 0) {
            System.out.printf("Games with an error: %d%n", errors);
        }
        System.out.printf("Time         : %.1f seconds%n", seconds);
    }

    public static void printError(String message) {
        System.out.println(AnsiColor.RED.paint(message));
    }

    public static void printInfo(String message) {
        System.out.println(AnsiColor.YELLOW.paint(message));
    }
}
