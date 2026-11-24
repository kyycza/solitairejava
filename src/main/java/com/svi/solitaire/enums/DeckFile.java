package com.svi.solitaire.enums;

/** The choices for the deck when the user plays again with new options. */
public enum DeckFile {
    SAME_DECK("Use the same deck again"),
    NEW_DECK("Load another deck file");

    private final String description;

    DeckFile(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}