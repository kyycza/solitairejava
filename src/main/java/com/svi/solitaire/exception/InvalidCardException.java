package com.svi.solitaire.exception;

/** Thrown when a piece of text is not a valid card, for example "X-2" or "D-15". */
public class InvalidCardException extends IllegalArgumentException {

    public InvalidCardException(String message) {
        super(message);
    }
}
