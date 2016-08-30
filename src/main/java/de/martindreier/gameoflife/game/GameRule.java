/**
 * Copyright &copy; 2016, SAP SE.
 * All rights reserved.
 */
package de.martindreier.gameoflife.game;

import java.util.Arrays;
import java.util.List;

/**
 * This functional interface describes a ruleset for Game of Life.
 *
 * @author D043987
 *
 */
@FunctionalInterface
public interface GameRule {

    /**
     * Calculate the state of a cell based on the current number of neighbors which are alive.
     *
     * @param neighborsAlive
     *            Number of neighbors which are currently alive.
     * @return New cell state.
     */
    public CellState getNewState(CellState currentState, int neighborsAlive);

    /**
     * Create a new game rule object.
     *
     * @param keepAlive
     *            Number of alive neighbors which allow a cell to stay alive.
     * @param birth
     *            Number of alive neighbors required for a dead cell to be reborn.
     * @return Game rule object.
     */
    public static GameRule createRule(Integer[] keepAlive, Integer[] birth) {
        List<Integer> keepAliveList = Arrays.asList(keepAlive);
        List<Integer> birthList = Arrays.asList(birth);
        return (currentState, neighborsAlive) -> {
            switch (currentState) {
                case ALIVE:
                    return keepAliveList.contains(neighborsAlive) ? CellState.ALIVE : CellState.DEAD;
                case DEAD:
                    return birthList.contains(neighborsAlive) ? CellState.ALIVE : CellState.DEAD;
                default:
                    return currentState;
            }
        };
    }

    /**
     * Create a new game rule object. The parameters are passed as a sequence of integers, i.e. <code>"23"</code> for
     * <code>{2, 3}</code>.
     *
     * @param keepAlive
     *            Number of alive neighbors which allow a cell to stay alive.
     * @param birth
     *            Number of alive neighbors required for a dead cell to be reborn.
     * @return Game rule object.
     */
    public static GameRule createRule(String keepAlive, String birth) {
        return createRule(parse(keepAlive), parse(birth));
    }

    /**
     * Parse a sequence of integers into an {@link Integer} array. For example <code>23</code> to <code>{2, 3}</code>.
     *
     * @param argument
     *            Sequence of integers.
     * @return Array of integers.
     * @throws IllegalArgumentException
     *             If any of the characters in the input sequence is not numeric.
     */
    public static Integer[] parse(String argument) {
        // Null arguments become empty arrays
        if (argument == null) {
            return new Integer[0];
        }
        argument = argument.trim();

        // Convert each character
        Integer[] out = new Integer[argument.length()];
        for (int index = 0; index < argument.length(); index++) {
            char currentChar = argument.charAt(index);
            try {
                Integer value = Integer.valueOf(Character.toString(currentChar));
                out[index] = value;
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException(String.format("Argument %s contains non-numeric characters", argument));
            }
        }
        return out;
    }
}
