package com.svi.solitaire;

import com.svi.solitaire.app.SolitaireApp;

/** Starts the program and closes it with the right exit code. */
public class Main {

    public static void main(String[] args) {
        int exitCode = new SolitaireApp().run();
        System.exit(exitCode);
    }
}
