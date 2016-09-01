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
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import de.martindreier.gameoflife.game.CellState;
import de.martindreier.gameoflife.game.GameRule;
import de.martindreier.gameoflife.game.Grid;
import de.martindreier.gameoflife.game.io.GridLoader;

/**
 * Loader for the Life 1.05 and XLife 2.0 file formats.
 *
 * @author D043987
 *
 */
public class Life105Loader implements GridLoader {

    /**
     * Prefix of the line indicating the file version.
     */
    private static final String LIFE_LINE_PREFIX = "#Life";

    /**
     * Line type: Comment.
     */
    private static final char   LINE_TYPE_COMMENT       = 'D';

    /**
     * Line type: Comment.
     */
    private static final char   LINE_TYPE_XLIFE_COMMENT = 'C';

    /**
     * Line type: Comment.
     */
    private static final char   LINE_TYPE_XLIFE_CREATED = 'O';

    /**
     * Line type: Rule set.
     */
    private static final char   LINE_TYPE_RULE          = 'R';

    /**
     * Line type: Rule set.
     */
    private static final char   LINE_TYPE_CELL_BLOCK    = 'P';

    /**
     * Line type: Rule set.
     */
    private static final char   LINE_TYPE_STANDARD_RULE = 'N';

    /**
     * File comments.
     */
    private StringBuilder       comments         = new StringBuilder();

    /**
     * Game rule defined in the file
     */
    private GameRule            rule;

    /**
     * Cell blocks defined in the file.
     */
    private List<CellBlock>     cellBlocks              = new ArrayList<>();

    /**
     * The cell block being currently parsed.
     */
    private CellBlock           currentBlock;

    /**
     * Constructor for testing only. Creates class instance for method access but does not perform file loading.
     */
    Life105Loader() {
        // Empty constructor
    }

    /**
     * Create a new file loader with a specified file.
     *
     * @throws IOException
     */
    public Life105Loader(Path lifeFile) throws IOException {
        if (!Files.isReadable(lifeFile)) {
            throw new IOException(String.format("Data file %s does not exist or is not readable", lifeFile.toString()));
        }
        try (Stream<String> lines = Files.lines(lifeFile)) {
            String lifeLine = lines.findFirst().orElse("");
            if (!lifeLine.startsWith(LIFE_LINE_PREFIX)) {
                throw new IOException(String.format("File %s is not a valid Life 1.05 file, #Life declaration missing", lifeFile.toString()));
            }
            String version = lifeLine.substring(LIFE_LINE_PREFIX.length()).trim();
            if (!version.equals("1.05")) {
                throw new IOException(String.format("File %s is not a valid Life 1.05 file, unsupported version %s", lifeFile.toString(), version));
            }
            Files.lines(lifeFile).forEach(this::parseLine);
        }
        if (this.cellBlocks.size() == 0) {
            throw new IOException(String.format("File %s does not define any patterns", lifeFile.toString()));
        }
    }

    /**
     * Parse a single line of the life file.
     *
     * @param line
     *            Line content.
     */
    protected void parseLine(String line) {
        if (line.trim().equals("")) {
            // Skip blank lines
            return;
        }
        if (line.startsWith("#")) {
            this.parseHashLine(line);
        } else {
            this.parsePatternLine(line);
        }
    }

    /**
     * Parse a pattern line.
     *
     * @param line
     *            Pattern line.
     */
    protected void parsePatternLine(String line) {
        // If no #P tags are present in the file, define a new block
        if (this.currentBlock == null) {
            this.currentBlock = new CellBlock();
            this.currentBlock.x = 0;
            this.currentBlock.y = 0;
            this.cellBlocks.add(this.currentBlock);
        }
        this.currentBlock.pattern.add(line.trim().toCharArray());
    }

