/*******************************************************************************
 * Copyright (C) 2016 Martin Dreier <martin@martindreier.de>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.martindreier.gameoflife.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import de.martindreier.gameoflife.game.io.GridLoader;

/**
 * The game grid. Holds an array of {@link Cell Cells} and access methods for it. The grid's coordinate system is
 * centered at the top left corner, i.e. the top left cell is (0,0).
 *
 * @author Martin Dreier <martin@martindreier.de>
 *
 */
public class Grid {

    /**
     * The cells in this grid (column, row).
     */
    private Cell[][] cells;

    /**
     * Create an uninitialized grid.
     */
    public Grid() {
        // Empty constructor
    }

    /**
     * Create an empty grid with the specified dimensions.
     *
     * @param width
     *            Grid width (number of cells in X dimension).
     * @param height
     *            Grid height (number of cells in Y dimension).
     */
    public Grid(int width, int height) {
        this();
        this.initialize(width, height);
    }

    /**
     * Create a grid and initialize it from the grid loader.
     *
     * @param loader
     *            Grid loader to specify grid dimensions and initial content.
     */
    public Grid(GridLoader loader) {
        this();
        this.initialize(loader);
    }

    /**
     * Create a grid with a specified grid size and initialize the initial state from the grid loader.
     *
     * @param width
     *            Grid width (number of cells in X dimension).
     * @param height
     *            Grid height (number of cells in Y dimension).
     * @param loader
     *            Grid loader to specify grid dimensions and initial content.
     */
    public Grid(int width, int height, GridLoader loader) {
        this();
        this.initialize(width, height, loader);
    }

    /**
     * Initialize an empty grid with the specified dimensions.
     *
     * @param width
     *            Size in x dimension (column count).
     * @param height
     *            Size in y dimension (row count).
     */
    public void initialize(int width, int height) {
        if (this.cells != null) {
            throw new IllegalStateException("Grid is already initialized");
        }
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException(String.format("Height and width must be greather than 0 (height: %s, width: %s)", height, width));
        }
        this.cells = new Cell[width][];

        // Create cell array
        for (int x = 0; x < width; x++) {
            Cell[] column = new Cell[height];
            for (int y = 0; y < height; y++) {
                column[y] = new Cell();
            }
            this.cells[x] = column;
        }

        // Update neighbor information
        this.updateNeighbors();
    }

    /**
     * Initialize a grid using a loader.
     *
     * @param loader
     *            The grid loader.
     */
    public void initialize(GridLoader loader) {
        if (loader == null) {
            throw new IllegalArgumentException("Grid loader must not be null");
        }
        this.initialize(loader.getWidth(), loader.getHeight());
        loader.setInitialState(this);
    }

    /**
     * Initialize a grid width fixed dimensions using a loader. The method checks that the loaded initial state fits
     * inside the specified grid.
     *
     * @param width
     *            Size in x dimension (column count).
     * @param height
     *            Size in y dimension (row count).
     *
     * @param loader
     *            The grid loader.
     */
    public void initialize(int width, int height, GridLoader loader) {
        if (loader == null) {
            throw new IllegalArgumentException("Grid loader must not be null");
        }
        if (width < loader.getWidth() || height < loader.getHeight()) {
            throw new IllegalArgumentException("Pattern from loader does not fit into specified grid");
        }
        this.initialize(width, height);
        loader.setInitialState(this);
    }

    /**
     * Fill neighbors into all cells. This is done once during initialization and should not be repeated unless the grid
     * layout has changed.
     */
    protected void updateNeighbors() {
        if (this.cells == null) {
            throw new IllegalStateException("Grid is not initialized");
        }
        for (int x = 0; x < this.cells.length; x++) {
            for (int y = 0; y < this.cells[x].length; y++) {
                Set<Cell> neighbors = new HashSet<>();
                // Calculate surrounding area, taking into account borders of the grid
                int minX = Math.max(0, x - 1);
                int maxX = Math.min(this.cells.length - 1, x + 1);
                int minY = Math.max(0, y - 1);
                int maxY = Math.min(this.cells[x].length - 1, y + 1);
                // Fill surrounding cells
                for (int neighborX = minX; neighborX <= maxX; neighborX++) {
                    for (int neighborY = minY; neighborY <= maxY; neighborY++) {
                        if (neighborX == x && neighborY == y) {
                            // Skip current cell
                            continue;
                        }
                        neighbors.add(this.cells[neighborX][neighborY]);
                    }
                }
                // Update current cell with new neighbors
                this.cells[x][y].setNeighbors(neighbors);
            }
        }
    }

    /**
     * Get the cell at the specified coordinates. The coordinate system starts at the top left corner (x is across, y is
     * down).
     *
     * @param x
     *            X coordinate (horizontal).
     * @param y
     *            Y coordinate (vertical).
     * @return Specified cell.
     */
    public CellState get(int x, int y) {
        return this.cells[x][y].getCurrentState();
    }

    /**
     * Set the state of a cell.
     *
     * @param x
     *            X coordinate (horizontal).
     * @param y
     *            Y coordinate (vertical).
     * @param state
     *            State of the cell.
     */
    public void set(int x, int y, CellState state) {
        this.cells[x][y].setCurrentState(state);
    }

    /**
     * Get the current grid as rows and columns.
     *
     * @return Grid content.
     */
    public List<List<Cell>> getGrid() {
        List<List<Cell>> grid = new ArrayList<>(this.cells[0].length);
        for (int row = 0; row < this.cells[0].length; row++) {
            List<Cell> rowContent = new ArrayList<>(this.cells.length);
            for (int column = 0; column < this.cells.length; column++) {
                rowContent.add(this.cells[column][row]);
            }
            grid.add(rowContent);
        }
        return grid;
    }

    /**
     * Get a stream of all cells in this grid. Order is by column, then by row.
     *
     * @return All cells in grid.
     */
    public Stream<Cell> getCells() {
        return Arrays.stream(this.cells).flatMap(column -> Arrays.stream(column));
    }

    /**
     *
     * @param ruleSet
     */
    public void iterate(GameRule ruleSet) {
        this.getCells().forEach(cell -> cell.setNextState(ruleSet.getNewState(cell.getCurrentState(), cell.countNeighborsAlive())));
        this.getCells().forEach(Cell::update);
    }

}
