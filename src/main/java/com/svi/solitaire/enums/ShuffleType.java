package com.svi.solitaire.enums;

/** The kinds of shuffle the player can choose. */
public enum ShuffleType {
    RANDOM("Random"),
    OUT_FARO("Out-faro (perfect shuffle, top half first)"),
    IN_FARO("In-faro (perfect shuffle, bottom half first)"),
    NONE("No shuffle (keep the deck in its order)");

    private final String description;

    ShuffleType(String description) {
        this.description = description;
    }

    public boolean isFaro() {
        return this == OUT_FARO || this == IN_FARO;
    }

    @Override
    public String toString() {
        return description;
    }
}
