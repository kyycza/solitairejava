package com.svi.solitaire.view;

import com.svi.solitaire.model.*;

import java.util.List;

/** Draws the solitaire table: stock, waste and foundations on top, the 7 stacks below. */
public final class BoardPrinter {

    private static final int STOCK_COLUMN_WIDTH = 13;
    private static final int WASTE_COLUMN_WIDTH = 20;
    private static final String GAP = " ";

    private BoardPrinter() {
    }

    /**
     * Prints the table after a move.
     *
     * @param tableau     the table
     * @param step        the number of the move that was just played (0 = game start)
     * @param description what the move did
     * @param highlighted the cards that just moved; they are shown in blue
     */
    public static void print(Tableau tableau, int step, String description, List<Card> highlighted) {
        System.out.println();
        System.out.println(AnsiColor.CYAN.paint("--- Step " + step + ": " + description + " ---"));
        printTopRow(tableau, highlighted);
        System.out.println();
        printStacks(tableau, highlighted);
    }

    private static void printTopRow(Tableau tableau, List<Card> highlighted) {
        System.out.printf("%-" + STOCK_COLUMN_WIDTH + "s%-" + WASTE_COLUMN_WIDTH + "s%s%n",
                "STOCK", "WASTE", "FOUNDATIONS");
        System.out.println(stockText(tableau.getTalon()) + wasteText(tableau.getTalon(), highlighted)
                + foundationsText(tableau, highlighted));
    }

    /** For example "[####] x24", padded to the width of the STOCK column. */
    private static String stockText(Talon talon) {
        int cards = talon.getStock().size();
        String box = cards == 0 ? CardText.empty() : CardText.faceDown();
        return box + String.format(" x%-" + (STOCK_COLUMN_WIDTH - CardText.BOX_WIDTH - 2) + "d", cards);
    }

    /** The last 1 or 3 waste cards, like a real fanned waste; the last one is the playable card. */
    private static String wasteText(Talon talon, List<Card> highlighted) {
        Waste waste = talon.getWaste();
        int shown = Math.min(talon.getDrawMode().getCardsPerDraw(), waste.size());
        StringBuilder text = new StringBuilder();
        for (int position = waste.size() - shown; position < waste.size(); position++) {
            text.append(CardText.faceUp(waste.getCard(position), highlighted));
        }
        if (shown == 0) {
            text.append(CardText.empty());
        }
        int boxesPrinted = Math.max(shown, 1);
        return text + " ".repeat(WASTE_COLUMN_WIDTH - boxesPrinted * CardText.BOX_WIDTH);
    }

    private static String foundationsText(Tableau tableau, List<Card> highlighted) {
        StringBuilder text = new StringBuilder();
        for (Foundation foundation : tableau.getFoundations()) {
            text.append(foundation.isEmpty()
                    ? CardText.emptyFoundation(foundation.getSuit())
                    : CardText.faceUp(foundation.getTopCard(), highlighted));
        }
        return text.toString();
    }

    /** Prints the stack numbers, then the stacks side by side, one row at a time. */
    private static void printStacks(Tableau tableau, List<Card> highlighted) {
        StringBuilder header = new StringBuilder();
        for (int number = 1; number <= Tableau.STACK_COUNT; number++) {
            header.append(String.format("  %-" + (CardText.BOX_WIDTH - 2) + "d", number)).append(GAP);
        }
        int tallest = 1;
        for (Manoeuvre stack : tableau.getStacks()) {
            tallest = Math.max(tallest, stack.size());
        }
        System.out.println(header);
        for (int row = 0; row < tallest; row++) {
            StringBuilder line = new StringBuilder();
            for (Manoeuvre stack : tableau.getStacks()) {
                line.append(cellText(stack, row, highlighted)).append(GAP);
            }
            System.out.println(line);
        }
    }

    private static String cellText(Manoeuvre stack, int row, List<Card> highlighted) {
        if (stack.isEmpty() && row == 0) {
            return CardText.empty();
        }
        if (row >= stack.size()) {
            return CardText.BLANK;
        }
        return stack.isHidden(row) ? CardText.faceDown() : CardText.faceUp(stack.getCard(row), highlighted);
    }
}