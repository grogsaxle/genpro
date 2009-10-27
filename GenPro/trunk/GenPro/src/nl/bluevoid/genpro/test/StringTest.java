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
import nl.bluevoid.genpro.GenerationRunner;
import nl.bluevoid.genpro.Grid;
import nl.bluevoid.genpro.Setup;
import nl.bluevoid.genpro.TestSet;
import nl.bluevoid.genpro.cell.ConstantCell;
import nl.bluevoid.genpro.cell.LibraryCell;
import nl.bluevoid.genpro.cell.ReferenceCell;
import nl.bluevoid.genpro.operations.BooleanOperations;
import nl.bluevoid.genpro.operations.NumberOperations;
import nl.bluevoid.genpro.util.Debug;
/**
 * @author Rob van der Veer
 * @since 1.0
 */
public class StringTest extends TestCase {

  private TestSet testSet;
  private Setup setup;

  // @Override
  protected void setUp() throws Exception {
    setup = new Setup("StringTest");
    setup.setEvaluateMultiThreaded(false);
    setup.addInputCell("in", String.class);
    setup.addOutputCell("out", String.class);

    ConstantCell cCell = new ConstantCell("const1", Integer.class, -100, 100);
    ConstantCell cCell2 = new ConstantCell("const2", Integer.class, -100, 100);
    setup.setConstantCells(new ConstantCell[] { cCell, cCell2 });

    setup.setLibraryCells(new LibraryCell[] { NumberOperations.NUM_OPS, BooleanOperations.BOOL_OPS });

    setup.setCallCells(4, "c", new Class[] { String.class });// , Integer.class, Boolean.class });
    // setup.addIfCell(1);

    testSet = new TestSet(setup, new String[] { "in", "out" }) {
      public double scoreOutput(ReferenceCell cell, Object calculated, Object expected) {
        return getStringDifference((String) calculated, (String) expected);
      }

      @Override
      public double scoreGrid(Grid g) {
        return 0;
      }
    };

    testSet.addCellValues( "Annapurna", "napur" );
    testSet.addCellValues( "Kailash", "ilash" );
    testSet.addCellValues("Everest", "erest" );
    testSet.addCellValues("Mont Blanc", "nt bl" );
    testSet.addCellValues( "Stone Henge", "one h" );
    testSet.addCellValues( "Tour Eifel", "ur ei" );
    // testSet.addCellValues(new String[] { "Mont blanc", "nt bl" });
  }

  public void runGenerations() {
    try {
      GenerationRunner gr = new GenerationRunner(setup, testSet);
      gr.runGenerations();
    } catch (Throwable e) {
      Debug.printFullStackTrace(e);
    }
  }

  public static void main(String[] args) throws Exception {
    StringTest t = new StringTest();
    t.setUp();
    t.runGenerations();
  }

}
