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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import junit.framework.AssertionFailedError;

/**
 * A test utility which calls a piece of code for a list of inputs and checks that an exception is thrown.
 *
 * @author D043987
 *
 */
@RunWith(JUnit4.class)
public class MultiInputExceptionCatcherTest {

    private static final String[] inputs = new String[] { "a", "b", "c" };

    /**
     * Test that expected exceptions do not lead to test failures.
     */
    @Test
    public void expectedExceptionsThrown() {
        try {
            MultiInputExceptionCatcher.execute(inputs, c -> ((String) null).length(), NullPointerException.class);
        }
        catch (AssertionFailedError e) {
            fail("Expected exception caused assertion failure");
        }
    }

    /**
     * Test that missing expected exceptions cause assertion failure.
     */
    @Test
    public void expectedExceptionsNotThrown() {
        try {
            MultiInputExceptionCatcher.execute(inputs, c -> c.length(), NullPointerException.class);
            fail("Expected exception caused assertion failure");
        }
        catch (AssertionFailedError e) {
            assertTrue("Missing expected exception caused assertion error", true);
        }
    }

    /**
     * Test that unexpected exceptions cause assertion failure.
     */
    @Test
    public void unexpectedExceptionsThrown() {
        try {
            MultiInputExceptionCatcher.execute(inputs, c -> ((String) null).length(), IllegalArgumentException.class);
            fail("Unexpected exception caused assertion failure");
        }
        catch (AssertionFailedError e) {
            assertTrue("Unexpected exception caused assertion error", true);
        }
    }

    /**
     * Test that an exception class is required.
     */
    @Test(expected = IllegalArgumentException.class)
    public void ExceptionClassRequired() {
        MultiInputExceptionCatcher.execute(inputs, c -> c.length());
    }
}
