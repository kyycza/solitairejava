package com.svi.solitaire.service;

import com.svi.solitaire.model.Move;
import com.svi.solitaire.model.Tableau;

import java.util.List;

/** Anything that can decide what to do next in a game of solitaire. */
public interface Player {

    /**
     * Decides the next moves. The table is only looked at, never changed.
     *
     * @param tableau the current table
     * @return the moves to play next, or an empty list when the player gives up
     */
    List<Move> planNextMoves(Tableau tableau);
}