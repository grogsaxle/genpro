/*
 * Copyright 2002-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
/**
 * @author Rob van der Veer
 * @since 1.0
 */
public class CalcTest2 extends TestCase {

  public void testMiniWire() throws Exception {
    // setup
    Setup setup=new Setup("CalcTest2");
    setup.addInputCell("i1", Integer.class);
    setup.addInputCell("i2", Integer.class);
    setup.addOutputCell("out1", Integer.class);
    setup.addOutputCell("out2", Boolean.class);
    
    //hoe Calculables: IfCell, forEachCell en SubroutineCell toevoegen?
        
    setup.setCallCells(5, "c", new Class[]{Integer.class, Boolean.class});
    //setup.addIfCell(1);
    
    LibraryCell fCell = new LibraryCell(Math.class);
    LibraryCell nCell = new LibraryCell(NumberOperations.class);
    LibraryCell bCell = new LibraryCell(BooleanOperations.class);

    setup.setLibraryCells(new LibraryCell[] { fCell, nCell, bCell });
    //setup.setLibraryCells(new LibraryCell[] { nCell, bCell });
    
    Grid grid=setup.generateSolution();
    
    System.out.println("Start grid:");
    grid.printSolution();
    JavaGenerator.printJavaProgram(grid, "calctest2","nl.bluevoid.gp", true);
    System.out.println("\n");
    long start = System.currentTimeMillis();
    // search
    int count = 0;
    while (count++ < 2000) {
      grid.createSolution();
      ValueCell oCell=grid.getOutputCell("out1");
      //grid.printSolution();
      // we try to find Math.max
      grid.getInputCell("i1").setValue(12);
      grid.getInputCell("i2").setValue(30);
      grid.calc();
      if (oCell.getValue().toString().equals("30")) {
        // first testcase passed, try the second testcase
        grid.getInputCell("i1").setValue(new Integer(45));
        grid.getInputCell("i2").setValue(new Integer(20));
        grid.calc();
        if (oCell.getValue().toString().equals("45")) {
          // we found a solution!!!
          break;
        }
      }
    }

    long time = System.currentTimeMillis() - start;
    Assert.assertTrue(count < 2000);
    System.out.println("\n\nSolution grid:");
    grid.printSolution();
    System.out.println("found solution in " + count + " tries, time:" + time + " millis, " + time / count
        + " millis per try");
    JavaGenerator program = new JavaGenerator();
    String progString = program.getProgram("FindMax", "nl.robbio.gp", grid, false);
    System.out.println("\n\nProgram:\n" + progString);
  }
}
