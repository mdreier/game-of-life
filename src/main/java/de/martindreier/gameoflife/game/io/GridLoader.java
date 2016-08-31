/**
 * Copyright &copy; 2016, SAP SE.
 * All rights reserved.
 */
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
