/*******************************************************************************
 * Copyright (C) 2016 Martin Dreier <martin@martindreier.de>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.martindreier.gameoflife.game;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests for the {@link Cell} class.
 *
 * @author D043987
 *
 */
@RunWith(JUnit4.class)
public class CellTest {

    /**
     * Cell under test.
     */
    private Cell cell;

    /**
     * Create the cell under test.
     */
    @Before
    public void createCellUnderTest() {
        this.cell = new Cell();
    }

    /**
     * Test that the current cell state is switched to the next state on update.
     */
    @Test
    public void stateSwitchOnUpdate() {
        this.cell.setNextState(CellState.ALIVE);
        this.cell.update();
        assertEquals("Cell updated to next state", CellState.ALIVE, this.cell.getCurrentState());
    }

    /**
     * Test that new cells are created in a dead state.
     */
    @Test
    public void newCellsAreDead() {
        assertEquals("New cells should be dead", CellState.DEAD, this.cell.getCurrentState());
    }

    /**
     * Test that the cell state is not changed after a reset and subsequent update.
     */
    @Test
    public void resetNextCellState() {
        // Set a next state
        this.cell.setNextState(CellState.ALIVE);

        // Now reset and update
        this.cell.reset();
        this.cell.update();

        assertEquals("Cell state should be initial (DEAD)", CellState.DEAD, this.cell.getCurrentState());
    }

    /**
     * Test counting of neighbors.
     */
    @Test
    public void countNeighbors() {
        // Neighbor set with three neighbors, two alive and one dead.
        Set<Cell> neighbors = new HashSet<>();
        neighbors.addAll(Arrays.asList(new Cell(CellState.ALIVE), new Cell(CellState.ALIVE), new Cell(CellState.DEAD)));

        // Cell under test
        this.cell.setNeighbors(neighbors);

        // Count neighbors
        assertEquals("Expected number of neighbors alive", 2, this.cell.countNeighborsAlive());
    }

    /**
     * Test that no invalid values are allowed for the current cell state.
     */
    @Test(expected = IllegalArgumentException.class)
    public void illegalCurrentState() {
        this.cell.setCurrentState(null);
    }

    /**
     * Test that no invalid values are allowed for the next cell state.
     */
    @Test(expected = IllegalArgumentException.class)
    public void illegalNextState() {
        this.cell.setNextState(null);
    }

    /**
     * Test that no invalid values are allowed for the initial cell state.
     */
    @Test(expected = IllegalArgumentException.class)
    public void illegalInitialState() {
        @SuppressWarnings("unused")
        Cell cell = new Cell(null);
    }
}
