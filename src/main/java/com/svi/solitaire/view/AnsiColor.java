package com.svi.solitaire.view;

import java.io.IOException;

/**
 * Terminal colors. Only the view package uses them, so color codes never mix with the game logic.
 * <p>
 * The classic Windows console (cmd) only shows colors after they are switched on, otherwise it prints
 * codes like "[36m". Running an empty "cmd /c" command once switches them on for the whole window.
 * To turn colors off completely, start the program with -Dcolors=false.
 */
public enum AnsiColor {
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    CYAN("\u001B[36m"),
    BLUE("\u001B[94m"),
    GRAY("\u001B[90m");

    private static final String RESET = "\u001B[0m";

    /** True unless the program was started with -Dcolors=false. */
    private static final boolean COLORS_ON = !"false".equals(System.getProperty("colors"));

    static {
        if (COLORS_ON) {
            switchOnWindowsColors();
        }
    }

    private final String code;

    AnsiColor(String code) {
        this.code = code;
    }

    /** Wraps the text in this color, then switches the color off again. Returns the text unchanged when colors are off. */
    public String paint(String text) {
        if (!COLORS_ON) {
            return text;
        }
        return code + text + RESET;
    }

    /** On Windows, runs an empty cmd command once; cmd switches on color support in the console and leaves it on. */
    private static void switchOnWindowsColors() {
        if (!System.getProperty("os.name").startsWith("Windows")) {
            return;                                     // other systems show colors already
        }
        try {
            new ProcessBuilder("cmd", "/c", "").inheritIO().start().waitFor();
        } catch (IOException couldNotStart) {
            // colors may not show in this console, but the game still works
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
        }
    }
}
