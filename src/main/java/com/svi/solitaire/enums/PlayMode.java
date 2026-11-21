package com.svi.solitaire.enums;

/** What the program should do with the chosen options. */
public enum PlayMode {
    WATCH_ONE_GAME("Watch one game, move by move"),
    TEST_WIN_RATE("Test the win rate over many games");

    private final String description;

    PlayMode(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
