/**
 * Copyright &copy; 2016, SAP SE.
 * All rights reserved.
 */
package de.martindreier.gameoflife.game;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

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
    public void checkCellStates(boolean iterated) {
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                if (iterated && (x == 2) && (y == 1 || y == 2 || y == 3)) {
                    // These cells should be alive after a single iteration
                    assertEquals(String.format("Cell at (%d,%d) should be alive", x, y), CellState.ALIVE, this.grid.get(x, y));
                } else if ((!iterated) && (y == 2) && (x == 1 || x == 2 || x == 3)) {
                    // These cells should be alive in the initial state
                    assertEquals(String.format("Cell at (%d,%d) should be alive", x, y), CellState.ALIVE, this.grid.get(x, y));
                } else {
                    // These cells should be dead
                    assertEquals(String.format("Cell at (%d,%d) should be dead", x, y), CellState.DEAD, this.grid.get(x, y));
                }
            }
        }
    }

    /**
     * Check the initial state after grid creation.
     */
    @Test
    public void initialState() {
        this.checkCellStates(false);
    }

    /**
     * Run one iteration with the standard rule set.
     */
    @Test
    public void oneIteration() {
        GameRule standardRule = GameRule.createRule("23", "3");
        this.grid.iterate(standardRule);
        this.checkCellStates(true);
    }

    /**
     * Run one iteration with the standard rule set. The grid should be in the initial state again.
     */
    @Test
    public void twoIterations() {
        GameRule standardRule = GameRule.createRule("23", "3");
        this.grid.iterate(standardRule);
        this.grid.iterate(standardRule);
        this.checkCellStates(false);
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
        this.grid.initialize(10, 10);
    }
}
