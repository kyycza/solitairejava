# Autoplay Klondike Solitaire

The computer plays Klondike Solitaire by itself in the console.

## Run
    mvn package
    java -jar target/solitaire-1.0.jar

Needs JDK 25 and Maven. Use a UTF-8 terminal (IntelliJ, VS Code, Windows Terminal) so the suit symbols show.

## Options
Draw 1 / Draw 3 · Random, out-faro or in-faro shuffle · any deck file (cards like `D-A`, `S-10`; Enter = standard deck) · Lookahead or Sampling player · watch one game or test the win rate.

Sample decks are in `src/main/resources/decks`.
