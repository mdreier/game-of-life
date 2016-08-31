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
package de.martindreier.gameoflife.game.io;

import java.util.Optional;

import de.martindreier.gameoflife.game.GameRule;
import de.martindreier.gameoflife.game.Grid;

/**
 * Interface for classes which can load a grid.
 *
 * @author D043987
 *
 */
public interface GridLoader {

    /**
     * Set the initial state of the grid cells.
     */
    public void setInitialState(Grid grid);

    /**
     * Get the desired width for the grid.
     *
     * @return Grid width.
     */
    public int getWidth();

    /**
     * Get the desired height for the grid.
     *
     * @return Grid height.
     */
    public int getHeight();

    /**
     * Return the game rule specified by this loader. The return value may be empty (but not <code>null</code>) if the
     * loader does not specify a game rule.
     *
     * @return The game rule specified by this loader.
     */
    public Optional<GameRule> getGameRule();
}
