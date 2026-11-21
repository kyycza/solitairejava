package com.svi.solitaire.io;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

/**
 * Asks the player questions in the console and keeps asking until the answer is valid.
 * If the input is closed (for example Ctrl+D), the NoSuchElementException is passed up to the caller.
 */
public class InputHandler implements AutoCloseable {

    private final Scanner scanner;

    public InputHandler(InputStream source) {
        this.scanner = new Scanner(source);
    }

    /**
     * Asks for a whole number in a range.
     *
     * @param question the question to show
     * @param min      the smallest allowed number
     * @param max      the largest allowed number
     * @return a number between min and max
     */
    public int askNumber(String question, int min, int max) {
        while (true) {
            System.out.print(question + " (" + min + "-" + max + "): ");
            String answer = scanner.nextLine().trim();
            try {
                int number = Integer.parseInt(answer);
                if (number >= min && number <= max) {
                    return number;
                }
                System.out.println("  Please type a number from " + min + " to " + max + ".");
            } catch (NumberFormatException notANumber) {
                System.out.println("  \"" + answer + "\" is not a number. Please try again.");
            }
        }
    }

    /**
     * Shows a numbered list and lets the player pick one option.
     *
     * @param question the question to show
     * @param options  the options; their toString() is shown
     * @param <T>      the type of the options
     * @return the chosen option
     */
    public <T> T askChoice(String question, List<T> options) {
        System.out.println(question);
        for (int i = 0; i < options.size(); i++) {
            System.out.println("  " + (i + 1) + ") " + options.get(i));
        }
        return options.get(askNumber("Your choice", 1, options.size()) - 1);
    }

    /**
     * Asks for a line of text.
     *
     * @param question the question to show
     * @return the answer without spaces around it (may be empty)
     */
    public String askText(String question) {
        System.out.print(question + ": ");
        return scanner.nextLine().trim();
    }

    @Override
    public void close() {
        scanner.close();
    }
}
