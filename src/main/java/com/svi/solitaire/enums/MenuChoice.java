package com.svi.solitaire.enums;

/** The choices after a game has finished. */
public enum MenuChoice {
    PLAY_AGAIN_SAME_OPTIONS("Play again with the same options"),
    PLAY_AGAIN_NEW_OPTIONS("Play again with new options"),
    QUIT("Quit");

    private final String description;

    MenuChoice(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
