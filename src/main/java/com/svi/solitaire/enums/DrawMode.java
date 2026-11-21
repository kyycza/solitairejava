package com.svi.solitaire.enums;

/** How many cards are turned from the stock to the waste at a time. */
public enum DrawMode {
    DRAW_ONE(1),
    DRAW_THREE(3);

    private final int cardsPerDraw;

    DrawMode(int cardsPerDraw) {
        this.cardsPerDraw = cardsPerDraw;
    }

    public int getCardsPerDraw() {
        return cardsPerDraw;
    }

    @Override
    public String toString() {
        return "Draw " + cardsPerDraw;
    }
}
