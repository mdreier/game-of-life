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
import de.martindreier.gameoflife.game.grids.StandardTestGrid;
import de.martindreier.gameoflife.test.util.MultiInputExceptionCatcher;

/**
 * @author D043987
 *
 */
@RunWith(JUnit4.class)
public class RLEFileLoaderTest {

    /**
     * Test a number of invalid header lines which should all cause exceptions.
     */
    @Test
    public void parseInvalidHeaderLines() {
        // Invalid headers to test against
        String[] invalidHeaders = new String[] { // Comments to prevent auto-formatting
                "x = 1, y = b", //
                "x = a, y = 2", //
                "x = a, y = b", //
                "x = 2", //
                "y = 4", //
                "x = 2, y = 2, rule = B3/S23, other = stuff", //
                "x = 2 = 4, y = 2, rule = B3/S23", //
                "x = 2, z = 2, rule = X3/S23", //
                "x = 2, y = 2, rule = B3+S23", //
                "x = 2, y = 2, rule = B3/Y23", //
                "x = 3, y = 3, code = B3/S23", //
                "", //
                null, //
        };
        RLEFileLoader loader = new RLEFileLoader();

        MultiInputExceptionCatcher.execute(invalidHeaders, headerLine -> loader.parseHeader(headerLine), IllegalArgumentException.class);

    }

    /**
     * Test a number of invalid header lines which should all cause exceptions.
     */
    @Test
    public void parseValidHeaderLines() {
        // Invalid headers to test against
        String[] validHeaders = new String[] { // Comments to prevent auto-formatting
                "x = 3, y = 3", //
                "x=3, y = 3", //
                "x = 3, y=3", //
                "X = 3, Y = 3", //
                "x = 3, y = 2", //
                "x = 2, y = 3", //
                "x = 10, y = 10", //
                "x = 3, y = 3, rule = B3/S23", //
                "x = 3, y = 3, rule = B3 / S23", //
                "x = 3, y = 3, rule=B3/S23", //
        };
        RLEFileLoader loader = new RLEFileLoader();

        // Loop through invalid header lines
        for (String headerLine : validHeaders) {
            // Try to parse the line
            loader.parseHeader(headerLine);
        }
    }

    /**
     * Test that dimensions defined in the header line are parsed correctly.
     */
    @Test
    public void parseHeaderLineDimensions() {
        String headerLine = "x = 3, y = 4";
        RLEFileLoader loader = new RLEFileLoader();

        loader.parseHeader(headerLine);
        assertEquals("Grid dimension parsing: width", 3, loader.getWidth());
        assertEquals("Grid dimension parsing: height", 4, loader.getHeight());
        assertTrue("Default game rule defined", loader.getGameRule().isPresent());
        assertEquals("Grid dimension parsing: width", GameRule.CONWAY, loader.getGameRule().get());
    }

    /**
     * Test that dimensions defined in the header line are parsed correctly.
     */
    @Test
    public void parseHeaderLineRule() {
        String headerLine = "x = 3, y = 4, rule = B3/S23";
        RLEFileLoader loader = new RLEFileLoader();

        // Parse the header
        loader.parseHeader(headerLine);
        assertTrue("Default game rule defined", loader.getGameRule().isPresent());

        // Create a grid and test the parsed rule
        Grid grid = new Grid(new StandardTestGrid());
        grid.iterate(loader.getGameRule().get());

        GridTest.checkCellStates(grid, true);
    }

    /**
     * Test that dimensions defined in the header line are parsed correctly.
     */
    @Test
    public void parseHashLineRule() {
        RLEFileLoader loader = new RLEFileLoader();

        // Parse the header
        loader.parseHashLine("#r 23/3");
        assertTrue("Default game rule defined", loader.getGameRule().isPresent());

        // Create a grid and test the parsed rule
        Grid grid = new Grid(new StandardTestGrid());
        grid.iterate(loader.getGameRule().get());

        GridTest.checkCellStates(grid, true);
    }

    /**
     * Test that dimensions defined in the header line are parsed correctly.
     */
    @Test()
    public void parseHashLineInvalidRules() {
        RLEFileLoader loader = new RLEFileLoader();

        // Invalid lines
        String[] invalidRules = new String[] { // Comments to prevent auto-format
                "#r 23/", //
                "#r /23", //
                "#r a/23", //
                "#r 3/x", //
                "#r 23", //
                "#r ", //
                "#r", //
        };

        // Parse the header
        MultiInputExceptionCatcher.execute(invalidRules, hashLine -> loader.parseHashLine(hashLine), IllegalArgumentException.class);
    }

