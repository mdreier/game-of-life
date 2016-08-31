/**
 * Copyright &copy; 2016, SAP SE.
 * All rights reserved.
 */
package de.martindreier.gameoflife.game.grids;

import de.martindreier.gameoflife.game.CellState;
import de.martindreier.gameoflife.game.Grid;
import de.martindreier.gameoflife.game.io.GridLoader;

/**
 * Loader which generates a 5x5 grid. It initially contains 3 alive cells in a centered horizontal row.
 *
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
 *
 * @author D043987
 *
 */
public class StandardTestGrid implements GridLoader {

    /**
     * @see de.martindreier.gameoflife.game.io.GridLoader#setInitialState(de.martindreier.gameoflife.game.Grid)
     */
    @Override
    public void setInitialState(Grid grid) {
        grid.set(1, 2, CellState.ALIVE);
        grid.set(2, 2, CellState.ALIVE);
        grid.set(3, 2, CellState.ALIVE);
    }

    /**
     * @see de.martindreier.gameoflife.game.io.GridLoader#getWidth()
     */
    @Override
    public int getWidth() {
        return 5;
    }

    /**
     * @see de.martindreier.gameoflife.game.io.GridLoader#getHeight()
     */
    @Override
    public int getHeight() {
        return 5;
    }

}
