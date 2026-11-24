package com.svi.solitaire.exception;

/** Thrown when the user closes the input (for example Ctrl+D or Ctrl+Z), so no more answers can be read. */
public class InputClosedException extends RuntimeException {

    public InputClosedException() {
        super("The input was closed.");
    }
}