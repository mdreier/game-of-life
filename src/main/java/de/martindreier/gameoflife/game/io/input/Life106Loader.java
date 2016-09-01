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
package de.martindreier.gameoflife.game.io.input;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import de.martindreier.gameoflife.game.CellState;
import de.martindreier.gameoflife.game.GameRule;
import de.martindreier.gameoflife.game.Grid;
import de.martindreier.gameoflife.game.io.GridLoader;

/**
 * Loader to load files in the Life 1.06 format.
 *
 * @author D043987
 *
 */
public class Life106Loader implements GridLoader {

    /**
     * Prefix <code>#Life</code> of header line.
     */
    private static final String PREFIX_LIFE_LINE = "#Life";

    /**
     * List of coordinates of alive cells loaded from the file.
     */
    private List<CellCoordinates> aliveCells       = new LinkedList<>();

    /**
     * Constructor for testing. This constructor does not load any files, so the class is not properly initialized after
     * the call.
     */
    Life106Loader() {
        // Empty constructor
    }

    /**
     * Load data from a file.
     *
     * @param lifeFile
     *            Path to the life file.
     * @throws IOException
     *             Error reading from file.
     */
    public Life106Loader(Path lifeFile) throws IOException {
        if (!Files.isReadable(lifeFile)) {
            throw new IOException(String.format("File %s is not readable or does not exist", lifeFile));
        }

        // Validate header
        String headerLine = Files.lines(lifeFile).findFirst().orElse("");
        if (!headerLine.startsWith(PREFIX_LIFE_LINE)) {
            throw new IOException(String.format("File %s is not a life 1.06 file", lifeFile));
        }
        if (headerLine.length() > PREFIX_LIFE_LINE.length()) {
            String version = headerLine.substring(PREFIX_LIFE_LINE.length()).trim();
            if (!version.equals("1.06")) {
                throw new IOException(String.format("File %s has an invalid version (%s), expected 1.06", lifeFile, version));
            }
        }

        // Parse content
        Files.lines(lifeFile).forEach(this::parseLine);
    }

    /**
     * Parse a line in the file.
     *
     * @param line
     *            Line content.
     */
    protected void parseLine(String line) {
        // Skip header line
        if (line.startsWith("#")) {
            return;
        }
        this.aliveCells.add(CellCoordinates.parse(line));
    }

    /**
     * @see de.martindreier.gameoflife.game.io.GridLoader#setInitialState(de.martindreier.gameoflife.game.Grid)
     */
    @Override
    public void setInitialState(Grid grid) {
        for (CellCoordinates coordinates : this.aliveCells) {
            grid.set(coordinates.x, coordinates.y, CellState.ALIVE);
        }
    }

    /**
     * @see de.martindreier.gameoflife.game.io.GridLoader#getWidth()
     */
    @Override
    public int getWidth() {
        return this.aliveCells.stream().mapToInt(cell -> cell.x).max().orElse(-1) + 1;
    }

    /**
     * @see de.martindreier.gameoflife.game.io.GridLoader#getHeight()
     */
    @Override
    public int getHeight() {
        return this.aliveCells.stream().mapToInt(cell -> cell.y).max().orElse(-1) + 1;
    }

    /**
     * @see de.martindreier.gameoflife.game.io.GridLoader#getGameRule()
     */
    @Override
    public Optional<GameRule> getGameRule() {
        // File format does not allow rules specification
        return Optional.empty();
    }

    /**
     * Get a list of alive cells loaded from the files.
     *
     * @return Unmodifiable list of coordinates.
     */
    public List<CellCoordinates> getAliveCells() {
        return Collections.unmodifiableList(this.aliveCells);
    }

    /**
     * Coordinates for a cell.
     *
     * @author D043987
     *
     */
    public static class CellCoordinates {
        /**
         * Horizontal coordinate.
         */
        private int x;
        /**
         * Vertical coordinate.
         */
        private int y;

        /**
         * Create a new coordinate set.
         *
         * @param x
         *            Horizontal coordinate.
         * @param y
         *            Vertical coordinate.
         */
        public CellCoordinates(int x, int y) {
            this.x = x;
            this.y = y;
        }

        /**
         * Get the horizontal coordinate.
         *
         * @return the x
         */
        public int getX() {
            return this.x;
        }

        /**
         * Get the vertical coordinate.
         *
         * @return the y
         */
        public int getY() {
            return this.y;
        }

        /**
         * Parse a coordinate specification. Expects a {@link String} with the x and y coordinate, separated by a space.
         * Example:
         * <p>
         * <code>23 7</code>
         * </p>
         *
         * @param coordinates
         *            Coordinates.
         * @return Parsed coordinates.
         */
        public static CellCoordinates parse(String coordinates) {
            if (coordinates == null) {
                throw new IllegalArgumentException("Coordinates must not be null");
            }
            String[] content = coordinates.split(" ");
            if (content.length != 2) {
                throw new IllegalArgumentException("Input coordinates must be a pair of numbers separated by space");
            }
            try {
                int x = Integer.valueOf(content[0]);
                int y = Integer.valueOf(content[1]);
                return new CellCoordinates(x, y);
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException(String.format("Input %s are not two valid numbers", coordinates), e);
            }
        }
    }
}
