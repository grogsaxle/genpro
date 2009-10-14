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

import junit.framework.Assert;
import junit.framework.TestCase;
import nl.bluevoid.genpro.Grid;
import nl.bluevoid.genpro.JavaGenerator;
import nl.bluevoid.genpro.Setup;
import nl.bluevoid.genpro.cell.LibraryCell;
import nl.bluevoid.genpro.cell.ValueCell;
import nl.bluevoid.genpro.operations.BooleanOperations;
import nl.bluevoid.genpro.operations.NumberOperations;

public class CalcTest extends TestCase {

  public void testMiniWire() throws Exception {
    // setup
    Setup setup=new Setup("testMiniWire");
    setup.addInputCell("i1", Integer.class);
    setup.addInputCell("i2", Integer.class);
    setup.addOutputCell("out1", Integer.class);
    setup.setCallCells(1, "c", new Class[]{Integer.class});
    LibraryCell fCell = new LibraryCell(Math.class);
    LibraryCell nCell = new LibraryCell(NumberOperations.class);
    LibraryCell bCell = new LibraryCell(BooleanOperations.class);

    setup.setLibraryCells(new LibraryCell[] { fCell, nCell, bCell });
    
    Grid grid=setup.generateSolution();
    
    System.out.println("Start grid:");
    grid.printSolution();
    System.out.println("\n");
    long start = System.currentTimeMillis();
    // search
    int count = 0;
    while (count++ < 200) {
      grid.createSolution();
      ValueCell oCell=grid.getOutputCell("out1");
      //grid.printSolution();
      // we try to find Math.max
      grid.getInputCell("i1").setValue(12);
      grid.getInputCell("i2").setValue(30);
      grid.calc();
      if (oCell.getValue().toString().equals("360")) {
        // first testcase passed, try the second testcase
        grid.getInputCell("i1").setValue(new Integer(45));
        grid.getInputCell("i2").setValue(new Integer(20));
        grid.calc();
        if (oCell.getValue().toString().equals("900")) {
          // we found a solution!!!
          break;
        }
      }
    }

    long time = System.currentTimeMillis() - start;
    Assert.assertTrue(count < 200);
    System.out.println("\n\nSolution grid:");
    grid.printSolution();
    System.out.println("found solution in " + count + " tries, time:" + time + " millis, " + time / count
        + " millis per try");
    JavaGenerator program = new JavaGenerator();
    String progString = program.getProgram("FindMax", "nl.robbio.gp", grid, false);
    System.out.println("\n\nProgram:\n" + progString);
  }
}
