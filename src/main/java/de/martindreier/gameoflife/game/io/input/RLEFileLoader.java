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
import java.util.Arrays;
import java.util.Optional;

import de.martindreier.gameoflife.game.CellState;
import de.martindreier.gameoflife.game.GameRule;
import de.martindreier.gameoflife.game.Grid;
import de.martindreier.gameoflife.game.io.GridLoader;

/**
 * A loader for grids stored in a Run-Length Encoded (RLE) file.
 *
 * @author D043987
 *
 */
public class RLEFileLoader implements GridLoader {

    /**
     * Hash line type: comment.
     */
    private static final String LINE_TYPE_COMMENT               = "C";
    /**
     * Hash line type: comment.
     */
    private static final String LINE_TYPE_NAME                  = "N";
    /**
     * Hash line type: comment.
     */
    private static final String LINE_TYPE_CREATION              = "O";
    /**
     * Hash line type: comment.
     */
    private static final String LINE_TYPE_COORDINATES_TOP_LEFT  = "P";
    /**
     * Hash line type: comment.
     */
    private static final String LINE_TYPE_COORDINATES_PLACEMENT = "R";
    /**
     * Hash line type: comment.
     */
    private static final String LINE_TYPE_RULES                 = "r";

    /**
     * Header line has been parsed.
     */
    private boolean headerParsed = false;

    /**
     * Parser has reached the end of the pattern.
     */
    private boolean             endOfPatternReached             = false;

    /**
     * Holder for comment lines.
     */
    private StringBuilder comments     = new StringBuilder();

    /**
     * Holder for pattern.
     */
    private StringBuilder pattern      = new StringBuilder();

    /**
     * Grid width (X dimension).
     */
    private int           width;

    /**
     * Grid height (Y dimension).
     */
    private int           height;

    /**
     * Game rule set in the pattern.
     */
    private GameRule      rule;

    /**
     * Name of the pattern;
     */
    private String              patternName;

    /**
     * Constructor for testing only. Creates class instance for method access but does not perform file loading.
     */
    RLEFileLoader() {
        // Empty constructor
    }

    /**
     * Create a new file loader with a specified file.
     *
     * @throws IOException
     */
    public RLEFileLoader(Path rleFile) throws IOException {
        if (!Files.isReadable(rleFile)) {
            throw new IOException(String.format("Data file %s does not exist or is not readable", rleFile.toString()));
        }
        Files.lines(rleFile).forEach(this::parseLine);
        if (!this.headerParsed) {
            throw new IOException(String.format("Data file %s does not contain a header line"));
        }
    }

    /**
     * Parse a single line in the data file.
     *
     * @param line
     *            The line content
     * @throws IOException
     */
    protected void parseLine(String line) {
        if (line.startsWith("#")) {
            this.parseHashLine(line);
        } else if (!this.headerParsed) {
            this.parseHeader(line);
        } else if (this.endOfPatternReached) {
            this.appendComment(null, line);
        } else {
            int bangIndex = line.indexOf('!');
            if (bangIndex >= 0) {
                this.endOfPatternReached = true;
                if (line.length() >= bangIndex) {
                    this.appendComment(null, line.substring(bangIndex + 1));
                }
            }
            this.pattern.append(line);
        }
    }

    /**
     * Parse a line starting with a hash.
     *
     * @param line
     *            Line content.
     */
    protected void parseHashLine(String line) {
        if (line.length() < 2) {
            // Empty line
            return;
        }
        String lineType = line.substring(1, 2);
        String lineContent = "";
        if (line.length() > 2) {
            lineContent = line.substring(2).trim();
        }
        if (lineType.equalsIgnoreCase(LINE_TYPE_COMMENT)) {
            this.appendComment(null, lineContent);
        } else if (lineType.equals(LINE_TYPE_NAME)) {
            this.appendComment("Pattern Name", lineContent);
            this.patternName = lineContent;
        } else if (lineType.equals(LINE_TYPE_CREATION)) {
            this.appendComment("Created", lineContent);
        } else if (lineType.equals(LINE_TYPE_COORDINATES_PLACEMENT) || lineType.equals(LINE_TYPE_COORDINATES_TOP_LEFT)) {
            // Currently not supported
        } else if (lineType.equals(LINE_TYPE_RULES)) {
            int separatorIndex = lineContent.indexOf('/');
            if (separatorIndex <= 0 || separatorIndex == lineContent.length() - 1) {
                throw new IllegalArgumentException(String.format("Illegal rule pattern in line: %s", line));
            }
            this.rule = GameRule.createRule(lineContent.substring(0, separatorIndex).trim(), lineContent.substring(separatorIndex + 1).trim());
        }
    }

    /**
     * Append a line to the comments. The comment will be prefixed with the label and a colon as separator if one is
     * supplied.
     *
     * @param label
     *            Label for the line. May be <code>null</code>.
     * @param comment
     *            The comment line.
     */
    private void appendComment(String label, String comment) {
        if (comment == null) {
            return;
        }
        if (this.comments.length() > 0) {
            this.comments.append("\n");
        }
        if (label != null) {
            this.comments.append(label).append(": ");
        }
        this.comments.append(comment);
    }

