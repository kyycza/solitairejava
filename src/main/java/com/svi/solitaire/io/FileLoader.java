package com.svi.solitaire.io;

import com.svi.solitaire.enums.Rank;
import com.svi.solitaire.enums.Suit;
import com.svi.solitaire.exception.InvalidCardException;
import com.svi.solitaire.exception.InvalidDeckException;
import com.svi.solitaire.model.Card;
import com.svi.solitaire.model.Deck;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Loads a deck from any text file. The file name is never written in the code:
 * it is given by the caller (the name the player typed).
 * <p>
 * The name is looked for in two places, in this order:
 * <ol>
 *   <li>as a normal file path, so any file anywhere works ("C:/decks/my deck.txt");</li>
 *   <li>in the project's resources folder, so a bundled deck needs only its name ("sample-deck.txt").</li>
 * </ol>
 * Cards are written as codes such as D-A or S-10, separated by commas, spaces or new lines.
 */
public final class FileLoader {

    /** Where the bundled decks are inside src/main/resources: "/" means directly in that folder. */
    private static final String BUNDLED_DECKS_FOLDER = "/";
    private static final String CARD_SEPARATORS = ", \t";
    /** Separates the suit from the rank in a card code such as "D-A". */
    private static final String CODE_SEPARATOR = "-";
    /** A code has exactly 3 parts: the suit, the dash, the rank. */
    private static final int PARTS_IN_CODE = 3;
    /** An invisible mark some editors (for example Windows Notepad) put at the very start of a file. */
    private static final String BYTE_ORDER_MARK = "\uFEFF";

    private FileLoader() {
    }

    /**
     * Loads a deck in two steps: first every card in the file is parsed,
     * then the parsed cards are checked as a whole deck.
     *
     * @param fileName a file name or path, for example "input.txt" or "C:/decks/test.csv"
     * @return the deck, top card first
     * @throws NoSuchFileException  if the file is neither at that path nor in the resources folder
     * @throws IOException          if the file cannot be read
     * @throws InvalidCardException if the file contains text that is not a card
     * @throws InvalidDeckException if the cards are not exactly 52 different cards
     */
    public static Deck loadDeck(String fileName) throws IOException {
        try (BufferedReader reader = openDeckFile(fileName)) {
            List<Card> cards = parseCards(reader);         // step 1: text -> cards
            return new Deck(cards);                        // step 2: Deck checks 52 cards, no duplicates
        }
    }

    /** Opens the file at the given path, or else the bundled deck with that name. */
    private static BufferedReader openDeckFile(String fileName) throws IOException {
        Path path = Path.of(fileName);
        if (Files.isRegularFile(path)) {
            return Files.newBufferedReader(path);
        }
        InputStream bundledDeck = FileLoader.class.getResourceAsStream(BUNDLED_DECKS_FOLDER + fileName);
        if (bundledDeck != null) {
            return new BufferedReader(new InputStreamReader(bundledDeck, StandardCharsets.UTF_8));
        }
        throw new NoSuchFileException(fileName);
    }

    /** Reads line by line and turns every card code into a Card. */
    private static List<Card> parseCards(BufferedReader reader) throws IOException {
        List<Card> cards = new ArrayList<>();
        String line;
        boolean firstLine = true;
        while ((line = reader.readLine()) != null) {
            if (firstLine && line.startsWith(BYTE_ORDER_MARK)) {
                line = line.substring(BYTE_ORDER_MARK.length());   // not a card, so skip it
            }
            firstLine = false;
            StringTokenizer codes = new StringTokenizer(line, CARD_SEPARATORS);
            while (codes.hasMoreTokens()) {
                cards.add(parseCard(codes.nextToken()));
            }
        }
        return cards;
    }

    /**
     * Reads a card code such as "D-A" (Ace of Diamonds) or "S-10" (Ten of Spades).
     *
     * @param code the card code: suit letter, a dash, then the rank label
     * @return the card
     * @throws InvalidCardException if the code is not a valid card
     */
    private static Card parseCard(String code) {
        // "true" also returns the dashes as parts, so "D--A", "-D-A" and "D-" are refused instead of slipping through
        StringTokenizer parts = new StringTokenizer(code, CODE_SEPARATOR, true);
        if (parts.countTokens() != PARTS_IN_CODE) {
            throw new InvalidCardException("\"" + code + "\" is not a card.");
        }
        String suitText = parts.nextToken();
        String dash = parts.nextToken();
        String rankText = parts.nextToken();
        if (!dash.equals(CODE_SEPARATOR)) {
            throw new InvalidCardException("\"" + code + "\" is not a card.");
        }
        return new Card(Suit.fromLetter(suitText), Rank.fromLabel(rankText));
    }
}