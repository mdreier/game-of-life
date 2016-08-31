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
package de.martindreier.gameoflife.game;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests for the GameRule class.
 *
 * @author D043987
 *
 */
@RunWith(JUnit4.class)
public class GameRuleTest {

    /**
     * Standard 23/3 rule set.
     */
    private GameRule standardRule;

    /**
     * Create a game rule representing the default 23/3 rule.
     */
    @Before
    public void createStandardGameRule() {
        this.standardRule = GameRule.createRule(new Integer[] { 2, 3 }, new Integer[] { 3 });
    }

    /**
     * Test that {@link String} arguments to the game rule creation method are parsed correctly.
     */
    @Test()
    public void argumentParsing() {
        // Space is in there on purpose, should not cause an exception
        Integer[] parsed = GameRule.parse("23 ");
        assertEquals("Correct number of elements in result", 2, parsed.length);
        assertEquals("First element parsed correctly", new Integer(2), parsed[0]);
        assertEquals("Second element parsed correctly", new Integer(3), parsed[1]);

    }

    /**
     * Test that {@link String} arguments to the game rule creation method are parsed correctly.
     */
    @Test()
    public void argumentParsingWithEmptyInput() {
        Integer[] parsed = GameRule.parse("");
        assertEquals("Correct number of elements in result", 0, parsed.length);

        parsed = GameRule.parse(null);
        assertEquals("Correct number of elements in result", 0, parsed.length);
    }

    /**
     * Test that {@link String} arguments to the game rule creation method are parsed correctly.
     */
    @Test(expected = IllegalArgumentException.class)
    public void argumentParsingWithIllegalCharacters() {
        GameRule.parse("ABC");
    }

    /**
     * Test that dead cells without exactly three neighbors stay dead
     */
    @Test
    public void deadCellsStayDead() {
        CellState nextState;

        nextState = this.standardRule.getNewState(CellState.DEAD, Integer.MIN_VALUE);
        assertEquals("Dead cell still dead", CellState.DEAD, nextState);

        nextState = this.standardRule.getNewState(CellState.DEAD, 0);
        assertEquals("Dead cell still dead", CellState.DEAD, nextState);

        nextState = this.standardRule.getNewState(CellState.DEAD, 1);
        assertEquals("Dead cell still dead", CellState.DEAD, nextState);

        nextState = this.standardRule.getNewState(CellState.DEAD, 2);
        assertEquals("Dead cell still dead", CellState.DEAD, nextState);

        nextState = this.standardRule.getNewState(CellState.DEAD, 4);
        assertEquals("Dead cell still dead", CellState.DEAD, nextState);

        nextState = this.standardRule.getNewState(CellState.DEAD, Integer.MAX_VALUE);
        assertEquals("Dead cell still dead", CellState.DEAD, nextState);
    }

    /**
     * Test that dead cells with exactly three neighbors come alive
     */
    @Test
    public void deadCellsAreReborn() {
        CellState nextState;

        nextState = this.standardRule.getNewState(CellState.DEAD, 3);
        assertEquals("Dead cell still dead", CellState.ALIVE, nextState);
    }

    /**
     * Test that alive cells without exactly two or three neighbors die
     */
    @Test
    public void aliveCellsDie() {
        CellState nextState;

        nextState = this.standardRule.getNewState(CellState.ALIVE, Integer.MIN_VALUE);
        assertEquals("Dead cell still dead", CellState.DEAD, nextState);

        nextState = this.standardRule.getNewState(CellState.ALIVE, 0);
        assertEquals("Dead cell still dead", CellState.DEAD, nextState);

        nextState = this.standardRule.getNewState(CellState.ALIVE, 1);
        assertEquals("Dead cell still dead", CellState.DEAD, nextState);

        nextState = this.standardRule.getNewState(CellState.ALIVE, 4);
        assertEquals("Dead cell still dead", CellState.DEAD, nextState);

        nextState = this.standardRule.getNewState(CellState.ALIVE, Integer.MAX_VALUE);
        assertEquals("Dead cell still dead", CellState.DEAD, nextState);
    }

    /**
     * Test that dead cells without exactly three neighbors stay dead
     */
    @Test
    public void aliveCellsStayAlive() {
        CellState nextState;

        nextState = this.standardRule.getNewState(CellState.ALIVE, 2);
        assertEquals("Dead cell still dead", CellState.ALIVE, nextState);

        nextState = this.standardRule.getNewState(CellState.ALIVE, 3);
        assertEquals("Dead cell still dead", CellState.ALIVE, nextState);

    }
}
