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

import junit.framework.TestCase;
import nl.bluevoid.genpro.Grid;
import nl.bluevoid.genpro.JavaGenerator;
import nl.bluevoid.genpro.Setup;
import nl.bluevoid.genpro.TestSet;
import nl.bluevoid.genpro.cell.CallCell;
import nl.bluevoid.genpro.cell.ConstantCell;
import nl.bluevoid.genpro.cell.ReferenceCell;
import nl.bluevoid.genpro.operations.NumberOperations;
/**
 * @author Rob van der Veer
 * @since 1.0
 */
public class GridTest extends TestCase {

  private Grid grid;
  private TestSet testSet;

  @Override
  protected void setUp() throws Exception {
    Setup setup=new Setup("GridTest");
    setup.addInputCell("fahrenheit", Double.class);
    setup.addOutputCell("celcius", Double.class);
    setup.setCallCells(6, "c",  Double.class );
   
    ConstantCell cCell = new ConstantCell("const1", Double.class, -100, 100);
    ConstantCell cCell2 = new ConstantCell("const2", Double.class, -100, 100);
    ConstantCell cCell3 = new ConstantCell("const3", Double.class, -100, 100);
    setup.setConstantCells( cCell, cCell2, cCell3 );

    
    setup.setLibraryCells( NumberOperations.NUM_OPS);// , NumberOperations.MATH_CLASS);
    
    grid = setup.generateSolution();
    
    // System.out.println("start grid");
    // grid.printSolution();

    testSet = new TestSet(setup, new String[] { "fahrenheit", "celcius" }) {
      public double scoreOutput(ReferenceCell cell, Object calculated, Object expected) {
        return getAbsoluteNumberDifference((Number)calculated, (Number)expected);
      }

      @Override
      public double scoreGrid(Grid g) {
        return 0;
      }
    };
    // testSet.addCellValues(new Double[] { 0.0, -17.77777777777778 });
    // testSet.addCellValues(new Double[] { 32d, 0.0 });
    testSet.addCellValues( 33.8, 1d );
    testSet.addCellValues( 39.2d, 4d );
    testSet.addCellValues(64.4d, 18d );
    testSet.addCellValues( 71.6d, 22d );
    testSet.addCellValues( 98.60000000000001d, 37d );
    testSet.addCellValues( 113d, 45d );
    testSet.addCellValues( 212d, 100d );
    testSet.addCellValues( 392d, 200d );
    testSet.addCellValues( 752d, 400d );
  }

  public void testClone() {
    grid.createSolution();
    JavaGenerator.printJavaProgram(grid, "to clone","nl.bluevoid.gp", true);
    grid.setScore(300);
    Grid clone = grid.clone();
    JavaGenerator.printJavaProgram(clone, "cloned","nl.bluevoid.gp", true);
    assertNotSame(clone.getConstantCells(), grid.getConstantCells());
    assertNotSame(clone.getCallCells(), grid.getCallCells());
    assertNotSame(clone.getInputCells(), grid.getInputCells());
    assertNotSame(clone.getOutputCells(), grid.getOutputCells());
    assertNotSame(clone.getLibraryCells(), grid.getLibraryCells());
    assertNotSame(clone.getOutputCell("celcius"),grid.getOutputCell("celcius") );
    assertNotSame(clone.getOutputCell("celcius").getReferedCell(),grid.getOutputCell("celcius").getReferedCell() );
    
    assertNotSame(clone.getInputCell("fahrenheit"),grid.getInputCell("fahrenheit") );
    assertNotSame(((CallCell)clone.getCallCells()[0]).getParams()[0], ((CallCell)grid.getCallCells()[0]).getParams()[0]);
    
    //clone.getInputCell("fahrenheit").setValue(40.0);
    //System.out.println("fahrenheit:"+grid.getInputCell("fahrenheit").getValue());
    
    //score must be reset
    assertTrue(clone.getScore()==-1);
    //setup must stay the same
    assertTrue(clone.getSetup()==grid.getSetup());
  }

  public void assertNotSame(Object[] a, Object[] b) {
    super.assertNotSame(a, b);
    for (int i = 0; i < a.length; i++) {
      super.assertNotSame(a[i], b[i]);
    }
  }
  
  public void testXML(){
    grid.createSolution();
    String xml=grid.getXML();
    System.out.println(xml);
  }
}
