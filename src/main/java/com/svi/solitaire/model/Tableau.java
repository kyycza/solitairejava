package com.svi.solitaire.model;

import com.svi.solitaire.enums.DrawMode;
import com.svi.solitaire.enums.Rank;
import com.svi.solitaire.enums.Suit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * The whole solitaire table: the talon, the 4 foundations and the 7 manoeuvre stacks.
 * <p>
 * Every pile has a fixed number, its index in {@link #getPiles()}, so a {@link Move} can say
 * "from pile 3 to pile 9" and still work on a copy of the board.
 * From outside the model package, the only way to change a board is {@link #apply(Move)}.
 */
public final class Tableau {

    public static final int STACK_COUNT = 7;
    private static final int RANKS_PER_SUIT = Rank.values().length;

    private final Talon talon;
    private final List<Foundation> foundations = new ArrayList<>();
    private final List<Manoeuvre> stacks = new ArrayList<>();
    private final List<Pile> allPiles = new ArrayList<>();

    /** Creates an empty table. Use {@link #deal(Deck, DrawMode)} to get a table with cards. */
    private Tableau(DrawMode drawMode) {
        this.talon = new Talon(drawMode);
        for (Suit suit : Suit.values()) {
            foundations.add(new Foundation(suit));
        }
        for (int number = 1; number <= STACK_COUNT; number++) {
            stacks.add(new Manoeuvre(number));
        }
        numberThePiles();
    }

    /** Copy constructor: every pile is copied, so the copy can be changed freely. */
    private Tableau(Tableau original) {
        this.talon = original.talon.copy();
        for (Foundation foundation : original.foundations) {
            foundations.add(foundation.copy());
        }
        for (Manoeuvre stack : original.stacks) {
            stacks.add(stack.copy());
        }
        numberThePiles();
    }

    /** Gives every pile its fixed number: stock, waste, the 4 foundations, then the 7 stacks. */
    private void numberThePiles() {
        allPiles.add(talon.getStock());
        allPiles.add(talon.getWaste());
        allPiles.addAll(foundations);
        allPiles.addAll(stacks);
    }

    /**
     * Deals a new game. Cards are dealt row by row from left to right, so stack 1 gets 1 card and
     * stack 7 gets 7 cards. Only the top card of each stack is face up. The other 24 cards form the stock.
     *
     * @param deck     the deck to deal, top card first
     * @param drawMode draw one or draw three
     * @return the dealt table
     */
    public static Tableau deal(Deck deck, DrawMode drawMode) {
        Tableau tableau = new Tableau(drawMode);
        List<Card> cards = deck.getCards();
        int next = 0;
        for (int row = 0; row < STACK_COUNT; row++) {
            for (int column = row; column < STACK_COUNT; column++) {
                tableau.stacks.get(column).putFaceDown(cards.get(next++));
            }
        }
        for (Manoeuvre stack : tableau.stacks) {
            stack.turnTopCardFaceUp();
        }
        tableau.talon.load(cards.subList(next, cards.size()));
        return tableau;
    }

    /** An independent copy of this table. The player thinks ahead on copies. */
    public Tableau copy() {
        return new Tableau(this);
    }

    /**
     * Plays a move on this table.
     *
     * @param move the move to play
     * @return true if the move turned a face-down card face up
     */
    public boolean apply(Move move) {
        int hiddenBefore = countHiddenCards();
        move.applyTo(this);
        return countHiddenCards() < hiddenBefore;
    }

    /**
     * A copy in which the face-down cards are put back in a random order.
     * This is exactly what a real player can imagine: once the talon has been seen, the face-down cards
     * are known as a group (every card not visible anywhere else), but not their places.
     *
     * @param random the source of randomness
     * @return a copy with the face-down cards shuffled among the face-down places
     */
    public Tableau imagineHiddenCards(Random random) {
        Tableau imagined = copy();
        List<Card> hiddenCards = new ArrayList<>();
        for (Manoeuvre stack : imagined.stacks) {
            for (int position = 0; position < stack.getHiddenCount(); position++) {
                hiddenCards.add(stack.getCard(position));
            }
        }
        Collections.shuffle(hiddenCards, random);
        int next = 0;
        for (Manoeuvre stack : imagined.stacks) {
            for (int position = 0; position < stack.getHiddenCount(); position++) {
                stack.replaceCard(position, hiddenCards.get(next++));
            }
        }
        return imagined;
    }

    // ----- the two basic actions, used by the Move classes -----

    /** Moves the top {@code count} cards from one pile to another, after checking that it is allowed. */
    void moveCards(int fromIndex, int toIndex, int count) {
        Pile from = getPile(fromIndex);
        Pile to = getPile(toIndex);
        if (count < 1 || count > from.countMovableCards()
                || !to.canAcceptRun(from.getRunStart(count), count)) {
            throw new IllegalStateException("Illegal move: " + count + " card(s) from "
                    + from.getName() + " to " + to.getName());
        }
        to.putCards(from.takeCards(count));
    }

    void draw() {
        talon.draw();
    }

    // ----- questions about the table -----

    public boolean isWon() {
        return countFoundationCards() == Deck.SIZE;
    }

    public int countFoundationCards() {
        int total = 0;
        for (Foundation foundation : foundations) {
            total += foundation.size();
        }
        return total;
    }

    public int countHiddenCards() {
        int total = 0;
        for (Manoeuvre stack : stacks) {
            total += stack.getHiddenCount();
        }
        return total;
    }

    public int countEmptyStacks() {
        int total = 0;
        for (Manoeuvre stack : stacks) {
            if (stack.isEmpty()) {
                total++;
            }
        }
        return total;
    }

    /**
     * A short text that is the same for two tables in the same position.
     * The player uses it to avoid thinking about the same position twice.
     */
    public String toKey() {
        StringBuilder key = new StringBuilder(Deck.SIZE * 2);
        for (Pile pile : allPiles) {
            if (pile instanceof Manoeuvre stack) {
                key.append(stack.getHiddenCount());
            }
            for (Card card : pile.getCards()) {
                key.append((char) ('A' + card.suit().ordinal() * RANKS_PER_SUIT + card.rank().ordinal()));
            }
            key.append('|');
        }
        return key.toString();
    }

    // ----- read-only access to the piles -----

    public Talon getTalon() {
        return talon;
    }

    public DrawMode getDrawMode() {
        return talon.getDrawMode();
    }

    public List<Foundation> getFoundations() {
        return Collections.unmodifiableList(foundations);
    }

    public List<Manoeuvre> getStacks() {
        return Collections.unmodifiableList(stacks);
    }

    /** Every pile in its fixed order: stock, waste, 4 foundations, 7 stacks. */
    public List<Pile> getPiles() {
        return Collections.unmodifiableList(allPiles);
    }

    public Pile getPile(int index) {
        return allPiles.get(index);
    }
}
