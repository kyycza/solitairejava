package com.svi.solitaire.service;

import com.svi.solitaire.enums.DrawMode;
import com.svi.solitaire.model.Tableau;
import com.svi.solitaire.model.Card;
import com.svi.solitaire.model.CardMove;
import com.svi.solitaire.model.DrawMove;
import com.svi.solitaire.model.Foundation;
import com.svi.solitaire.model.Manoeuvre;
import com.svi.solitaire.model.Move;
import com.svi.solitaire.model.Pile;
import com.svi.solitaire.rules.MoveFinder;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

/**
 * The computer player. It plays honestly: it never looks at a face-down card.
 * <p>
 * How it decides, in three ideas:
 * <ol>
 *   <li><b>Look through the talon once.</b> At the start it draws through the whole stock, so it knows
 *       every talon card (any human may do the same).</li>
 *   <li><b>Think ahead.</b> It tries hundreds of move sequences on COPIES of the board (breadth-first
 *       search), scores every board it reaches, and plays the plan that leads to the highest-scoring one.
 *       A branch stops when a face-down card is turned, because nobody knows what will appear.</li>
 *   <li><b>Imagine the face-down cards (Draw 3 only).</b> It knows WHICH cards are face down (every card
 *       it cannot see elsewhere), just not where. So it imagines several random arrangements, plans in
 *       each one, and plays the opening moves most of them agree on. Tests showed this raises the
 *       Draw 3 win rate by several points, but does not help Draw 1 and is much slower, so Draw 1 skips it.</li>
 * </ol>
 */
public class AutoPlayer implements Player {

    private static final int BOARDS_PER_DECISION = 300;
    private static final int IMAGINED_GAMES_PER_DECISION = 8;

    private final BoardEvaluator evaluator = new BoardEvaluator();
    private final Random random;

    /**
     * @param seed fixes the random imagining, so the same deck is always played the same way
     */
    public AutoPlayer(long seed) {
        this.random = new Random(seed);
    }

    @Override
    public List<Move> planNextMoves(Tableau tableau) {
        if (!tableau.getTalon().isFullySeen()) {
            return List.of(new DrawMove(tableau.getTalon().countDrawsToTurnOver()));
        }
        if (shouldImagineHiddenCards(tableau)) {
            return planByImagining(tableau);
        }
        return findHighestScoringPlan(tableau, false);
    }

    private boolean shouldImagineHiddenCards(Tableau tableau) {
        return tableau.getDrawMode() == DrawMode.DRAW_THREE && tableau.countHiddenCards() > 0;
    }

    // ----- idea 3: imagine the face-down cards and vote -----

    private List<Move> planByImagining(Tableau tableau) {
        Map<String, Integer> votes = new HashMap<>();
        Map<String, List<Move>> openingByTableKey = new HashMap<>();
        for (int imaginedGame = 0; imaginedGame < IMAGINED_GAMES_PER_DECISION; imaginedGame++) {
            Tableau imagined = tableau.imagineHiddenCards(random);
            List<Move> opening = findSafeOpeningMoves(tableau, findHighestScoringPlan(imagined, true));
            if (!opening.isEmpty()) {
                String tableKeyAfterOpening = tableKeyAfter(tableau, opening);
                votes.merge(tableKeyAfterOpening, 1, Integer::sum);
                openingByTableKey.putIfAbsent(tableKeyAfterOpening, opening);
            }
        }
        return openingByTableKey.getOrDefault(findMostVotedTableKey(votes), List.of());
    }

    /**
     * Gets the moves from an imagined plan that are safe to execute
     * on the real game board. It includes moves up to and including
     * the first move that reveals a face-down card.
     *
     * @param realTableau the actual game board
     * @param imaginedPlan the sequence of moves planned using an imagined board
     * @return the opening moves that can safely be executed on the real board
     */
    private List<Move> findSafeOpeningMoves(Tableau realTableau, List<Move> imaginedPlan) {
        Tableau trial = realTableau.copy();
        List<Move> opening = new ArrayList<>();

        for (Move move : imaginedPlan) {
            opening.add(move);
            if (trial.apply(move)) {
                break;
            }
        }
        return opening;
    }

    /** Two openings that reach the same board count as the same choice, even if their order differs. */
    private String tableKeyAfter(Tableau realTableau, List<Move> opening) {
        Tableau trial = realTableau.copy();
        for (Move move : opening) {
            trial.apply(move);
        }
        return trial.toKey();
    }

    private String findMostVotedTableKey(Map<String, Integer> votes) {
        String mostVotedKey = null;
        for (Map.Entry<String, Integer> entry : votes.entrySet()) {
            if (mostVotedKey == null || entry.getValue() > votes.get(mostVotedKey)) {
                mostVotedKey = entry.getKey();
            }
        }
        return mostVotedKey;
    }

    // ----- idea 2: think ahead -----

