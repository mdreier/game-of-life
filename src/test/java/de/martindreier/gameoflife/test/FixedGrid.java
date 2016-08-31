/**
 * Copyright &copy; 2016, SAP SE.
 * All rights reserved.
 */
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
