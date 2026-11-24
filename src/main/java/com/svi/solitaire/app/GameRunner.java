package com.svi.solitaire.app;

import com.svi.solitaire.model.*;
import com.svi.solitaire.service.Player;
import com.svi.solitaire.view.BoardPrinter;
import com.svi.solitaire.view.DeckPrinter;
import com.svi.solitaire.view.MessagePrinter;
import com.svi.solitaire.service.AutoPlayer;
import com.svi.solitaire.shuffle.Shuffler;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/** Runs games: one game shown move by move, or many silent games to measure the win rate. */
public final class GameRunner {

    /** A safety limit, so a game can never run forever. */
    private static final int MAX_DECISIONS_PER_GAME = 10_000;
    private static final double MILLIS_PER_SECOND = 1000.0;

    /** Something that wants to know about every move, for example the board printer. */
    @FunctionalInterface
    private interface MoveListener {
        void moveWasPlayed(Tableau tableau, int moveNumber, String description, List<Card> highlighted);
    }
    /** Used by the win-rate test: nobody listens, nothing is printed. */
    private static final MoveListener NOBODY = (game, moveNumber, description, highlighted) -> { };

    private GameRunner() {
    }

    /**
     * Plays one game and shows the deck before and after the shuffle, then the table after every move.
     *
     * @param settings the user's choices
     */
    public static void watchOneGame(GameSettings settings) {
        Deck startingDeck = settings.startingDeck();
        DeckPrinter.print(startingDeck, "DECK BEFORE SHUFFLE");
        Deck shuffledDeck = settings.shuffler().shuffle(startingDeck);
        DeckPrinter.print(shuffledDeck, "DECK AFTER SHUFFLE (" + settings.shuffler().describe() + ")");

        Tableau tableau = Tableau.deal(shuffledDeck, settings.drawMode());
        BoardPrinter.print(tableau, 0, "Game start", List.of());
        Player player = new AutoPlayer(shuffledDeck.hashCode());
        int moveCount = playToEnd(tableau, player, BoardPrinter::print);
        MessagePrinter.printGameResult(tableau, moveCount);
    }

    /**
     * Plays many games at the same time without printing them, then prints the win rate.
     * <p>
     * With a random shuffle every game gets a new random deck. A perfect (faro) shuffle always makes the same
     * deck, so instead every game uses a DIFFERENT number of shuffles (1, 2, 3 ... up to the cycle length).
     * That plays every deck the shuffle can make exactly once.
     * <p>
     * A game that fails with an unexpected error is counted separately and does not stop the others.
     *
     * @param settings the user's choices; for a faro shuffle its game count is the cycle length
     */
    public static void testWinRate(GameSettings settings) {
        AtomicInteger wins = new AtomicInteger();
        AtomicInteger errors = new AtomicInteger();
        long start = System.currentTimeMillis();
        boolean differentDeckPerGame = settings.shuffler().getCycleLength() > 0;

        IntStream.range(0, settings.gameCount()).parallel().forEach(gameNumber -> {
            try {
                // faro: game 0 = 1 shuffle, game 1 = 2 shuffles ... so every game is a different deck
                Shuffler shuffler = differentDeckPerGame
                        ? settings.shuffler().withTimes(gameNumber + 1)
                        : settings.shuffler();
                Deck deck = shuffler.shuffle(settings.startingDeck());
                Tableau tableau = Tableau.deal(deck, settings.drawMode());
                playToEnd(tableau, new AutoPlayer(gameNumber), NOBODY);
                if (tableau.isWon()) {
                    wins.incrementAndGet();
                }
            } catch (RuntimeException unexpected) {
                errors.incrementAndGet();
            }
        });

        double seconds = (System.currentTimeMillis() - start) / MILLIS_PER_SECOND;
        MessagePrinter.printWinRate(settings.gameCount(), wins.get(), errors.get(), seconds);
    }

    /**
     * Lets the player play until the game is won or the player gives up.
     *
     * @param tableau     the table to play on (it is changed)
     * @param player   the player who decides the moves
     * @param listener who should hear about every move
     * @return the number of moves played
     */
    private static int playToEnd(Tableau tableau, Player player, MoveListener listener) {
        int moveCount = 0;
        for (int decision = 0; decision < MAX_DECISIONS_PER_GAME && !tableau.isWon(); decision++) {
            List<Move> plan = player.planNextMoves(tableau);
            if (plan.isEmpty()) {
                break;                                              // the player sees no useful move
            }
            for (Move move : plan) {
                String description = move.describe(tableau);
                tableau.apply(move);
                moveCount++;
                listener.moveWasPlayed(tableau, moveCount, description, move.cardsToHighlight(tableau));
            }
        }
        return moveCount;
    }
}