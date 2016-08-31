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
package de.martindreier.gameoflife.test.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import junit.framework.AssertionFailedError;

/**
 * A test utility which calls a piece of code for a list of inputs and checks that an exception is thrown.
 *
 * @author D043987
 *
 */
public class MultiInputExceptionCatcher {

    /**
     * Call handler with a list of inputs.
     *
     * @param inputs
     *            List of inputs.
     * @param handler
     *            Handler to be called for each input.
     * @param expectedExceptions
     *            Exceptions which are expected to be thrown.
     */
    @SafeVarargs
    public static void execute(Collection<String> inputs, Consumer<String> handler, Class<? extends Throwable>... expectedExceptions) {
        if (expectedExceptions == null || expectedExceptions.length == 0) {
            throw new IllegalArgumentException("At least one expected exception must be specified");
        }
        List<Class<? extends Throwable>> exceptions = Arrays.asList(expectedExceptions);
        for (String input : inputs) {
            boolean exceptionThrown = false;
            try {
                handler.accept(input);
            }
            catch (Exception e) {
                if (exceptions.contains(e.getClass())) {
                    exceptionThrown = true;
                } else {
                    throw new AssertionFailedError(String.format("Unexpected exception %s thrown: %s", e.getClass().getName(), e.getMessage()));
                }
            }
            if (!exceptionThrown) {
                throw new AssertionFailedError(String.format("Expected exception(s) not thrown for input %s", input));
            }
        }
    }

    /**
     * Call handler with a list of inputs.
     *
     * @param inputs
     *            List of inputs.
     * @param handler
     *            Handler to be called for each input.
     * @param expectedExceptions
     *            Exceptions which are expected to be thrown.
     */
    @SafeVarargs
    public static void execute(String[] inputs, Consumer<String> handler, Class<? extends Throwable>... expectedExceptions) {
        execute(Arrays.asList(inputs), handler, expectedExceptions);
    }
}
