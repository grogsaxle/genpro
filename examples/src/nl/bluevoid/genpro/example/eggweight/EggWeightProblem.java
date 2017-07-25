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

package nl.bluevoid.genpro.example.eggweight;

import nl.bluevoid.genpro.Grid;
import nl.bluevoid.genpro.GridSolutionEvaluator;
import nl.bluevoid.genpro.ScoringType;
import nl.bluevoid.genpro.Setup;
import nl.bluevoid.genpro.TestSet;
import nl.bluevoid.genpro.TestSetSolutionEvaluator;
import nl.bluevoid.genpro.cell.ConstantCell;
import nl.bluevoid.genpro.cell.LibraryCell;
import nl.bluevoid.genpro.cell.ReferenceCell;
import nl.bluevoid.genpro.operations.NumberOperations;
import nl.bluevoid.genpro.view.TrainerVisual;

/**
 * @author Rob van der Veer
 * @since 1.0
 */
public class EggWeightProblem extends TrainerVisual {

  public static void main(String[] args) throws Exception {
    EggWeightProblem ep = new EggWeightProblem();
    ep.startTraining();
  }

  @Override
  public Setup createSetup() {
    Setup setup = new Setup("EggWeight");

    // create all cells
    setup.addInputCell("height", Double.class);
    setup.addInputCell("width", Double.class);
    setup.addOutputCell("weight", Double.class);

    setup.setCallCells(5, "c", Double.class);

    ConstantCell cCell1 = new ConstantCell("const1", Double.class, -100, 100);
    ConstantCell cCell2 = new ConstantCell("const2", Double.class, -100, 100);
    ConstantCell cCell3 = new ConstantCell("const3", Double.class, -100, 100);
    setup.setConstantCells(cCell1, cCell2, cCell3);
    setup.setLibraryCells(NumberOperations.NUM_OPS, // NumberOperations.MATH_CLASS,
        GonioOperations.GONIO_OPS, new LibraryCell(Egg.class));

    setup.setGenerationSize(2000);
    setup.setMutatePercentage(30);
    setup.setMaxIndividualsWithSameScore(30);
    setup.setMinimumScoreForSaving(3);
    return setup;
  }

  @Override
  public TestSetSolutionEvaluator createEvaluator() {
    GridSolutionEvaluator gse = new GridSolutionEvaluator() {
      public double scoreOutput(ReferenceCell cell, Object calculated, Object expected) {
        if (calculated == null)
          return 300;
        return getAbsoluteNumberDifferencePercentage((Number) calculated, (Number) expected);
      }

      @Override
      public double scoreGrid(Grid g) {
        return 0;// g.getNrOfUsedCallCells()*0.1; // each cell may cost a 0.1 gram deviation
      }

      @Override
      public double scoreGridException(Throwable t) {
        return 0;
      }

      @Override
      public TestSet createTestSet() {
        TestSet testSet = new TestSet(setup, "height", "width", "weight");
        testSet.addCellValuesFromFile("eggData.txt");
        return testSet;
      }
    };
    
    gse.setScoringType(ScoringType.SCORING_AVARAGE_PERCENTAGE_PER_TESTCASE);
    return gse;
  }
}