    /**
     * Parse a line with a hash.
     *
     * @param line
     *            Line content.
     */
    protected void parseHashLine(String line) {
        if (line.startsWith(LIFE_LINE_PREFIX)) {
            // Line was already parsed
            return;
        }
        char lineType = line.charAt(1);
        switch (lineType) {
            case LINE_TYPE_COMMENT:
            case LINE_TYPE_XLIFE_COMMENT:
            case LINE_TYPE_XLIFE_CREATED:
                if (line.length() < 3) {
                    this.comments.append("\n");
                }
                this.comments.append(line.substring(2).trim()).append("\n");
                break;
            case LINE_TYPE_STANDARD_RULE:
                this.rule = GameRule.CONWAY;
                break;
            case LINE_TYPE_RULE:
                if (line.length() < 3) {
                    throw new IllegalArgumentException("Line starts with #R but is missing rules definition");
                }
                String ruleDefinition = line.substring(3);
                String[] ruleParts = ruleDefinition.split("/");
                if (ruleParts.length != 2) {
                    throw new IllegalArgumentException(String.format("Invalid rule definition: %s", ruleDefinition));
                }
                this.rule = GameRule.createRule(ruleParts[0].trim(), ruleParts[1].trim());
                break;
            case LINE_TYPE_CELL_BLOCK:
                this.currentBlock = new CellBlock();
                this.cellBlocks.add(this.currentBlock);
                String[] parts = line.split(" ");
                if (parts.length == 3) {
                    try {
                        this.currentBlock.x = Integer.parseInt(parts[1].trim());
                        this.currentBlock.y = Integer.parseInt(parts[2].trim());
                    }
                    catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid definition of pattern position: " + line, e);
                    }
                } else {
                    throw new IllegalArgumentException("Cell block must define X and Y coordinates for pattern");
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid line type: " + lineType);
        }
    }

    /**
     * @see de.martindreier.gameoflife.game.io.GridLoader#setInitialState(de.martindreier.gameoflife.game.Grid)
     */
    @Override
    public void setInitialState(Grid grid) {
        CellBlock block = this.cellBlocks.get(0);
        for (int row = 0; row < block.pattern.size(); row++) {
            char[] patternRow = block.pattern.get(row);
            for (int column = 0; column < patternRow.length; column++) {
                if (patternRow[column] == '.') {
                    grid.set(column, row, CellState.DEAD);
                } else if (patternRow[column] == '*') {
                    grid.set(column, row, CellState.ALIVE);
                } else {
                    throw new IllegalArgumentException("Pattern contains invalid character: " + patternRow[column]);
                }
            }
        }
    }

    /**
     * Gets width of first cell block.
     *
     * @see de.martindreier.gameoflife.game.io.GridLoader#getWidth()
     */
    @Override
    public int getWidth() {
        if (this.cellBlocks.size() == 0) {
            return 0;
        }
        return this.cellBlocks.get(0).pattern.stream().mapToInt(line -> line.length).max().orElse(0);
    }

    /**
     * Gets the height of the first cell block.
     *
     * @see de.martindreier.gameoflife.game.io.GridLoader#getHeight()
     */
    @Override
    public int getHeight() {
        if (this.cellBlocks.size() == 0) {
            return 0;
        }
        return this.cellBlocks.get(0).pattern.size();
    }

    /**
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
     *
     * @return
     */
    public String getComment() {
        return this.comments.toString().trim();
    }

    /**
     * Get the cell blocks defined in the file.
     *
     * @return List of cell blocks.
     */
    public List<CellBlock> getCellBlocks() {
        return Collections.unmodifiableList(this.cellBlocks);
    }

    /**
     * Class representing a single cell block. Includes position and pattern.
     *
     * @author D043987
     *
     */
    public class CellBlock {
        /**
         * Get the horizontal position of the pattern.
         *
         * @return the x
         */
        public int getX() {
            return this.x;
        }

        /**
         * Get the vertical position of the pattern.
         *
         * @return the y
         */
        public int getY() {
            return this.y;
        }

        /**
         * The unparsed pattern.
         *
         * @return the pattern
         */
        List<char[]> getPattern() {
            return this.pattern;
        }

        /**
         * Horizontal position of pattern.
         */
        int                 x;

        /**
         * Vertical position of pattern.
         */
        int                 y;

        /**
         * Pattern.
         */
        public List<char[]> pattern = new LinkedList<>();
    }
}
