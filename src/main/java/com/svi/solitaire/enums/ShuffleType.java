package com.svi.solitaire.enums;

/** The kinds of shuffle the player can choose. */
public enum ShuffleType {
    RANDOM("Random"),
    OUT_FARO("Out-faro (perfect shuffle, top half first)"),
    IN_FARO("In-faro (perfect shuffle, bottom half first)");

    private final String description;

    ShuffleType(String description) {
        this.description = description;
    }

    /** True for the two perfect shuffles, which need a number of times to shuffle. */
    public boolean isFaro() {
        return this != RANDOM;
    }

    @Override
    public String toString() {
        return description;
    }
}