    /**
     * Parse the header line. The header line is expected to have one of these formats:
     * <p>
     * <code>x = m, y = n</code>
     * </p>
     * <p>
     * or
     * </p>
     * <p>
     * <code>x = m, y = n, rule = abc</code>
     * </p>
     *
     * @param line
     *            Header line.
     */
    protected void parseHeader(String line) {
        if (line == null) {
            throw new IllegalArgumentException("Header line must not be null");
        }
        // Split line into comma-separated segments
        String[] segments = line.trim().split(",");
        if (segments.length < 2 || segments.length > 3) {
            throw new IllegalArgumentException(String.format("Illegal header line format: %s", line));
        }
        // Parse grid size settings
        for (int index = 0; index < 2; index++) {
            String[] parts = segments[index].trim().split("=");
            if (parts.length != 2) {
                throw new IllegalArgumentException(String.format("Illegal header line format (dimensions): %s", line));
            }
            int length = -1;
            try {
                length = Integer.valueOf(parts[1].trim());
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException(String.format("Illegal header line format (dimensions): %s", line), e);
            }
            String dimension = parts[0].trim().toLowerCase();
            if (dimension.equals("x")) {
                this.width = length;
            } else if (dimension.equals("y")) {
                this.height = length;
            } else {
                throw new IllegalArgumentException(String.format("Illegal header line format (dimensions): %s", line));
            }
        }
        // Parse game rule
        if (segments.length == 3) {
            int equalsIndex = segments[2].indexOf("=");
            if ((!segments[2].trim().toLowerCase().startsWith("rule")) || equalsIndex <= 0) {
                throw new IllegalArgumentException(String.format("Illegal header line format (rule): %s", line));
            }
            String ruleDefinition = segments[2].substring(equalsIndex + 1);
            String[] parts = ruleDefinition.split("/");
            if (parts.length != 2) {
                throw new IllegalArgumentException(String.format("Illegal header line format (rule): %s", line));
            }
            String born = null;
            String survive = null;
            for (String part : parts) {
                part = part.trim().toUpperCase();
                if (part.startsWith("B")) {
                    born = part.substring(1);
                } else if (part.startsWith("S")) {
                    survive = part.substring(1);
                } else {
                    throw new IllegalArgumentException(String.format("Illegal header line format (rule): %s", line));
                }
                this.rule = GameRule.createRule(survive, born);
            }
        }
        this.headerParsed = true;
    }

    /**
     * @see de.martindreier.gameoflife.game.io.GridLoader#setInitialState(de.martindreier.gameoflife.game.Grid)
     */
    @Override
    public void setInitialState(Grid grid) {
        String[] lines = this.expandPattern(this.pattern.toString()).split("\\n");
        // remove any remaining spaces
        for (int index = 0; index < lines.length; index++) {
            lines[index] = lines[index].trim();
        }
        for (int row = 0; row < this.height; row++) {
            if (row >= lines.length) {
                // Unspecified rows remain dead
                continue;
            }
            for (int column = 0; column < this.width; column++) {
                if (column >= lines[row].trim().length()) {
                    // Unspecified cells at the end of rows remain dead
                    continue;
                }
                char cell = lines[row].charAt(column);
                if (cell == 'b') {
                    grid.set(column, row, CellState.DEAD);
                } else if (cell == 'o') {
                    grid.set(column, row, CellState.ALIVE);
                } else {
                    throw new IllegalArgumentException(String.format("Illegal character %s in pattern", cell));
                }
            }
        }
    }

    /**
     * @param string
     * @return
     */
    protected String expandPattern(String pattern) {
        StringBuffer expandedPattern = new StringBuffer();
        for (int index = 0; index < pattern.length(); index++) {
            char currentChar = pattern.charAt(index);
            if (Character.isWhitespace(currentChar)) {
                // Skip whitespaces
                continue;
            } else if (Character.isDigit(currentChar)) {
                // Combination number + character
                // This also accepts non-arabic numbers, but we can live with that for now
                int count = Integer.parseInt(Character.toString(currentChar));
                if (index == pattern.length() - 1) {
                    // Last character cannot be a number
                    throw new IllegalArgumentException(String.format("Invalid pattern: %s", pattern));
                }
                char nextChar = pattern.charAt(++index);
                char[] filler = new char[count];
                Arrays.fill(filler, nextChar);
                expandedPattern.append(filler);
            } else if (currentChar == '$') {
                // End of line
                expandedPattern.append("\n");
            } else if (currentChar == '!') {
                // End of pattern
                break;
            } else {
                expandedPattern.append(currentChar);
            }
        }
        return expandedPattern.toString();
    }

    /**
     * @see de.martindreier.gameoflife.game.io.GridLoader#getWidth()
     */
    @Override
    public int getWidth() {
        return this.width;
    }

    /**
     * @see de.martindreier.gameoflife.game.io.GridLoader#getHeight()
     */
    @Override
    public int getHeight() {
        return this.height;
    }

    /**
     * Get the game rule as specified in the file. If none is specified, returns the rule for Conway's Game of Life
     * (B3/S23).
     *
     * @see de.martindreier.gameoflife.game.io.GridLoader#getGameRule()
     */
    @Override
    public Optional<GameRule> getGameRule() {
        if (this.rule == null) {
            return Optional.of(GameRule.CONWAY);
        } else {
            return Optional.of(this.rule);
        }
    }

    /**
     * Get the name of this pattern.
     *
     * @return the pattern name.
     */
    public String getPatternName() {
        return this.patternName;
    }

    /**
     * Get the comments from the file.
     *
     * @return Comments. May be an empty {@link String}, but never <code>null</code>.
     */
    public String getComment() {
        return this.comments.toString();
    }

}
