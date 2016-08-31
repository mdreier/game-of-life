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
