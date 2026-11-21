package com.svi.solitaire.view;

/** Terminal colors. Only the view package uses them, so color codes never mix with the game logic. */
public enum AnsiColor {
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    CYAN("\u001B[36m"),
    BLUE("\u001B[94m"),
    GRAY("\u001B[90m");

    private static final String RESET = "\u001B[0m";

    private final String code;

    AnsiColor(String code) {
        this.code = code;
    }

    /** Wraps the text in this color, then switches the color off again. */
    public String paint(String text) {
        return code + text + RESET;
    }
}
