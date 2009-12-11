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

package nl.bluevoid.genpro.example.roman;

import java.util.ArrayList;

import nl.bluevoid.genpro.Grid;
import nl.bluevoid.genpro.GridSolutionEvaluator;
import nl.bluevoid.genpro.ScoringType;
import nl.bluevoid.genpro.Setup;
import nl.bluevoid.genpro.TestSet;
import nl.bluevoid.genpro.TestSetSolutionEvaluator;
import nl.bluevoid.genpro.cell.ConstantCell;
import nl.bluevoid.genpro.cell.LibraryCell;
import nl.bluevoid.genpro.cell.ReferenceCell;
import nl.bluevoid.genpro.operations.BooleanOperations;
import nl.bluevoid.genpro.operations.IfOperations;
import nl.bluevoid.genpro.operations.NumberOperations;
import nl.bluevoid.genpro.view.TrainerVisual;

/**
 * @author Rob van der Veer
 * @since 1.0
 */
public class RomanFiguresProblem extends TrainerVisual {

  public static void main(String[] args) {
    RomanFiguresProblem rfp = new RomanFiguresProblem();
    rfp.startTraining();
  }

  @Override
  public Setup createSetup() {
    Setup setup = new Setup(getClass().getSimpleName());
    // create all cells
    setup.addInputCell("roman", String.class);
    setup.addOutputCell("arabic", Integer.class);
    setup.setCallCells(15, "c", Integer.class, String.class, Boolean.class);
    // setup.setMaxSwitchCellNr(4, Boolean.class, Integer.class);

    setup.setLibraryCells(NumberOperations.NUM_OPS, BooleanOperations.BOOL_OPS, new LibraryCell(String.class),
        IfOperations.IF_OPS, new LibraryCell(Helper.class));

    ArrayList<ConstantCell> consts = new ArrayList<ConstantCell>();

    consts.add(new ConstantCell("constA", Integer.class, 0, 10));
    consts.add(new ConstantCell("constB", Integer.class, 0, 10));
    consts.add(new ConstantCell("constC", Integer.class, 0, 10));
    consts.add(new ConstantCell("constD", Integer.class, 0, 10));
    consts.add(new ConstantCell("constE", Integer.class, 0, 10));

    consts.add(new ConstantCell("constI", String.class, "I"));
    consts.add(new ConstantCell("constV", String.class, "V"));
    consts.add(new ConstantCell("constX", String.class, "X"));
    consts.add(new ConstantCell("constL", String.class, "L"));
    consts.add(new ConstantCell("constM", String.class, "M"));
    consts.add(new ConstantCell("constD", String.class, "D"));
    consts.add(new ConstantCell("constC", String.class, "C"));
    setup.setConstantCells(consts.toArray(new ConstantCell[0]));

    setup.setGenerationSize(2000);
    setup.setMutatePercentage(100);
    setup.setCrossingPercentage(0);

    setup.setMaxIndividualsWithSameScore(30);
    setup.setMinimumScoreForSaving(0);
    return setup;
  }

  @Override
  public TestSetSolutionEvaluator createEvaluator() {
    GridSolutionEvaluator gse = new GridSolutionEvaluator() {
      public double scoreOutput(ReferenceCell cell, Object calculated, Object expected) {
        if(calculated==null) return 500;
        return getAbsoluteNumberDifference((Number) calculated, (Number) expected);
      }

      @Override
      public double scoreGridException(Throwable t) {
        return 1000;
      }

      @Override
      public double scoreGrid(Grid g) {
        return g.getNrOfUsedCallCells() * 0.1; // each cell may cost a 0.1 deviation
      }

      @Override
      public TestSet createTestSet() {
        TestSet testSet = new TestSet(setup, "roman", "arabic");
        testSet.addCellValues("I", 1);
        testSet.addCellValues("II", 2);
        testSet.addCellValues("III", 3);
        testSet.addCellValues("IV", 4);
        testSet.addCellValues("V", 5);
        testSet.addCellValues("VI", 6);
        testSet.addCellValues("VII", 7);
        testSet.addCellValues("VIII", 8);

        testSet.addCellValues("IX", 9);
        testSet.addCellValues("X", 10);
        testSet.addCellValues("XI", 11);
        testSet.addCellValues("XII", 12);
        testSet.addCellValues("XIX", 19);
        testSet.addCellValues("XX", 20);
        testSet.addCellValues("XXX", 30);
        
        testSet.addCellValues("XL", 40);
        testSet.addCellValues("L", 50);
        testSet.addCellValues("C", 100);
        testSet.addCellValues("D", 500);
        testSet.addCellValues("M", 1000);
        testSet.addCellValues("LXXXII", 82);
        testSet.addCellValues("MXXXII", 1032);
        testSet.addCellValues("MDLXXXIV", 1584);
        return testSet;
      }
    };

    gse.setScoringType(ScoringType.SCORING_AVARAGE_PERCENTAGE_PER_TESTCASE);
    return gse;
  }
}