    /**
     * Test parsing of comment lines.
     */
    @Test
    public void parseComments() {
        String testComment = "my comment";
        String headerLine = "#C " + testComment;

        // Parse one line
        RLEFileLoader loader = new RLEFileLoader();
        loader.parseHashLine(headerLine);
        assertEquals("Comment parsed correctly", testComment, loader.getComment());

        // Parse multiple lines
        loader.parseHashLine(headerLine);
        assertEquals("Comment parsed correctly", testComment + "\n" + testComment, loader.getComment());
    }

    /**
     * Test parsing of name lines.
     */
    @Test
    public void parseName() {
        String testName = "NAME";
        String headerLine = "#N " + testName;

        RLEFileLoader loader = new RLEFileLoader();
        loader.parseHashLine(headerLine);
        assertEquals("Name parsed correctly", "Pattern Name: " + testName, loader.getComment());
        assertEquals("Name parsed correctly", testName, loader.getPatternName());
    }

    /**
     * Test parsing of name lines.
     */
    @Test
    public void parseCreation() {
        String created = "CREATED";
        String headerLine = "#O " + created;

        RLEFileLoader loader = new RLEFileLoader();
        loader.parseHashLine(headerLine);
        assertEquals("Name parsed correctly", "Created: " + created, loader.getComment());
    }

    /**
     * Test loading a grid from an RLE-encoded file.
     *
     * @throws URISyntaxException
     * @throws IOException
     */
    @Test
    public void loadTestFile() throws URISyntaxException, IOException {
        Path testFile = Paths.get(this.getClass().getResource("/blinker.rle").toURI());

        RLEFileLoader loader = new RLEFileLoader(testFile);
        Grid grid = new Grid(loader);
        GridTest.checkCellStates(grid, false);

        grid.iterate(loader.getGameRule().get());
        GridTest.checkCellStates(grid, true);
    }

    /**
     * Test loading a grid from an RLE-encoded file.
     *
     * @throws URISyntaxException
     * @throws IOException
     */
    @Test(expected = IllegalArgumentException.class)
    public void loadInvalidFile() throws URISyntaxException, IOException {
        Path testFile = Paths.get(this.getClass().getResource("/blinker_invalid.rle").toURI());

        RLEFileLoader loader = new RLEFileLoader(testFile);
        Grid grid = new Grid(loader);
    }

    /**
     * Test pattern expansion.
     */
    @Test
    public void expandPattern() {
        RLEFileLoader loader = new RLEFileLoader();

        String expanded = loader.expandPattern("b");
        assertEquals("Correct expansion of pattern", "b", expanded);

        expanded = loader.expandPattern("o");
        assertEquals("Correct expansion of pattern", "o", expanded);

        expanded = loader.expandPattern("bo");
        assertEquals("Correct expansion of pattern", "bo", expanded);

        expanded = loader.expandPattern("ob");
        assertEquals("Correct expansion of pattern", "ob", expanded);

        expanded = loader.expandPattern("2o");
        assertEquals("Correct expansion of pattern", "oo", expanded);

        expanded = loader.expandPattern("3o");
        assertEquals("Correct expansion of pattern", "ooo", expanded);

        expanded = loader.expandPattern("2b");
        assertEquals("Correct expansion of pattern", "bb", expanded);

        expanded = loader.expandPattern("2bo");
        assertEquals("Correct expansion of pattern", "bbo", expanded);

        expanded = loader.expandPattern("b2o");
        assertEquals("Correct expansion of pattern", "boo", expanded);

        expanded = loader.expandPattern("2b2o");
        assertEquals("Correct expansion of pattern", "bboo", expanded);

        expanded = loader.expandPattern("2b 2o");
        assertEquals("Correct expansion of pattern", "bboo", expanded);

        expanded = loader.expandPattern("oo$bb");
        assertEquals("Correct expansion of pattern", "oo\nbb", expanded);

        expanded = loader.expandPattern("2o$o2b");
        assertEquals("Correct expansion of pattern", "oo\nobb", expanded);

        expanded = loader.expandPattern("oo!bb");
        assertEquals("Correct expansion of pattern", "oo", expanded);
    }

    /**
     * Test expansion of invalid patterns.
     */
    @Test
    public void expandInvalidPatterns() {
        RLEFileLoader loader = new RLEFileLoader();
        String[] invalidPatterns = new String[] { // Comments to prevent auto-format
                "bob2", //
        };
        MultiInputExceptionCatcher.execute(invalidPatterns, pattern -> loader.expandPattern(pattern), IllegalArgumentException.class);
    }
}
