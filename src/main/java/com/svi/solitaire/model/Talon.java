package com.svi.solitaire.model;

import com.svi.solitaire.enums.DrawMode;

import java.util.ArrayList;
import java.util.List;

/**
 * The talon: the stock (face down) and the waste (face up) together.
 * It is not a pile itself: it is made of two piles and knows how to draw.
 */
public final class Talon {

    private final DrawMode drawMode;
    private final Stock stock;
    private final Waste waste;

    /** Becomes true the first time the waste is turned over: every talon card has then been seen. */
    private boolean fullySeen;

    Talon(DrawMode drawMode) {
        this.drawMode = drawMode;
        this.stock = new Stock();
        this.waste = new Waste();
    }

    private Talon(Talon original) {
        this.drawMode = original.drawMode;
        this.stock = original.stock.copy();
        this.waste = original.waste.copy();
        this.fullySeen = original.fullySeen;
    }

    Talon copy() {
        return new Talon(this);
    }

    /** Puts the cards left after dealing into the stock. The first card of the list is drawn first. */
    void load(List<Card> cardsInDrawOrder) {
        for (int cardIndex = cardsInDrawOrder.size() - 1; cardIndex >= 0; cardIndex--) {
            stock.putCard(cardsInDrawOrder.get(cardIndex));
        }
    }

    /** Turns 1 or 3 cards from the stock onto the waste, or turns the waste over when the stock is empty. */
    void draw() {
        if (stock.isEmpty()) {
            if (waste.isEmpty()) {
                throw new IllegalStateException("Cannot draw: the stock and the waste are both empty.");
            }
            while (!waste.isEmpty()) {
                stock.putCard(waste.takeTopCard());
            }
            fullySeen = true;
            return;
        }
        int cardsToTurn = Math.min(drawMode.getCardsPerDraw(), stock.size());
        for (int cardsTurned = 0; cardsTurned < cardsToTurn; cardsTurned++) {
            waste.putCard(stock.takeTopCard());
        }
    }

    /** How many draws empty the stock and then turn the waste over (one full look through the talon). */
    public int countDrawsToTurnOver() {
        int cardsPerDraw = drawMode.getCardsPerDraw();
        int drawsToEmptyStock = (stock.size() + cardsPerDraw - 1) / cardsPerDraw;
        return drawsToEmptyStock + 1;
    }

    /**
     * Shows what would be on top of the waste after 1, 2, 3 ... draws, without changing the talon.
     * It covers the rest of this pass and one complete pass after it, so every reachable card appears.
     *
     * @return the waste top after each draw (index 0 = after 1 draw); null where the waste would be empty
     */
    public List<Card> previewWasteTops() {
        Talon preview = copy();
        int draws = countDrawsToTurnOver() + (countCards() + drawMode.getCardsPerDraw() - 1) / drawMode.getCardsPerDraw();
        List<Card> tops = new ArrayList<>();
        for (int drawsDone = 0; drawsDone < draws && preview.canDraw(); drawsDone++) {
            preview.draw();
            tops.add(preview.waste.getTopCard());
        }
        return tops;
    }

    public boolean canDraw() {
        return !stock.isEmpty() || !waste.isEmpty();
    }

    public boolean isFullySeen() {
        return fullySeen || countCards() == 0;
    }

    public int countCards() {
        return stock.size() + waste.size();
    }

    public DrawMode getDrawMode() {
        return drawMode;
    }

    public Stock getStock() {
        return stock;
    }

    public Waste getWaste() {
        return waste;
    }
}
