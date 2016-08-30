/*******************************************************************************
 * Copyright (C) 2016 Martin Dreier <martin@martindreier.de>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.martindreier.gameoflife.game;

import java.util.Collections;
import java.util.Set;

/**
 * A cell in the game grid. The cell knows about its neighbors. This class should be only used through the {@link Grid}
 * and never directly.
 *
 * @author Martin Dreier <martin@martindreier.de>
 *
 */
public class Cell {

    /**
     * The current state of the cell.
     */
    private CellState currentState = CellState.DEAD;

    /**
     * The next state the cell with have.
     */
    private CellState nextState    = CellState.DEAD;

    /**
     * Neighbors of this cell.
     */
    private Set<Cell> neighbors    = Collections.emptySet();

    /**
     * Create a new, dead cell.
     */
    public Cell() {
        // No initialization
    }

    /**
     * Create a new cell with a given initial state.
     *
     * @param initialState
     *            The initial cell state.
     */
    Cell(CellState initialState) {
        this();
        this.setCurrentState(initialState);
    }

    /**
     * Set the neighbors of this cell.
     *
     * @param neighbors
     */
    void setNeighbors(Set<Cell> neighbors) {
        this.neighbors = neighbors;
    }

    /**
     * Count the number of neighbor cells which are alive.
     *
     * @return
     */
    public int countNeighborsAlive() {
        long aliveCount = this.neighbors.stream().filter(cell -> cell.currentState == CellState.ALIVE).count();
        if (aliveCount > Integer.MAX_VALUE) {
            throw new IllegalStateException("Number of neighbors exceeds value range");
        }
        return (int) aliveCount;
    }

    /**
     * Get the current state of the cell.
     *
     * @return the current state.
     */
    public CellState getCurrentState() {
        return this.currentState;
    }

    /**
     * Set the current state of the cell.
     *
     * @param currentState
     *            the new current state. Must not be <code>null</code>.
     */
    void setCurrentState(CellState currentState) {
        if (currentState == null) {
            throw new IllegalArgumentException("The current state must not be null");
        }
        this.currentState = currentState;
    }

    /**
     * Set the next state of the cell. The state will be set after a call to {@link #update()}.
     *
     * @param nextState
     *            the next state.
     */
    public void setNextState(CellState nextState) {
        if (nextState == null) {
            throw new IllegalArgumentException("The next state must not be null");
        }
        this.nextState = nextState;
    }

    /**
     * Update the current state from the next state.
     */
    public void update() {
        this.currentState = this.nextState;
    }

    /**
     * Reset the next state. A call to {@link #update()} will not change the cell state.
     */
    public void reset() {
        this.nextState = this.currentState;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Cell [" + this.currentState + "]";
    }
}
