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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import de.martindreier.gameoflife.game.Grid;
import de.martindreier.gameoflife.game.GridTest;
import de.martindreier.gameoflife.game.io.input.Life106Loader.CellCoordinates;
import de.martindreier.gameoflife.test.util.MultiInputExceptionCatcher;

/**
 * @author D043987
 *
 */
@RunWith(JUnit4.class)
public class Life106LoaderTest {

    /**
     * Test that incorrect file versions throw an exception.
     *
     * @throws URISyntaxException
     * @throws IOException
     *             This should be thrown.
     */
    @Test(expected = IOException.class)
    public void loadInvalidFileVersion() throws URISyntaxException, IOException {
        Path testFile = Paths.get(this.getClass().getResource("/blinker.lif").toURI());
        new Life106Loader(testFile);
    }

    /**
     * Test that incorrect file type throws an exception.
     *
     * @throws URISyntaxException
     * @throws IOException
     *             This should be thrown.
     */
    @Test(expected = IOException.class)
    public void loadNonLifeFile() throws URISyntaxException, IOException {
        Path testFile = Paths.get(this.getClass().getResource("/blinker.rle").toURI());
        new Life106Loader(testFile);
    }

    /**
     * Test loading of sample file.
     *
     * @throws URISyntaxException
     * @throws IOException
     */
    @Test
    public void loadLifeFile() throws URISyntaxException, IOException {
        Path testFile = Paths.get(this.getClass().getResource("/blinker.life").toURI());

        Life106Loader loader = new Life106Loader(testFile);
        Grid grid = new Grid(5, 5, loader);
        GridTest.checkCellStates(grid, false);
        assertFalse("Life 1.06 loader should not specify a version", loader.getGameRule().isPresent());
    }

    /**
     * Test loading of sample file.
     *
     * @throws URISyntaxException
     * @throws IOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void loadInvalidLifeFile() throws URISyntaxException, IOException {
        Path testFile = Paths.get(this.getClass().getResource("/blinker_invalid.life").toURI());

        new Life106Loader(testFile);
    }

    /**
     * Test parsing of coordinates.
     */
    @Test
    public void parseCoordinates() {
        // Check that coordinates are parsed correctly
        CellCoordinates coordinates = CellCoordinates.parse("4 5");
        assertEquals("X coordinate parsed correctly", 4, coordinates.getX());
        assertEquals("Y coordinate parsed correctly", 5, coordinates.getY());

        // Check that a line is parsed
        Life106Loader loader = new Life106Loader();
        loader.parseLine("4 5");
        assertEquals("Line added to coordinate list", 1, loader.getAliveCells().size());
        assertEquals("X coordinate parsed correctly", 4, loader.getAliveCells().get(0).getX());
        assertEquals("Y coordinate parsed correctly", 5, loader.getAliveCells().get(0).getY());
    }

    /**
     * Test that invalid coordinates are not accepted.
     */
    @Test
    public void parseInvalidCoordinates() {
        String[] invalidCoordinates = new String[] { // Comments to avoid auto-format
                "4", //
                "4 a", //
                "X 7", //
                "abc", //
                "", //
        };
        MultiInputExceptionCatcher.execute(invalidCoordinates, line -> CellCoordinates.parse(line), IllegalArgumentException.class);
    }
}
