# Klondike Solitaire

The computer plays Klondike Solitaire by itself in the console.

## Run
    mvn package
    java -jar target/solitaire-1.0.jar

Needs JDK 25 and Maven. Cards are shown the same way as in deck files: suit letter, a dash, then the rank, for example `S-A` (Ace of Spades) or `H-10` (Ten of Hearts).

## Options
- Draw 1 or Draw 3
- Random, out-faro or in-faro shuffle (1 to 52 faro shuffles), no shuffle
- Any deck file, with cards like `D-A` or `S-10` separated by commas, spaces or new lines (press Enter for the standard deck)
- Watch one game move by move, or test the win rate over many games

Sample decks are in `src/main/resources`. Type just the name, for example `sample-deck.txt`.