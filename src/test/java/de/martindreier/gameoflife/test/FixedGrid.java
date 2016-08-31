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
package de.martindreier.gameoflife.test;

import java.util.Optional;

import de.martindreier.gameoflife.game.CellState;
import de.martindreier.gameoflife.game.GameRule;
import de.martindreier.gameoflife.game.Grid;
import de.martindreier.gameoflife.game.io.GridLoader;

/**
 * @author D043987
 *
 */
public class FixedGrid implements GridLoader {

    /**
     * @see de.martindreier.gameoflife.game.io.GridLoader#setInitialState(de.martindreier.gameoflife.game.Grid)
     */
    @Override
    public void setInitialState(Grid grid) {
        grid.set(2, 1, CellState.ALIVE);
        grid.set(2, 2, CellState.ALIVE);
        grid.set(2, 3, CellState.ALIVE);
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
