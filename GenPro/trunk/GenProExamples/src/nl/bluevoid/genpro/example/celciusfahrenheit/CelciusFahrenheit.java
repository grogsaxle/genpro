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

package nl.bluevoid.genpro.example.celciusfahrenheit;

import nl.bluevoid.genpro.Grid;
import nl.bluevoid.genpro.GridSolutionEvaluator;
import nl.bluevoid.genpro.Setup;
import nl.bluevoid.genpro.TestSet;
import nl.bluevoid.genpro.TestSetSolutionEvaluator;
import nl.bluevoid.genpro.cell.ConstantCell;
import nl.bluevoid.genpro.cell.LibraryCell;
import nl.bluevoid.genpro.cell.ReferenceCell;
import nl.bluevoid.genpro.operations.NumberOperations;
import nl.bluevoid.genpro.view.TrainerVisual;

/**
 * This example shows the setup that is needed for finding the formula which converts degrees celcius to
 * degrees fahrenheit.
 * 
 * Extending nl.bluevoid.genpro.Trainer instead of TrainerVisual will not start the visual part.
 * 
 * Anyway 2 methods need to be implemented:
 * 
 * Setup createSetup() TestSet createTestSet()
 * 
 * Setup defines the inputs and outputs, the "search space" and the search parameters. The search space
 * consists of all the cells (constants, libraries(operations) and callcells)
 * 
 * TestSet defines the fitness function and the testvalues for each input and output.
 * 
 * @author Rob van der Veer
 * @since 1.0
 */
public class CelciusFahrenheit extends TrainerVisual {

  // Tested at august 19: 500 generations: 56-58 ms
  // idem at 3000 generations 153 ms (after speedup)

  public static void main(String[] args) throws Exception {
    final CelciusFahrenheit t = new CelciusFahrenheit();
    t.startTraining();
  }

  @Override
  public Setup createSetup() {
    Setup setup = new Setup();

    // define inputs and outputs
    setup.addInputCell("fahrenheit", Double.class);
    setup.addOutputCell("celcius", Double.class);

    // define searchspace (constants, libraries(operations) and nr of callcells)
    ConstantCell cCell = new ConstantCell("const1", Double.class, -100, 100);
    ConstantCell cCell2 = new ConstantCell("const2", Double.class, -100, 100);
    ConstantCell cCell3 = new ConstantCell("const3", Double.class, -100, 100);
    setup.setConstantCells(new ConstantCell[] { cCell, cCell2, cCell3 });

    setup.setLibraryCells(new LibraryCell(NumberOperations.class));
    // , new LibraryCell(Math.class) });
    setup.setCallCells(5, "c", new Class[] { Double.class });

    // define search parameters
    setup.setGenerationSize(1000);
    setup.setMutatePercentage(20);
    setup.setMaxIndividualsWithSameScore(30);
    setup.setGridHistoryTracking(true);
    return setup;
  }

  @Override
  public TestSetSolutionEvaluator createEvaluator() {
    return new GridSolutionEvaluator() {
      @Override
      public double scoreOutput(ReferenceCell outputCell, Object calculated, Object expected) {
        if (calculated == null)
          return 100;
        return getAbsoluteNumberDifference((Number) calculated, (Number) expected);
      }

      @Override
      public double scoreGrid(Grid g) {
        return 0;
      }

      @Override
      public double scoreGridException(Throwable t) {
        return 0;
      }

      @Override
      public TestSet createTestSet() {
        TestSet testSet = new TestSet(getSetup(), new String[] { "fahrenheit", "celcius" });
        testSet.addCellValues(33.8, 1d);
        testSet.addCellValues(39.2, 4d);
        testSet.addCellValues(64.4, 18d);
        testSet.addCellValues(71.6, 22d);
        testSet.addCellValues(98.60000000000001, 37d);
        testSet.addCellValues(113d, 45d);
        testSet.addCellValues(212d, 100d);
        testSet.addCellValues(392d, 200d);
        testSet.addCellValues(752d, 400d);
        testSet.addCellValues(32d, 0.0);
        return testSet;
      }
    };
  }
}