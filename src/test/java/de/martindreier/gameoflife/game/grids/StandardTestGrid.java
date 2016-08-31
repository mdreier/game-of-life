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
package de.martindreier.gameoflife.game.grids;

import java.util.Optional;

import de.martindreier.gameoflife.game.CellState;
import de.martindreier.gameoflife.game.GameRule;
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

    /**
     * @see de.martindreier.gameoflife.game.io.GridLoader#getGameRule()
     */
    @Override
    public Optional<GameRule> getGameRule() {
        return Optional.empty();
    }

}
