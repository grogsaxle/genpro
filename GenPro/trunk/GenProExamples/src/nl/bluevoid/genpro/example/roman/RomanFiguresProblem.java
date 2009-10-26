package nl.bluevoid.genpro.example.roman;

import java.util.ArrayList;

import nl.bluevoid.genpro.Grid;
import nl.bluevoid.genpro.ScoringType;
import nl.bluevoid.genpro.Setup;
import nl.bluevoid.genpro.TestSet;
import nl.bluevoid.genpro.Trainer;
import nl.bluevoid.genpro.cell.ConstantCell;
import nl.bluevoid.genpro.cell.ReferenceCell;
import nl.bluevoid.genpro.operations.BooleanOperations;
import nl.bluevoid.genpro.operations.NumberOperations;
import nl.bluevoid.genpro.view.TrainerVisual;

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
    setup.setMaxSwitchCellNr(4, Boolean.class, Integer.class);
    
    setup.setLibraryCells(NumberOperations.NUM_OPS, BooleanOperations.BOOL_OPS);

    ArrayList<ConstantCell> consts = new ArrayList<ConstantCell>();
    consts.add(new ConstantCell("const1", Integer.class, 1));
    consts.add(new ConstantCell("const5", Integer.class, 5));
    consts.add(new ConstantCell("const10", Integer.class, 10));
    consts.add(new ConstantCell("const50", Integer.class, 50));
    consts.add(new ConstantCell("const100", Integer.class, 100));
    consts.add(new ConstantCell("const500", Integer.class, 500));
    consts.add(new ConstantCell("const1000", Integer.class, 1000));
    
    consts.add(new ConstantCell("constA", Integer.class, 0,10));
    consts.add(new ConstantCell("constB", Integer.class, 0,10));
    consts.add(new ConstantCell("constC", Integer.class, 0,10));
    consts.add(new ConstantCell("constD", Integer.class, 0,10));
    consts.add(new ConstantCell("constE", Integer.class, 0,10));
    
    consts.add(new ConstantCell("constI", String.class, "I"));
    consts.add(new ConstantCell("constV", String.class, "V"));
    consts.add(new ConstantCell("constX", String.class, "X"));
    consts.add(new ConstantCell("constL", String.class, "L"));
    consts.add(new ConstantCell("constM", String.class, "M"));
    consts.add(new ConstantCell("constD", String.class, "D"));
    consts.add(new ConstantCell("constC", String.class, "C"));
    setup.setConstantCells(consts.toArray(new ConstantCell[0]));

    setup.setLibraryCells(NumberOperations.NUM_OPS);
    setup.setGenerationSize(2000);
    setup.setMutatePercentage(100);
    setup.setCrossingPercentage(0);
    
    setup.setMaxIndividualsWithSameScore(30);
    setup.setMinimumScoreForSaving(0);
    setup.setStopAtScore(1);
    return setup;
  }

  @Override
  public TestSet createTestSet() {
    TestSet testSet = new TestSet(setup, "roman", "arabic") {
      public double scoreOutput(ReferenceCell cell, Object calculated, Object expected) {
        return getAbsoluteNumberDifference((Number)calculated, (Number)expected);
      }

      @Override
      public double scoreGrid(Grid g) {
        return g.getNrOfUsedCallCells() * 0.1; // each cell may cost a 0.1 deviation
      }
    };
    testSet.setScoringType(ScoringType.SCORING_AVARAGE_PER_TESTCASE);

    testSet.addCellValues("I", 1);
    testSet.addCellValues("II", 2);
    testSet.addCellValues("III", 2);
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
//    testSet.addCellValues("XL", 40);
//    testSet.addCellValues("L", 50);
//    testSet.addCellValues("C", 100);
//    testSet.addCellValues("D", 500);
//    testSet.addCellValues("M", 1000);
//    testSet.addCellValues("LXXXII", 82);
//    testSet.addCellValues("MXXXII", 1032);
//    testSet.addCellValues("MDLXXXIV", 1584);
    return testSet;
  }
}