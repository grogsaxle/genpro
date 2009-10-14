/*
 * This file is part of GenPro, Reflective Object Oriented Genetic Programming.
 *
 * GenPro offers a dual license model containing the GPL (GNU General Public License) version 2  
 * as well as a commercial license.
 *
 * For licensing information please see the file license.txt included with GenPro
 * or have a look at the top of class nl.bluevoid.genpro.cell.Cell which representatively
 * includes the GenPro license policy applicable for any file delivered with GenPro.
 */

package nl.bluevoid.genpro.test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import junit.framework.Assert;
import junit.framework.TestCase;
import nl.bluevoid.genpro.Generation;
import nl.bluevoid.genpro.GenerationRunner;
import nl.bluevoid.genpro.Grid;
import nl.bluevoid.genpro.JavaGenerator;
import nl.bluevoid.genpro.Setup;
import nl.bluevoid.genpro.TestSet;
import nl.bluevoid.genpro.cell.Calculable;
import nl.bluevoid.genpro.cell.Cell;
import nl.bluevoid.genpro.cell.CellInterface;
import nl.bluevoid.genpro.cell.ConstantCell;
import nl.bluevoid.genpro.cell.LibraryCell;
import nl.bluevoid.genpro.cell.ReferenceCell;
import nl.bluevoid.genpro.operations.NumberOperations;
import nl.bluevoid.genpro.util.Calc;

public class TemperatureTest extends TestCase {

  private TestSet testSet;
  private Setup setup;

  // Tested at august 19: 500 generations: 56-58 ms
  // idem at 3000 generations 153 ms (after speedup)

  // @Override
  protected void setUp() throws Exception {
    setup = new Setup("TemperatureTest");
    setup.addInputCell("fahrenheit", Double.class);
    setup.addOutputCell("celcius", Double.class);
    setup.setCallCells(5, "c", new Class[] { Double.class });
    setup.setGenerationSize(3000);
    setup.setMutatePercentage(10);
    setup.setMaxIndividualsWithSameScore(30);
    setup.setEvaluateMultiThreaded(false);

    ConstantCell cCell = new ConstantCell("const1", Double.class, -100, 100);
    ConstantCell cCell2 = new ConstantCell("const2", Double.class, -100, 100);
    ConstantCell cCell3 = new ConstantCell("const3", Double.class, -100, 100);
    setup.setConstantCells(new ConstantCell[] { cCell, cCell2, cCell3 });

    setup.setLibraryCells(new LibraryCell[] { NumberOperations.NUM_OPS });// , NumberOperations.MATH_CLASS });

    testSet = new TestSet(setup, new String[] { "fahrenheit", "celcius" }) {
      public double scoreOutput(ReferenceCell cell, Object calculated, Object expected) {
        return getAbsoluteNumberDifference((Number)calculated, (Number)expected);
      }

      @Override
      public double scoreGrid(Grid g) {
        return 0;
      }
    };

    testSet.addCellValues(33.8, 1d);
    testSet.addCellValues(39.2, 4d);
    testSet.addCellValues(64.4, 18d);
    testSet.addCellValues(71.6, 22d);
    testSet.addCellValues(98.60000000000001, 37d);
    testSet.addCellValues(113d, 45d);
    testSet.addCellValues(212d, 100d);
    testSet.addCellValues(392d, 200d);
    testSet.addCellValues(752d, 400d);
    // testSet.addCellValues(new Double[] { 0.0, -17.77777777777778 });
    testSet.addCellValues(32d, 0.0);
  }

  public static void main(String[] args) throws Exception {
    TemperatureTest t = new TemperatureTest();
    t.setUp();
    t.runGenerations();
  }

  public void detestCrossing() {
    // generate solution
    Grid grid = setup.generateSolution();
    // evaluate

    System.out.println("evaluate original ");
    double score = testSet.evaluate(grid);

    grid.setScore(score);
    JavaGenerator.printJavaProgram(grid, "s1", "nl.bluevoid.gp", true);
    grid.printSolution();

    // cross with him self: should generate same solution!! and same score
    Grid[] children = grid.cross(grid);
    System.out.println("evaluate child 0 ");
    double score2 = testSet.evaluate(children[0]);
    System.out.println("evaluate child 1 ");
    double score3 = testSet.evaluate(children[1]);

    JavaGenerator.printJavaProgram(children[0], "child 0", "nl.bluevoid.gp", true);
    JavaGenerator.printJavaProgram(children[1], "child 1", "nl.bluevoid.gp", true);

    System.out.println("child 0");
    children[0].printSolution();
    System.out.println("child 1");
    children[1].printSolution();

    System.out.println("score " + score);
    System.out.println("score2 child 0 " + score2);
    System.out.println("score3 child 1 " + score3);

    if (!Calc.isNaNorInfinite(score))
      Assert.assertTrue(Math.abs(score - score2) < 0.001);
    // showGrid(grid);
    if (!Calc.isNaNorInfinite(score))
      Assert.assertTrue(Math.abs(score - score3) < 0.001);

    double scoreB = testSet.evaluate(grid);
    Assert.assertEquals(score, scoreB);
  }

