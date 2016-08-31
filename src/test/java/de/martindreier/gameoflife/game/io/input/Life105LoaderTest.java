/**
 * Copyright &copy; 2016, SAP SE.
 * All rights reserved.
 */
package de.martindreier.gameoflife.game.io.input;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import de.martindreier.gameoflife.game.GameRule;
import de.martindreier.gameoflife.game.Grid;
import de.martindreier.gameoflife.game.GridTest;
import de.martindreier.gameoflife.test.util.MultiInputExceptionCatcher;

/**
 * Test the loader for Life 1.05 files.
 *
 * @author D043987
 *
 */
@RunWith(JUnit4.class)
public class Life105LoaderTest {

    /**
     * Test loading of a life file.
     *
     * @throws URISyntaxException
     * @throws IOException
     */
    @Test
    public void loadLifeFile() throws URISyntaxException, IOException {
        Path testFile = Paths.get(this.getClass().getResource("/blinker.lif").toURI());

        Life105Loader loader = new Life105Loader(testFile);
        Grid grid = new Grid(loader);
        GridTest.checkCellStates(grid, false);
        assertEquals("Comment loaded correctly", "A simple blinker", loader.getComment());
        assertEquals("Grid size determination", 5, loader.getWidth());
        assertEquals("Grid size determination", 5, loader.getHeight());

        grid.iterate(loader.getGameRule().get());
        GridTest.checkCellStates(grid, true);
    }

    /**
     * Test loading of a life file.
     *
     * @throws URISyntaxException
     * @throws IOException
     */
    @Test
    public void loadMultiPatternLifeFile() throws URISyntaxException, IOException {
        Path testFile = Paths.get(this.getClass().getResource("/blinker_multi.lif").toURI());

        Life105Loader loader = new Life105Loader(testFile);
        Grid grid = new Grid(loader);
        GridTest.checkCellStates(grid, false);
        assertEquals("Comment loaded correctly", "A simple blinker", loader.getComment());
        assertEquals("Grid size determination", 5, loader.getWidth());
        assertEquals("Grid size determination", 5, loader.getHeight());

        grid.iterate(loader.getGameRule().get());
        GridTest.checkCellStates(grid, true);

        assertEquals("All patterns loaded", 2, loader.getCellBlocks().size());
    }

    /**
     * Test loading of a life file.
     *
     * @throws URISyntaxException
     * @throws IOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void loadInvalidLifeFile() throws URISyntaxException, IOException {
        Path testFile = Paths.get(this.getClass().getResource("/blinker_invalid.lif").toURI());
        Life105Loader loader = new Life105Loader(testFile);
        new Grid(loader);
    }

    /**
     * Test loading of a life file.
     *
     * @throws URISyntaxException
     * @throws IOException
     */
    @Test(expected = IOException.class)
    public void loadInvalidLifeFileVersion() throws URISyntaxException, IOException {
        Path testFile = Paths.get(this.getClass().getResource("/blinker.life").toURI());
        new Life105Loader(testFile);
    }

    /**
     * Test parsing of comment lines.
     */
    @Test
    public void parseComments() {
        String myComment = "a comment";
        Life105Loader loader = new Life105Loader();

        loader.parseHashLine("#D " + myComment);
        assertEquals("Comment parsing", myComment, loader.getComment());

        loader.parseHashLine("#C  " + myComment);
        assertEquals("Comment parsing", myComment + "\n" + myComment, loader.getComment());

        loader.parseHashLine("#O " + myComment + " ");
        assertEquals("Comment parsing", myComment + "\n" + myComment + "\n" + myComment, loader.getComment());
    }

    /**
     * Parse rule definitions.
     */
    @Test
    public void parseRule() {
        Life105Loader loader = new Life105Loader();
        loader.parseHashLine("#N");

        assertTrue("Rule provided", loader.getGameRule().isPresent());
        assertEquals("Standard rule set", GameRule.CONWAY, loader.getGameRule().get());
    }

    /**
     * Test parsing of invalid rules.
     */
    @Test
    public void parseInvalidRules() {
        String[] invalidRules = new String[] { // Comments to prevent auto-format
                "#R", //
                "#R ", //
                "#R 3/A", //
                "#R A/4", //
        };
        Life105Loader loader = new Life105Loader();
        MultiInputExceptionCatcher.execute(invalidRules, rule -> loader.parseHashLine(rule), IllegalArgumentException.class);
    }
}
