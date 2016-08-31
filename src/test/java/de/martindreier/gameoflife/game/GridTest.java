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

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import de.martindreier.gameoflife.game.grids.StandardTestGrid;
import de.martindreier.gameoflife.game.io.GridLoader;

/**
 * Test the game grid.
 *
 * @author D043987
 *
 */
@RunWith(JUnit4.class)
public class GridTest {

    /**
     * Grid under test.
     */
    private Grid grid;

    /**
     * Create a 5x5 grid with alive cells in a centered horizontal line.
     * <table border="1">
     * <tr>
     * <td>DEAD</td>
     * <td>DEAD</td>
     * <td>DEAD</td>
     * <td>DEAD</td>
     * <td>DEAD</td>
     * </tr>
     * <tr>
     * <td>DEAD</td>
     * <td>DEAD</td>
     * <td>DEAD</td>
     * <td>DEAD</td>
     * <td>DEAD</td>
     * </tr>
     * <tr>
     * <td>DEAD</td>
     * <td>ALIVE</td>
     * <td>ALIVE</td>
     * <td>ALIVE</td>
     * <td>DEAD</td>
     * </tr>
     * <tr>
     * <td>DEAD</td>
     * <td>DEAD</td>
     * <td>DEAD</td>
     * <td>DEAD</td>
     * <td>DEAD</td>
     * </tr>
     * <tr>
     * <td>DEAD</td>
     * <td>DEAD</td>
     * <td>DEAD</td>
     * <td>DEAD</td>
     * <td>DEAD</td>
     * </tr>
     * </table>
     */
    @Before
    public void createGrid() {
        // Create grid object
        this.grid = new Grid();
        this.grid.initialize(5, 5);

        // Set cell states for alive cells
        this.grid.set(1, 2, CellState.ALIVE);
        this.grid.set(2, 2, CellState.ALIVE);
        this.grid.set(3, 2, CellState.ALIVE);
    }

    /**
     * Test the cell state. If the parameter <code>iterated</code> is set to <code>false</code>, it tests for the
     * initial state created in the {@link #createGrid()} method. Otherwise it tests for the state after one iteration
     * with the (23/3) rule set, which expects the following state:
     *
     * <table border="1">
     * <tr>
     * <td>DEAD</td>
     * <td>DEAD</td>
     * <td>DEAD</td>
     * <td>DEAD</td>
     * <td>DEAD</td>
     * </tr>
     * <tr>
     * <td>DEAD</td>
     * <td>DEAD</td>
     * <td>ALIVE</td>
     * <td>DEAD</td>
     * <td>DEAD</td>
     * </tr>
     * <tr>
     * <td>DEAD</td>
     * <td>DEAD</td>
     * <td>ALIVE</td>
     * <td>DEAD</td>
     * <td>DEAD</td>
     * </tr>
     * <tr>
     * <td>DEAD</td>
     * <td>DEAD</td>
     * <td>ALIVE</td>
     * <td>DEAD</td>
     * <td>DEAD</td>
     * </tr>
     * <tr>
     * <td>DEAD</td>
     * <td>DEAD</td>
     * <td>DEAD</td>
     * <td>DEAD</td>
     * <td>DEAD</td>
     * </tr>
     * </table>
     *
     * @param iterated
     *            <code>true</code> if the expected state is after an iteration.
     */
    public static void checkCellStates(Grid grid, boolean iterated) {
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                if ((iterated) && (x == 2) && (y == 1 || y == 2 || y == 3)) {
                    // These cells should be alive after a single iteration
                    assertEquals(String.format("Cell at (%d,%d) should be alive", x, y), CellState.ALIVE, grid.get(x, y));
                } else if ((!iterated) && (y == 2) && (x == 1 || x == 2 || x == 3)) {
                    // These cells should be alive in the initial state
                    assertEquals(String.format("Cell at (%d,%d) should be alive", x, y), CellState.ALIVE, grid.get(x, y));
                } else {
                    // These cells should be dead
                    assertEquals(String.format("Cell at (%d,%d) should be dead", x, y), CellState.DEAD, grid.get(x, y));
                }
            }
        }
    }

    /**
     * Test the initialization of a grid from a loader.
     */
    @Test
    public void gridLoader() {
        GridLoader loader = new StandardTestGrid();
        Grid grid = new Grid(loader);
        GridTest.checkCellStates(grid, false);
    }

    /**
     * Test the initialization of a grid from a loader.
     */
    @Test(expected = IllegalArgumentException.class)
    public void gridLoaderNull() {
        new Grid(null);
    }

    /**
     * Check the initial state after grid creation.
     */
    @Test
    public void initialState() {
        GridTest.checkCellStates(this.grid, false);
    }

    /**
     * Run one iteration with the standard rule set.
     */
    @Test
    public void oneIteration() {
        GameRule standardRule = GameRule.createRule("23", "3");
        this.grid.iterate(standardRule);
        GridTest.checkCellStates(this.grid, true);
    }

    /**
     * Run one iteration with the standard rule set. The grid should be in the initial state again.
     */
    @Test
    public void twoIterations() {
        GameRule standardRule = GameRule.createRule("23", "3");
        this.grid.iterate(standardRule);
        this.grid.iterate(standardRule);
        GridTest.checkCellStates(this.grid, false);
    }

    /**
     * Test for invalid grid size.
     */
    @Test(expected = IllegalArgumentException.class)
    public void negativeGridSizeX() {
        Grid grid = new Grid();
        grid.initialize(-1, 5);
    }

    /**
     * Test for invalid grid size.
     */
    @Test(expected = IllegalArgumentException.class)
    public void negativeGridSizeY() {
        Grid grid = new Grid();
        grid.initialize(5, -1);
    }

    /**
     * Test for invalid grid size.
     */
    @Test(expected = IllegalArgumentException.class)
    public void zeroGridSizeX() {
        Grid grid = new Grid();
        grid.initialize(0, 5);
    }

    /**
     * Test for invalid grid size.
     */
    @Test(expected = IllegalArgumentException.class)
    public void negativeGridSize() {
        Grid grid = new Grid();
        grid.initialize(-1, -1);
    }

    /**
     * Test for invalid grid size.
     */
    @Test(expected = IllegalArgumentException.class)
    public void zeroGridSize() {
        Grid grid = new Grid();
        grid.initialize(0, 0);
    }

    /**
     * Test for invalid grid size.
     */
    @Test(expected = IllegalArgumentException.class)
    public void zeroGridSizeY() {
        Grid grid = new Grid();
        grid.initialize(5, 0);
    }

    /**
     * Test the stream generator for cells.
     */
    @Test
    public void cellList() {
        assertEquals("Expected amount of cells in grid", 25, this.grid.getCells().count());
    }

    @Test
    public void getGrid() {
        List<List<Cell>> gridList = this.grid.getGrid();
        assertEquals("Row count in grid", 5, gridList.size());
        for (int row = 0; row < 5; row++) {
            assertEquals(String.format("Column count in grid row %d", row), 5, gridList.get(row).size());
        }
    }

    /**
     * Grid may be initialized only once.
     */
    @Test(expected = IllegalStateException.class)
    public void initializeOnlyOnce() {
        Grid grid = new Grid(5, 5);
        grid.initialize(10, 10);
    }
}
