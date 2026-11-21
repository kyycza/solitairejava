package com.svi.solitaire.exception;

/** Thrown when a deck does not have exactly 52 different cards. */
public class InvalidDeckException extends IllegalArgumentException {

    public InvalidDeckException(String message) {
        super(message);
    }
}