  public void detestStripGrid() {
    Grid grid = setup.generateSolution();
    double score = testSet.evaluate(grid);
    JavaGenerator.printJavaProgram(grid, "", "nl.bluevoid.gp", true);
    Collection<Cell> cells = grid.getUsedCells();
    System.out.println("Used cells:");
    for (CellInterface cell : cells) {
      System.out.println("   " + cell);
    }

    grid.stripUnusedCells();
    JavaGenerator.printJavaProgram(grid, "", "nl.bluevoid.gp", true);
    double score2 = testSet.evaluate(grid);
    Assert.assertEquals(score, score2, 0.0001);
  }

  public void detestMutate() {
    System.out.println("testMutate============================================");

    Grid grid = setup.generateSolution();
    grid.stripUnusedCells();
    JavaGenerator.printJavaProgram(grid, "", "nl.bluevoid.gp", true);
    double score = testSet.evaluate(grid);
    boolean s1Invalid = Calc.isNaNorInfinite(score);
    grid.mutate();
    JavaGenerator.printJavaProgram(grid, "", "nl.bluevoid.gp", true);
    System.out.println("getMutatedConstants: " + grid.getMutatedConstants());
    System.out.println("getMutatedParams: " + grid.getMutatedParams());
    System.out.println("getMutatedMethods: " + grid.getMutatedMethods());
    double score2 = testSet.evaluate(grid);
    boolean s2Invalid = Calc.isNaNorInfinite(score2);
    if (s1Invalid && s2Invalid) {
      // can not say anything
      System.out.println("s1Invalid && s2Invalid");
    } else if ((s1Invalid && !s2Invalid) || (!s1Invalid && s2Invalid)) {
      // changed from invalid to valid, test succeded
      System.out.println("(s1Invalid && !s2Invalid) || (!s1Invalid && s2Invalid)");
    } else {// both are valid: we expect a change in result ~ TODO wrong assumption!!!
      Assert.assertTrue(Math.abs(score2 - score) > 0.000001);
    }
  }

  public void runGenerations() throws IllegalAccessException, InvocationTargetException, IOException {
    GenerationRunner gr = new GenerationRunner(setup, testSet);
    gr.runGenerations();
  }

  public void testMultiThread() throws IllegalAccessException, InvocationTargetException, IOException {
    System.out.println("testMultiThread");
    // Test to see if all in a generation is calculated correct when doing multithreaded calculation
    GenerationRunner gr = new GenerationRunner(setup, testSet);

    // create and evaluated multithreaded

    Generation gen = gr.getCurGen();
    // evaluate singlethreaded
    checkScore(gen);

    Generation gen2 = gen.next();
    gr.evaluateGeneration(gen2);
    checkScore(gen2);

    Generation gen3 = gen2.next();
    gr.evaluateGeneration(gen3);
    checkScore(gen3);
  }

  private void checkScore(Generation gen) {
    System.out.println("checkScore");
    int i = 0;
    for (Grid g : gen.getSolutions()) {
      double score = testSet.evaluate(g);

      if (Math.abs(score - g.getScore()) > 0.001) {
        System.out.println("i " + i + " calculatedBy " + g.calculatedBy);
        JavaGenerator.printJavaProgram(g, "fail", "nl.bluevoid.gp", true);
        for (int j = 0; j < g.calcResults.length; j++) {
          System.out.println("new, old " + g.calcResults[j] + " " + g.oldCalcResults[j]);
        }

        checkConsts(gen, g);
        checkCallCells(gen, g);
      }
      assertEquals(g.getScore(), score, 0.001);
      i++;
    }
  }

  private void checkConsts(Generation gen, Grid g) {
    for (ConstantCell cc : g.getConstantCells()) {
      for (Grid g2 : gen.getSolutions()) {
        if (g != g2) {
          for (ConstantCell constantCell : g2.getConstantCells()) {
            if (constantCell == cc) {
              System.out.println("duplicate ConstantCell found");
            }
          }
        }
      }
    }
  }

  private void checkCallCells(Generation gen, Grid g) {
    for (Calculable cc : g.getCallCells()) {
      for (Grid g2 : gen.getSolutions()) {
        if (g != g2) {
          for (Calculable constantCell : g2.getCallCells()) {
            if (constantCell == cc) {
              System.out.println("duplicate CallCell found");
            }
          }
        }
      }
    }
  }
}
