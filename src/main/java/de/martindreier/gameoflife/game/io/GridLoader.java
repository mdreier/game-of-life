/**
 * Copyright &copy; 2016, SAP SE.
 * All rights reserved.
 */
package de.martindreier.gameoflife.game.io;

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
}