    /**
     * Finds the plan that reaches the highest-scoring table, first with careful moves only,
     * then (as a last chance) with every legal move.
     *
     * @param tableau      the table to start from
     * @param seesAllCards true only for an imagined board, whose face-down cards may be looked at
     * @return the moves to that table, or an empty list if no plan raises the score
     */
    private List<Move> findHighestScoringPlan(Tableau tableau, boolean seesAllCards) {
        List<Move> plan = search(tableau, true, seesAllCards);
        if (plan.isEmpty()) {
            plan = search(tableau, false, seesAllCards);
        }
        return plan;
    }

    /** One board reached during the search, with the step that led to it. */
    private record Step(Tableau tableau, Step previous, Move move) {
    }

    private List<Move> search(Tableau start, boolean carefulMovesOnly, boolean seesAllCards) {
        Step startStep = new Step(start, null, null);
        Step highestScoringStep = startStep;
        int highestScore = evaluator.score(start);

        Queue<Step> stepsToExplore = new ArrayDeque<>();
        Set<String> boardsSeen = new HashSet<>();
        stepsToExplore.add(startStep);
        boardsSeen.add(start.toKey());

        while (!stepsToExplore.isEmpty() && boardsSeen.size() < BOARDS_PER_DECISION) {
            Step currentStep = stepsToExplore.poll();
            for (Move move : chooseSensibleMoves(currentStep.tableau(), carefulMovesOnly)) {
                Tableau nextTableau = currentStep.tableau().copy();
                boolean cardTurned = nextTableau.apply(move);
                if (!boardsSeen.add(nextTableau.toKey())) {
                    continue;                                       // already thought about this board
                }
                Step nextStep = new Step(nextTableau, currentStep, move);
                if (nextTableau.isWon()) {
                    return movesLeadingTo(nextStep);
                }
                int nextScore = evaluator.score(nextTableau);
                if (nextScore > highestScore) {
                    highestScore = nextScore;
                    highestScoringStep = nextStep;
                }
                if (!cardTurned || seesAllCards) {
                    stepsToExplore.add(nextStep);
                }
            }
        }
        return movesLeadingTo(highestScoringStep);
    }

    /** Walks back from the last step to the start and returns the moves in playing order. */
    private List<Move> movesLeadingTo(Step lastStep) {
        LinkedList<Move> moves = new LinkedList<>();
        for (Step step = lastStep; step.previous() != null; step = step.previous()) {
            moves.addFirst(step.move());
        }
        return moves;
    }

    /** The legal moves, without the ones a good player would never try. */
    private List<Move> chooseSensibleMoves(Tableau tableau, boolean carefulMovesOnly) {
        List<Move> sensibleMoves = new ArrayList<>();
        // The piles whose King was already sent to an empty stack. All empty stacks are alike, so one
        // empty stack per King is enough, but different Kings must each get their chance.
        Set<Integer> pilesAlreadySentToEmptyStack = new HashSet<>();
        for (Move move : MoveFinder.findLegalMoves(tableau)) {
            if (!(move instanceof CardMove cardMove)) {
                sensibleMoves.add(move);
                continue;
            }
            Pile from = tableau.getPile(cardMove.fromPile());
            Pile to = tableau.getPile(cardMove.toPile());
            if (from instanceof Foundation) {
                continue;                                           // cards stay on the foundation
            }
            if (to instanceof Manoeuvre && to.isEmpty()) {
                if (isKingAlreadyAtBottom(from, cardMove.cardCount())
                        || !pilesAlreadySentToEmptyStack.add(cardMove.fromPile())) {
                    continue;                                       // pointless, or already tried
                }
            }
            if (carefulMovesOnly && to instanceof Foundation
                    && !isSafeForFoundation(tableau, from.getTopCard()) && !turnsHiddenCard(from)) {
                continue;
            }
            sensibleMoves.add(move);
        }
        return sensibleMoves;
    }

    /** Moving a King that already sits at the bottom of its stack to an empty stack changes nothing. */
    private boolean isKingAlreadyAtBottom(Pile from, int runSize) {
        return from instanceof Manoeuvre stack && stack.getHiddenCount() == 0 && runSize == stack.size();
    }

    /** True if taking the top card of this pile would turn a face-down card. */
    private boolean turnsHiddenCard(Pile from) {
        return from instanceof Manoeuvre stack
                && stack.getHiddenCount() > 0 && stack.countMovableCards() == 1;
    }

    /**
     * The activity's advice: move a card up only when nobody needs it any more, that is when both
     * foundations of the other color already hold the card one rank lower. Aces and twos are always safe.
     */
    private boolean isSafeForFoundation(Tableau tableau, Card card) {
        int rankBelow = card.rank().getValue() - 1;
        if (rankBelow <= 1) {
            return true;                                            // Aces and twos
        }
        for (Foundation foundation : tableau.getFoundations()) {
            if (foundation.getSuit().isRed() != card.isRed() && foundation.size() < rankBelow) {
                return false;
            }
        }
        return true;
    }
}