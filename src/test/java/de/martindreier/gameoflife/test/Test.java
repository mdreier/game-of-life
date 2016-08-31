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
package de.martindreier.gameoflife.test;
import de.martindreier.gameoflife.game.GameRule;
import de.martindreier.gameoflife.game.Grid;
import de.martindreier.gameoflife.game.io.GridLoader;
import de.martindreier.gameoflife.game.io.GridPrinter;
import de.martindreier.gameoflife.game.io.output.StandardOutGridPrinter;

/**
 * Copyright &copy; 2016, SAP SE.
 * All rights reserved.
 */

/**
 * Small test program.
 *
 * @author D043987
 *
 */
public class Test {

    /**
     * @param args
     */
    public static void main(String[] args) {
        GridLoader loader = new FixedGrid();
        GridPrinter printer = new StandardOutGridPrinter();
        GameRule rule = GameRule.createRule(new Integer[] { 2, 3 }, new Integer[] { 3 });

        Grid grid = new Grid(loader);
        printer.printGrid(grid);

        grid.iterate(rule);
        printer.printGrid(grid);
    }

}
